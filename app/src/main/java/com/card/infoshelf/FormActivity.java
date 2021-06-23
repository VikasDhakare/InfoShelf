package com.card.infoshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class FormActivity extends AppCompatActivity {


    private Spinner prof_spinner;
    private ArrayList<String> arrayList, saoi , sdc , gaoi , gdc , jcn;
    private ArrayAdapter<String> arrayAdapter;
    private LinearLayout schooling_form, graduation_form, job_form, l_saoi, l_sdc, l_gaoi, l_gdc, l_jcn;

    private EditText et_school_name, et_standard;
    private EditText et_college_name, et_course;
    private EditText et_job_experience, et_job_role, past_college_name, qualification;
    private AutoCompleteTextView s_area_of_interest, s_dream_company, g_area_of_interest, g_dream_company, j_company_name;
    private ImageView btn_proceed, saoi_add, sdc_add, gaoi_add, gdc_add, jcn_add;
    private DatabaseReference detailRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);


        prof_spinner = findViewById(R.id.prof_spinner);
        schooling_form = findViewById(R.id.schooling_form);
        graduation_form = findViewById(R.id.graduation_form);
        job_form = findViewById(R.id.job_form);
        et_school_name = findViewById(R.id.et_school_name);
        et_standard = findViewById(R.id.et_standard);
        s_area_of_interest = findViewById(R.id.s_area_of_interest);
        s_dream_company = findViewById(R.id.s_dream_company);

        et_college_name = findViewById(R.id.et_college_name);
        et_course = findViewById(R.id.et_course);
        g_area_of_interest = findViewById(R.id.g_area_of_interest);
        g_dream_company = findViewById(R.id.g_dream_company);

        j_company_name = findViewById(R.id.j_company_name);
        et_job_experience = findViewById(R.id.et_job_experience);
        et_job_role = findViewById(R.id.et_job_role);
        past_college_name = findViewById(R.id.past_college_name);
        qualification = findViewById(R.id.qualification);

        btn_proceed = findViewById(R.id.btn_proceed);
        saoi_add = findViewById(R.id.saoi_add);
        sdc_add = findViewById(R.id.sdc_add);
        gaoi_add = findViewById(R.id.gaoi_add);
        gdc_add = findViewById(R.id.gdc_add);
        jcn_add = findViewById(R.id.jcn_add);

        l_saoi = findViewById(R.id.l_saoi);
        l_sdc = findViewById(R.id.l_sdc);
        l_gaoi = findViewById(R.id.l_gaoi);
        l_gdc = findViewById(R.id.l_gdc);
        l_jcn = findViewById(R.id.l_jcn);

        saoi = new ArrayList<>();
        sdc = new ArrayList<>();
        gaoi = new ArrayList<>();
        gdc = new ArrayList<>();
        jcn = new ArrayList<>();

        arrayList = new ArrayList<>();
        arrayList.add("Schooling");
        arrayList.add("Graduation");
        arrayList.add("Job");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        detailRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(CurrentUserId);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        prof_spinner.setAdapter(arrayAdapter);
        prof_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                if (name.equals("Schooling")) {
                    schooling_form.setVisibility(View.VISIBLE);
                    graduation_form.setVisibility(View.GONE);
                    job_form.setVisibility(View.GONE);
                }
                if (name.equals("Graduation")) {
                    schooling_form.setVisibility(View.GONE);
                    graduation_form.setVisibility(View.VISIBLE);
                    job_form.setVisibility(View.GONE);
                }
                if (name.equals("Job")) {
                    graduation_form.setVisibility(View.GONE);
                    schooling_form.setVisibility(View.GONE);
                    job_form.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] ArrayCompany = {"Reliance Industries", "Indian Oil Corporation", "Oil  Natural Gas Corporation", "State Bank of India", "Bharat Petroleum Corporation", "Tata Motors", "Rajesh Exports", "Tata Consultancy Services", "ICICI Bank", "Larsen  Toubro", "HDFC Bank", "Tata Steel", "Hindalco Industries", "NTPC", "HDFC", "Coal India", "Mahindra  Mahindra", "Infosys", "Bank of Baroda", "Bharti Airtel", "Nayara Energy", "Vedanta", "Axis Bank", "Grasim Industries", "Maruti Suzuki India", "GAIL (India)", "JSW Steel", "HCL Technologies", "Steel Authority of India", "Punjab National Bank", "Motherson Sumi Systems", "Wipro", "Power Finance Corporation", "Canara Bank", "Bajaj Finserv", "ITC", "General Insurance Corporation of India", "Kotak Mahindra Bank", "Bank of India", "Vodafone Idea", "Jindal Steel  Power", "Union Bank of India", "UltraTech Cement", "Power Grid Corporation of India", "Hindustan Unilever", "Tech Mahindra", "Yes Bank", "InterGlobe Aviation", "UPL", "IndusInd Bank", "Sun Pharmaceuticals Industries", "NABARD", "Bajaj Auto", "Tata Power Company", "Hero MotoCorp", "New India Assurance Company", "IFFCO", "Adani Power", "Ambuja Cements", "Central Bank of India", "IDBI Bank", "Avenue Supermarts", "Indian Bank", "Aurobindo Pharma", "Reliance Capital", "Bharat Heavy Electricals", "Reliance Infrastructure", "Titan Company", "Hindustan Aeronautics", "Ashok Leyland", "Asian Paints", "Indian Overseas Bank", "Future Retail", "Sundaram Clayton", "LIC Housing Finance", "TVS Motor Company", "Max Financial Services", "Dr. Reddys Laboratories", "PTC India", "UCO Bank", "IDFC First Bank", "Cipla", "Citibank", "Tata Chemicals", "Tata Communications", "Aditya Birla Capital", "EID Parry (India)", "Shriram Transport Finance Company", "MRF", "Apollo Tyres", "Toyota Kirloskar Motor", "Lupin", "ACC", "Federal Bank", "Piramal Enterprises", "Rail Vikas Nigam", "Exide Industries", "Oil India", "Standard Chartered Bank", "Cadila Healthcare", "Siemens", "Torrent Power", "HSBC", "Adani Ports and Special Economic Zone", "Indiabulls Housing Finance", "SpiceJet", "SIDBI", "National Fertilizer", "Jindal Stainless", "Bank of Maharashtra", "Shree Cement", "Bharat Electronics", "Kalpataru Power Transmission", "Nestle India", "Macrotech Developers", "NMDC", "Bandhan Bank", "Gujarat State Petronet", "Chambal Fertilisers  Chemicals", "Bombay Burmah Trading Corporation", "Godrej Industries", "KEC International", "MM Financial Services", "CESC", "Britannia Industries", "NLC India", "Adani Transmission", "godrej  Boyce Manufacturing Company", "Apollo Hospitals Enterprise", "Jindal Saw", "Adani Enterprises", "Varroc Engineering", "Welspun Corp", "Jaiprakash Associates", "NHPC", "Rain Industries", "Glenmark Pharmaceuticals", "RBL Bank", "Bosch", "Godrej Consumer Products", "Afcons Infrastructure", "Hindustan Construction Company", "Dalmia Bharat", "Rashtriya Chemicals  Fertilizers", "Tata Capital", "Tata Consumer Products", "Dilip Buildcon", "Muthoot Finance", "Eicher Motors", "Edelweiss Financial Services", "Dewan Housing Finance Corporation", "Jubilant Life Sciences", "Havells India", "United Spirits", "GMR Infrastructure", "JSW Energy", "Aditya Birla Fashion  Retail", "Polycab India", "National Aluminium Company", "Dabur India", "Mphasis", "Aster DM Healthcare", "NCC", "jammu and Kashmir Bank", "Punjab  Sind Bank", "South Indian Bank", "JK Tyre  Industries", "Cholamandalam Investment  Finance Co.", "Varun Beverages", "Alkem Laboratories", "Export-Import Bank of India", "Bharti Infratel", "Security  Intelligence Services (India)", "DCM Shriram", "NBCC (India)", "Reliance Power", "Torrent Pharmaceuticals", "Bharat Forge", "Voltas", "marico", "APL Apollo Tubes", "Karnataka Bank", "Mahindra CIE Automotive", "BASF India", "Shree Renuka Sugars", "GSFC", "Whirlpool of India", "Apar Industries", "HUDCO", "Uflex", "Allcargo Logistics", "SRF", "ABB India", "Pidilite Industries", "Deutsche Bank", "Arvind", "DLF", "Aegis Logistics", "Karur Vysya Bank", "IRB Infrastructure Developers", "Birla Corporation", "Raymond", "Vardhman Textiles", "Welspun India", "Endurance Technologies", "Thomas Cook (India)", "Container Corporation Of India", "Amara Raja Batteries", "CEAT", "Biocon", "Prestige Estates Projects", "Bajaj Hindusthan Sugar", "United Breweries", "Berger Paints India", "Future Lifestyle Fashions", "Jain Irrigation Systems", "Shriram City Union Finance", "Prism Johnson", "SREI Infrastructure Finance", "ISGEC Heavy Engineering", "JK Cement", "Escorts", "Sterling  Wilson Solar", "Thermax", "Fullerton India Credit Company", "PNC Infratech", "Supreme Industries", "PC Jeweller", "Divis Laboratories", "Hexaware Technologies", "Surya Roshni", "Mazagon Dock Shipbuilders", "Ircon International", "Manappuram Finance", "Cummins India", "Minda Industries", "Nirma", "The Ramco Cements", "Future Enterprises", "Blue Star", "Zee Entertainment Enterprises", "Sadbhav Engineering", "GNFC", "National Housing Bank", "Kansai Nerolac Paints", "The India Cements", "Hatsun Agro Product", "Team Lease Services", "Ashoka Buildcon", "CG Power  Industrial Solutions", "Sterlite Technologies", "Hinduja Global Solutions", "Oracle Financial Services Software", "KEI Industries", "AGC Networks", "Balkrishna Industries", "AU Small Finance Bank", "Bajaj Electricals", "Walmart India", "Fortis Healthcare", "Ipca Laboratories", "City Union Bank", "Alembic Pharmaceuticals", "IIFL Finance", "Trident", "India Infrastructure Finance Company", "Balrampur Chini Mills", "Sundaram Finance", "Bank of America", "Crompton Greaves Consumer Electrical", "Shipping Corporation of India", "Tube Investments of India", "Indian Hotels Co", "RattanIndia Power", "Deepak Fertilisers  Petrochemicals Corp", "Polyplex Corporation", "Cyient", "Colgate-Palmolive (India)", "JK Lakshmi Cement", "Dixon Technologies (India)", "Schaeffler India", "KRBL", "Triveni Engineering and Industries", "Zensar Technologies", "Coforge", "Deepak Nitrite", "Avanti Feeds", "Aarti Industries", "Atul", "Sobha", "Zuari Agro Chemicals", "Abbott India", "Firstsource Solutions", "Simplex Infrastructures", "LT Foods", "Future Consumer", "Jubilant Foodworks", "Amber Enterprises India", "Tamilnad Mercantile Bank", "Tamil Nadu Newsprint  Papers", "HFCL", "Great Eastern Shipping Company", "DCB Bank", "GSK Pharmaceuticals", "Castrol India", "Gujarat Ambuja Exports", "Sonata Software", "Sun TV Network", "The Fertilizers  Chemicals Travancore", "Gokul Agro Resources", "Trent", "Persistent Systems", "Sundram Fasteners", "Cochin Shipyard", "Shoppers Stop", "Jindal Poly Films", "Jayaswal Neco Industries", "Gayatri Projects", "Bayer CropScience", "Time Technoplast", "Dish TV India", "JK Paper", "KPR Mill", "Jaiprakash Power Ventures", "Kirloskar Oil Engines", "Century Textiles  Industries", "Engineers India", "India Glycols", "Dhampur Sugar Mills", "Oberoi Realty", "JM Financial", "PVR", "PI Industries", "Honeywell Automation India", "BEML", "Birlasoft", "SJVN", "GHCL", "IFB Industries", "Phillips Carbon Black", "Electrotherm (India)", "Godawari Power  Ispat", "GE TD India", "Venkys (India)", "Blue Dart Express", "Graphite India", "Kirloskar Brothers", "Sanofi India", "Bata India", "Narayana Hrudayalaya", "Finolex Industries", "Brigade Enterprises", "Godfrey Phillips India", "AIA Engineering", "Laurus Labs", "Finolex Cables", "PG Hygiene and Health Care", "Heritage Foods", "Force Motors", "Mukand", "Prime Focus", "J Kumar Infraprojects", "Jai Balaji Industries", "Prakash Industries", "Ujjivan Financial Services", "SKF India", "Strides Pharma Science", "Page Industries", "Montecarlo", "Equitas Holdings", "Indiabulls Real Estate", "Dhani Services", "Kajaria Ceramics", "Forbes  Company", "IFCI", "Wockhardt", "ITD Cementation India", "Nava Bharat Ventures", "Garden Silk Mills", "Redington India", "Electrosteel Castings", "Godrej Properties", "JBF Industries", "Filatex India", "RSWM", "Patel Engineering", "EPL", "Gujarat Alkalies  Chemicals", "G4S Secure Solutions (India)", "Minda Corporation", "Gland Pharma", "Transport Corporation of India", "Konkan Railway Corporation", "BGR Energy Systems", "Akzo Nobel India", "Supreme Petrochem", "Granules India", "Emami", "Bharat Dynamics", "Brightcom Group", "Asahi India Glass", "Ratnamani Metals  Tubes", "Maharashtra Seamless", "Ajanta Pharma", "Shankara Building Products", "Carborundum Universal", "Kesoram Industries", "GFL", "Astral Poly Technik", "Orient Cement", "Rites", "West Coast Paper Mills", "Wheels India", "V-Guard Industries", "Usha Martin", "Parag Milk Foods", "Linde India", "Avadh Sugar  Energy", "Va Tech Wabag", "HIL", "Huhtamaki PPL", "GE Power India", "Canon India", "Galaxy Surfactants", "Magma Fincorp", "Lakshmi Vilas Bank", "Suzlon Energy", "KNR Constructions", "Denso Haryana", "Jayant Agro Organics", "Relaxo Footwears", "MEP Infrastructure Developers", "Radico Khaitan", "Sutlej Textiles and Industries", "BNP Paribas", "Siyaram Silk Mills", "Jana Small Finance Bank", "Himatsingka Seide", "Johnson Controls - Hitachi Air Condition. India", "Religare Enterprises", "3M India", "Dalmia Bharat Sugar  Industries", "Nectar Lifescience", "Pfizer", "Transcorp International", "Motilal Oswal Financial Services", "IRCTC", "Hindusthan National Glass  Industries", "HT Media", "Solar Industries India", "NIIT", "Sumitomo Chemical India", "Century Plyboards (India)", "Sify Technologies", "Nilkamal", "Vindhya Telelinks", "Meghmani Organics", "Cosmo Films", "Take Solutions", "DB Corp", "H.G. Infra Engineering", "HEG", "Rane Holdings", "HeidelbergCement India", "Sheela Foam", "Indo Rama Synthetics (India)", "Jindal Worldwide", "Orient Electric", "Power Mech Projects", "KPIT Technologies", "American Express Bank", "Pennar Industries", "Jagran Prakashan", "VRL Logistics", "TVS Srichakra", "Dishman Carbogen Amcis", "Indo Count Industries", "Natco Pharma", "TTK Prestige", "Southern Petrochemical Industries Corporation", "Subros", "KIOCL", "Savita Oil Technologies", "Nahar Spinning Mills", "Sharda Cropchem", "Gokul Refoils and Solvent", "Sarda Energy  Minerals", "HSIL", "Max Healthcare Institute", "Greaves Cotton", "WABCO India", "Tanla Platforms", "Bombay Dyeing  Manufacturing Company", "Sandhar Technologies", "Texmaco Rail  Engineering", "Phoenix Mills", "Kirloskar Industries", "Bilcare", "Ahluwalia Contracts (India)", "IOL Chemicals  Pharmaceuticals", "Flipkart", "Vmware", "Nutanix", "Oyo rooms", "Goibibo", "MakeMyTrip", "Wow! Momo", "Ola Cabs", "AddressHealth", "Zomato", "One97 (Paytm)", "FreshToHome", "‍FreshMenu", "Flyrobe", "Myra", "Cure.Fit", "Dunzo", "Shuttl", "Digit Insurance", "CoolBerg", "Cleardekho", "The Minimalist", "Razorpay", "Nineleaps", "Innov8 Coworking", "Schbang", "Acko General Insurance", "Treebo Hotels", "InCred", "Jumbotail", "DocTalk", "Smallcase", "Vedantu", "Instavans", "Loan Frame", "Overcart", "Flock", "Doctor Insta", "Cowrks", "1Mg", "Cars24", "Dailyhunt", "Ebutor", "Meesho", "MilkBasket", "PharmEasy", "Policybazaar", "Revv", "Sharechat", "Nykaa", "Toppr", "TravelTriangle", "Urban Ladder", "Aisle", "Me", "Bombay Shaving Company", "POPxo", "Zestmoney", "Xpressbees", "Swiggy", "Boeing", "Snapdeal", "Jio", "Arcesium", "Amazon", "Microsoft", "Apple", "Netflix", "Facebook", "Snapdeal", "Ixigo", "Walmart", "Hashedin", "Linkedin", "Zomato", "Swiggy", "Uber", "Urban company", "Dunzo", "Kronos", "Tekio", "Mmt", "Adobe", "Cadence", "Genpact", "Jp morgan", "Salesforce", "Visa", "Phonepe", "Mpl", "Deloitte", "Qualcomm", "Mind tree", "Zoho", "Atlassian", "Skack", "Ibm", "Oracle", "Dell", "Samsung", "Sap", "Goibibo", "Paytm", "Paypal", "Gojek", "Mckinsey", "Vedantu", "Sharechat", "Reliance jio", "Vmware", "Harness", "Kafka", "Morgan Stanley", "Nutanix", "Pratilipi", "Digital guardian", "Flobiz", "Grab", "Coforge ltd", "SanDisk", "Target", "Jaro tech", "Scaler", "McAfee", "Boeing", "Siemens", "Texas instruments", "Commvault", "Veeam", "Radware", "Amdocs", "Innovaccer", "Intuit", "Inmobi", "Curefit", "Infosys specialist programmer", "Arcesium", "Optum", "Publicis sapient", "Exl", "Sony", "Hughes systique technology (hst)", "Tata cliq", "Wells Fargo", "Sandive", "Global logic", "Bny melon", "Ion group"};

        String[] ArrayInterest = {"Effective communication", "Teamwork", "Responsibility", "Creativity", "Problem-solving", "Leadership", "Extroversion", "People skills", "Openness", "Adaptability", "Data analysis", "Web analytics", "Wordpress", "Email marketing", "Web scraping", "CRO and A/B Testing", "Data visualization & pattern-finding through critical thinking", "Search Engine and Keyword Optimization", "Project/campaign management", "Social media and mobile marketing", "Paid social media advertisements", "B2B Marketing", "The 4 P-s of Marketing", "Consumer Behavior Drivers", "Brand management", "Creativity", "Copywriting", "Storytelling", "Sales", "CMS Tools", "Six Sigma techniques", "The McKinsey 7s Framework", "Porter’s Five Forces", "PESTEL", "Emotional Intelligence", "Dealing with work-related stress", "Motivation", "Task delegation", "Technological savviness", "People management", "Business Development", "Strategic Management", "Negotiation", "Planning", "Proposal writing", "Problem-solving", "Innovation", "Charisma", "Algorithms", "Analytical Skills", "Big Data", "Calculating", "Compiling Statistics", "Data Analytics", "Data Mining", "Database Design", "Database Management", "Documentation", "Modeling", "Modification", "Needs Analysis", "Quantitative Research", "Quantitative Reports", "Statistical Analysis", "HTML", "Implementation", "Information Technology", "ICT (Information and Communications Technology)", "Infrastructure", "Languages", "Maintenance", "Network Architecture", "Network Security", "Networking", "New Technologies", "Operating Systems", "Programming", "Restoration", "Security", "Servers", "Software", "Solution Delivery", "Storage", "Structures", "Systems Analysis", "Technical Support", "Technology", "Testing", "Tools", "Training", "Troubleshooting", "Usability", "Benchmarking", "Budget Planning", "Engineering", "Fabrication", "Following Specifications", "Operations", "Performance Review", "Project Planning", "Quality Assurance", "Quality Control", "Scheduling", "Task Delegation", "Task Management", "Content Management Systems (CMS)", "Blogging", "Digital Photography", "Digital Media", "Networking", "Search Engine Optimization (SEO)", "Social Media Platforms (Twitter, Facebook, Instagram, LinkedIn, TikTok, Medium, etc.)", "Web Analytics", "Automated Marketing Software", "Client Relations", "Email", "Requirements Gathering", "Research", "Subject Matter Experts (SMEs)", "Technical Documentation", "Information Security", "Microsoft Office Certifications", "Video Creation", "Customer Relationship Management (CRM)", "Productivity Software", "Cloud/SaaS Services", "Database Management", "Telecommunications", "Human Resources Software", "Accounting Software", "Enterprise Resource Planning (ERP) Software", "Database Software", "Query Software", "Blueprint Design", "Medical Billing", "Medical Coding", "Sonography", "Structural Analysis", "Artificial Intelligence (AI)", "Mechanical Maintenance", "Manufacturing", "Inventory Management", "Numeracy", "Information Management", "Hardware Verification Tools and Techniques", "PHP", "TypeScript", "Scala", "Shell", "PowerShell", "Perl", "Haskell", "Kotlin", "Visual Basic .NET", "SQL", "Delphi", "MATLAB", "Groovy", "Lua", "Rust", "Ruby", "HTML and CSS", "Python", "Java", "JavaScript", "Swift", "C++", "C#", "R", "Golang (Go)", "Soccer", "Football", "Cycling", "Running", "Basketball", "Swimming", "Tennis", "Baseball", "Yoga", "Hiking", "Camping", "Fishing", "Trekking", "Mountain climbing", "Gardening", "Drawing", "Painting", "Watercoloring", "Sculpture", "Woodworking", "Dance", "graphics designing", "Front-End development", "Back-End development", "Content Writting", "essay writting", "Event organising", "Hackathon", "Bookkeeping", "Graphic design", "Data analysis", "Microsoft Excel", "Public speaking", "Budgeting", "Teaching", "Research", "Microsoft Word", "Scheduling", "Sales", "Project management", "Office management", "Fundraising", "Writing", "Editing", "Event promotion", "Event planning", "Bilingual", "Management experience", "Communication skills (both written and oral)", "Customer service", "Problem-solving", "Organizational skills", "Inventive", "Handling conflict", "Listening", "Attention to detail", "Collaboration", "Curious", "Diplomacy", "Friendly", "Flexible", "Responsible", "Punctual", "Reliable", "Takes initiative", "Persistent", "Leadership", "Enthusiastic", "Android Studio", "Android Development", "Web Development", "Machine Learning", "Artificial intelligence", "Robots", "Augmented reality", "virtual reality", "Cryptography", "Hacking", "Xml"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormActivity.this, android.R.layout.select_dialog_item, ArrayCompany);
        s_dream_company.setAdapter(adapter);
        g_dream_company.setAdapter(adapter);
        j_company_name.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FormActivity.this, android.R.layout.select_dialog_item, ArrayInterest);
        s_area_of_interest.setAdapter(adapter1);
        g_area_of_interest.setAdapter(adapter1);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = prof_spinner.getSelectedItem().toString();

                if (name.equals("Schooling")) {
                    if (et_school_name.getText().toString().isEmpty() && et_standard.getText().toString().isEmpty() && saoi.isEmpty())
                    {
                        Toast.makeText(FormActivity.this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
                    }else if (et_school_name.getText().toString().isEmpty() || et_standard.getText().toString().isEmpty() || saoi.isEmpty())
                    {
                        if (et_school_name.getText().toString().isEmpty())
                        {
                            et_school_name.setError("Required Field");
                        }
                        if (et_standard.getText().toString().isEmpty())
                        {
                            et_standard.setError("Required Field");
                        }
                        if (saoi.isEmpty())
                        {
                            s_area_of_interest.setError("Required Field");
                        }
                    }
                    else
                    {
                        String school_name = et_school_name.getText().toString().trim();
                        String standard = et_standard.getText().toString().trim();

                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("school_name", school_name);
                        haspmap.put("standard", standard);
                        haspmap.put("profession", name);
                        haspmap.put("userId", CurrentUserId);

                        detailRef.setValue(haspmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                detailRef.child("AreaOfInterest").setValue(saoi).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                            detailRef.child("CompanyName").setValue(sdc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(FormActivity.this, "Upload", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    }
                                });
                            }
                        });

                    }

                }
                if (name.equals("Graduation")) {
                    if (et_college_name.getText().toString().isEmpty() && et_course.getText().toString().isEmpty() && gaoi.isEmpty())
                    {

                        Toast.makeText(FormActivity.this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
                    }else if (et_college_name.getText().toString().isEmpty() || et_course.getText().toString().isEmpty() || gaoi.isEmpty())
                    {
                        if (et_college_name.getText().toString().isEmpty())
                        {
                            et_college_name.setError("Required Field");
                        }
                        if (et_course.getText().toString().isEmpty())
                        {
                            et_course.setError("Required Field");
                        }
                        if (gaoi.isEmpty())
                        {
                            g_area_of_interest.setError("Required Field");
                        }
                    }
                    else
                    {

                        String college_name = et_college_name.getText().toString().trim();
                        String course = et_course.getText().toString().trim();

                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("college_name", college_name);
                        haspmap.put("course", course);
                        haspmap.put("profession", name);
                        haspmap.put("userId", CurrentUserId);

                        detailRef.setValue(haspmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                detailRef.child("AreaOfInterest").setValue(gaoi).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                            detailRef.child("CompanyName").setValue(gdc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(FormActivity.this, "Upload", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    }
                                });
                            }
                        });
                    }

                }
                if (name.equals("Job")) {
                    if (past_college_name.getText().toString().isEmpty() && qualification.getText().toString().isEmpty() && et_job_role.getText().toString().isEmpty() && et_job_experience.getText().toString().isEmpty() && jcn.isEmpty())
                    {

                        Toast.makeText(FormActivity.this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
                    }else if (past_college_name.getText().toString().isEmpty() || qualification.getText().toString().isEmpty() || et_job_role.getText().toString().isEmpty() || et_job_experience.getText().toString().isEmpty() || jcn.isEmpty())
                    {
                        if (past_college_name.getText().toString().isEmpty())
                        {
                            past_college_name.setError("Required Field");
                        }
                        if (qualification.getText().toString().isEmpty())
                        {
                            qualification.setError("Required Field");
                        }
                        if (et_job_experience.getText().toString().isEmpty())
                        {
                            et_job_experience.setError("Required Field");
                        }
                        if (et_job_role.getText().toString().isEmpty())
                        {
                            et_job_role.setError("Required Field");
                        }
                        if (jcn.isEmpty())
                        {
                            j_company_name.setError("Required Field");
                        }
                    }
                    else
                    {

                        String jo_ex = et_job_experience.getText().toString().trim();
                        String job_role = et_job_role.getText().toString().trim();
                        String past_cl_name = past_college_name.getText().toString().trim();
                        String q = qualification.getText().toString().trim();

                        HashMap<Object, String> haspmap = new HashMap<>();
                        haspmap.put("job_ex", jo_ex);
                        haspmap.put("job_role", job_role);
                        haspmap.put("past_clg_name", past_cl_name);
                        haspmap.put("qualification", q);
                        haspmap.put("profession", name);
                        haspmap.put("userId", CurrentUserId);

                        detailRef.setValue(haspmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                detailRef.child("CompanyName").setValue(jcn).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(FormActivity.this, "Upload", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }


                }

            }
        });

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
                        TextView textView = new TextView(FormActivity.this);
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


        sdc_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s_dream_company.getText().toString().isEmpty()) {
                    String name = s_dream_company.getText().toString();
                    sdc.add(name);
                    l_sdc.removeAllViews();
                    for (int i = 0; i < sdc.size(); i++) {
                        String data = sdc.get(i).toString();
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(FormActivity.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_sdc.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sdc.remove(data);
                                l_sdc.removeView(textView);

                            }
                        });
                        s_dream_company.setText("");


                    }
                }
            }
        });


        gaoi_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!g_area_of_interest.getText().toString().isEmpty()) {
                    String name = g_area_of_interest.getText().toString();
                    gaoi.add(name);
                    l_gaoi.removeAllViews();
                    for (int i = 0; i < gaoi.size(); i++) {
                        String data = gaoi.get(i).toString();
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(FormActivity.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_gaoi.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gaoi.remove(data);
                                l_gaoi.removeView(textView);

                            }
                        });
                        g_area_of_interest.setText("");


                    }
                }
            }
        });


        gdc_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!g_dream_company.getText().toString().isEmpty()) {
                    String name = g_dream_company.getText().toString();
                    gdc.add(name);
                    l_gdc.removeAllViews();
                    for (int i = 0; i < gdc.size(); i++) {
                        String data = gdc.get(i).toString();
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(FormActivity.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_gdc.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gdc.remove(data);
                                l_gdc.removeView(textView);

                            }
                        });
                        g_dream_company.setText("");


                    }
                }
            }
        });


        jcn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!j_company_name.getText().toString().isEmpty()) {
                    String name = j_company_name.getText().toString();
                    jcn.add(name);
                    l_jcn.removeAllViews();
                    for (int i = 0; i < jcn.size(); i++) {
                        String data = jcn.get(i).toString();
                        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(FormActivity.this);
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setLayoutParams(pa);
                        textView.setText(" " + data + " ");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
                        pa.setMargins(5, 5, 5, 5);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setPadding(20, 10, 10, 10);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        l_jcn.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                jcn.remove(data);
                                l_jcn.removeView(textView);

                            }
                        });
                        j_company_name.setText("");


                    }
                }
            }
        });
    }
}