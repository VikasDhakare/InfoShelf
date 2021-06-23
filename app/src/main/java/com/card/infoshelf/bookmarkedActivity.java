package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.bottomfragment.TagModel;
import com.card.infoshelf.bottomfragment.TimelineFragment;
import com.card.infoshelf.bottomfragment.filterPostAdapter;
import com.card.infoshelf.bottomfragment.timeLine_model;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.googlecode.mp4parser.authoring.Edit;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class bookmarkedActivity extends AppCompatActivity {

    private RecyclerView bookmarked_recycler, recyclerView;
    private filterPostAdapter postFilterAdapter;
    private shareBottomSheetAdapter shareAdapter;
    private ArrayList<timeLine_model> filterPostList;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private DatabaseReference postRef, RootRef, shareRef;
    private BottomSheetDialog moreBottomSheet, sharebottomsheet;
    private LinearLayout edit_l, delete_l;
    private TextView copyUrl, share, bookmark, delete, edit, done;
    private EditText userinput;
    List<TagModel> contacts = new ArrayList<>();
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked);

        bookmarked_recycler = findViewById(R.id.bookmarked_recycler);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
        RootRef = FirebaseDatabase.getInstance().getReference();
        shareRef = FirebaseDatabase.getInstance().getReference();

        bookmarked_recycler.setLayoutManager(new LinearLayoutManager(this));
        filterPostList = new ArrayList<>();
        postFilterAdapter = new filterPostAdapter(this, filterPostList);
        bookmarked_recycler.setAdapter(postFilterAdapter);

        // bottom sheet
        moreBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        sharebottomsheet = new BottomSheetDialog(this, R.style.BottomSheetStyle);

        View v = LayoutInflater.from(this).inflate(R.layout.more_bottom_sheet, (LinearLayout) findViewById(R.id.sheet2));
        View viewShare = LayoutInflater.from(this).inflate(R.layout.share_bottom_sheet, findViewById(R.id.sharebottomlayout));

        moreBottomSheet.setContentView(v);
        sharebottomsheet.setContentView(viewShare);

        // more bottom item
        edit_l = moreBottomSheet.findViewById(R.id.edit_l);
        delete_l = moreBottomSheet.findViewById(R.id.delete_l);
        copyUrl = moreBottomSheet.findViewById(R.id.copyUrl);
        share = moreBottomSheet.findViewById(R.id.share);
        bookmark = moreBottomSheet.findViewById(R.id.bookmark);
        delete = moreBottomSheet.findViewById(R.id.delete);
        edit = moreBottomSheet.findViewById(R.id.edit);

        //share sheet irtem
        recyclerView = sharebottomsheet.findViewById(R.id.shareRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        shareAdapter = new shareBottomSheetAdapter(contacts);
        recyclerView.setAdapter(shareAdapter);

        userinput = sharebottomsheet.findViewById(R.id.TypeChipsTag);
        done = sharebottomsheet.findViewById(R.id.DoneTag);

        getBookmarkedPosts();
        sharebottomsheetDataWork();
    }

    private void sharebottomsheetDataWork() {
        shareRef.child("Friends").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        String id = npsnapshot.getKey();
//                        Ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
                        shareRef.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                TagModel l = snapshot.getValue(TagModel.class);
                                contacts.add(l);
                                shareAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getBookmarkedPosts() {
        postRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    filterPostList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String key = ""+ds.getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles");
                        ref.orderByChild("timeStamp").equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds1 : dataSnapshot.getChildren()){
                                    timeLine_model user = ds1.getValue(timeLine_model.class);
                                    filterPostList.add(user);
                                }
                                postFilterAdapter.notifyDataSetChanged();
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

    public class filterPostAdapter extends RecyclerView.Adapter<filterPostAdapter.Myviewholder>{

        private Context context;
        private ArrayList<timeLine_model> list;
        private boolean bookmarkProcess = false;

        public filterPostAdapter(Context context, ArrayList<timeLine_model> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public filterPostAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
            return new Myviewholder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull filterPostAdapter.Myviewholder holder, int position) {

            timeLine_model model = list.get(position);

            String postTime = model.getTimeStamp();
            String pId = model.getTimeStamp();

            String showPostTime = holder.getFormateDate(context, postTime);
            String postUrl = model.getPostURL();
            String postDesc = model.getTextBoxData();
            String type = model.getFileType();
            String userId = model.getUserId();

            holder.getUserInfo(userId, model);
            holder.getPostViews(pId, holder);

            Toast.makeText(context, ""+model.getTimeStamp(), Toast.LENGTH_SHORT).show();
            Log.d("postId", model.getTimeStamp());

            checkBookmarkPost(holder, pId);


            holder.bookmark_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookmarkProcess = true;

                    DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
                    bookmarkREf.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (bookmarkProcess == true){
                                if (snapshot.child(CurrentUserId).hasChild(pId)){
                                    bookmarkREf.child(CurrentUserId).child(pId).removeValue();
                                    bookmarkProcess = false;
                                }
                                else {
                                    bookmarkREf.child(CurrentUserId).child(pId).child("postId").setValue(pId);
                                    bookmarkProcess = false;

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

            holder.more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (userId.equals(CurrentUserId)){
                        delete_l.setVisibility(View.VISIBLE);
                        edit_l.setVisibility(View.VISIBLE);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletePost(pId, postUrl);
                            }
                        });

                        moreBottomSheet.show();
                    }
                    else {
                        delete_l.setVisibility(View.GONE);
                        edit_l.setVisibility(View.GONE);
                        moreBottomSheet.show();
                    }


                }
            });

            holder.share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postId = model.getTimeStamp();
