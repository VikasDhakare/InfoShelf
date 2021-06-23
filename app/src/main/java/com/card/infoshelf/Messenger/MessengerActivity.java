package com.card.infoshelf.Messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.AllFriends.AllFriendsActivity;
import com.card.infoshelf.MainActivity;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MessengerActivity extends AppCompatActivity {

    ImageView btn_add  , iv_back ;
    private RecyclerView chat_rv;
    private ArrayList<networkModel> list;
    private MessengerAdaptor adaptor;
    private DatabaseReference Ref, userInfoRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private String userid;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        getSupportActionBar().hide();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chat_rv = findViewById(R.id.chat_rv);
        linearLayoutManager.setReverseLayout(true);
        chat_rv.setLayoutManager(linearLayoutManager);


        list = new ArrayList<>();
        adaptor = new MessengerAdaptor(this, list);
        chat_rv.setAdapter(adaptor);

        search = findViewById(R.id.search);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");

        btn_add  = findViewById(R.id.btn_add);
        iv_back  = findViewById(R.id.iv_back);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MessengerActivity.this , AllFriendsActivity.class);
                startActivity(intent);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MessengerActivity.this , MainActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        Ref.child("ChatList").child(CurrentUserId).orderByChild("time").limitToLast(200).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        networkModel user = dataSnapshot.getValue(networkModel.class);
                        list.add(user);
                    }
                    adaptor.notifyDataSetChanged();

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

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String CurrentDate, CurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM , yyyy");
        CurrentDate = currentDate.format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        CurrentTime = currentTime.format(calendar1.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time", CurrentTime);
        onlineState.put("date", CurrentDate);
        onlineState.put("status", status);


        ref.updateChildren(onlineState);
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