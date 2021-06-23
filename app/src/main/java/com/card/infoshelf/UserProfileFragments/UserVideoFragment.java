package com.card.infoshelf.UserProfileFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.card.infoshelf.profileFragments.GridModel;
import com.card.infoshelf.profileFragments.videoAdapter;
import com.card.infoshelf.profileFragments.videoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserVideoFragment extends videoFragment {
    List<GridModel> videoArray = new ArrayList<>();
    DatabaseReference Ref,newRef;
    RecyclerView recyclerView;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    String userid;

    userVideoFragmentAdapter uservideoadapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_video, container, false);
        String userId = getActivity().getIntent().getStringExtra("userid");

        recyclerView = view.findViewById(R.id.Rv_grid);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        uservideoadapter = new userVideoFragmentAdapter(getActivity(),videoArray);

        recyclerView.setAdapter(uservideoadapter);

        Ref = FirebaseDatabase.getInstance().getReference();
        Ref.child("UserPostData").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    uservideoadapter.notifyDataSetChanged();
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
