package com.card.infoshelf.profileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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


public class videoFragment extends Fragment {
    List<GridModel> videoArray = new ArrayList<>();
    DatabaseReference Ref,newRef;
    RecyclerView recyclerView;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    videoAdapter videoAdapter;
    GridModel gridModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        recyclerView = view.findViewById(R.id.Rv_grid);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        videoAdapter = new videoAdapter(getActivity(),videoArray);

        recyclerView.setAdapter(videoAdapter);

        Ref = FirebaseDatabase.getInstance().getReference();
        Ref.child("UserPostData").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    videoArray.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String timestampUrl = ds.getValue().toString();
                        newRef = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestampUrl);
                        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                GridModel model = snapshot.getValue(GridModel.class);
                                String type = model.getFileType();
                                if (type.equals("video")){
                                    videoArray.add(model);
                                    videoAdapter.notifyDataSetChanged();
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