//                        loadShareBottomSheetdata(pId);
                    sharebottomsheet.show();
                }
            });

            if (type.equals("image")){
                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                Picasso.get().load(postUrl).into(holder.post_image);
                holder.video_view.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.GONE);

            }
            else if (type.equals("video")){
                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.GONE);

                // set video

                holder.setVideo(postUrl, context);

            }
            else if (type.equals("pdf")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.fileName.setText(filename);
                holder.doc_img.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        context.startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("ppt") || type.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||type.equals("vnd.ms-powerpoint")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.fileName.setText(filename);
                holder.doc_img.setImageResource(R.drawable.ppt);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        context.startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| type.equals("xlsx") || type.equals("application/vnd.ms-excel")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.fileName.setText(filename);
                holder.doc_img.setImageResource(R.drawable.excel);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        context.startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("application/msword") || type.equals("doc") || type.equals("docx") || type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.fileName.setText(filename);
                holder.doc_img.setImageResource(R.drawable.word);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        context.startActivity(browserIntent);
                    }
                });
            }
            else if (type.equals("none")){

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.documentView.setVisibility(View.GONE);
            }
            else {
                String filename = model.getFileName();

                holder.post_time.setText(showPostTime);
                holder.post_desc.setText(postDesc);
                holder.video_view.setVisibility(View.GONE);
                holder.post_image.setVisibility(View.GONE);
                holder.fileName.setText(filename);
                holder.doc_img.setImageResource(R.drawable.zip);

                // set document

                holder.download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                        context.startActivity(browserIntent);
                    }
                });
            }

            holder.setLikes(pId, CurrentUserId);
            holder.setComment(pId, CurrentUserId);
            holder.getActionLikeBtn(pId, CurrentUserId, model);

            holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, postDetailsActivity.class);
                    intent.putExtra("pId", pId);
                    context.startActivity(intent);
                }
            });

            holder.viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, postDetailsActivity.class);
                    intent.putExtra("pId", pId);
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder {

            CircleImageView user_profile;
            TextView userName, post_time, post_desc, fileName, like_count, comment_count, viewDetails, copyUrl, share, bookmark, edit, delete, post_views;
            ImageView bookmark_btn, post_image, download_btn, like_btn, comment_btn, share_btn, more_btn, doc_img;
            PlayerView video_view;
            CardView documentView;
            SimpleExoPlayer exoPlayer;
            boolean mPrecessLikes = false;
            private BottomSheetDialog moreBottomSheet;
            private LinearLayout delete_l, edit_l;

            public Myviewholder(@NonNull View itemView) {
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

            public String getFormateDate(Context context, String postTime) {
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

//                                            addToHisNotification(model.getUserId(), pId, "Liked Your Post", currentUserId);
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

            public void setVideo(String postUrl, Context context) {

                try {

                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
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

            public void getPostViews(String pId, Myviewholder holder) {
                DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
                postviewsRef.child(pId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            int countViews = (int) snapshot.getChildrenCount();
                            holder.post_views.setText(""+countViews);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void deletePost(String pId, String postUrl) {
        if (postUrl.equals("none")){
            deleteWithoutImage(pId);
        }
        else {
            deleteWithImage(pId, postUrl);
        }
    }

    private void deleteWithImage(String pId, String postUrl) {

        final ProgressDialog pd = new ProgressDialog(bookmarkedActivity.this);
        pd.setMessage("Deleting...");
        pd.show();
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(postUrl);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query query = FirebaseDatabase.getInstance().getReference("POSTFiles").orderByChild("timeStamp").equalTo(pId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(bookmarkedActivity.this, "Deleted succefully..", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteWithoutImage(String pId) {

        final ProgressDialog pd = new ProgressDialog(bookmarkedActivity.this);
        pd.setMessage("Deleting...");
        pd.show();
        Query query = FirebaseDatabase.getInstance().getReference("POSTFiles").orderByChild("timeStamp").equalTo(pId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(bookmarkedActivity.this, "Deleted succefully..", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkBookmarkPost(filterPostAdapter.Myviewholder holder, String pId) {
        DatabaseReference bookmarkREf = FirebaseDatabase.getInstance().getReference("Bookmark_Post");
        bookmarkREf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(CurrentUserId).hasChild(pId)){
                    holder.bookmark_btn.setImageResource(R.drawable.bookmark_fill);
                }
                else {
                    holder.bookmark_btn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // share post adapter

    public class shareBottomSheetAdapter extends RecyclerView.Adapter<shareBottomSheetAdapter.MyviewHolder>{

        private List<TagModel> tagPersonList;

        public shareBottomSheetAdapter(List<TagModel> tagPersonList) {
            this.tagPersonList = tagPersonList;
        }

        @NonNull
        @Override
        public shareBottomSheetAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_view,parent,false);
            return new MyviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull shareBottomSheetAdapter.MyviewHolder holder, int position) {

            TagModel tagModel = tagPersonList.get(position);
            holder.PersonName.setText(tagModel.getUserName());
            String imageUri = tagModel.getProfile_image();
            Picasso.get().load(imageUri).into(holder.personimg);

            holder.sharepersonBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePost(tagModel.getUserId(), holder.sharepersonBtn);
                }
            });
        }

        @Override
        public int getItemCount() {
            return tagPersonList.size();
        }

        public class MyviewHolder extends RecyclerView.ViewHolder {


            TextView PersonName;
            LinearLayout rootView;
            TextView sharepersonBtn;
            ImageView personimg;

            public MyviewHolder(@NonNull View itemView) {
                super(itemView);

                PersonName = itemView.findViewById(R.id.personnameTag);
                rootView = itemView.findViewById(R.id.rootView);
                sharepersonBtn = itemView.findViewById(R.id.sharepersonBtn);
                personimg = itemView.findViewById(R.id.PersonImage);
            }
        }
    }

    private void sharePost(String to, TextView sharepersonBtn) {

        String CurrentTime, CurrentDate;
        String messageSenderRef = "Messages/" + CurrentUserId + "/" + to;
        String messageReceiverRef = "Messages/" + to + "/" + CurrentUserId;

        Calendar date = Calendar.getInstance();
        SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
        CurrentDate = CurrentDateFormat.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
        CurrentTime = CurrentTimeFormat.format(time.getTime());

        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(CurrentUserId).child(to).push();


        String messagePushID = userMessageKeyRef.getKey();


        Map messageTextBody = new HashMap();
        messageTextBody.put("message", postId);
        messageTextBody.put("type", "post");
        messageTextBody.put("from", CurrentUserId);
        messageTextBody.put("to", to);
        messageTextBody.put("messageID", messagePushID);
        messageTextBody.put("timestamp", timestamp);
        messageTextBody.put("time", CurrentTime);
        messageTextBody.put("date", CurrentDate);
        messageTextBody.put("isSeen", "0");

        Map messageTextBody1 = new HashMap();
        messageTextBody1.put("message", postId);
        messageTextBody1.put("type", "post");
        messageTextBody1.put("from", CurrentUserId);
        messageTextBody1.put("to", to);
        messageTextBody1.put("messageID", messagePushID);
        messageTextBody1.put("timestamp", timestamp);
        messageTextBody1.put("time", CurrentTime);
        messageTextBody1.put("date", CurrentDate);
        messageTextBody1.put("isSeen", "1");

        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody1);


        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    sharepersonBtn.setText("Cancel");
                    UpdateChatList(timestamp, CurrentUserId, to);

                } else {

                    Toast.makeText(bookmarkedActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void UpdateChatList(String timestamp, String sender, String receiver) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatList");
        HashMap map = new HashMap();
        map.put("userId", receiver);
        map.put("time", timestamp);
        ref.child(sender).child(receiver).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap map1 = new HashMap();
                map1.put("userId", sender);
                map1.put("time", timestamp);

                ref.child(receiver).child(sender).setValue(map1);
            }
        });
    }
}