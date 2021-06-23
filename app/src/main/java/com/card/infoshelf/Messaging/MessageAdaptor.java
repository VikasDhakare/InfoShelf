package com.card.infoshelf.Messaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.networkModel;
import com.card.infoshelf.bottomfragment.timeLine_model;
import com.card.infoshelf.postDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MessageHolder> {

    private Context context;
    private ArrayList<MessageModel> list;
    String messageSenderID;
    FirebaseAuth mAuth;
    BottomSheetDialog imageViewer;


    public MessageAdaptor(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_messages ,  parent , false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        mAuth =  FirebaseAuth.getInstance();
        String messageSenderID = mAuth.getCurrentUser().getUid();

        MessageModel model = list.get(position);

        String fromUserID = model.getFrom();
        String fromMessageType = model.getType();


        holder.layoutSenderMessageText.setVisibility(View.GONE);
        holder.layoutReceiverMessageText.setVisibility(View.GONE);
        holder.layoutSenderMessageImage.setVisibility(View.GONE);
        holder.layoutReceiverMessageImage.setVisibility(View.GONE);
        holder.layoutSenderMessageDoc.setVisibility(View.GONE);
        holder.layoutReceiverMessageDoc.setVisibility(View.GONE);
        holder.layoutReceiverPostImage.setVisibility(View.GONE);
        holder.layoutReceiverPostDoc.setVisibility(View.GONE);
        holder.layoutReceiverPostText.setVisibility(View.GONE);
        holder.layoutSenderPostImage.setVisibility(View.GONE);
        holder.layoutSenderPostDoc.setVisibility(View.GONE);
        holder.layoutSenderPostText.setVisibility(View.GONE);
        holder.l_send.setVisibility(View.GONE);
        holder.play_icon.setVisibility(View.GONE);
        holder.s_play_icon.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {


            if (fromUserID.equals(messageSenderID)) {
                holder.layoutSenderMessageText.setVisibility(View.VISIBLE);
                holder.SenderMessageTextTime.setText(model.getTime());
                holder.SenderMessageText.setText(model.getMessage());
            } else {

                holder.layoutReceiverMessageText.setVisibility(View.VISIBLE);
                holder.ReceiverMessageTextTime.setText(model.getTime());
                holder.ReceiverMessageText.setText(model.getMessage());

            }
        }  if (fromMessageType.equals("image")) {
            if (fromUserID.equals(messageSenderID)) {



                holder.layoutSenderMessageImage.setVisibility(View.VISIBLE);
                holder.SenderMessageImageTime.setText(model.getTime());
                Picasso.get().load(model.getMessage()).into(holder.SenderMessageImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowBottomSheet(model);

                    }
                });


            } else {

                holder.layoutReceiverMessageImage.setVisibility(View.VISIBLE);
                holder.ReceiverMessageImageTime.setText(model.getTime());
                Picasso.get().load(model.getMessage()).into(holder.ReceiverMessageImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowBottomSheet(model);


                    }
                });

            }
        } if (fromMessageType.equals("doc")) {
            if (fromUserID.equals(messageSenderID)) {
                holder.layoutSenderMessageDoc.setVisibility(View.VISIBLE);
                holder.SenderMessageDocTime.setText(model.getTime());
                holder.SenderMessageDocName.setText(model.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        Uri uri = Uri.parse(model.getMessage());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        holder.layoutSenderMessageDoc.getContext().startActivity(Intent.createChooser(intent, "Select Browser"));


                    }
                });
            } else {

                holder.layoutReceiverMessageDoc.setVisibility(View.VISIBLE);
                holder.ReceiverMessageDocTime.setText(model.getTime());
                holder.ReceiverMessageDocName.setText(model.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        Uri uri = Uri.parse(model.getMessage());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        holder.layoutSenderMessageDoc.getContext().startActivity(Intent.createChooser(intent, "Select Browser"));

                    }
                });
            }
        }if(fromMessageType.equals("post"))
        {
            if (fromUserID.equals(messageSenderID)) {
                String ms = model.getMessage();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("POSTFiles").child(ms).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        timeLine_model m = snapshot.getValue(timeLine_model.class);
                        String type = snapshot.child("FileType").getValue().toString();
                        GetSenderPostData(type , holder , m , model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                String msg = model.getMessage();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("POSTFiles").child(msg).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        timeLine_model m = snapshot.getValue(timeLine_model.class);
                        String type = snapshot.child("FileType").getValue().toString();
                        getReceiverPostData(type , holder , m , model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context , postDetailsActivity.class);
                    intent.putExtra("pId", model.getMessage());
                    context.startActivity(intent);
                }
            });
        }

        if (fromUserID.equals(messageSenderID)) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog builder1 = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_message_dialog , null);
                    builder1.setView(view);

                    builder1.show();
                    TextView deleteForMe = view.findViewById(R.id.delete_for_me);
                    TextView deleteForEveryone = view.findViewById(R.id.delete_for_everyone);
                    TextView cancel = view.findViewById(R.id.cancel);
                    deleteForMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteSenderMessages(position, holder, model);
                            builder1.dismiss();

                        }
                    });
                    deleteForEveryone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteMessageForEveryone(position, holder, model);
                            builder1.dismiss();


                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder1.dismiss();
                        }
                    });

                    return true;
                }
            });
        }
        else {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog builder1 = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_message_dialog , null);
                    builder1.setView(view);

                    builder1.show();
                    TextView deleteForMe = view.findViewById(R.id.delete_for_me);
                    TextView deleteForEveryone = view.findViewById(R.id.delete_for_everyone);
                    TextView cancel = view.findViewById(R.id.cancel);
                    deleteForEveryone.setVisibility(View.GONE);
                    deleteForMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteSenderMessages(position, holder, model);
                            builder1.dismiss();

                        }
                    });
                    deleteForEveryone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteMessageForEveryone(position, holder, model);
                            builder1.dismiss();


                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder1.dismiss();
                        }
                    });


                    return true;
                }
            });
        }



        if (fromUserID.equals(messageSenderID)) {
            if (position == list.size() - 1) {
                if (model.getIsSeen().equals("1")) {
                    holder.l_send.setVisibility(View.VISIBLE);
                    holder.seen.setVisibility(View.VISIBLE);
                    holder.unseen.setVisibility(View.GONE);
                } else {
                    holder.l_send.setVisibility(View.VISIBLE);
                    holder.unseen.setVisibility(View.VISIBLE);
                    holder.seen.setVisibility(View.GONE);
                }
            } else {
                holder.l_send.setVisibility(View.GONE);
                holder.unseen.setVisibility(View.GONE);
                holder.seen.setVisibility(View.GONE);
            }
        }

        long previousTs = 0;
        if(position>=1){
            MessageModel pm = list.get(position-1);
            previousTs = Long.parseLong(pm.getTimestamp());


        }
        setTimeTextVisibility(Long.parseLong(model.getTimestamp()), previousTs, holder.textDate , model , position);



    }

    private void getReceiverPostData(String type, MessageHolder holder, timeLine_model m, MessageModel model) {
        if (type.equals("image")){
            holder.layoutReceiverPostImage.setVisibility(View.VISIBLE);
            Picasso.get().load(m.getPostURL()).into(holder.ReceiverPostImage);
            holder.ReceiverPostImageDes.setText(m.getTextBoxData());
            holder.ReceiverPostImageTime.setText(model.getTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Hlo", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if (type.equals("video")){
            holder.layoutReceiverPostImage.setVisibility(View.VISIBLE);
            holder.ReceiverPostImageDes.setText(m.getTextBoxData());
            holder.ReceiverPostImageTime.setText(model.getTime());
            Glide.with(context)
                    .load(m.getPostURL())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.ReceiverPostImage);
            holder.play_icon.setVisibility(View.VISIBLE);




        }
        else if (type.equals("pdf")){
            holder.layoutReceiverPostDoc.setVisibility(View.VISIBLE);
            holder.ReceiverMessageDocName.setText(m.getFileName());
            holder.ReceiverPostDocDes.setText(m.getTextBoxData());
            holder.ReceiverPostDocTime.setText(model.getTime());


        }
        else if (type.equals("ppt") || type.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||type.equals("vnd.ms-powerpoint")){
            holder.layoutReceiverPostDoc.setVisibility(View.VISIBLE);
            holder.ReceiverMessageDocName.setText(m.getFileName());
            holder.ReceiverPostDocDes.setText(m.getTextBoxData());
            holder.ReceiverPostDocTime.setText(model.getTime());

        }
        else if (type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| type.equals("xlsx") || type.equals("application/vnd.ms-excel")){
            holder.layoutReceiverPostDoc.setVisibility(View.VISIBLE);
            holder.ReceiverMessageDocName.setText(m.getFileName());
            holder.ReceiverPostDocDes.setText(m.getTextBoxData());
            holder.ReceiverPostDocTime.setText(model.getTime());
        }
        else if (type.equals("application/msword") || type.equals("doc") || type.equals("docx") || type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
            holder.layoutReceiverPostDoc.setVisibility(View.VISIBLE);
            holder.ReceiverMessageDocName.setText(m.getFileName());
            holder.ReceiverPostDocDes.setText(m.getTextBoxData());
            holder.ReceiverPostDocTime.setText(model.getTime());
        }
        else if (type.equals("none")){

            holder.layoutReceiverPostText.setVisibility(View.VISIBLE);
            holder.ReceiverPostText.setText(m.getTextBoxData());
            holder.ReceiverPostTextTime.setText(model.getTime());
        }
        else {
            holder.layoutReceiverPostDoc.setVisibility(View.VISIBLE);
            holder.ReceiverMessageDocName.setText(m.getFileName());
            holder.ReceiverPostDocDes.setText(m.getTextBoxData());
            holder.ReceiverPostDocTime.setText(model.getTime());
        }
    }

    private void GetSenderPostData(String type, MessageHolder holder, timeLine_model m , MessageModel model) {
        if (type.equals("image")){
            holder.layoutSenderPostImage.setVisibility(View.VISIBLE);
            Picasso.get().load(m.getPostURL()).into(holder.SenderPostImage);
            holder.SenderPostImageDes.setText(m.getTextBoxData());
            holder.SenderPostImageTime.setText(model.getTime());

        }
        else if (type.equals("video")){
            holder.layoutSenderPostImage.setVisibility(View.VISIBLE);
            holder.SenderPostImageDes.setText(m.getTextBoxData());
            holder.SenderPostImageTime.setText(model.getTime());
            Glide.with(context)
                    .load(m.getPostURL())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.SenderPostImage);
            holder.s_play_icon.setVisibility(View.VISIBLE);




        }
        else if (type.equals("pdf")){
            holder.layoutSenderPostDoc.setVisibility(View.VISIBLE);
            holder.SenderMessageDocName.setText(m.getFileName());
            holder.SenderPostDocDes.setText(m.getTextBoxData());
            holder.SenderPostDocTime.setText(model.getTime());

        }
        else if (type.equals("ppt") || type.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||type.equals("vnd.ms-powerpoint")){
            holder.layoutSenderPostDoc.setVisibility(View.VISIBLE);
            holder.SenderMessageDocName.setText(m.getFileName());
            holder.SenderPostDocDes.setText(m.getTextBoxData());
            holder.SenderPostDocTime.setText(model.getTime());

        }
        else if (type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| type.equals("xlsx") || type.equals("application/vnd.ms-excel")){
            holder.layoutSenderPostDoc.setVisibility(View.VISIBLE);
            holder.SenderMessageDocName.setText(m.getFileName());
            holder.SenderPostDocDes.setText(m.getTextBoxData());
            holder.SenderPostDocTime.setText(model.getTime());
        }
        else if (type.equals("application/msword") || type.equals("doc") || type.equals("docx") || type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
            holder.layoutSenderPostDoc.setVisibility(View.VISIBLE);
            holder.SenderMessageDocName.setText(m.getFileName());
            holder.SenderPostDocDes.setText(m.getTextBoxData());
            holder.SenderPostDocTime.setText(model.getTime());
        }
        else if (type.equals("none")){

            holder.layoutSenderPostText.setVisibility(View.VISIBLE);
            holder.SenderPostText.setText(m.getTextBoxData());
            holder.SenderPostTextTime.setText(model.getTime());
        }
        else {
            holder.layoutSenderPostDoc.setVisibility(View.VISIBLE);
            holder.SenderMessageDocName.setText(m.getFileName());
            holder.SenderPostDocDes.setText(m.getTextBoxData());
            holder.SenderPostDocTime.setText(model.getTime());
        }
    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText , MessageModel model , int position){

        if(ts2==0){
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(list.get(position).getDate());
        }else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

            if(sameMonth){
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            }else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(list.get(position).getDate());
            }

        }
    }


    private void ShowBottomSheet(MessageModel model) {
        View view = LayoutInflater.from(context).inflate(R.layout.msg_image_viewer_bottom_sheet, null);

        ImageView image = view.findViewById(R.id.msg_image);

        Picasso.get().load(model.getMessage()).into(image);

        imageViewer = new BottomSheetDialog(context);
        imageViewer.setContentView(view);
        imageViewer.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        RelativeLayout layoutSenderMessageText , layoutReceiverMessageText , layoutSenderMessageImage , layoutReceiverMessageImage , layoutSenderMessageDoc , layoutReceiverMessageDoc;
        RelativeLayout layoutReceiverPostImage , layoutSenderPostImage , layoutReceiverPostDoc , layoutSenderPostDoc , layoutReceiverPostText , layoutSenderPostText;
        TextView ReceiverMessageText , ReceiverMessageTextTime ,SenderMessageText , SenderMessageTextTime  , SenderPostImageDes , SenderPostImageTime , SenderPostFileName , SenderPostDocDes ,SenderPostDocTime;
        TextView ReceiverMessageDocName , ReceiverMessageDocTime ,SenderMessageDocName , SenderMessageDocTime , SenderPostText , SenderPostTextTime;
        TextView  ReceiverMessageImageTime , SenderMessageImageTime , ReceiverPostImageDes , ReceiverPostImageTime  , ReceiverPostFileName , ReceiverPostDocDes , ReceiverPostDocTime , ReceiverPostText , ReceiverPostTextTime;
        ImageView SenderMessageImage , ReceiverMessageImage  , ReceiverPostImage , SenderPostImage;
        ImageView unseen , seen  , play_icon , s_play_icon;
        LinearLayout l_send;

        RelativeLayout l_date;

        TextView textDate;



        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            layoutSenderMessageText = itemView.findViewById(R.id.layoutSenderMessageText);
            SenderMessageText = itemView.findViewById(R.id.SenderMessageText);
            SenderMessageTextTime = itemView.findViewById(R.id.SenderMessageTextTime);

            layoutReceiverMessageText = itemView.findViewById(R.id.layoutReceiverMessageText);
            ReceiverMessageText = itemView.findViewById(R.id.ReceiverMessageText);
            ReceiverMessageTextTime = itemView.findViewById(R.id.ReceiverMessageTextTime);

            layoutSenderMessageImage = itemView.findViewById(R.id.layoutSenderMessageImage);
            SenderMessageImage = itemView.findViewById(R.id.SenderMessageImage);
            SenderMessageImageTime = itemView.findViewById(R.id.SenderMessageImageTime);

            layoutReceiverMessageImage = itemView.findViewById(R.id.layoutReceiverMessageImage);
            ReceiverMessageImage = itemView.findViewById(R.id.ReceiverMessageImage);
            ReceiverMessageImageTime = itemView.findViewById(R.id.ReceiverMessageImageTime);

            layoutSenderMessageDoc = itemView.findViewById(R.id.layoutSenderMessageDoc);
            SenderMessageDocName = itemView.findViewById(R.id.SenderrMessageDocName);
            SenderMessageDocTime = itemView.findViewById(R.id.SenderMessageDocTime);

            layoutReceiverMessageDoc = itemView.findViewById(R.id.layoutReceiverMessageDoc);
            ReceiverMessageDocName = itemView.findViewById(R.id.ReceiverMessageDocName);
            ReceiverMessageDocTime = itemView.findViewById(R.id.ReceiverMessageDocTime);

            l_send = itemView.findViewById(R.id.l_send);
            unseen = itemView.findViewById(R.id.unseen);
            seen = itemView.findViewById(R.id.seen);
            play_icon = itemView.findViewById(R.id.play_icon);
            s_play_icon = itemView.findViewById(R.id.s_play_icon);


            l_date = itemView.findViewById(R.id.l_date);
            textDate = itemView.findViewById(R.id.textDate);

            layoutReceiverPostImage = itemView.findViewById(R.id.layoutReceiverPostImage);
            layoutSenderPostImage = itemView.findViewById(R.id.layoutSenderPostImage);

            layoutReceiverPostDoc = itemView.findViewById(R.id.layoutReceiverPostDoc);
            layoutSenderPostDoc = itemView.findViewById(R.id.layoutSenderPostDoc);

            layoutReceiverPostText = itemView.findViewById(R.id.layoutReceiverPostText);
            layoutSenderPostText = itemView.findViewById(R.id.layoutSenderPostText);

            ReceiverPostImage = itemView.findViewById(R.id.ReceiverPostImage);
            ReceiverPostImageDes = itemView.findViewById(R.id.ReceiverPostImageDes);
            ReceiverPostImageTime = itemView.findViewById(R.id.ReceiverPostImageTime);

            SenderPostImage = itemView.findViewById(R.id.SenderPostImage);
            SenderPostImageDes = itemView.findViewById(R.id.SenderPostImageDes);
            SenderPostImageTime = itemView.findViewById(R.id.SenderPostImageTime);

            ReceiverPostFileName =itemView.findViewById(R.id.ReceiverPostFileName);
            ReceiverPostDocDes =itemView.findViewById(R.id.ReceiverPostDocDes);
            ReceiverPostDocTime =itemView.findViewById(R.id.ReceiverPostDocTime);

            SenderPostFileName =itemView.findViewById(R.id.SenderPostFileName);
            SenderPostDocDes =itemView.findViewById(R.id.SenderPostDocDes);
            SenderPostDocTime =itemView.findViewById(R.id.SenderPostDocTime);

            ReceiverPostText = itemView.findViewById(R.id.ReceiverPostText);
            ReceiverPostTextTime = itemView.findViewById(R.id.ReceiverPostTextTime);

            SenderPostText = itemView.findViewById(R.id.SenderPostText);
            SenderPostTextTime = itemView.findViewById(R.id.SenderPostTextTime);

        }
    }


    private void deleteSenderMessages(final int position, final MessageHolder holder, final MessageModel model) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(model.getFrom()).child(model.getTo()).child(model.getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });

    }

    private void deleteReceiverMessages(final int position, final MessageHolder holder, final MessageModel model) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(model.getTo()).child(model.getFrom()).child(model.getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });

    }

    private void deleteMessageForEveryone(final int position, final MessageHolder holder, final MessageModel model) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(model.getTo()).child(model.getFrom()).child(model.getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    rootRef.child("Messages").child(model.getFrom()).child(model.getTo()).child(model.getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (model.getType().equals("image") || model.getType().equals("doc")){
                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference ref = firebaseStorage.getReferenceFromUrl(model.getMessage());
                                    ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                        }
                    });

                }
            }
        });

    }

}
