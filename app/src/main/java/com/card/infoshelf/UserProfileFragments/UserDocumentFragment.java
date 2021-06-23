package com.card.infoshelf.UserProfileFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.card.infoshelf.profileFragments.FileAdapter;
import com.card.infoshelf.profileFragments.GridModel;
import com.card.infoshelf.profileFragments.documentFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDocumentFragment extends documentFragment {

    List<GridModel> fileArray = new ArrayList<>();
    DatabaseReference Ref,newRef;
    RecyclerView recyclerView;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    UserDocumentFragmentAdapter userDocumentFragmentAdapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_documents, container, false);
        String userId = getActivity().getIntent().getStringExtra("userid");
        recyclerView = view.findViewById(R.id.Rv_grid);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);
        userDocumentFragmentAdapter = new UserDocumentFragmentAdapter(getActivity(),fileArray);
        fileArray.clear();
        recyclerView.setAdapter(userDocumentFragmentAdapter);

        Ref = FirebaseDatabase.getInstance().getReference();
        Ref.child("UserPostData").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    userDocumentFragmentAdapter.notifyDataSetChanged();
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
