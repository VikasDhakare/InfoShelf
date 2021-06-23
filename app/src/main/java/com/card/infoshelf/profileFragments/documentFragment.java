package com.card.infoshelf.profileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.card.infoshelf.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class documentFragment extends Fragment {

    List<GridModel> fileArray = new ArrayList<>();
    DatabaseReference Ref,newRef;
    RecyclerView recyclerView;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
   FileAdapter fileAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_document, container, false);
        recyclerView = view.findViewById(R.id.Rv_grid);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);
        fileAdapter = new FileAdapter(getActivity(),fileArray);
        fileArray.clear();
        recyclerView.setAdapter(fileAdapter);

        Ref = FirebaseDatabase.getInstance().getReference();
        Ref.child("UserPostData").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String timestampUrl = ds.getValue().toString();
                        newRef = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestampUrl);
                        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                GridModel model = snapshot.getValue(GridModel.class);
                                String type = model.getFileType();
                                if (!type.equals("video") && !type.equals("image") && !type.equals("none")){
                                    fileArray.add(model);
                                    fileAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}