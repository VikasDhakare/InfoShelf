package com.card.infoshelf.bottomfragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.Friends.FriendsActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.Requests.RequestsActivity;
import com.card.infoshelf.bookmarkedActivity;
import com.card.infoshelf.loginandsignIn.LogInActivity;
import com.card.infoshelf.myProfileTabaccessAdaptor;
import com.card.infoshelf.userProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Profilefragment extends Fragment {

    private TextView userName, userinfo, viewProfile, uploadprofile , f_count , r_count;
    private LinearLayout network , requests, ProfileUploadLayout, blocked_l, credentials_l, about_l, logout_l, wvp_l, bookmarked_l;
    private String userid;
    private ImageView coverPic, dialogImage, ProfileOptionBtn;
    private CircleImageView userProfile;
    private BottomSheetDialog bottomSheetDialog;
    ProgressDialog pd;
    private StorageReference UserProfileImageRef, uploadCoverPic;
    private DatabaseReference databaseReference , Ref;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private Dialog mDialog, logout_dialog;

    String cameraPermission[];
    String storagePermission[];
    private boolean coverPicIsClicked = false;
    private boolean profilePicIsClicked = false;

    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleImage";

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private myProfileTabaccessAdaptor tabaccessAdaptor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profilefragment, container, false);

        userinfo = root.findViewById(R.id.userinfo);
        userName = root.findViewById(R.id.userName);
        coverPic = root.findViewById(R.id.coverPic);
        userProfile = root.findViewById(R.id.userProfile);
        network = root.findViewById(R.id.network);
        f_count = root.findViewById(R.id.f_count);
        requests = root.findViewById(R.id.requests);
        r_count = root.findViewById(R.id.r_count);
        ProfileOptionBtn = root.findViewById(R.id.profileoptionBTN);
        pd = new ProgressDialog(getActivity());

        logout_dialog = new Dialog(getActivity());
        logout_dialog.setContentView(R.layout.logout_dialog);
        logout_dialog.setCancelable(false);

        Button yes = logout_dialog.findViewById(R.id.yes);
        Button no = logout_dialog.findViewById(R.id.no);

        cameraPermission = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        uploadCoverPic = FirebaseStorage.getInstance().getReference().child("Cover_pic");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Ref = FirebaseDatabase.getInstance().getReference("Friends");

        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.show_profile_cover_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogImage = mDialog.findViewById(R.id.dialog_image);


        myViewPager = root.findViewById(R.id.my_profile_pager);
        tabaccessAdaptor = new myProfileTabaccessAdaptor(getActivity().getSupportFragmentManager());
        myViewPager.setAdapter(tabaccessAdaptor);

        myTabLayout = root.findViewById(R.id.my_profile_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_picture_in_picture_24);
        myTabLayout.getTabAt(1).setIcon(R.drawable.video);
        myTabLayout.getTabAt(2).setIcon(R.drawable.document);
