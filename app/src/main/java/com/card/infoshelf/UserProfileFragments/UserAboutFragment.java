package com.card.infoshelf.UserProfileFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.card.infoshelf.R;
import com.card.infoshelf.bottomfragment.GridModel;
import com.card.infoshelf.profileFragments.AboutFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAboutFragment extends AboutFragment {


    DatabaseReference Ref, newRef;
    private String CurrentUserId, userId;
    private FirebaseAuth mAuth;
    TextInputEditText user_name, user_email, user_bio, user_college, user_course, user_profession;
    Button savechange;

    private ArrayList<String> saoi;
    private LinearLayout l_saoi;
    private ImageView saoi_add;
    private AutoCompleteTextView s_area_of_interest, s_dream_company, g_area_of_interest, g_dream_company, j_company_name;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_about, container, false);
        String userId = getActivity().getIntent().getStringExtra("userid");


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();


        Ref = FirebaseDatabase.getInstance().getReference();
        user_name = view.findViewById(R.id.u_Name);
        user_email = view.findViewById(R.id.u_Email);
        user_profession = view.findViewById(R.id.u_Profession);
        user_bio = view.findViewById(R.id.u_bio);
        user_college = view.findViewById(R.id.u_College);
        user_course = view.findViewById(R.id.u_Course);
        savechange = view.findViewById(R.id.saveChanges);

        s_area_of_interest = view.findViewById(R.id.s_area_of_interest);
        saoi_add = view.findViewById(R.id.saoi_add);
        l_saoi = view.findViewById(R.id.l_saoi);

        saoi = new ArrayList<>();

        Ref.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GridModel gridModel = snapshot.getValue(GridModel.class);
                    user_name.setText(gridModel.getUserName().toString());
                    user_email.setText(gridModel.getUserEmail().toString());

                    Ref.child("UserDetails").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            GridModel gridModel = snapshot.getValue(GridModel.class);
                            if (snapshot.child("user_bio").exists()) {

                                user_bio.setText(gridModel.getUser_bio().toString());
                            } else {
                                user_bio.setVisibility(View.GONE);
                            }
                            if (snapshot.child("profession").getValue().equals("Graduation")) {
                                user_profession.setText(gridModel.getProfession().toString());
                                user_college.setText(gridModel.getCollege_name().toString());
                                user_course.setText(gridModel.getCourse().toString());

                            } else if (snapshot.child("profession").getValue().equals("Schooling")) {
                                user_profession.setText(gridModel.getProfession().toString());
                                user_college.setText(gridModel.getSchool_name().toString());
                                user_course.setText(gridModel.getStandard().toString());

                            } else {
                                user_profession.setText(gridModel.getProfession().toString());
                                user_college.setText(gridModel.getJobCompany().toString());
                                user_course.setVisibility(View.GONE);

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


        // getting interest area node data in to an arraylist
        Ref.child("UserDetails").child(userId).child("AreaOfInterest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                    saoi.add(snapshot.child(i + "").getValue().toString());
                }
                printAreaOFInterest();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void printAreaOFInterest() {
        for (int i = 0; i < saoi.size(); i++) {
            String data = saoi.get(i).toString();
            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getContext());
            textView.setBackgroundResource(R.drawable.tag_bg);
            textView.setLayoutParams(pa);
            textView.setText(" " + data + " ");
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
            pa.setMargins(5, 5, 5, 5);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setPadding(20, 10, 10, 10);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            l_saoi.addView(textView);


        }

    }
}
