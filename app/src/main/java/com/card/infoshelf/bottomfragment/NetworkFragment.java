package com.card.infoshelf.bottomfragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.MainActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.allUserActivity;
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

public class NetworkFragment extends Fragment {

    private RecyclerView network_recycler, peoples_u_recycler, near_by_recycler;
    private ArrayList<networkModel> list, userList;
    private ArrayList<nearByModel> nearByList;
    private DatabaseReference userInfoRef;
    private network_adaptor adaptor, allUserAdaptor;
    private nearByAdapter nearbyadapter;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private TextView more_rec, more_umk, near_by, allow_permission;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int REQUEST_CODE = 1;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_network, container, false);

        network_recycler = root.findViewById(R.id.network_recycler);
        peoples_u_recycler = root.findViewById(R.id.peoples_u_recycler);
        more_rec = root.findViewById(R.id.more_rec);
        near_by_recycler = root.findViewById(R.id.near_by_recycler);
        more_umk = root.findViewById(R.id.more_umk);
        near_by = root.findViewById(R.id.near_by);
        allow_permission = root.findViewById(R.id.allow_permission);

//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        userInfoRef = FirebaseDatabase.getInstance().getReference("UserDetails");
        CurrentUserId = mAuth.getCurrentUser().getUid();

        network_recycler.setHasFixedSize(true);
        network_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        network_recycler.setItemAnimator(new DefaultItemAnimator());

        peoples_u_recycler.setHasFixedSize(true);
        peoples_u_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        peoples_u_recycler.setItemAnimator(new DefaultItemAnimator());

        near_by_recycler.setHasFixedSize(true);
        near_by_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        near_by_recycler.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        userList = new ArrayList<>();
        nearByList = new ArrayList<>();
        adaptor = new network_adaptor(getActivity(),list);
        allUserAdaptor = new network_adaptor(getActivity(), userList);
        nearbyadapter = new nearByAdapter(getActivity(), nearByList);
        network_recycler.setAdapter(adaptor);
        peoples_u_recycler.setAdapter(allUserAdaptor);
        near_by_recycler.setAdapter(nearbyadapter);


        near_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), allUserActivity.class);
                intent.putExtra("type", "nearBy");
                startActivity(intent);
            }
        });

        more_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), allUserActivity.class);
                intent.putExtra("type", "recommended");
                startActivity(intent);
            }
        });

        more_umk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), allUserActivity.class);
                intent.putExtra("type", "umk");
                startActivity(intent);
            }
        });

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

        getUsersLocation();
        getAllUsers();

        allow_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    getUsersLocation();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    }, REQUEST_CODE);

                }

            }
        });

        return root;
    }

    private void getUsersLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            near_by.setVisibility(View.VISIBLE);
            near_by_recycler.setVisibility(View.VISIBLE);
            allow_permission.setVisibility(View.GONE);
            getCurrentLocation();

        } else {
            allow_permission.setVisibility(View.VISIBLE);
//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

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

                            if (distance < 100000){
                                nearByModel model = ds.getValue(nearByModel.class);
                                nearByList.add(model);
                            }
                        }
                    }
                    nearbyadapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllUsers() {
        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String id = ""+dataSnapshot.child("userId").getValue().toString();

                    if (!id.equals(CurrentUserId)){
                        networkModel user = dataSnapshot.getValue(networkModel.class);
                        userList.add(user);
                    }
                }
                allUserAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                getCurrentLocation();
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}