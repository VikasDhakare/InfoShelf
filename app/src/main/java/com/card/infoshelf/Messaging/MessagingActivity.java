package com.card.infoshelf.Messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.Messenger.MessengerActivity;
import com.card.infoshelf.Messenger.MessengerAdaptor;
import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.networkModel;
import com.card.infoshelf.userProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    ImageView iv_back, btn_send, select_file, menu;
    CircleImageView profile_image;
    TextView user_name, status;
    EditText et_msg;
    private RecyclerView msg_rv;
    LinearLayoutManager linearLayoutManager;
    String name, image, MessageSenderID, MessageReceiverID , state;
    private ArrayList<MessageModel> list;
    private MessageAdaptor adaptor;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, ref, typingRef;
    private int i = 0;
    private RelativeLayout l2;

    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog LoadingBar;

    private Uri FileUri;
    private String myUrl = "";
    private String checker, Extension, displayName, size;
    private int displaysize = 0;
    private StorageTask uploadTask;

    private ValueEventListener seenListener;

    private boolean isTyping = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        getSupportActionBar().hide();

        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        status = findViewById(R.id.status);
        iv_back = findViewById(R.id.iv_back);
        btn_send = findViewById(R.id.btn_send);
        et_msg = findViewById(R.id.et_msg);
        select_file = findViewById(R.id.select_file);
        menu = findViewById(R.id.menu);
        l2 = findViewById(R.id.l2);

        linearLayoutManager = new LinearLayoutManager(this);
        msg_rv = findViewById(R.id.msg_rv);
        msg_rv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        LoadingBar = new ProgressDialog(this);

        list = new ArrayList<>();
        adaptor = new MessageAdaptor(this, list);
        msg_rv.setAdapter(adaptor);


        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("profile_image");
        MessageReceiverID = getIntent().getStringExtra("userid");
        state = getIntent().getStringExtra("state");

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MessagingActivity.this, v);
                popupMenu.inflate(R.menu.menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_profile:
                                Intent intent = new Intent(MessagingActivity.this, userProfileActivity.class);
                                intent.putExtra("userid", MessageReceiverID);
                                startActivity(intent);
                                return true;
                            case R.id.clear_chat:
                                AlertDialog builder1 = new AlertDialog.Builder(MessagingActivity.this).create();
                                View view = LayoutInflater.from(MessagingActivity.this).inflate(R.layout.clear_chat_dialog , null);
                                builder1.setView(view);

                                builder1.show();
                                TextView clear_chat = view.findViewById(R.id.clear_chat);
                                TextView cancel = view.findViewById(R.id.cancel);
                                clear_chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ClearChat();
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
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        mAuth = FirebaseAuth.getInstance();
        MessageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        typingRef = FirebaseDatabase.getInstance().getReference("typing");

        user_name.setText(name);
        Picasso.get().load(image).placeholder(R.drawable.def_user).into(profile_image);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagingActivity.this, MessengerActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Typing(false);
                SendMessage();
            }
        });

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MessagingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessagingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
                ShowBottomSheet();
            }
        });

        RootRef.child("Users").child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    typingRef.child(MessageSenderID).child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            if (s.exists()) {
                                Boolean sts = (Boolean) s.child("isTyping").getValue();
                                if (sts) {
                                    status.setText("typing...");
                                } else {
                                    String st = snapshot.child("status").getValue().toString();
                                    String d = snapshot.child("date").getValue().toString();
                                    String t = snapshot.child("time").getValue().toString();
                                    if (st.equals("online")) {
                                        status.setText(st);
                                    }
                                    if (st.equals("offline")) {
                                        status.setText("Last Seen : " + d + " at " + t);
                                    }
                                }
                            } else {
                                String stt = snapshot.child("status").getValue().toString();
                                String d = snapshot.child("date").getValue().toString();
                                String t = snapshot.child("time").getValue().toString();
                                if (stt.equals("online")) {
                                    status.setText(stt);
                                }
                                if (stt.equals("offline")) {
                                    status.setText("Last Seen : " + d + " at " + t);
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


        ShowMessages();

        isSeen();

        TypingStatus();


    }

    private void ClearChat() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
       rootRef.child("Messages").child(MessageReceiverID).child(MessageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists())
               {
                   rootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).removeValue();
                   rootRef.child("ChatList").child(MessageSenderID).child(MessageReceiverID).removeValue();
                   Toast.makeText(MessagingActivity.this, "Chat Deleted", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(MessagingActivity.this, MessengerActivity.class);
                   startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
               }
               else
               {
                   rootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           for (DataSnapshot ds :  snapshot.getChildren()) {
                               String type = ds.child("type").getValue().toString();
                               String msg = ds.child("message").getValue().toString();
                               if (type.equals("image") || type.equals("doc")) {
                                   FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                   StorageReference ref = firebaseStorage.getReferenceFromUrl(msg);
                                   ref.delete();
                                   ds.getRef().removeValue();
                                   Toast.makeText(MessagingActivity.this, "image , doc", Toast.LENGTH_SHORT).show();
                               }else
                               {
                                   Toast.makeText(MessagingActivity.this, "text  , post", Toast.LENGTH_SHORT).show();
                                   ds.getRef().removeValue();
                               }
                           }
                           rootRef.child("ChatList").child(MessageSenderID).child(MessageReceiverID).removeValue();
                           Toast.makeText(MessagingActivity.this, "Chat Deleted", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(MessagingActivity.this, MessengerActivity.class);
                           startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


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




    private void TypingStatus() {
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    Typing(false);
                } else {
                    Typing(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });
    }

    private void Typing(boolean b) {
        typingRef.child(MessageReceiverID).child(MessageSenderID).child("isTyping").setValue(b);
    }

    private void isSeen() {
        ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(MessageReceiverID).child(MessageSenderID);
        seenListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        MessageModel model = dataSnapshot.getValue(MessageModel.class);
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("isSeen" , "1");
                        dataSnapshot.getRef().updateChildren(map);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.message_bottom_sheet, null);


        view.findViewById(R.id.r1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent.createChooser(intent, "Select Document"), 1);

                checker = "Doc";

                bottomSheetDialog.dismiss();
            }
        });

        view.findViewById(R.id.r2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent.createChooser(intent, "Select Image"), 1);

                checker = "image";

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);


        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            LoadingBar.setTitle("Sending....");
            LoadingBar.setMessage("Please Wait");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();

            FileUri = data.getData();
            String uriString = FileUri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            Extension = path.substring(path.lastIndexOf("."));

            Cursor cursor = null;
            try {
                cursor = getApplication().getContentResolver().query(FileUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    displaysize = cursor.getColumnIndex(OpenableColumns.SIZE);
                    size = String.valueOf(displaysize);


                }

            } finally {
                cursor.close();
            }


            if (!checker.equals("image")) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                et_msg.setText("");
                final String CurrentTime, CurrentDate;
                final String messageSenderRef = "Messages/" + MessageSenderID + "/" + MessageReceiverID;
                final String messageReceiverRef = "Messages/" + MessageReceiverID + "/" + MessageSenderID;

                Calendar date = Calendar.getInstance();
                SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
                CurrentDate = CurrentDateFormat.format(date.getTime());


                Calendar time = Calendar.getInstance();
                SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
                CurrentTime = CurrentTimeFormat.format(time.getTime());

                String timestamp = String.valueOf(System.currentTimeMillis());


                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).push();


                final String messagePushID = userMessageKeyRef.getKey();


                final StorageReference filePath = storageReference.child(timestamp).child(displayName + "." + Extension);

                filePath.putFile(FileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", task.getResult().toString());
                                    messageTextBody.put("name", displayName);
                                    messageTextBody.put("size", size);
                                    messageTextBody.put("type", "doc");
                                    messageTextBody.put("from", MessageSenderID);
                                    messageTextBody.put("to", MessageReceiverID);
                                    messageTextBody.put("messageID", messagePushID);
                                    messageTextBody.put("timestamp", timestamp);
                                    messageTextBody.put("time", CurrentTime);
                                    messageTextBody.put("date", CurrentDate);
                                    messageTextBody.put("isSeen", "0");

                                    Map messageTextBody1 = new HashMap();
                                    messageTextBody1.put("message", task.getResult().toString());
                                    messageTextBody1.put("name", displayName);
                                    messageTextBody1.put("size", size);
                                    messageTextBody1.put("type", "doc");
                                    messageTextBody1.put("from", MessageSenderID);
                                    messageTextBody1.put("to", MessageReceiverID);
                                    messageTextBody1.put("messageID", messagePushID);
                                    messageTextBody1.put("timestamp", timestamp);
                                    messageTextBody1.put("time", CurrentTime);
                                    messageTextBody1.put("date", CurrentDate);
                                    messageTextBody1.put("isSeen", "1");

                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody1);

                                    RootRef.updateChildren(messageBodyDetails);
                                    UpdateChatList(timestamp);
                                    LoadingBar.dismiss();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoadingBar.dismiss();
                        Toast.makeText(MessagingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double P = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        LoadingBar.setMessage((int) P + "% Uploading...");
                    }
                });


            }
            else if (checker.equals("image")) {


                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                et_msg.setText("");
                final String CurrentTime, CurrentDate;
                final String messageSenderRef = "Messages/" + MessageSenderID + "/" + MessageReceiverID;
                final String messageReceiverRef = "Messages/" + MessageReceiverID + "/" + MessageSenderID;

                Calendar date = Calendar.getInstance();
                SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
                CurrentDate = CurrentDateFormat.format(date.getTime());


                Calendar time = Calendar.getInstance();
                SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
                CurrentTime = CurrentTimeFormat.format(time.getTime());

                String timestamp = String.valueOf(System.currentTimeMillis());


                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).push();


                final String messagePushID = userMessageKeyRef.getKey();


                final StorageReference filePath = storageReference.child(timestamp).child(displayName + "." + Extension);

                uploadTask = filePath.putFile(FileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();


                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", displayName);
                            messageTextBody.put("size", size);
                            messageTextBody.put("type", "image");
                            messageTextBody.put("from", MessageSenderID);
                            messageTextBody.put("to", MessageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("timestamp", timestamp);
                            messageTextBody.put("time", CurrentTime);
                            messageTextBody.put("date", CurrentDate);
                            messageTextBody.put("isSeen", "0");


                            Map messageTextBody1 = new HashMap();
                            messageTextBody1.put("message", myUrl);
                            messageTextBody1.put("name", displayName);
                            messageTextBody1.put("size", size);
                            messageTextBody1.put("type", "image");
                            messageTextBody1.put("from", MessageSenderID);
                            messageTextBody1.put("to", MessageReceiverID);
                            messageTextBody1.put("messageID", messagePushID);
                            messageTextBody1.put("timestamp", timestamp);
                            messageTextBody1.put("time", CurrentTime);
                            messageTextBody1.put("date", CurrentDate);
                            messageTextBody1.put("isSeen", "1");

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody1);

                            UpdateChatList(timestamp);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        LoadingBar.dismiss();
                                    } else {
                                        LoadingBar.dismiss();
                                        Toast.makeText(MessagingActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    }
                });

            }


        }


    }

    private void ShowMessages() {
        RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        MessageModel model = ds.getValue(MessageModel.class);
                        list.add(model);
                    }
                    adaptor.notifyDataSetChanged();
                    msg_rv.smoothScrollToPosition(msg_rv.getAdapter().getItemCount() - 1);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendMessage() {

        String MessageText = et_msg.getText().toString().trim();


        if (TextUtils.isEmpty(MessageText)) {
            Toast.makeText(this, "Write Message First.....", Toast.LENGTH_SHORT).show();
        } else {
            et_msg.setText("");

            String CurrentTime, CurrentDate;
            String messageSenderRef = "Messages/" + MessageSenderID + "/" + MessageReceiverID;
            String messageReceiverRef = "Messages/" + MessageReceiverID + "/" + MessageSenderID;

            Calendar date = Calendar.getInstance();
            SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("dd MMM , yyyy");
            CurrentDate = CurrentDateFormat.format(date.getTime());


            Calendar time = Calendar.getInstance();
            SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
            CurrentTime = CurrentTimeFormat.format(time.getTime());

            String timestamp = String.valueOf(System.currentTimeMillis());


            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(MessageSenderID).child(MessageReceiverID).push();


            String messagePushID = userMessageKeyRef.getKey();



            Map messageTextBody = new HashMap();
            messageTextBody.put("message", MessageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", MessageSenderID);
            messageTextBody.put("to", MessageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("timestamp", timestamp);
            messageTextBody.put("time", CurrentTime);
            messageTextBody.put("date", CurrentDate);
            messageTextBody.put("isSeen", "0");

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);


            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        UpdateChatList(timestamp);

                    } else {

                        Toast.makeText(MessagingActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                    }

                }
            });
