package com.card.infoshelf;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.card.infoshelf.Friends.FriendsActivity;
import com.card.infoshelf.Requests.userProfileTabAccessAdaptor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfileActivity extends AppCompatActivity {

    private TextView userName, userinfo, viewProfile, request, accept, reject, send, accepted, f_count;
    private LinearLayout network;
    private String userid;
    private ImageView coverPic, dialogImage;
    private CircleImageView userProfile;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private BottomSheetDialog bottomSheetDialog;
    private Dialog mDialog;
    private DatabaseReference UsersRef, ChatRef, FriendsRef, PostRef;
    private String currentState, senderUserID, receiverUserID;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private userProfileTabAccessAdaptor tabaccessAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().hide();

        userid = getIntent().getStringExtra("userid").toString();

        userinfo = findViewById(R.id.userinfo);
        userName = findViewById(R.id.userName);
        coverPic = findViewById(R.id.coverPic);
        userProfile = findViewById(R.id.userProfile);
        request = findViewById(R.id.request);
        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);
        send = findViewById(R.id.send);
        accepted = findViewById(R.id.accepted);
        network = findViewById(R.id.network);
        f_count = findViewById(R.id.f_count);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = userid;
        currentState = "new";

        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.show_profile_cover_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogImage = mDialog.findViewById(R.id.dialog_image);

        myViewPager = findViewById(R.id.my_profile_pager);
        tabaccessAdaptor = new userProfileTabAccessAdaptor(this.getSupportFragmentManager());
        myViewPager.setAdapter(tabaccessAdaptor);

        myTabLayout = findViewById(R.id.my_profile_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_picture_in_picture_24);
        myTabLayout.getTabAt(1).setIcon(R.drawable.video);
        myTabLayout.getTabAt(2).setIcon(R.drawable.document);
        myTabLayout.getTabAt(3).setIcon(R.drawable.about);


        bottomSheetDialog = new BottomSheetDialog(userProfileActivity.this, R.style.BottomSheetStyle);

        View view = LayoutInflater.from(userProfileActivity.this).inflate(R.layout.bottom_sheet_all_profile, (LinearLayout) findViewById(R.id.sheet1));

        bottomSheetDialog.setContentView(view);

        viewProfile = bottomSheetDialog.findViewById(R.id.viewProfile);
        TextView titleProfile = bottomSheetDialog.findViewById(R.id.titleProfile);

        FriendsRef.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount();
                    f_count.setText("" + count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        coverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleProfile.setText("Cover Photo");
                viewProfile.setText("View Cover Photo");

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage("cover");
                        Toast.makeText(userProfileActivity.this, "Cover Photo", Toast.LENGTH_SHORT).show();
                    }
                });

                bottomSheetDialog.show();

            }
        });


        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleProfile.setText("Profile Photo");
                viewProfile.setText("View Profile Photo");

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage("profile");
                        Toast.makeText(userProfileActivity.this, "profile Photo", Toast.LENGTH_SHORT).show();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfileActivity.this, FriendsActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        if (!senderUserID.equals(userid)) {
            request.setVisibility(View.VISIBLE);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SendChatRequest();
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CancelChatRequest();
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AcceptChatRequest();
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CancelChatRequest();
                }
            });
            accepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RemoveSpecificContact();
                }
            });
        } else {
            request.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            accepted.setVisibility(View.GONE);


        }

        ManageChatRequests();

        getUserInfo();

        whoViewedYourProfile();


    }

    private void whoViewedYourProfile() {
        DatabaseReference ViewedRef = FirebaseDatabase.getInstance().getReference("ViewedProfile");
        if (!userid.equals(CurrentUserId)) {
            int count = 1;
            ViewedRef.child(userid).child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String VT = snapshot.child("Count").getValue().toString();
                        int ViewedTimes = Integer.parseInt(VT);
                        int c = ViewedTimes + count;
                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("Count", String.valueOf(c));
                        haspmap.put("ViewedById", CurrentUserId);
                        ViewedRef.child(userid).child(CurrentUserId).setValue(haspmap);
                    } else {
                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("Count", String.valueOf(count));
                        haspmap.put("ViewedById", CurrentUserId);
                        ViewedRef.child(userid).child(CurrentUserId).setValue(haspmap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


    private void openImage(String cover) {
        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (cover.equals("cover")) {
                    //                for cover pic
                    if (snapshot.child("cover_pic").exists()) {
                        String user_cover = snapshot.child("cover_pic").getValue().toString();
                        Picasso.get().load(user_cover).into(dialogImage);
                        mDialog.show();
                    }
                }
                if (cover.equals("profile")) {
                    if (snapshot.child("profile_image").exists()) {
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(dialogImage);
                        mDialog.show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {

        DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
        infoREf.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profession = snapshot.child("profession").getValue().toString();

                if (profession.equals("Schooling")) {
                    String school_name = snapshot.child("school_name").getValue().toString();
                    userinfo.setText(profession + " at " + school_name);
                    getUserName(userid);

                }
                if (profession.equals("Graduation")) {
                    String college_name = snapshot.child("college_name").getValue().toString();
                    String course = snapshot.child("course").getValue().toString();

                    userinfo.setText(course + " at " + college_name);
                    getUserName(userid);
                }
                if (profession.equals("Job")) {
                    String job_role = snapshot.child("job_role").getValue().toString();
                    userinfo.setText(job_role);
                    getUserName(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void getUserName(String userId) {

        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name = snapshot.child("userName").getValue().toString();
                userName.setText(user_name);
//                for profile pic
                if (snapshot.child("profile_image").exists()) {
                    String user_Profile = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(user_Profile).into(userProfile);
                }
//                else {
//                    Picasso.get().load(R.drawable.profile).into(userProfile);
//                }

//                for cover pic
                if (snapshot.child("cover_pic").exists()) {
                    String user_cover = snapshot.child("cover_pic").getValue().toString();

                    Picasso.get().load(user_cover).into(coverPic);
                }
//                else {
//                    Picasso.get().load(R.drawable.profile).into(coverPic);
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequests() {
        ChatRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserID)) {
                    String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();

                    if (request_type.equals("sent")) {
                        currentState = "request_sent";

                        request.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                        accept.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        accepted.setVisibility(View.GONE);


                    } else if (request_type.equals("received")) {
                        currentState = "request_received";

                        request.setVisibility(View.GONE);
                        send.setVisibility(View.GONE);
                        accept.setVisibility(View.VISIBLE);
                        reject.setVisibility(View.VISIBLE);
                        accepted.setVisibility(View.GONE);

                    } else if (request_type.equals("cancel")) {
                        currentState = "new";

                        request.setVisibility(View.VISIBLE);
                        send.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        accepted.setVisibility(View.GONE);
                    }
                } else {

                    FriendsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserID)) {
                                String f = snapshot.child(receiverUserID).child("Friends").getValue().toString();
                                if (f.equals("Saved")) {
                                    currentState = "friends";

                                    request.setVisibility(View.GONE);
                                    send.setVisibility(View.GONE);
                                    accept.setVisibility(View.GONE);
                                    reject.setVisibility(View.GONE);
                                    accepted.setVisibility(View.VISIBLE);

                                } else if (f.equals("UnFriend")) {
                                    currentState = "new";

                                    request.setVisibility(View.VISIBLE);
                                    send.setVisibility(View.GONE);
                                    accept.setVisibility(View.GONE);
                                    reject.setVisibility(View.GONE);
                                    accepted.setVisibility(View.GONE);
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

    private void SendChatRequest() {
        currentState = "new";
        ChatRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    ChatRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                currentState = "request_sent";
                                ManageChatRequests();

                            }
                        }
                    });
                }

            }
        });

        addToHisNotification(receiverUserID , "" , "Received" , senderUserID , "RequestReceived");
    }
    private void addToHisNotification(final String hisUid, String pId, String message, String currentUserId , String type){
        String timestamp = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId",pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", currentUserId);
        hashMap.put("status", "0");
        hashMap.put("type", type);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void CancelChatRequest() {

        ChatRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    ChatRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                currentState = "new";
                                ManageChatRequests();

                                ChatRef.child(senderUserID).child(receiverUserID).child("request_type").removeValue();
                                ChatRef.child(receiverUserID).child(senderUserID).child("request_type").removeValue();
                            }
                        }
                    });
                }

            }
        });

        cancelNotification(receiverUserID , "" , "Received" , senderUserID , "RequestReceived");
        cancelNotification(senderUserID, "", "Received", receiverUserID, "RequestReceived");


    }

    private void cancelNotification(final String hisUid, String pId, String message, String currentUserId , String type) {

        String timestamp = ""+System.currentTimeMillis();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").orderByChild("sUid").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        if (ds.child("type").getValue().toString().equals(type))
                        {
                            ds.getRef().removeValue();
                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void AcceptChatRequest() {
        FriendsRef.child(senderUserID).child(receiverUserID).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(receiverUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ManageChatRequests();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(receiverUserID).child(senderUserID).child("Friends").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(receiverUserID).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ManageChatRequests();

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ChatRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "friends";
                                                    ManageChatRequests();

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

        addToHisNotification(receiverUserID , "" , "Your Request is Accepted" , senderUserID , "RequestAccepted");
        cancelNotification(senderUserID , "" , "Received" , receiverUserID , "RequestReceived");
    }

    private void RemoveSpecificContact() {
        FriendsRef.child(senderUserID).child(receiverUserID).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String name = snapshot.child("userName").getValue().toString();
                                FriendsRef.child(senderUserID).child(receiverUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        currentState = "new";
                                        ManageChatRequests();


                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FriendsRef.child(receiverUserID).child(senderUserID).child("Friends").setValue("UnFriend").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UsersRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            String name = snapshot.child("userName").getValue().toString();
                                            FriendsRef.child(receiverUserID).child(senderUserID).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests();


                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ChatRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ChatRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    currentState = "new";
                                                    ManageChatRequests();


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

        cancelNotification(senderUserID , "" , "Received" , receiverUserID , "RequestAccepted");
        cancelNotification(receiverUserID , "" , "Received" , senderUserID , "RequestAccepted");

    }

    private void status(String status) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        HashMap map = new HashMap();
        map.put("status", status);

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