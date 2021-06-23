package com.card.infoshelf.Messenger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.card.infoshelf.Messaging.MessageModel;
import com.card.infoshelf.Messaging.MessagingActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.networkModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengerAdaptor extends RecyclerView.Adapter<MessengerAdaptor.ChatListHolder> {

    private Context context;
    private ArrayList<networkModel> list;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private String l_msg, l_time;


    public MessengerAdaptor(Context context, ArrayList<networkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new ChatListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, int position) {

        networkModel user = list.get(position);
        String userId = user.getUserId();
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        holder.getUserName(userId, user);
//        holder.getLastMessage(userId , user);

        getLastMessage(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference  ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Friends").child(CurrentUserId).child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String state = snapshot.child("Friends").getValue().toString();
                        Intent intent = new Intent(context, MessagingActivity.class);
                        intent.putExtra("userid", userId);
                        intent.putExtra("name", user.getUserName());
                        intent.putExtra("profile_image", user.getProfile_image());
                        intent.putExtra("state", state);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        CountUnReadMessages(position, user, userId, holder, CurrentUserId);

    }

    private void CountUnReadMessages(int position, networkModel user, String userId, ChatListHolder holder, String currentUserId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Messages").child(userId).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    ref.child("Messages").child(userId).child(CurrentUserId).orderByChild("to").equalTo(CurrentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ref.child("Messages").child(userId).child(currentUserId).orderByChild("isSeen").equalTo("0").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long count = snapshot.getChildrenCount();

                                        if (count == 0) {

                                            holder.tv_count.setVisibility(View.GONE);
                                        } else {

                                            holder.tv_count.setVisibility(View.VISIBLE);
                                            holder.tv_count.setText(count + "");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else {
                                holder.tv_count.setVisibility(View.GONE);
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

    private void getLastMessage(ChatListHolder holder, int position) {

        networkModel model = list.get(position);
        String userId = model.getUserId();

        final String[] l_m = new String[1];
        final String[] tm = new String[1];

        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

//        ref.child("Messages").child(CurrentUserId).child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren())
//                {
//                    MessageModel model = ds.getValue(MessageModel.class);
//                    if (model.getFrom().equals(mAuth.getUid().toString()) && model.getTo().equals(userId.toString()) || model.getFrom().equals(userId.toString()) && model.getTo().equals(mAuth.getUid().toString()))
//                    {
//                        String type = model.getType();
//                        ref.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists())
//                                {
//                                    ref.child("typing").child(CurrentUserId).child(userId).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if (snapshot.exists())
//                                            {
//                                                if ((Boolean) snapshot.child("isTyping").getValue())
//                                                {
//                                                    holder.lastMessage.setText("typing...");
//                                                    holder.lastMessage.setTextColor(Color.parseColor("#00FA9A"));
//                                                }
//                                                else
//                                                {
//                                                    if (type.equals("text"))
//                                                    {
//
//                                                        holder.lastMessage.setText(model.getMessage());
//                                                        holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                        holder.time.setText(model.getTime());
//                                                    }
//                                                    else if (type.equals("image"))
//                                                    {
//                                                        holder.lastMessage.setText(model.getName());
//                                                        holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                        holder.time.setText(model.getTime());
//                                                    }
//                                                    else if (type.equals("doc"))
//                                                    {
//                                                        holder.lastMessage.setText(model.getName());
//                                                        holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                        holder.time.setText(model.getTime());
//                                                    }else
//                                                    {
//                                                        holder.lastMessage.setText("");
//                                                        holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                        holder.time.setText("");
//                                                    }
//                                                }
//                                            }
//                                            else
//                                            {
//                                                if (type.equals("text"))
//                                                {
//                                                    holder.lastMessage.setText(model.getMessage());
//                                                    holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                    holder.time.setText(model.getTime());
//                                                }
//                                                else if (type.equals("image"))
//                                                {
//                                                    holder.lastMessage.setText(model.getName());
//                                                    holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                    holder.time.setText(model.getTime());
//                                                }
//                                                else if (type.equals("doc"))
//                                                {
//                                                    holder.lastMessage.setText(model.getName());
//                                                    holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                    holder.time.setText(model.getTime());
//                                                }else
//                                                {
//                                                    holder.lastMessage.setText("");
//                                                    holder.lastMessage.setTextColor(Color.parseColor("#000000"));
//                                                    holder.time.setText("");
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//
//
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        ref.child("typing").child(CurrentUserId).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if ((Boolean) snapshot.child("isTyping").getValue()) {
                       model.setMessage("typing...");
                       holder.lastMessage.setText(model.getMessage());
                        holder.lastMessage.setTextColor(Color.parseColor("#00FA9A"));
                    } else {
                        ref.child("Messages").child(CurrentUserId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String type = ds.child("type").getValue().toString();
                                    if (type.equals("text")) {
                                        model.setMessage(ds.child("message").getValue().toString());
                                        model.setTime(ds.child("time").getValue().toString());
                                    } else if (type.equals("image")) {
                                        model.setMessage(ds.child("name").getValue().toString());
                                        model.setTime(ds.child("time").getValue().toString());
                                    } else if (type.equals("doc")) {
                                        model.setMessage(ds.child("name").getValue().toString());
                                        model.setTime(ds.child("time").getValue().toString());

                                    }
                                    else if (type.equals("post")){
                                        model.setMessage("Shared a Post");
                                        model.setTime(ds.child("time").getValue().toString());
                                    }
                                }
                                holder.lastMessage.setText(model.getMessage());
                                holder.lastMessage.setTextColor(Color.parseColor("#000000"));
                                holder.time.setText(model.getTime());
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatListHolder extends RecyclerView.ViewHolder {

        private TextView username, lastMessage, time, tv_count;
        private CircleImageView userProfile, status;

        public ChatListHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.tv_time);
            userProfile = itemView.findViewById(R.id.userProfile);
            status = itemView.findViewById(R.id.status);
            tv_count = itemView.findViewById(R.id.tv_count);
        }

        public void getUserName(String userId, networkModel user) {

            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
            userNameRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String user_name = snapshot.child("userName").getValue().toString();
                        String s = snapshot.child("status").getValue().toString();
                        user.setUserName(user_name);
                        username.setText(user.getUserName());

                        if (snapshot.child("profile_image").exists()) {
                            String user_Profile = snapshot.child("profile_image").getValue().toString();
                            user.setProfile_image(user_Profile);
                            Picasso.get().load(user.getProfile_image()).placeholder(R.drawable.def_user).into(userProfile);
                        } else {
                            Picasso.get().load(R.drawable.def_user).placeholder(R.drawable.def_user).into(userProfile);
                        }
                        if (s.equals("online")) {
                            status.setVisibility(View.VISIBLE);
                        } else {
                            status.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        public void getLastMessage(String userId, networkModel user) {
            FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserId).child(userId);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        MessageModel model = ds.getValue(MessageModel.class);
                        if (model.getFrom().equals(mAuth.getUid().toString()) && model.getTo().equals(userId.toString()) || model.getFrom().equals(userId.toString()) && model.getTo().equals(mAuth.getUid().toString())) {
                            String type = model.getType();
                            if (type.equals("text")) {
                                l_msg = model.getMessage();
                                l_time = model.getTime();
                            }
                            if (type.equals("image")) {
                                l_msg = model.getName();
                                l_time = model.getTime();
                            }
                            if (type.equals("doc")) {
                                l_msg = model.getName();
                                l_time = model.getTime();
                            }

                        }
                    }
                    lastMessage.setText(l_msg);
                    time.setText(l_time);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    public void filterList(ArrayList<networkModel> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

}
