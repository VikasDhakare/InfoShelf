package com.card.infoshelf;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class storyAdaptor extends RecyclerView.Adapter<storyAdaptor.Viewholder>{

    private Context mcontext;
    private List<storyModel> mstory;

    public storyAdaptor(Context mcontext, List<storyModel> mstory) {
        this.mcontext = mcontext;
        this.mstory = mstory;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == 0){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.add_story_item, parent, false);
            return new storyAdaptor.Viewholder(view);
        }
        else {
            Toast.makeText(mcontext, "Enter", Toast.LENGTH_SHORT).show();
            View view = LayoutInflater.from(mcontext).inflate(R.layout.story_item, parent, false);
            return new storyAdaptor.Viewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {

        storyModel mStry = mstory.get(position);

        userInfo(holder, mStry.getUserid(), position);

        if (holder.getAdapterPosition() != 0){
            seenStory(holder, mStry.getUserid());
        }

        if (holder.getAdapterPosition() == 0){
            myStory(holder.add_story_text, holder.story_plus, false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0){
                    myStory(holder.add_story_text, holder.story_plus, true);
                }
                else {

                    Intent intent = new Intent(mcontext, StoryActivity.class);
                    intent.putExtra("userid", mStry.getUserid());
                    mcontext.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mstory.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public CircleImageView story_photo,story_plus,story_photo_seen;
        public TextView story_username, add_story_text;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_pic);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_pic_seen);
            story_username = itemView.findViewById(R.id.story_username);
            add_story_text = itemView.findViewById(R.id.add_story_text);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            return 0;
        }
        return 1;
    }

    private void userInfo(final Viewholder viewholder, String userId, final int pos){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String user_name = snapshot.child("userName").getValue().toString();

                //                for profile pic
                if (snapshot.child("profile_image").exists()){
                    String user_Profile = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(user_Profile).into(viewholder.story_photo);

                    if (pos != 0){
                        Picasso.get().load(user_Profile).into(viewholder.story_photo_seen);
                        viewholder.story_username.setText(user_name);
                    }

                }


//                getUsers user = snapshot.getValue(getUsers.class);
//                Glide.with(mcontext).load(user.getUser_image()).into(viewholder.story_photo);
//                if (pos != 0){
//                    Glide.with(mcontext).load(user.getUser_image()).into(viewholder.story_photo_seen);
//                    viewholder.story_username.setText(user.getName());
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timeCurent = System.currentTimeMillis();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    storyModel mStory = snapshot1.getValue(storyModel.class);
                    if (timeCurent > mStory.getTimestart() && timeCurent < mStory.getTimeend()){
                        count++;
                    }
                }
                if (click){
                    if (count > 0){
                        Toast.makeText(mcontext, ""+count, Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(mcontext, StoryActivity.class);
                                intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                mcontext.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mcontext, AddStoryActivity.class);
                                mcontext.startActivity(intent);
                                dialog.dismiss();

                            }
                        });
                        alertDialog.show();
                    }
                    else {
                        Intent intent = new Intent(mcontext, AddStoryActivity.class);
                        mcontext.startActivity(intent);
                    }
                }
                else {
                    if (count > 0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }
                    else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(final Viewholder viewholder, String userId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Story")
                .child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (!ds.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists() && System.currentTimeMillis() < ds.getValue(storyModel.class).getTimeend()){
                        i++;
                    }
                }
                if (i > 0){
                    viewholder.story_photo.setVisibility(View.VISIBLE);
                    viewholder.story_photo_seen.setVisibility(View.GONE);
                }
                else {
                    viewholder.story_photo.setVisibility(View.GONE);
                    viewholder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