//        myTabLayout.getTabAt(3).setIcon(R.drawable.about);


        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet_dialog, (LinearLayout)root.findViewById(R.id.sheet));

        bottomSheetDialog.setContentView(view);

        viewProfile = bottomSheetDialog.findViewById(R.id.viewProfile);
        uploadprofile = bottomSheetDialog.findViewById(R.id.uploadProfile);
        TextView titleProfile = bottomSheetDialog.findViewById(R.id.titleProfile);
        ProfileUploadLayout = bottomSheetDialog.findViewById(R.id.uploadProfileLayout);
        blocked_l = bottomSheetDialog.findViewById(R.id.blocked_l);
        bookmarked_l = bottomSheetDialog.findViewById(R.id.bookmarked_l);
        about_l = bottomSheetDialog.findViewById(R.id.about_l);
        credentials_l = bottomSheetDialog.findViewById(R.id.credentials_l);
        logout_l = bottomSheetDialog.findViewById(R.id.logiut_l);
        wvp_l = bottomSheetDialog.findViewById(R.id.wvp_l);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity() , LogInActivity.class);
                startActivity(intent);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_dialog.dismiss();
            }
        });

        logout_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_dialog.show();
            }
        });


        Ref.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    f_count.setText(""+count);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       requests.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext() , RequestsActivity.class);
               startActivity(intent);
           }
       });


        coverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profilePicIsClicked = false;

                titleProfile.setText("Cover Photo");
                viewProfile.setText("View Cover Photo");
                ProfileUploadLayout.setVisibility(View.VISIBLE);
                uploadprofile.setText("Upload Cover Photo");
                blocked_l.setVisibility(View.GONE);
                bookmarked_l.setVisibility(View.GONE);
                about_l.setVisibility(View.GONE);
                logout_l.setVisibility(View.GONE);
                credentials_l.setVisibility(View.GONE);
                wvp_l.setVisibility(View.GONE);


                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        openImage("cover");
                        Toast.makeText(getActivity(), "Cover Photo", Toast.LENGTH_SHORT).show();
                    }
                });


                uploadprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        coverPicIsClicked = true;
                        starCrop();
                    }
                });

                bottomSheetDialog.show();

            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverPicIsClicked = false;

                titleProfile.setText("Profile Photo");
                viewProfile.setText("View Profile Photo");
                ProfileUploadLayout.setVisibility(View.VISIBLE);
                uploadprofile.setText("Upload Profile Photo");
                blocked_l.setVisibility(View.GONE);
                bookmarked_l.setVisibility(View.GONE);
                about_l.setVisibility(View.GONE);
                logout_l.setVisibility(View.GONE);
                credentials_l.setVisibility(View.GONE);
                wvp_l.setVisibility(View.GONE);

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        openImage("profile");
                        Toast.makeText(getActivity(), "profile Photo", Toast.LENGTH_SHORT).show();
                    }
                });

                uploadprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        profilePicIsClicked = true;
                       starProfileCrop();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        ProfileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleProfile.setText("Your Profile Settings");
                viewProfile.setText("Edit Profile");
                ProfileUploadLayout.setVisibility(View.GONE);
                blocked_l.setVisibility(View.VISIBLE);
                bookmarked_l.setVisibility(View.VISIBLE);
                about_l.setVisibility(View.VISIBLE);
                logout_l.setVisibility(View.VISIBLE);
                credentials_l.setVisibility(View.VISIBLE);
                wvp_l.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();

                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EditProfile.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "edit profile about section", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bookmarked_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), bookmarkedActivity.class);
                startActivity(intent);
            }
        });


        network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , FriendsActivity.class);
                intent.putExtra("userid" , CurrentUserId);
                startActivity(intent);
            }
        });


        getUserInfo();

        return root;
    }

    private void starProfileCrop() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Image")
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(100)
                .start(getContext(), this);
    }

    private void openImage(String cover) {
        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (cover.equals("cover")){
                    //                for cover pic
                    if (snapshot.child("cover_pic").exists()){
                        String user_cover = snapshot.child("cover_pic").getValue().toString();
                        Picasso.get().load(user_cover).into(dialogImage);
                        mDialog.show();
                    }
                }
                if (cover.equals("profile")){
                    if (snapshot.child("profile_image").exists()){
                        String user_Profile = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(user_Profile).into(dialogImage);
                        mDialog.show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void starCrop() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Image")
                .setAspectRatio(16, 9)
                .setOutputCompressQuality(100)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final File file = new File(SiliCompressor.with(getActivity()).compress(FileUtils.getPath(getActivity(), resultUri), new File(getActivity().getCacheDir(), "temp")));
                Uri outputUri = Uri.fromFile(file);

                if (profilePicIsClicked == true){
                    userProfile.setImageURI(outputUri);

                    uploadProfileImage(outputUri, "Profile Pic");
                }
                if (coverPicIsClicked == true){
                    coverPic.setImageURI(outputUri);
                    uploadCoverImage(outputUri, "Cover Pic");
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadCoverImage(Uri resultUri, String cover_pic) {


        pd.setTitle("Uploading Image");
        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        final StorageReference filepath = uploadCoverPic.child(CurrentUserId + ".jpg");

        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String uri = task.getResult().toString();

                                    databaseReference.child(CurrentUserId).child("cover_pic").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getActivity(), "Upload "+cover_pic, Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                            else
                                            {
                                                Toast.makeText(getActivity(), "Error : "+ task.getException(), Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        }
                                    });
                        }
                    });

                }
                else
                {

                    pd.dismiss();
                    String msg = task.getException().toString();

                    Toast.makeText(getActivity(), "Error : " + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadProfileImage(Uri resultUri, String type) {

        pd.setTitle("Uploading Image");
        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        final StorageReference filepath = UserProfileImageRef.child(CurrentUserId + ".jpg");

        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {

                    filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String uri = task.getResult().toString();

                                    databaseReference.child(CurrentUserId).child("profile_image").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getActivity(), "Upload "+type, Toast.LENGTH_SHORT).show();
                                                pd.dismiss();

                                            }
                                            else
                                            {
                                                Toast.makeText(getActivity(), "Error : "+ task.getException(), Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        }
                                    });
                        }
                    });

                }
                else
                {

                    pd.dismiss();
                    String msg = task.getException().toString();

                    Toast.makeText(getActivity(), "Error : " + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserInfo() {

        DatabaseReference infoREf = FirebaseDatabase.getInstance().getReference("UserDetails");
        infoREf.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profession = snapshot.child("profession").getValue().toString();

                if (profession.equals("Schooling")){
                    String school_name = snapshot.child("school_name").getValue().toString();
                    userinfo.setText(profession+" at "+ school_name);
                    getUserName(CurrentUserId);

                }
                if (profession.equals("Graduation")){
                    String college_name = snapshot.child("college_name").getValue().toString();
                    String course = snapshot.child("course").getValue().toString();

                    userinfo.setText(course+" at "+ college_name);
                    getUserName(CurrentUserId);
                }
                if (profession.equals("Job")){
                    String job_role = snapshot.child("job_role").getValue().toString();
                    userinfo.setText(job_role);
                    getUserName(CurrentUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void getUserName(String userId) {

        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users");
        userNameRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name = snapshot.child("userName").getValue().toString();
                userName.setText(user_name);

//                for profile pic

                if (snapshot.child("profile_image").exists()){
                    String user_Profile = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(user_Profile).into(userProfile);
                }
                else {
                    Picasso.get().load(R.drawable.profile).into(userProfile);
                }

//                for cover pic
                if (snapshot.child("cover_pic").exists()){
                    String user_cover = snapshot.child("cover_pic").getValue().toString();

                    Picasso.get().load(user_cover).into(coverPic);
                }
                else {
                    Picasso.get().load(R.drawable.profile).into(coverPic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}