//            UpdateChatListAndLastMessage(MessageText);


        }
    }

    private void UpdateChatList(String timestamp) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatList");
        HashMap map = new HashMap();
        map.put("userId", MessageReceiverID);
        map.put("time", timestamp);
        ref.child(MessageSenderID).child(MessageReceiverID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap map1 = new HashMap();
                map1.put("userId", MessageSenderID);
                map1.put("time", timestamp);

                ref.child(MessageReceiverID).child(MessageSenderID).setValue(map1);
            }
        });
    }


    private void UpdateChatListAndLastMessage(String data){
        String CurrentTime  , MessageText;
        String timestamp = String.valueOf(System.currentTimeMillis());
        MessageText = data;
        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
        CurrentTime = CurrentTimeFormat.format(time.getTime());


        RootRef.child("Chat List").child(MessageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String key = dataSnapshot.getKey().toString();
                        String id =  dataSnapshot.child("userId").getValue().toString();
                        if (id.equals(MessageReceiverID))
                        {
                            i=1;
                            RootRef.child("Chat List").child(MessageSenderID).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    RootRef.child("Chat List").child(MessageSenderID).child(timestamp).child("userId").setValue(MessageReceiverID);
                                }
                            });
                        }


                    }
                    if (i==0)
                    {
                        RootRef.child("Chat List").child(MessageSenderID).child(timestamp).child("userId").setValue(MessageReceiverID);
                        i=0;
                    }
                }
                else
                {
                    RootRef.child("Chat List").child(MessageSenderID).child(timestamp).child("userId").setValue(MessageReceiverID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        RootRef.child("Chat List").child(MessageReceiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {

                        String key = dataSnapshot.getKey().toString();
                        String id =  dataSnapshot.child("userId").getValue().toString();
                        if (id.equals(MessageSenderID))
                        {
                            Toast.makeText(MessagingActivity.this, "already", Toast.LENGTH_SHORT).show();
                            i =1 ;
                            RootRef.child("Chat List").child(MessageReceiverID).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    RootRef.child("Chat List").child(MessageReceiverID).child(timestamp).child("userId").setValue(MessageSenderID);
                                }
                            });
                        }


                    }
                    if (i==0)
                    {
                        Toast.makeText(MessagingActivity.this, "new", Toast.LENGTH_SHORT).show();
                        RootRef.child("Chat List").child(MessageReceiverID).child(timestamp).child("userId").setValue(MessageSenderID);
                        i=0;
                    }
                }
                else
                {
                    RootRef.child("Chat List").child(MessageReceiverID).child(timestamp).child("userId").setValue(MessageSenderID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void  status (String status){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        String CurrentDate, CurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM , yyyy");
        CurrentDate = currentDate.format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        CurrentTime = currentTime.format(calendar1.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time", CurrentTime);
        onlineState.put("date", CurrentDate);
        onlineState.put("status", status);


        ref.updateChildren(onlineState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(seenListener);
        status("offline");
        Typing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (state.equals("UnFriend"))
        {
            l2.setVisibility(View.INVISIBLE);
            et_msg.setVisibility(View.GONE);
            btn_send.setVisibility(View.GONE);

        }

    }
}