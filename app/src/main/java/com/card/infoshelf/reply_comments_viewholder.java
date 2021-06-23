package com.card.infoshelf;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class reply_comments_viewholder extends RecyclerView.ViewHolder {

    CircleImageView userProfile;
    TextView userName, user_c, comment_time;

    public reply_comments_viewholder(@NonNull View itemView) {
        super(itemView);

        userProfile = itemView.findViewById(R.id.userProfile);
        userName = itemView.findViewById(R.id.userName);
        user_c = itemView.findViewById(R.id.user_c);
        comment_time = itemView.findViewById(R.id.comment_time);

    }

    public String getFormattedDate(ReplyCommentActivity replyCommentActivity, String timestamp) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(timestamp));

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

    public void getUserInfo(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = snapshot.child("userName").getValue().toString();
                if (snapshot.child("profile_image").exists()){
                    String pUrl = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(pUrl).into(userProfile);

                }
                userName.setText(uName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
