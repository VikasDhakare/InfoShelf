package com.card.infoshelf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.bottomfragment.nearByModel;
import com.card.infoshelf.bottomfragment.networkModel;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class allNearByUserAdapter extends RecyclerView.Adapter<allNearByUserAdapter.Myviewholder> {

    private Context context;
    private ArrayList<nearByModel> list;
    private DatabaseReference UsersRef , ChatRef , FriendsRef , PostRef;
    private FirebaseAuth mAuth;
    private String currentState , senderUserID  , receiverUserID;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public allNearByUserAdapter(Context context, ArrayList<nearByModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alluserlayout,parent,false);
        return new Myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder holder, int position) {

        nearByModel user = list.get(position);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = user.getUserId();
        currentState = "new";

        String userId = user.getUserId();

        holder.getUserInfo(userId);
        getUserLocation(user.getLatitude(), user.getLongitude(), holder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userProfileActivity.class);
                intent.putExtra("userid", userId);
                context.startActivity(intent);
            }
        });

        if (!senderUserID.equals(userId))
        {
            holder.request.setVisibility(View.VISIBLE);
            holder.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String id = user.getUserId();
                    SendChatRequest(holder , id);
                }
            });
            holder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    CancelChatRequest(holder , id , position);
                }
            });
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    AcceptChatRequest(holder , id);
                }
            });
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    CancelChatRequest(holder , id, position);
                }
            });
            holder.accepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = user.getUserId();
                    RemoveSpecificContact(holder , id);
                }
            });
        }
        else
        {
            holder.request.setVisibility(View.GONE);
            holder.send.setVisibility(View.GONE);
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            holder.accepted.setVisibility(View.GONE);


        }

        ManageChatRequests(holder , userId);
    }

    private void getUserLocation(double latitude, double longitude, Myviewholder holder) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(context, latitude, longitude, holder);

        } else {
//            ActivityCompat.requestPermissions(SendCurrentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(Context context, double latitude, double longitude, Myviewholder holder) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

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

                            double mlatitude = location1.getLatitude();
                            double mlongitude = location1.getLongitude();

                            float[] results = new float[1];
                            Location.distanceBetween(mlatitude, mlongitude, latitude, longitude, results);
                            float distance = results[0];
                            String text = String.format("%.2f", distance);

                            holder.userInfo.setText(""+text+" m"+" away");
                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        }else {
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }

    private void ManageChatRequests(Myviewholder holder, String userId) {
        ChatRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId))
                {
                    String request_type = snapshot.child(userId).child("request_type").getValue().toString();

                    if (request_type.equals("sent"))
                    {
                        currentState = "request_sent";

                        holder.request.setVisibility(View.GONE);
                        holder.send.setVisibility(View.VISIBLE);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.GONE);
                        holder.accepted.setVisibility(View.GONE);


                    }
                    else if (request_type.equals("received"))
                    {
                        currentState = "request_received";

                        holder.request.setVisibility(View.GONE);
                        holder.send.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.accepted.setVisibility(View.GONE);

                    }
                    else if (request_type.equals("cancel"))
                    {
                        currentState = "new";

                        holder.request.setVisibility(View.VISIBLE);
                        holder.send.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.GONE);
                        holder.accepted.setVisibility(View.GONE);
                    }
                }
                else {

                    FriendsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userId))
                            {
                                String f = snapshot.child(userId).child("Friends").getValue().toString();
                                if (f.equals("Saved"))
                                {
                                    currentState = "friends";

                                    holder.request.setVisibility(View.GONE);
                                    holder.send.setVisibility(View.GONE);
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.accepted.setVisibility(View.VISIBLE);

                                }
                                else if (f.equals("UnFriend"))
                                {
                                    currentState = "new";

                                    holder.request.setVisibility(View.VISIBLE);
                                    holder.send.setVisibility(View.GONE);
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.accepted.setVisibility(View.GONE);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void RemoveSpecificContact(Myviewholder holder, String id) {
        FriendsRef.child(senderUserID).child(id).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    UsersRef.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(id).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        currentState = "new";
                                        ManageChatRequests(holder , id);


                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(id).child(senderUserID).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists())
                                        {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(id).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests(holder , id);



                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            ChatRef.child(id).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests(holder , id);
                                                    FriendsRef.child(senderUserID).child(id).removeValue();
                                                    FriendsRef.child(id).child(senderUserID).removeValue();

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptChatRequest(Myviewholder holder, String id) {
        FriendsRef.child(senderUserID).child(id).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    UsersRef.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(id).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ManageChatRequests(holder , id);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(id).child(senderUserID).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists())
                                        {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(id).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ManageChatRequests(holder , id);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            ChatRef.child(id).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "friends";
                                                    ManageChatRequests(holder , id);

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });

    }

    private void CancelChatRequest(Myviewholder holder, String id, int position) {
        ChatRef.child(senderUserID).child(id).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    ChatRef.child(id).child(senderUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                currentState = "new";
                                ManageChatRequests(holder , id);
                                ChatRef.child(senderUserID).child(id).child("request_type").removeValue();
                                ChatRef.child(id).child(senderUserID).child("request_type").removeValue();
                            }
                        }
                    });
                }

            }
        });
    }

    private void SendChatRequest(Myviewholder holder, String id) {
        currentState = "new";
        ChatRef.child(senderUserID).child(id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    ChatRef.child(id).child(senderUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                currentState = "request_sent";
                                ManageChatRequests(holder , id);
                            }
                        }
                    });
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Myviewholder extends RecyclerView.ViewHolder {

        private TextView username, userInfo;
        private CircleImageView userProfile;
        private TextView  request , accept , reject , send  , accepted;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username);
            userInfo = itemView.findViewById(R.id.userInfo);
            userProfile = itemView.findViewById(R.id.userProfile);
            request = itemView.findViewById(R.id.request);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            send = itemView.findViewById(R.id.send);
            accepted = itemView.findViewById(R.id.accepted);
        }

        public void getUserInfo(String userId) {
            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
            userNameRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String user_name = snapshot.child("userName").getValue().toString();
                    username.setText(user_name);

                    //                for profile pic
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(userProfile);
                    }
//                    else {
//                        Picasso.get().load(R.drawable.profile).into(userProfile);
//                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
