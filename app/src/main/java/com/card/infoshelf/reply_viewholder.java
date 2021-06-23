package com.card.infoshelf;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class reply_viewholder extends RecyclerView.ViewHolder {

    CircleImageView userProfile;
    TextView userName, user_c, comment_time;

    public reply_viewholder(@NonNull View itemView) {
        super(itemView);

        userProfile = itemView.findViewById(R.id.userProfile);
        userName = itemView.findViewById(R.id.userName);
        user_c = itemView.findViewById(R.id.user_c);
        comment_time = itemView.findViewById(R.id.comment_time);
    }
}
