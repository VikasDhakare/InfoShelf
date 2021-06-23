package com.card.infoshelf.AllFriends;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.Messaging.MessagingActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.networkModel;
import com.card.infoshelf.userProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllFriendsAdaptor extends RecyclerView.Adapter<AllFriendsAdaptor.AllFriendsHolder> {

    private Context context;
    private ArrayList<networkModel> list;
    private DatabaseReference Ref;
    private String CurrentUserId;

    public AllFriendsAdaptor(Context context, ArrayList<networkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AllFriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alluserlayout,parent,false);

        return new AllFriendsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllFriendsHolder holder, int position) {

        networkModel user = list.get(position);
        String userId = user.getUserId();
        String prof = user.getProfession();

        Ref = FirebaseDatabase.getInstance().getReference();
        CurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.getUserInfo(prof, userId , user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref.child("Friends").child(CurrentUserId).child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String state = snapshot.child("Friends").getValue().toString();
                            Intent intent = new Intent(context , MessagingActivity.class);
                            intent.putExtra("userid", userId);
                            intent.putExtra("name", user.getUserName());
                            intent.putExtra("profile_image", user.getProfile_image());
                            intent.putExtra("state", state);
                            context.startActivity(intent);









                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AllFriendsHolder extends RecyclerView.ViewHolder {

        private TextView username, userInfo;
        private CircleImageView userProfile;


        public AllFriendsHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            userInfo = itemView.findViewById(R.id.userInfo);
            userProfile = itemView.findViewById(R.id.userProfile);

        }

        public void getUserInfo(String prof, String userId , networkModel user) {

            DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
            infoREf.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profession = snapshot.child("profession").getValue().toString();

                    if (profession.equals("Schooling")) {
                        String school_name = snapshot.child("school_name").getValue().toString();
                        userInfo.setText(profession + " at " + school_name);
                        getUserName(userId , user);

                    }
                    if (profession.equals("Graduation")) {
                        String college_name = snapshot.child("college_name").getValue().toString();
                        String course = snapshot.child("course").getValue().toString();

                        userInfo.setText(course + " at " + college_name);
                        getUserName(userId , user);
                    }
                    if (profession.equals("Job")) {
                        String job_role = snapshot.child("job_role").getValue().toString();
                        userInfo.setText(job_role);
                        getUserName(userId , user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        public void getUserName(String userId , networkModel user) {

            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
            userNameRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String user_name = snapshot.child("userName").getValue().toString();
                    user.setUserName(user_name);
                    username.setText(user.getUserName());

                    //                for profile pic
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        user.setProfile_image(user_Profile);
                        Picasso.get().load(user.getProfile_image()).placeholder(R.drawable.def_user).into(userProfile);
                    }
                    else {
                        Picasso.get().load(R.drawable.def_user).placeholder(R.drawable.def_user).into(userProfile);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void filterList(ArrayList<networkModel> filterList)
    {
        list = filterList;
        notifyDataSetChanged();
    }


}
