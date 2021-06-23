package com.card.infoshelf.bottomfragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.card.infoshelf.userProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class network_adaptor extends RecyclerView.Adapter<network_adaptor.MyviewHolder>{

    private Context context;
    private ArrayList<networkModel> list;
    private DatabaseReference UsersRef , ChatRef , FriendsRef , PostRef;
    private FirebaseAuth mAuth;
    private String currentState , senderUserID  , receiverUserID;

    public network_adaptor(Context context, ArrayList<networkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public network_adaptor.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.network_layout,parent,false);

        return new network_adaptor.MyviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull network_adaptor.MyviewHolder holder, int position) {
        networkModel user = list.get(position);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = user.getUserId();
        currentState = "new";

        String userId = user.getUserId();
        String prof  = user.getProfession();



        holder.getUserInfo(prof, userId);

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


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder{

        private ImageView coverProfile;
        private TextView username, userInfo;
        private CircleImageView userProfile;
        private TextView  request , accept , reject , send  , accepted;


        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            coverProfile = itemView.findViewById(R.id.coverProfile);
            username = itemView.findViewById(R.id.username);
            userInfo = itemView.findViewById(R.id.userInfo);
            userProfile = itemView.findViewById(R.id.userProfile);
            request = itemView.findViewById(R.id.request);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            send = itemView.findViewById(R.id.send);
            accepted = itemView.findViewById(R.id.accepted);
        }

        public void getUserInfo(String prof, String userId) {

            DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
            infoREf.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profession = snapshot.child("profession").getValue().toString();

                    if (profession.equals("Schooling")){
                        String school_name = snapshot.child("school_name").getValue().toString();
                        userInfo.setText(profession+" at "+ school_name);
                        getUserName(userId);

                    }
                    if (profession.equals("Graduation")){
                        String college_name = snapshot.child("college_name").getValue().toString();
                        String course = snapshot.child("course").getValue().toString();

                        userInfo.setText(course+" at "+ college_name);
                        getUserName(userId);
                    }
                    if (profession.equals("Job")){
                        String job_role = snapshot.child("job_role").getValue().toString();
                        userInfo.setText(job_role);
                        getUserName(userId);
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
                    username.setText(user_name);

                    //                for profile pic
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(userProfile);
                    }
//                    else {
//                        Picasso.get().load(R.drawable.profile).into(userProfile);
//                    }

//                for cover pic
                    if (snapshot.child("cover_pic").exists()){
                        String user_cover = snapshot.child("cover_pic").getValue().toString();

                        Picasso.get().load(user_cover).into(coverProfile);
                    }
//                    else {
//                        Picasso.get().load(R.drawable.profile).into(coverProfile);
//                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void ManageChatRequests(MyviewHolder holder, String userId)
    {
        ChatRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserID)) {
                    String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();

                    if (request_type.equals("sent")) {
                        currentState = "request_sent";

                        holder.request.setVisibility(View.GONE);
                        holder.send.setVisibility(View.VISIBLE);
                        holder. accept.setVisibility(View.GONE);
                        holder. reject.setVisibility(View.GONE);
                        holder.accepted.setVisibility(View.GONE);


                    } else if (request_type.equals("received")) {
                        currentState = "request_received";

                        holder.request.setVisibility(View.GONE);
                        holder.send.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.accepted.setVisibility(View.GONE);

                    } else if (request_type.equals("cancel")) {
                        currentState = "new";

                        holder. request.setVisibility(View.VISIBLE);
                        holder.send.setVisibility(View.GONE);
                        holder.accept.setVisibility(View.GONE);
                        holder.reject.setVisibility(View.GONE);
                        holder.accepted.setVisibility(View.GONE);
                    }
                } else {

                    FriendsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserID)) {
                                String f = snapshot.child(receiverUserID).child("Friends").getValue().toString();
                                if (f.equals("Saved")) {
                                    currentState = "friends";

                                    holder.request.setVisibility(View.GONE);
                                    holder.send.setVisibility(View.GONE);
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.accepted.setVisibility(View.VISIBLE);

                                } else if (f.equals("UnFriend")) {
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
    private void SendChatRequest(network_adaptor.MyviewHolder holder, String id)
    {
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
                                ManageChatRequests(holder  , id);

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
    private void CancelChatRequest(network_adaptor.MyviewHolder holder, String id, int position)
    {
        ChatRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    ChatRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                currentState = "new";
                                ManageChatRequests(holder  , id);

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

    private void AcceptChatRequest(network_adaptor.MyviewHolder holder  , String id)
    {
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
                                        ManageChatRequests(holder  , id);
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
                                                    ManageChatRequests(holder  , id);

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
                                                    ManageChatRequests(holder  , id);

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
    private void RemoveSpecificContact(network_adaptor.MyviewHolder holder  , String id)
    {
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
                                        ManageChatRequests(holder  , id);


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
                                                    ManageChatRequests(holder  , id);


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
                                                    ManageChatRequests(holder  , id);


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



}
