package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class postDetailsActivity extends AppCompatActivity {

    private TextView userName, postTime, fileName, like_count, postDesc, edit, delete, share, bookmark, copyUrl, done, post_views;
    private ImageView download_btn, like_btn, share_btn, more_btn, cooment_send_neo, post_image, doc_img;
    EmojiconEditText add_commnet_et;
    private EditText userinput;
    EmojIconActions emojIconActions;
    ImageView emoji;
    View root_view;
    private String pId, UserId;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    ProgressDialog pd;
    private PlayerView video_view;
    private CardView documentView;
    SimpleExoPlayer exoPlayer;
    private boolean mPrecessLikes = false;
    private boolean ismPrecessLikes = false;
    private RecyclerView post_comment_recycler;
    private BottomSheetDialog moreBottomSheet, sharebottomsheet;
    private FirebaseRecyclerAdapter<modelComment, comment_viewholder> adaptor;
    private DatabaseReference commentREf, CommentLikeREf, RootRef, shareRef;
    private LinearLayout delete_l, edit_l, share_l, bookmark_l, copyUrl_l;
    private RecyclerView recyclerView;
    private shareBottomSheetAdapter shareAdapter;
    List<TagModel> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        pId = getIntent().getStringExtra("pId");

        userName = findViewById(R.id.userName);
        postTime = findViewById(R.id.postTime);
        fileName = findViewById(R.id.fileName);
        like_count = findViewById(R.id.like_count);
        download_btn = findViewById(R.id.download_btn);
        like_btn = findViewById(R.id.like_btn);
        share_btn = findViewById(R.id.share_btn);
        more_btn = findViewById(R.id.more_btn);
        root_view= findViewById(R.id.root_view);
        emoji = findViewById(R.id.emojicon_icon);
        add_commnet_et = findViewById(R.id.add_commnet_et);
        cooment_send_neo = findViewById(R.id.cooment_send_neo);
        postDesc = findViewById(R.id.postDesc);
        post_image = findViewById(R.id.post_image);
        video_view = findViewById(R.id.video_view);
        documentView = findViewById(R.id.documentView);
        post_comment_recycler = findViewById(R.id.post_comment_recycler);
        doc_img = findViewById(R.id.doc_img);
        post_views = findViewById(R.id.post_views);

        post_comment_recycler.setLayoutManager(new LinearLayoutManager(this));

        moreBottomSheet = new BottomSheetDialog(postDetailsActivity.this, R.style.BottomSheetStyle);
        sharebottomsheet = new BottomSheetDialog(postDetailsActivity.this, R.style.BottomSheetStyle);
        View v = LayoutInflater.from(postDetailsActivity.this).inflate(R.layout.more_bottom_sheet, (LinearLayout) findViewById(R.id.sheet2));
        View viewShare = LayoutInflater.from(postDetailsActivity.this).inflate(R.layout.share_bottom_sheet, findViewById(R.id.sharebottomlayout));
        moreBottomSheet.setContentView(v);
        sharebottomsheet.setContentView(viewShare);

        //share sheet irtem
        recyclerView = sharebottomsheet.findViewById(R.id.shareRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        shareAdapter = new shareBottomSheetAdapter(contacts);
        recyclerView.setAdapter(shareAdapter);

        userinput = sharebottomsheet.findViewById(R.id.TypeChipsTag);
        done = sharebottomsheet.findViewById(R.id.DoneTag);

        // more bottom item
        edit_l = moreBottomSheet.findViewById(R.id.edit_l);
        delete_l = moreBottomSheet.findViewById(R.id.delete_l);
        copyUrl = moreBottomSheet.findViewById(R.id.copyUrl);
        share = moreBottomSheet.findViewById(R.id.share);
        bookmark = moreBottomSheet.findViewById(R.id.bookmark);
        delete = moreBottomSheet.findViewById(R.id.delete);
        edit = moreBottomSheet.findViewById(R.id.edit);
        share_l = moreBottomSheet.findViewById(R.id.share_l);
        bookmark_l = moreBottomSheet.findViewById(R.id.bookmark_l);
        copyUrl_l = moreBottomSheet.findViewById(R.id.copyUrl_l);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");
        commentREf = FirebaseDatabase.getInstance().getReference("POSTFiles");
        CommentLikeREf = FirebaseDatabase.getInstance().getReference("Comment_Likes");
        RootRef = FirebaseDatabase.getInstance().getReference();
        shareRef = FirebaseDatabase.getInstance().getReference();

        emojIconActions = new EmojIconActions(this, root_view, add_commnet_et, emoji);
        emojIconActions.ShowEmojIcon();

        loadPostInfo();
        loadComment();
        setLikes(pId, CurrentUserId);
        sharebottomsheetDataWork();
        getPostViews();

        cooment_send_neo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrecessLikes = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (mPrecessLikes == true){

                            if (snapshot.child(pId).hasChild(CurrentUserId)){
                                likeRef.child(pId).child(CurrentUserId).removeValue();
                                mPrecessLikes = false;
                            }
                            else {
                                likeRef.child(pId).child(CurrentUserId).child("uId").setValue(CurrentUserId);
                                mPrecessLikes = false;

                                if (!UserId.equals(CurrentUserId)){

                                    addToHisNotification(UserId, pId, "Liked your post", CurrentUserId);
                                }

                                int post_like_count = (int) snapshot.child(pId).getChildrenCount();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharebottomsheet.show();
            }
        });
    }

    private void getPostViews() {
        DatabaseReference postviewsRef = FirebaseDatabase.getInstance().getReference("PostViews");
        postviewsRef.child(pId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int countViews = (int) snapshot.getChildrenCount();
                    post_views.setText(""+countViews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void postComment() {

        pd = new ProgressDialog(this);
        pd.setMessage("Adding Comment...");

        final String comment = add_commnet_et.getText().toString().trim();

        if (TextUtils.isEmpty(comment)){

            Toast.makeText(this, "Comment is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        else {

            String timestamp = String.valueOf(System.currentTimeMillis());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pId).child("Comment");

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("cId", timestamp);
            hashMap.put("timestamp", timestamp);
            hashMap.put("comment", comment);
            hashMap.put("uId", CurrentUserId);
//        hashMap.put("comment_like", "0");
            hashMap.put("post_id", pId);

            ref.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    pd.dismiss();
                    Toast.makeText(postDetailsActivity.this, "Comment Added Successfully..", Toast.LENGTH_SHORT).show();
                    add_commnet_et.setText("");
//                    UpdateCommentCount();

                    if (!UserId.equals(CurrentUserId)){

                        addToHisNotification(UserId, pId, "Commented on your post", CurrentUserId);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(postDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    boolean mProcessComment = false;
    private void UpdateCommentCount() {

        mProcessComment = true;

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment){
                    String comments = ""+ snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void loadComment() {

        FirebaseRecyclerOptions<modelComment> options = new FirebaseRecyclerOptions.Builder<modelComment>().setQuery(commentREf.child(pId).child("Comment"), modelComment.class)
                .build();
        adaptor = new FirebaseRecyclerAdapter<modelComment, comment_viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull comment_viewholder holder, int position, @NonNull modelComment model) {

                final String uid = model.getuId();
                final String postId = model.getPost_id();
                final String cid = model.getcId();
                final String comments = model.getComment();
                String timestamp = model.getTimestamp();
                String show_post_time = holder.getFormattedDate(postDetailsActivity.this , timestamp);

                holder.getUserInfo(uid);
                holder.user_c.setText(comments);
                holder.comment_time.setText(show_post_time);

//                holder.delete_c.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (UserId.equals(CurrentUserId)){
//                            delete_l.setVisibility(View.VISIBLE);
//                            edit_l.setVisibility(View.GONE);
//                            share_l.setVisibility(View.GONE);
//                            bookmark_l.setVisibility(View.GONE);
//                            copyUrl_l.setVisibility(View.GONE);
//
//                            delete.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            });
//
//                            moreBottomSheet.show();
//                        }
//                        else {
//
//                        }
//                    }
//                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(postDetailsActivity.this);
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure to delete this comment?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(postId);
                                ref.child("Comment").child(cid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        DatabaseReference cLikeRef = FirebaseDatabase.getInstance().getReference("Comment_Likes");
                                        cLikeRef.child(postId).child("Comment").child(cid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(postDetailsActivity.this, "Comment Deleted..", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(postDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        return false;
                    }
                });

                holder.allReplies.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(postDetailsActivity.this, ReplyCommentActivity.class);
                        intent.putExtra("pid", postId);
                        intent.putExtra("uid", uid);
                        intent.putExtra("cid", cid);
                        startActivity(intent);
                    }
                });
                checkAllReplies(cid, postId, holder);

                holder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.reply_layout.setVisibility(View.VISIBLE);
                    }
                });

                holder.cooment_send_neo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.postReplyComment(cid, uid, postId, postDetailsActivity.this, CurrentUserId);
                    }
                });

                holder.setLikes(uid, postId, cid, CurrentUserId);

                holder.c_like_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ismPrecessLikes = true;

                        CommentLikeREf.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (ismPrecessLikes == true){
                                    if (snapshot.child(postId).child("Comment").child(cid).hasChild(CurrentUserId)){
                                        CommentLikeREf.child(postId).child("Comment").child(cid).child(CurrentUserId).removeValue();
                                        ismPrecessLikes = false;
                                    }
                                    else {
                                        CommentLikeREf.child(postId).child("Comment").child(cid).child(CurrentUserId).setValue("Liked");
                                        ismPrecessLikes = false;

                                        if (!UserId.equals(CurrentUserId)){

                                            addToHisNotification(UserId, pId, "Liked your comment", CurrentUserId);
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

            @NonNull
            @Override
            public comment_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
                return new comment_viewholder(v);
            }
        };
        adaptor.notifyDataSetChanged();
        adaptor.startListening();
        post_comment_recycler.setAdapter(adaptor);

    }

    private void checkAllReplies(String cid, String postId, comment_viewholder holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(postId).child("Comment").child(cid).child("Reply");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.allReplies.setVisibility(View.VISIBLE);
                    int countReplies = (int) snapshot.getChildrenCount();
                    holder.allReplies.setText(""+countReplies+" replies");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTFiles").child(pId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String FileName = snapshot.child("FileName").getValue().toString();
                String FileType = snapshot.child("FileType").getValue().toString();
                String PostURL = snapshot.child("PostURL").getValue().toString();
                String TextBoxData = snapshot.child("TextBoxData").getValue().toString();
                UserId = snapshot.child("UserId").getValue().toString();
                String timeStamp = snapshot.child("timeStamp").getValue().toString();

                String showPostTime = getFormateDate(getApplicationContext() , timeStamp);


                more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (UserId.equals(CurrentUserId)){
                            delete_l.setVisibility(View.VISIBLE);
                            edit_l.setVisibility(View.VISIBLE);

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deletePost(pId, PostURL);
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


                if (FileType.equals("image")){
                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    Picasso.get().load(PostURL).into(post_image);
                    video_view.setVisibility(View.GONE);
                    documentView.setVisibility(View.GONE);

                }
                else if (FileType.equals("video")){
                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    post_image.setVisibility(View.GONE);
                    documentView.setVisibility(View.GONE);

                    // set video

                    setVideo(PostURL, getApplicationContext());

                }

                else if (FileType.equals("pdf")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("ppt") || FileType.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||FileType.equals("vnd.ms-powerpoint")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.ppt);

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| FileType.equals("xlsx") || FileType.equals("application/vnd.ms-excel")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.excel);

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("application/msword") || FileType.equals("doc") || FileType.equals("docx") || FileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.word);

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if (FileType.equals("none")){

                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    documentView.setVisibility(View.GONE);
                }
                else {
                    postTime.setText(showPostTime);
                    postDesc.setText(TextBoxData);
                    video_view.setVisibility(View.GONE);
                    post_image.setVisibility(View.GONE);
                    fileName.setText(FileName);
                    doc_img.setImageResource(R.drawable.zip);

                    // set document

                    download_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PostURL));
                            startActivity(browserIntent);
                        }
                    });
                }


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(UserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("profile_image").exists()){
                            String pUrl = snapshot.child("profile_image").getValue().toString();
                        }
                        String uName = snapshot.child("userName").getValue().toString();

                        userName.setText(uName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deletePost(String pId, String postURL) {
        if (postURL.equals("none")){
            deleteWithoutImage(pId);
        }
        else {
            deleteWithImage(pId, postURL);
        }
    }

    private void deleteWithImage(String pId, String postUrl) {


        final ProgressDialog pd = new ProgressDialog(this);
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
                        Toast.makeText(postDetailsActivity.this, "Deleted succefully..", Toast.LENGTH_SHORT).show();
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

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting...");
        pd.show();
        Query query = FirebaseDatabase.getInstance().getReference("POSTFiles").orderByChild("timeStamp").equalTo(pId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(postDetailsActivity.this, "Deleted succefully..", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes(String pId, String userId) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pId).hasChild(userId)){

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

    private void setVideo(String postURL, Context applicationContext) {
        try {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector);
            Uri videouri = Uri.parse(postURL);

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

    private String getFormateDate(Context applicationContext, String postTime) {

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
        messageTextBody.put("message", pId);
        messageTextBody.put("type", "post");
        messageTextBody.put("from", CurrentUserId);
        messageTextBody.put("to", to);
        messageTextBody.put("messageID", messagePushID);
        messageTextBody.put("timestamp", timestamp);
        messageTextBody.put("time", CurrentTime);
        messageTextBody.put("date", CurrentDate);
        messageTextBody.put("isSeen", "0");

        Map messageTextBody1 = new HashMap();
        messageTextBody1.put("message", pId);
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

                    Toast.makeText(postDetailsActivity.this, "Error....", Toast.LENGTH_SHORT).show();
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