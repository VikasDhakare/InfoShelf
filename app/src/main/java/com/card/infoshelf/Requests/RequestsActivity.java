package com.card.infoshelf.Requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.card.infoshelf.R;
import com.card.infoshelf.myProfileTabaccessAdaptor;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RequestsActivity extends AppCompatActivity {
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private RequestsTabAccessAdapter requestsTabAccessAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        myViewPager = findViewById(R.id.my_profile_pager);
        requestsTabAccessAdapter = new RequestsTabAccessAdapter(this.getSupportFragmentManager());
        myViewPager.setAdapter(requestsTabAccessAdapter);

        myTabLayout = findViewById(R.id.my_profile_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
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