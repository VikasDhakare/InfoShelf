package com.card.infoshelf.AllFriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


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

public class AllFriendsActivity extends AppCompatActivity {

    private RecyclerView all_friend_rv;
    private ArrayList<networkModel> list;
    private AllFriendsAdaptor adaptor;
    private DatabaseReference Ref, userInfoRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private String userid;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends);
        getSupportActionBar().hide();

        all_friend_rv = findViewById(R.id.all_friends_rv);
        all_friend_rv.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adaptor = new AllFriendsAdaptor(this, list);
        all_friend_rv.setAdapter(adaptor);

        search = findViewById(R.id.search);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");

        Ref.child("Friends").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.getKey();
                        String state = dataSnapshot.child("Friends").getValue().toString();
                        if (state.equals("Saved"))
                        {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filer(s.toString());
            }
        });


    }

    private void filer(String text) {
        ArrayList<networkModel> filterList = new ArrayList<>();

        for (networkModel model : list)
        {
            if (model.getUserName().toLowerCase().contains(text.toLowerCase()))
            {
                filterList.add(model);
            }
        }
        adaptor.filterList(filterList);

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