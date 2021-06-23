package com.card.infoshelf.bottomfragment;

import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.card.infoshelf.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class timeline_adaptor extends RecyclerView.ViewHolder {

    CircleImageView user_profile;
    TextView userName, post_time, post_desc, fileName, like_count, comment_count, viewDetails, post_views;
    ImageView bookmark_btn, post_image, download_btn, like_btn, comment_btn, share_btn, more_btn, doc_img;
    PlayerView video_view;
    CardView documentView;
    SimpleExoPlayer exoPlayer;
    boolean mPrecessLikes = false;

    public timeline_adaptor(@NonNull View itemView) {
        super(itemView);

        user_profile = itemView.findViewById(R.id.user_profile);
        userName = itemView.findViewById(R.id.userName);
        post_time = itemView.findViewById(R.id.post_time);
        post_desc = itemView.findViewById(R.id.post_desc);
        fileName = itemView.findViewById(R.id.fileName);
        like_count = itemView.findViewById(R.id.like_count);
        comment_count = itemView.findViewById(R.id.comment_count);
        viewDetails = itemView.findViewById(R.id.viewDetails);
        bookmark_btn = itemView.findViewById(R.id.bookmark_btn);
        post_image = itemView.findViewById(R.id.post_image);
        download_btn = itemView.findViewById(R.id.download_btn);
        like_btn = itemView.findViewById(R.id.like_btn);
        comment_btn = itemView.findViewById(R.id.comment_btn);
        share_btn = itemView.findViewById(R.id.share_btn);
        more_btn = itemView.findViewById(R.id.more_btn);
        video_view = itemView.findViewById(R.id.video_view);
        documentView = itemView.findViewById(R.id.documentView);
        doc_img = itemView.findViewById(R.id.doc_img);
        post_views = itemView.findViewById(R.id.post_views);
    }

    public String getFormateDate(FragmentActivity activity, String postTime) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(Long.parseLong(postTime));

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

    public void getUserInfo(String userId, timeLine_model model) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("profile_image").exists()){
                    String pUrl = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(pUrl).into(user_profile);
                }

                String uName = snapshot.child("userName").getValue().toString();
                userName.setText(uName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setVideo(String postUrl, FragmentActivity activity) {

        try {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
            Uri videouri = Uri.parse(postUrl);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

            video_view.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);

        } catch (Exception e) {
            // below line is used for handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }
    }

    public void setLikes(String pId, String currentUserId) {


        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pId).hasChild(currentUserId)){

                    int  likeCount = (int) snapshot.child(pId).getChildrenCount();
                    like_count.setText(likeCount+" ");
                    like_btn.setImageResource(R.drawable.favorite);

                }
                else {
                    int  likeCount = (int) snapshot.child(pId).getChildrenCount();
                    like_count.setText(likeCount+" ");
                    like_btn.setImageResource(R.drawable.fav);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setComment(String pId, String currentUserId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
        ref.child(pId).child("Comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                comment_count.setText(""+count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getActionLikeBtn(String pId, String currentUserId, timeLine_model model) {

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrecessLikes = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (mPrecessLikes == true){

                            if (snapshot.child(pId).hasChild(currentUserId)){
                                likeRef.child(pId).child(currentUserId).removeValue();
                                mPrecessLikes = false;
                            }
                            else {
                                likeRef.child(pId).child(currentUserId).child("uId").setValue(currentUserId);
                                mPrecessLikes = false;

                                if (!model.getUserId().equals(currentUserId)){

                                    addToHisNotification(model.getUserId(), pId, "Liked Your Post", currentUserId);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void addToHisNotification(final String hisUid, String pId, String message, String currentUserId){
        String timestamp = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();

        hashMap.put("pId",pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", currentUserId);
        hashMap.put("status", "0");


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
}
