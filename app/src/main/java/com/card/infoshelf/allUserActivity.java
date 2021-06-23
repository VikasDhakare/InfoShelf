package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;

import com.card.infoshelf.bottomfragment.nearByModel;
import com.card.infoshelf.bottomfragment.networkModel;
import com.card.infoshelf.bottomfragment.network_adaptor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class allUserActivity extends AppCompatActivity {

    private RecyclerView allUserRecycler;
    private ArrayList<networkModel> list;
    private allUserAdaptor adaptor;
    private allNearByUserAdapter nearByAdapter;
    private ArrayList<nearByModel> nearList;
    private DatabaseReference userInfoRef;
    private String type;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        type = getIntent().getStringExtra("type").toString();
        allUserRecycler = findViewById(R.id.allUserRecycler);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");

        allUserRecycler.setHasFixedSize(true);
        allUserRecycler.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        nearList = new ArrayList<>();
        adaptor = new allUserAdaptor(this,list);
        nearByAdapter = new allNearByUserAdapter(this, nearList);
        allUserRecycler.setAdapter(adaptor);
//        allUserRecycler.setAdapter(nearByAdapter);

        if (type.equals("recommended")){

            userInfoRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String getProf = snapshot.child("profession").getValue().toString();

                    userInfoRef.orderByChild("profession").equalTo(getProf).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                String id = ""+dataSnapshot.child("userId").getValue().toString();

                                if (!id.equals(CurrentUserId)){
                                    networkModel user = dataSnapshot.getValue(networkModel.class);
                                    list.add(user);
                                }
                            }
                            adaptor.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (type.equals("umk")){

            userInfoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String id = ""+dataSnapshot.child("userId").getValue().toString();

                        if (!id.equals(CurrentUserId)){
                            networkModel user = dataSnapshot.getValue(networkModel.class);
                            list.add(user);
                        }
                    }
                    adaptor.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            if (type.equals("nearBy")){

                getUsersLocation();
            }
        }
    }

    private void getUsersLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();

        } else {

//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    LocationCallback locationCallback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {

                            Location location1 = locationResult.getLastLocation();

                            double latitude = location1.getLatitude();
                            double longitude = location1.getLongitude();

                            getNearByUsers(latitude, longitude);
                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        }else {
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    private void getNearByUsers(double mlatitude, double mlongitude) {
        DatabaseReference userREf = FirebaseDatabase.getInstance().getReference("Users");
        userREf.orderByChild("permission").equalTo("Granted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot ds : snapshot.getChildren()){

                        double lat = Double.parseDouble(""+ds.child("latitude").getValue().toString());
                        double longi = Double.parseDouble(""+ds.child("longitude").getValue().toString());
                        String id = ""+ds.child("userId").getValue().toString();

                        if (!id.equals(CurrentUserId)){

                            float[] results = new float[1];
                            Location.distanceBetween(mlatitude, mlongitude, lat, longi, results);
                            float distance = results[0];

                            if (distance < 5000){
                                nearByModel model = ds.getValue(nearByModel.class);
                                nearList.add(model);
                            }
                        }
                    }
                    nearByAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}