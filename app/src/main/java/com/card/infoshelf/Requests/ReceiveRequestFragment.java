package com.card.infoshelf.Requests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.card.infoshelf.allUserAdaptor;
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ReceiveRequestFragment extends Fragment {

    View root;
    RecyclerView receive_req_rv;
    private ArrayList<networkModel> list;
    private allUserAdaptor adaptor;
    private DatabaseReference Ref, userInfoRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_receive_request, container, false);

        receive_req_rv = root.findViewById(R.id.receive_req_rv);
        receive_req_rv.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adaptor = new allUserAdaptor(getContext(), list);
        receive_req_rv.setAdapter(adaptor);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");

        Ref.child("Chat Requests").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String id = dataSnapshot.getKey();
                        Ref.child("Chat Requests").child(CurrentUserId).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                {
                                    String type = snapshot.child("request_type").getValue().toString();
                                    if (type.equals("received"))
                                    {
                                        userInfoRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                networkModel user = snapshot.getValue(networkModel.class);
                                                list.add(user);
                                                adaptor.notifyDataSetChanged();


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
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return root;
    }
}