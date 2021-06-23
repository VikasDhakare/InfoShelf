package com.card.infoshelf.bottomfragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.card.infoshelf.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {


    DatabaseReference Ref, newRef;
    private String CurrentUserId, userId;
    private FirebaseAuth mAuth;
    TextInputEditText user_name, user_email, user_bio, user_college, user_course, user_profession;
    Button savechange;
    Boolean isvalidation = false;
    TextView errztext;

    private ArrayList<String> saoi;
    private LinearLayout l_saoi,clgfield,coursefield;
    private ImageView saoi_add;
    private AutoCompleteTextView s_area_of_interest, s_dream_company, g_area_of_interest, g_dream_company, j_company_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();


        Ref = FirebaseDatabase.getInstance().getReference();

        user_name = findViewById(R.id.u_Name);
        user_email = findViewById(R.id.u_Email);
        user_profession = findViewById(R.id.u_Profession);
        user_bio = findViewById(R.id.u_bio);
        user_college = findViewById(R.id.u_College);
        user_course = findViewById(R.id.u_Course);
        savechange = findViewById(R.id.saveChanges);
        clgfield = findViewById(R.id.collegefieldLayout);
        coursefield = findViewById(R.id.CoursefieldLayout);
        errztext = findViewById(R.id.errortxt);

        s_area_of_interest = findViewById(R.id.s_area_of_interest);
        saoi_add = findViewById(R.id.saoi_add);
        l_saoi = findViewById(R.id.l_saoi);

        saoi = new ArrayList<>();

        // checck profession graduation schooing job
        Ref.child("Users").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GridModel gridModel = snapshot.getValue(GridModel.class);
                    user_name.setText(gridModel.getUserName().toString());
                    user_email.setText(gridModel.getUserEmail().toString());

                    Ref.child("UserDetails").child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            GridModel gridModel = snapshot.getValue(GridModel.class);
                            if (snapshot.child("user_bio").exists()) {
                                user_bio.setText(gridModel.getUser_bio().toString());
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
        Ref.child("UserDetails").child(CurrentUserId).child("AreaOfInterest").addListenerForSingleValueEvent(new ValueEventListener() {
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

        savechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                if (isvalidation == true) {
                    sendChangedDataFirebase();
                }

            }
        });

        String[] ArrayInterest = {"Effective communication", "Teamwork", "Responsibility", "Creativity", "Problem-solving", "Leadership", "Extroversion", "People skills", "Openness", "Adaptability", "Data analysis", "Web analytics", "Wordpress", "Email marketing", "Web scraping", "CRO and A/B Testing", "Data visualization & pattern-finding through critical thinking", "Search Engine and Keyword Optimization", "Project/campaign management", "Social media and mobile marketing", "Paid social media advertisements", "B2B Marketing", "The 4 P-s of Marketing", "Consumer Behavior Drivers", "Brand management", "Creativity", "Copywriting", "Storytelling", "Sales", "CMS Tools", "Six Sigma techniques", "The McKinsey 7s Framework", "Porter’s Five Forces", "PESTEL", "Emotional Intelligence", "Dealing with work-related stress", "Motivation", "Task delegation", "Technological savviness", "People management", "Business Development", "Strategic Management", "Negotiation", "Planning", "Proposal writing", "Problem-solving", "Innovation", "Charisma", "Algorithms", "Analytical Skills", "Big Data", "Calculating", "Compiling Statistics", "Data Analytics", "Data Mining", "Database Design", "Database Management", "Documentation", "Modeling", "Modification", "Needs Analysis", "Quantitative Research", "Quantitative Reports", "Statistical Analysis", "HTML", "Implementation", "Information Technology", "ICT (Information and Communications Technology)", "Infrastructure", "Languages", "Maintenance", "Network Architecture", "Network Security", "Networking", "New Technologies", "Operating Systems", "Programming", "Restoration", "Security", "Servers", "Software", "Solution Delivery", "Storage", "Structures", "Systems Analysis", "Technical Support", "Technology", "Testing", "Tools", "Training", "Troubleshooting", "Usability", "Benchmarking", "Budget Planning", "Engineering", "Fabrication", "Following Specifications", "Operations", "Performance Review", "Project Planning", "Quality Assurance", "Quality Control", "Scheduling", "Task Delegation", "Task Management", "Content Management Systems (CMS)", "Blogging", "Digital Photography", "Digital Media", "Networking", "Search Engine Optimization (SEO)", "Social Media Platforms (Twitter, Facebook, Instagram, LinkedIn, TikTok, Medium, etc.)", "Web Analytics", "Automated Marketing Software", "Client Relations", "Email", "Requirements Gathering", "Research", "Subject Matter Experts (SMEs)", "Technical Documentation", "Information Security", "Microsoft Office Certifications", "Video Creation", "Customer Relationship Management (CRM)", "Productivity Software", "Cloud/SaaS Services", "Database Management", "Telecommunications", "Human Resources Software", "Accounting Software", "Enterprise Resource Planning (ERP) Software", "Database Software", "Query Software", "Blueprint Design", "Medical Billing", "Medical Coding", "Sonography", "Structural Analysis", "Artificial Intelligence (AI)", "Mechanical Maintenance", "Manufacturing", "Inventory Management", "Numeracy", "Information Management", "Hardware Verification Tools and Techniques", "PHP", "TypeScript", "Scala", "Shell", "PowerShell", "Perl", "Haskell", "Kotlin", "Visual Basic .NET", "SQL", "Delphi", "MATLAB", "Groovy", "Lua", "Rust", "Ruby", "HTML and CSS", "Python", "Java", "JavaScript", "Swift", "C++", "C#", "R", "Golang (Go)", "Soccer", "Football", "Cycling", "Running", "Basketball", "Swimming", "Tennis", "Baseball", "Yoga", "Hiking", "Camping", "Fishing", "Trekking", "Mountain climbing", "Gardening", "Drawing", "Painting", "Watercoloring", "Sculpture", "Woodworking", "Dance", "graphics designing", "Front-End development", "Back-End development", "Content Writting", "essay writting", "Event organising", "Hackathon", "Bookkeeping", "Graphic design", "Data analysis", "Microsoft Excel", "Public speaking", "Budgeting", "Teaching", "Research", "Microsoft Word", "Scheduling", "Sales", "Project management", "Office management", "Fundraising", "Writing", "Editing", "Event promotion", "Event planning", "Bilingual", "Management experience", "Communication skills (both written and oral)", "Customer service", "Problem-solving", "Organizational skills", "Inventive", "Handling conflict", "Listening", "Attention to detail", "Collaboration", "Curious", "Diplomacy", "Friendly", "Flexible", "Responsible", "Punctual", "Reliable", "Takes initiative", "Persistent", "Leadership", "Enthusiastic", "Android Studio", "Android Development", "Web Development", "Machine Learning", "Artificial intelligence", "Robots", "Augmented reality", "virtual reality", "Cryptography", "Hacking", "Xml"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(EditProfile.this, android.R.layout.select_dialog_item, ArrayInterest);
        s_area_of_interest.setAdapter(adapter1);

        saoi_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s_area_of_interest.getText().toString().isEmpty()) {
                    String name = s_area_of_interest.getText().toString();
                    saoi.add(name);
                    l_saoi.removeAllViews();
                    for (int i = 0; i < saoi.size(); i++) {
                        String data = saoi.get(i).toString();
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(EditProfile.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_saoi.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saoi.remove(data);
                                l_saoi.removeView(textView);

                            }
                        });
                        s_area_of_interest.setText("");


                    }
                }

            }
        });

    }


    private void printAreaOFInterest() {
        for (int i = 0; i < saoi.size(); i++) {
            String data = saoi.get(i).toString();
            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(EditProfile.this);
            textView.setBackgroundResource(R.drawable.tag_bg);
            textView.setLayoutParams(pa);
            textView.setText(" " + data + " ");
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
            pa.setMargins(5, 5, 5, 5);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setPadding(20, 10, 10, 10);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            l_saoi.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saoi.remove(data);
                    l_saoi.removeView(textView);

                }
            });
            s_area_of_interest.setText("");


        }

    }

    private void sendChangedDataFirebase() {
        HashMap haspmap = new HashMap<>();
        haspmap.put("userName", user_name.getText().toString());
        haspmap.put("userEmail", user_email.getText().toString());

        HashMap hashMap2 = new HashMap();
        hashMap2.put("profession", user_profession.getText().toString());
        hashMap2.put("course", user_course.getText().toString());
        hashMap2.put("college_name", user_college.getText().toString());
        hashMap2.put("user_bio", user_bio.getText().toString());
        hashMap2.put("AreaOfInterest", saoi);


        Ref.child("Users").child(CurrentUserId).updateChildren(haspmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Ref.child("UserDetails").child(CurrentUserId).updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(EditProfile.this, "Data has been changed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void validation() {
        if (user_name.getText().toString().isEmpty() || user_email.getText().toString().isEmpty() || user_profession.getText().toString().isEmpty() || user_course.getText().toString().isEmpty() || user_college.getText().toString().isEmpty() || user_bio.getText().toString().isEmpty() || saoi.size() == 0) {
            errztext.setError("Please fill all the details");
            errztext.setText("Please fill some intrested fields");
        } else if (user_name.getText().toString().isEmpty()) {
            user_name.setError("Please fill profile name");
        } else if (user_email.getText().toString().isEmpty()) {
            user_email.setError("Please fill email");
        } else if (user_profession.getText().toString().isEmpty()) {
            user_profession.setError("Please choose profession");
        } else if (user_course.getText().toString().isEmpty()) {
            user_course.setError("Please fill course/degree");
        } else if (user_college.getText().toString().isEmpty()) {
            user_college.setError("Please fill your college name");
        } else if (user_bio.getText().toString().isEmpty()) {
            user_bio.setError("Please fill your profile heading");

        }else if (saoi.size() == 0){
            errztext.setError("Please fill some intrested fields");
            errztext.setText("Please fill some intrested fields");
        } else {
            isvalidation = true;
        }
    }
}