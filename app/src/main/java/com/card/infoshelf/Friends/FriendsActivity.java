package com.card.infoshelf.Friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.card.infoshelf.allUserAdaptor;
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsActivity extends AppCompatActivity {

    RecyclerView all_friend_rv;
    private ArrayList<networkModel> list;
    private allUserAdaptor adaptor;
    private DatabaseReference Ref, userInfoRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().hide();

        userid = getIntent().getStringExtra("userid").toString();

        all_friend_rv = findViewById(R.id.all_friends_rv);
        all_friend_rv.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adaptor = new allUserAdaptor(this, list);
        all_friend_rv.setAdapter(adaptor);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");


        Ref.child("Friends").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.getKey();
                        userInfoRef.child(id).addValueEventListener(new ValueEventListener() {
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
    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        HashMap map = new HashMap();
        map.put("status" , status);

        ref.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}