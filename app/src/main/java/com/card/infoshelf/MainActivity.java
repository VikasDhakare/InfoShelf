package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.card.infoshelf.bottomfragment.AddTimeline;
import com.card.infoshelf.bottomfragment.NetworkFragment;
import com.card.infoshelf.bottomfragment.NotificationFragment;
import com.card.infoshelf.bottomfragment.Profilefragment;
import com.card.infoshelf.bottomfragment.TimelineFragment;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DatabaseReference userREf;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFunctionRepeat.run();

        getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        int menuItemId = bottomNavigationView.getMenu().getItem(3).getItemId();
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
//        badge.setNumber(2);
//        badge.clearNumber();
        mAuth = FirebaseAuth.getInstance();
        userREf = FirebaseDatabase.getInstance().getReference("Users");
        CurrentUserId = mAuth.getCurrentUser().getUid();

        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new TimelineFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.timeline);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()){

                    case R.id.timeline:
                        fragment = new TimelineFragment();
                        break;
                    case R.id.network:
                        fragment = new NetworkFragment();
                        break;
                    case R.id.addTimeline:
                        fragment = new AddTimeline();
                        break;
                    case R.id.notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.profile:
                        fragment = new Profilefragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                return true;
            }
        });

        checkNotification(badge);

        getUserLocation();

    }

    public Runnable mFunctionRepeat = new Runnable() {
        @Override
        public void run() {
           getUserLocation();
//            Log.d(SCREEN_TOGGLE_TAG, "Code Repeat");
            mHandler.postDelayed(this, 5000);
        }
    };

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(MainActivity.this);

        } else {
//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(MainActivity mainActivity) {


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

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

                            uploadLocation(latitude, longitude);

                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        }else {
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    private void uploadLocation(double latitude, double longitude) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
        userRef.child("longitude").setValue(longitude);
        userRef.child("latitude").setValue(latitude);
        userRef.child("permission").setValue("Granted");
    }

    private void checkNotification(BadgeDrawable badge) {
        userREf.child(CurrentUserId).child("Notifications").orderByChild("status").equalTo("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        int noti_badge = (int) snapshot.getChildrenCount();
                        if (noti_badge > 0){
                            badge.setNumber(noti_badge);
                        }
                        else {
                            badge.setVisible(false);
                        }
                    }
                }
                else {
                    badge.setVisible(false);
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