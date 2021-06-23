package com.card.infoshelf.bottomfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.card.infoshelf.MainActivity;
import com.card.infoshelf.R;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.file.Files;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddTimeline extends Fragment {
    Spinner RelateSpin, ctcSpin;
    LinearLayout Article_snap, bottom_Text_tags, personTaglayout,Articledetails;
    ImageView Profile_avtar, doc_image, ImageBtn, VideoBtn, FileBtn, TagBtn, Deletebtn,backbutton;
    TextView Profilename, Profileprofession, Error_text, hashBtn, PostBtn;
    static TextView ShowTags1, ShowTags2, ShowTags3;
    EditText Articleheading, Articlewritebox,CtcNumber,JobProfile;
    String RelateSpinValue, CompanySpinValue, article_headingValue, article_box_Value,fileExt;
    String FileType ="none",fileName="none";
    TextView docname;
    TextView docsize;
    static TextView showTag;
    String title, extension,compnayNode;
    Boolean checkForUpload = false;
    Boolean validation = false;
    boolean isAllFieldCheckedTextMedia = false;
    boolean isAllFieldCheckedSpin = false;
    DatabaseReference Ref,node,CompanykRef,userDataRef;
    StorageReference storageReference;
    StorageReference reference;
    ProgressDialog pd;
    BottomSheetDialog bottomSheetDialog;
    AutoCompleteTextView autoCompleteTextView;
    long k;
    List<TagModel> contacts = new ArrayList<>();
    static List<String> PersonTagsClone;
    AnstronCoreHelper anstronCoreHelper;
    Uri uri;
    Uri uriCompressed;
    View view;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_video = 2;
    private static int RESULT_LOAD_File = 3;

    //================
    private EditText userinput;

    private TextView done;
    private List<String> PersonTags;
    private RecyclerView recyclerView;
    private TagAdapter tagAdapter;

    //=========


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.fragment_add_timeline, container, false);
        //casting all variables
        ShowTags1 = view.findViewById(R.id.ShoeTags1);
        ShowTags2 = view.findViewById(R.id.ShoeTags2);
        ShowTags3 = view.findViewById(R.id.ShoeTags3);
        //bottom_Text_tags = findViewById(R.id.TextBottomRecycler);
        Error_text = view.findViewById(R.id.errorText);
        Profile_avtar = view.findViewById(R.id.AvatarImg);
        Article_snap = view.findViewById(R.id.Gallery);
        Profilename = view.findViewById(R.id.PersonName);
//        Profileprofession = view.findViewById(R.id.PersonProf);
        ImageBtn = view.findViewById(R.id.image_btn);
        VideoBtn = view.findViewById(R.id.video_btn);
        FileBtn = view.findViewById(R.id.file_btn);
        TagBtn = view.findViewById(R.id.Tag_btn);
        PostBtn = view.findViewById(R.id.Post_btn);
        docname = view.findViewById(R.id.docname);
        RelateSpin = view.findViewById(R.id.relate_spinner);
        autoCompleteTextView = view.findViewById(R.id.company_spinner);
        Articlewritebox = view.findViewById(R.id.Article_writebox);
        backbutton = view.findViewById(R.id.backBtn);
        CtcNumber = view.findViewById(R.id.ctc_number);
        JobProfile = view.findViewById(R.id.job_title);
        ctcSpin = view.findViewById(R.id.ctc_spinner);
        Articledetails = view.findViewById(R.id.Article_details);
        anstronCoreHelper = new AnstronCoreHelper(getActivity());


        // on back button press

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //-------------------------firebase references-----------------

        Ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        //-------------------------BottomSheet dialog--------------------------------//
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetStyle);
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.activity_tag_person, view.findViewById(R.id.personTagLayout));
        bottomSheetDialog.setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
//setting profile Name and image on top bar==============================
        Ref = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String username = dataSnapshot.child("userName").getValue().toString();

                if(dataSnapshot.child("profile_image").exists()){
                    String userImage = dataSnapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(userImage).into(Profile_avtar);
                }

                Profilename.setText(username);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        ///------------------------Two spinners code starts-------------------------///


        String[] ArrayInterest = {"Casual", "Placement", "Internship"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> interestAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, ArrayInterest);
        //set the spinners adapter to the previously created one.
        RelateSpin.setAdapter(interestAdapter);
//        interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //-------------------------------Company Spinner code----------------------------//

        String[] ArrayCompany = {"None", "Reliance Industries", "Indian Oil Corporation", "Oil  Natural Gas Corporation", "State Bank of India", "Bharat Petroleum Corporation", "Tata Motors", "Rajesh Exports", "Tata Consultancy Services", "ICICI Bank", "Larsen Toubro", "HDFC Bank", "Tata Steel", "Hindalco Industries", "NTPC", "HDFC", "Coal India", "Mahindra  Mahindra", "Infosys", "Bank of Baroda", "Bharti Airtel", "Nayara Energy", "Vedanta", "Axis Bank", "Grasim Industries", "Maruti Suzuki India", "GAIL (India)", "JSW Steel", "HCL Technologies", "Steel Authority of India", "Punjab National Bank", "Motherson Sumi Systems", "Wipro", "Power Finance Corporation", "Canara Bank", "Bajaj Finserv", "ITC", "General Insurance Corporation of India", "Kotak Mahindra Bank", "Bank of India", "Vodafone Idea", "Jindal Steel  Power", "Union Bank of India", "UltraTech Cement", "Power Grid Corporation of India", "Hindustan Unilever", "Tech Mahindra", "Yes Bank", "InterGlobe Aviation", "UPL", "IndusInd Bank", "Sun Pharmaceuticals Industries", "NABARD", "Bajaj Auto", "Tata Power Company", "Hero MotoCorp", "New India Assurance Company", "IFFCO", "Adani Power", "Ambuja Cements", "Central Bank of India", "IDBI Bank", "Avenue Supermarts", "Indian Bank", "Aurobindo Pharma", "Reliance Capital", "Bharat Heavy Electricals", "Reliance Infrastructure", "Titan Company", "Hindustan Aeronautics", "Ashok Leyland", "Asian Paints", "Indian Overseas Bank", "Future Retail", "Sundaram Clayton", "LIC Housing Finance", "TVS Motor Company", "Max Financial Services", "Dr. Reddys Laboratories", "PTC India", "UCO Bank", "IDFC First Bank", "Cipla", "Citibank", "Tata Chemicals", "Tata Communications", "Aditya Birla Capital", "EID Parry (India)", "Shriram Transport Finance Company", "MRF", "Apollo Tyres", "Toyota Kirloskar Motor", "Lupin", "ACC", "Federal Bank", "Piramal Enterprises", "Rail Vikas Nigam", "Exide Industries", "Oil India", "Standard Chartered Bank", "Cadila Healthcare", "Siemens", "Torrent Power", "HSBC", "Adani Ports and Special Economic Zone", "Indiabulls Housing Finance", "SpiceJet", "SIDBI", "National Fertilizer", "Jindal Stainless", "Bank of Maharashtra", "Shree Cement", "Bharat Electronics", "Kalpataru Power Transmission", "Nestle India", "Macrotech Developers", "NMDC", "Bandhan Bank", "Gujarat State Petronet", "Chambal Fertilisers  Chemicals", "Bombay Burmah Trading Corporation", "Godrej Industries", "KEC International", "MM Financial Services", "CESC", "Britannia Industries", "NLC India", "Adani Transmission", "godrej  Boyce Manufacturing Company", "Apollo Hospitals Enterprise", "Jindal Saw", "Adani Enterprises", "Varroc Engineering", "Welspun Corp", "Jaiprakash Associates", "NHPC", "Rain Industries", "Glenmark Pharmaceuticals", "RBL Bank", "Bosch", "Godrej Consumer Products", "Afcons Infrastructure", "Hindustan Construction Company", "Dalmia Bharat", "Rashtriya Chemicals  Fertilizers", "Tata Capital", "Tata Consumer Products", "Dilip Buildcon", "Muthoot Finance", "Eicher Motors", "Edelweiss Financial Services", "Dewan Housing Finance Corporation", "Jubilant Life Sciences", "Havells India", "United Spirits", "GMR Infrastructure", "JSW Energy", "Aditya Birla Fashion  Retail", "Polycab India", "National Aluminium Company", "Dabur India", "Mphasis", "Aster DM Healthcare", "NCC", "jammu and Kashmir Bank", "Punjab  Sind Bank", "South Indian Bank", "JK Tyre  Industries", "Cholamandalam Investment  Finance Co.", "Varun Beverages", "Alkem Laboratories", "Export-Import Bank of India", "Bharti Infratel", "Security  Intelligence Services (India)", "DCM Shriram", "NBCC (India)", "Reliance Power", "Torrent Pharmaceuticals", "Bharat Forge", "Voltas", "marico", "APL Apollo Tubes", "Karnataka Bank", "Mahindra CIE Automotive", "BASF India", "Shree Renuka Sugars", "GSFC", "Whirlpool of India", "Apar Industries", "HUDCO", "Uflex", "Allcargo Logistics", "SRF", "ABB India", "Pidilite Industries", "Deutsche Bank", "Arvind", "DLF", "Aegis Logistics", "Karur Vysya Bank", "IRB Infrastructure Developers", "Birla Corporation", "Raymond", "Vardhman Textiles", "Welspun India", "Endurance Technologies", "Thomas Cook (India)", "Container Corporation Of India", "Amara Raja Batteries", "CEAT", "Biocon", "Prestige Estates Projects", "Bajaj Hindusthan Sugar", "United Breweries", "Berger Paints India", "Future Lifestyle Fashions", "Jain Irrigation Systems", "Shriram City Union Finance", "Prism Johnson", "SREI Infrastructure Finance", "ISGEC Heavy Engineering", "JK Cement", "Escorts", "Sterling  Wilson Solar", "Thermax", "Fullerton India Credit Company", "PNC Infratech", "Supreme Industries", "PC Jeweller", "Divis Laboratories", "Hexaware Technologies", "Surya Roshni", "Mazagon Dock Shipbuilders", "Ircon International", "Manappuram Finance", "Cummins India", "Minda Industries", "Nirma", "The Ramco Cements", "Future Enterprises", "Blue Star", "Zee Entertainment Enterprises", "Sadbhav Engineering", "GNFC", "National Housing Bank", "Kansai Nerolac Paints", "The India Cements", "Hatsun Agro Product", "Team Lease Services", "Ashoka Buildcon", "CG Power  Industrial Solutions", "Sterlite Technologies", "Hinduja Global Solutions", "Oracle Financial Services Software", "KEI Industries", "AGC Networks", "Balkrishna Industries", "AU Small Finance Bank", "Bajaj Electricals", "Walmart India", "Fortis Healthcare", "Ipca Laboratories", "City Union Bank", "Alembic Pharmaceuticals", "IIFL Finance", "Trident", "India Infrastructure Finance Company", "Balrampur Chini Mills", "Sundaram Finance", "Bank of America", "Crompton Greaves Consumer Electrical", "Shipping Corporation of India", "Tube Investments of India", "Indian Hotels Co", "RattanIndia Power", "Deepak Fertilisers  Petrochemicals Corp", "Polyplex Corporation", "Cyient", "Colgate-Palmolive (India)", "JK Lakshmi Cement", "Dixon Technologies (India)", "Schaeffler India", "KRBL", "Triveni Engineering and Industries", "Zensar Technologies", "Coforge", "Deepak Nitrite", "Avanti Feeds", "Aarti Industries", "Atul", "Sobha", "Zuari Agro Chemicals", "Abbott India", "Firstsource Solutions", "Simplex Infrastructures", "LT Foods", "Future Consumer", "Jubilant Foodworks", "Amber Enterprises India", "Tamilnad Mercantile Bank", "Tamil Nadu Newsprint  Papers", "HFCL", "Great Eastern Shipping Company", "DCB Bank", "GSK Pharmaceuticals", "Castrol India", "Gujarat Ambuja Exports", "Sonata Software", "Sun TV Network", "The Fertilizers  Chemicals Travancore", "Gokul Agro Resources", "Trent", "Persistent Systems", "Sundram Fasteners", "Cochin Shipyard", "Shoppers Stop", "Jindal Poly Films", "Jayaswal Neco Industries", "Gayatri Projects", "Bayer CropScience", "Time Technoplast", "Dish TV India", "JK Paper", "KPR Mill", "Jaiprakash Power Ventures", "Kirloskar Oil Engines", "Century Textiles  Industries", "Engineers India", "India Glycols", "Dhampur Sugar Mills", "Oberoi Realty", "JM Financial", "PVR", "PI Industries", "Honeywell Automation India", "BEML", "Birlasoft", "SJVN", "GHCL", "IFB Industries", "Phillips Carbon Black", "Electrotherm (India)", "Godawari Power  Ispat", "GE TD India", "Venkys (India)", "Blue Dart Express", "Graphite India", "Kirloskar Brothers", "Sanofi India", "Bata India", "Narayana Hrudayalaya", "Finolex Industries", "Brigade Enterprises", "Godfrey Phillips India", "AIA Engineering", "Laurus Labs", "Finolex Cables", "PG Hygiene and Health Care", "Heritage Foods", "Force Motors", "Mukand", "Prime Focus", "J Kumar Infraprojects", "Jai Balaji Industries", "Prakash Industries", "Ujjivan Financial Services", "SKF India", "Strides Pharma Science", "Page Industries", "Montecarlo", "Equitas Holdings", "Indiabulls Real Estate", "Dhani Services", "Kajaria Ceramics", "Forbes  Company", "IFCI", "Wockhardt", "ITD Cementation India", "Nava Bharat Ventures", "Garden Silk Mills", "Redington India", "Electrosteel Castings", "Godrej Properties", "JBF Industries", "Filatex India", "RSWM", "Patel Engineering", "EPL", "Gujarat Alkalies  Chemicals", "G4S Secure Solutions (India)", "Minda Corporation", "Gland Pharma", "Transport Corporation of India", "Konkan Railway Corporation", "BGR Energy Systems", "Akzo Nobel India", "Supreme Petrochem", "Granules India", "Emami", "Bharat Dynamics", "Brightcom Group", "Asahi India Glass", "Ratnamani Metals  Tubes", "Maharashtra Seamless", "Ajanta Pharma", "Shankara Building Products", "Carborundum Universal", "Kesoram Industries", "GFL", "Astral Poly Technik", "Orient Cement", "Rites", "West Coast Paper Mills", "Wheels India", "V-Guard Industries", "Usha Martin", "Parag Milk Foods", "Linde India", "Avadh Sugar  Energy", "Va Tech Wabag", "HIL", "Huhtamaki PPL", "GE Power India", "Canon India", "Galaxy Surfactants", "Magma Fincorp", "Lakshmi Vilas Bank", "Suzlon Energy", "KNR Constructions", "Denso Haryana", "Jayant Agro Organics", "Relaxo Footwears", "MEP Infrastructure Developers", "Radico Khaitan", "Sutlej Textiles and Industries", "BNP Paribas", "Siyaram Silk Mills", "Jana Small Finance Bank", "Himatsingka Seide", "Johnson Controls - Hitachi Air Condition. India", "Religare Enterprises", "3M India", "Dalmia Bharat Sugar  Industries", "Nectar Lifescience", "Pfizer", "Transcorp International", "Motilal Oswal Financial Services", "IRCTC", "Hindusthan National Glass  Industries", "HT Media", "Solar Industries India", "NIIT", "Sumitomo Chemical India", "Century Plyboards (India)", "Sify Technologies", "Nilkamal", "Vindhya Telelinks", "Meghmani Organics", "Cosmo Films", "Take Solutions", "DB Corp", "H.G. Infra Engineering", "HEG", "Rane Holdings", "HeidelbergCement India", "Sheela Foam", "Indo Rama Synthetics (India)", "Jindal Worldwide", "Orient Electric", "Power Mech Projects", "KPIT Technologies", "American Express Bank", "Pennar Industries", "Jagran Prakashan", "VRL Logistics", "TVS Srichakra", "Dishman Carbogen Amcis", "Indo Count Industries", "Natco Pharma", "TTK Prestige", "Southern Petrochemical Industries Corporation", "Subros", "KIOCL", "Savita Oil Technologies", "Nahar Spinning Mills", "Sharda Cropchem", "Gokul Refoils and Solvent", "Sarda Energy  Minerals", "HSIL", "Max Healthcare Institute", "Greaves Cotton", "WABCO India", "Tanla Platforms", "Bombay Dyeing  Manufacturing Company", "Sandhar Technologies", "Texmaco Rail  Engineering", "Phoenix Mills", "Kirloskar Industries", "Bilcare", "Ahluwalia Contracts (India)", "IOL Chemicals  Pharmaceuticals", "Flipkart", "Vmware", "Nutanix", "Oyo rooms", "Goibibo", "MakeMyTrip", "Wow! Momo", "Ola Cabs", "AddressHealth", "Zomato", "One97 (Paytm)", "FreshToHome", "‍FreshMenu", "Flyrobe", "Myra", "Cure.Fit", "Dunzo", "Shuttl", "Digit Insurance", "CoolBerg", "Cleardekho", "The Minimalist", "Razorpay", "Nineleaps", "Innov8 Coworking", "Schbang", "Acko General Insurance", "Treebo Hotels", "InCred", "Jumbotail", "DocTalk", "Smallcase", "Vedantu", "Instavans", "Loan Frame", "Overcart", "Flock", "Doctor Insta", "Cowrks", "1Mg", "Cars24", "Dailyhunt", "Ebutor", "Meesho", "MilkBasket", "PharmEasy", "Policybazaar", "Revv", "Sharechat", "Nykaa", "Toppr", "TravelTriangle", "Urban Ladder", "Aisle", "Me", "Bombay Shaving Company", "POPxo", "Zestmoney", "Xpressbees", "Swiggy", "Boeing", "Snapdeal", "Jio", "Arcesium", "Amazon", "Microsoft", "Apple", "Netflix", "Facebook", "Snapdeal", "Ixigo", "Walmart", "Hashedin", "Linkedin", "Zomato", "Swiggy", "Uber", "Urban company", "Dunzo", "Kronos", "Tekio", "Mmt", "Adobe", "Cadence", "Genpact", "Jp morgan", "Salesforce", "Visa", "Phonepe", "Mpl", "Deloitte", "Qualcomm", "Mind tree", "Zoho", "Atlassian", "Skack", "Ibm", "Oracle", "Dell", "Samsung", "Sap", "Goibibo", "Paytm", "Paypal", "Gojek", "Mckinsey", "Vedantu", "Sharechat", "Reliance jio", "Vmware", "Harness", "Kafka", "Morgan Stanley", "Nutanix", "Pratilipi", "Digital guardian", "Flobiz", "Grab", "Coforge ltd", "SanDisk", "Target", "Jaro tech", "Scaler", "McAfee", "Boeing", "Siemens", "Texas instruments", "Commvault", "Veeam", "Radware", "Amdocs", "Innovaccer", "Intuit", "Inmobi", "Curefit", "Infosys specialist programmer", "Arcesium", "Optum", "Publicis sapient", "Exl", "Sony", "Hughes systique technology (hst)", "Tata cliq", "Wells Fargo", "Sandive", "Global logic", "Bny melon", "Ion group"};

        ArrayAdapter<String> company_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, ArrayCompany);
        //set the spinners adapter to the previously created one.

        String[] CasualInterest = {"Effective communication", "Teamwork", "Responsibility", "Creativity", "Problem-solving", "Leadership", "Extroversion", "People skills", "Openness", "Adaptability", "Data analysis", "Web analytics", "Wordpress", "Email marketing", "Web scraping", "CRO and A/B Testing", "Data visualization & pattern-finding through critical thinking", "Search Engine and Keyword Optimization", "Project/campaign management", "Social media and mobile marketing", "Paid social media advertisements", "B2B Marketing", "The 4 P-s of Marketing", "Consumer Behavior Drivers", "Brand management", "Creativity", "Copywriting", "Storytelling", "Sales", "CMS Tools", "Six Sigma techniques", "The McKinsey 7s Framework", "Porter’s Five Forces", "PESTEL", "Emotional Intelligence", "Dealing with work-related stress", "Motivation", "Task delegation", "Technological savviness", "People management", "Business Development", "Strategic Management", "Negotiation", "Planning", "Proposal writing", "Problem-solving", "Innovation", "Charisma", "Algorithms", "Analytical Skills", "Big Data", "Calculating", "Compiling Statistics", "Data Analytics", "Data Mining", "Database Design", "Database Management", "Documentation", "Modeling", "Modification", "Needs Analysis", "Quantitative Research", "Quantitative Reports", "Statistical Analysis", "HTML", "Implementation", "Information Technology", "ICT (Information and Communications Technology)", "Infrastructure", "Languages", "Maintenance", "Network Architecture", "Network Security", "Networking", "New Technologies", "Operating Systems", "Programming", "Restoration", "Security", "Servers", "Software", "Solution Delivery", "Storage", "Structures", "Systems Analysis", "Technical Support", "Technology", "Testing", "Tools", "Training", "Troubleshooting", "Usability", "Benchmarking", "Budget Planning", "Engineering", "Fabrication", "Following Specifications", "Operations", "Performance Review", "Project Planning", "Quality Assurance", "Quality Control", "Scheduling", "Task Delegation", "Task Management", "Content Management Systems (CMS)", "Blogging", "Digital Photography", "Digital Media", "Networking", "Search Engine Optimization (SEO)", "Social Media Platforms (Twitter, Facebook, Instagram, LinkedIn, TikTok, Medium, etc.)", "Web Analytics", "Automated Marketing Software", "Client Relations", "Email", "Requirements Gathering", "Research", "Subject Matter Experts (SMEs)", "Technical Documentation", "Information Security", "Microsoft Office Certifications", "Video Creation", "Customer Relationship Management (CRM)", "Productivity Software", "Cloud/SaaS Services", "Database Management", "Telecommunications", "Human Resources Software", "Accounting Software", "Enterprise Resource Planning (ERP) Software", "Database Software", "Query Software", "Blueprint Design", "Medical Billing", "Medical Coding", "Sonography", "Structural Analysis", "Artificial Intelligence (AI)", "Mechanical Maintenance", "Manufacturing", "Inventory Management", "Numeracy", "Information Management", "Hardware Verification Tools and Techniques", "PHP", "TypeScript", "Scala", "Shell", "PowerShell", "Perl", "Haskell", "Kotlin", "Visual Basic .NET", "SQL", "Delphi", "MATLAB", "Groovy", "Lua", "Rust", "Ruby", "HTML and CSS", "Python", "Java", "JavaScript", "Swift", "C++", "C#", "R", "Golang (Go)", "Soccer", "Football", "Cycling", "Running", "Basketball", "Swimming", "Tennis", "Baseball", "Yoga", "Hiking", "Camping", "Fishing", "Trekking", "Mountain climbing", "Gardening", "Drawing", "Painting", "Watercoloring", "Sculpture", "Woodworking", "Dance", "graphics designing", "Front-End development", "Back-End development", "Content Writting", "essay writting", "Event organising", "Hackathon", "Bookkeeping", "Graphic design", "Data analysis", "Microsoft Excel", "Public speaking", "Budgeting", "Teaching", "Research", "Microsoft Word", "Scheduling", "Sales", "Project management", "Office management", "Fundraising", "Writing", "Editing", "Event promotion", "Event planning", "Bilingual", "Management experience", "Communication skills (both written and oral)", "Customer service", "Problem-solving", "Organizational skills", "Inventive", "Handling conflict", "Listening", "Attention to detail", "Collaboration", "Curious", "Diplomacy", "Friendly", "Flexible", "Responsible", "Punctual", "Reliable", "Takes initiative", "Persistent", "Leadership", "Enthusiastic", "Android Studio", "Android Development", "Web Development", "Machine Learning", "Artificial intelligence", "Robots", "Augmented reality", "virtual reality", "Cryptography", "Hacking", "Xml"};
        ArrayAdapter<String> CasualInterestAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,CasualInterest);


        ///------------------------Two spinners code ends-------------------------///

        //--------------------------- ctc spinner------//

        String[] ArrayCtc = {"none","Lpa","Crpa","Monthly"};
        ArrayAdapter<String> ctcAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,ArrayCtc);
        ctcSpin.setAdapter(ctcAdapter);

//-----------------------------ArrayInterest clicks---------------
        RelateSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (RelateSpin.getSelectedItem().toString().equals("Casual")) {
                    autoCompleteTextView.setHint("Related to");
                    autoCompleteTextView.setAdapter(CasualInterestAdapter);
                    ctcSpin.setSelection(0);
                    Articledetails.setVisibility(View.GONE);

                } else if (RelateSpin.getSelectedItem().toString().equals("Placement")) {
                    autoCompleteTextView.setEnabled(true);
                    autoCompleteTextView.setHint("Company Name");
                    autoCompleteTextView.setAdapter(company_adapter);
                    Articledetails.setVisibility(View.VISIBLE);
                } else if (RelateSpin.getSelectedItem().toString().equals("Internship")) {
                    autoCompleteTextView.setEnabled(true);
                    autoCompleteTextView.setHint("Company Name");
                    autoCompleteTextView.setAdapter(company_adapter);
                    Articledetails.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //------------------Grabbing Images on click-------------------------//

        ImageBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }

        });

        VideoBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, RESULT_LOAD_video);
            }
        });
        FileBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                String[] mimeTypes =
                        {"application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-powerpoint",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","application/vnd.ms-excel"
                                ,"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/zip",
                                "application/vnd.rar"};
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                startActivityForResult(Intent.createChooser(intent, "ChooseFile"), RESULT_LOAD_File);

            }
        });

        //-----------setting recyclerview in tag btn------------

//========************=============***************testing code ===================*****************========//
//========************=============***************testing code ===================*****************========//
//========************=============***************testing code ===================*****************========//

        recyclerView = bottomSheetDialog.findViewById(R.id.tabRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        tagAdapter = new TagAdapter(contacts);
        recyclerView.setAdapter(tagAdapter);

        userinput = bottomSheetDialog.findViewById(R.id.TypeChipsTag);
        done = bottomSheetDialog.findViewById(R.id.DoneTag);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Data = userinput.getText().toString();
                PersonTags = tagAdapter.getArrayTags();
                PersonTagsClone = PersonTags;
                if (!PersonTagsClone.isEmpty()) {
                    if (PersonTagsClone.size() > 1) {

                        // Read from the database

                        Ref = FirebaseDatabase.getInstance().getReference("Users").child(PersonTags.get(0)).child("userName");
                        Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                String value = dataSnapshot.getValue().toString();
                                ShowTags1.setText(value + ",");
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value

                            }
                        });

                        Ref = FirebaseDatabase.getInstance().getReference("Users").child(PersonTags.get(1)).child("userName");
                        Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                String value = dataSnapshot.getValue().toString();
                                ShowTags2.setText(value);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value

                            }
                        });
                        if ((PersonTagsClone.size() - 2) > 0) {
                            ShowTags3.setText(" and " + (PersonTagsClone.size() - 2) + " others");
                        }else {
                            ShowTags3.setText("");
                        }

                    } else {

                        Ref = FirebaseDatabase.getInstance().getReference("Users").child(PersonTags.get(0)).child("userName");
                        Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                String value = dataSnapshot.getValue().toString();
                                ShowTags1.setText(value);
                                ShowTags2.setText("");
                                ShowTags3.setText("");
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value

                            }
                        });
                    }
                }else {
                    ShowTags1.setText("");
                    ShowTags2.setText("");
                    ShowTags3.setText("");
                }

                bottomSheetDialog.dismiss();
            }
        });


        Ref = FirebaseDatabase.getInstance().getReference("Friends").child(CurrentUserId);
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        String id = npsnapshot.getKey();
                        Ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
                        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                TagModel l = snapshot.getValue(TagModel.class);
                                contacts.add(l);
                                tagAdapter.notifyDataSetChanged();
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
        userinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userInput = s.toString();
                List<TagModel> newContacts = new ArrayList<>();

                for (TagModel contacts : contacts) {
                    if (contacts.getUserName().toLowerCase().contains(userInput.toLowerCase())) {
                        newContacts.add(contacts);
                    }
                }
                tagAdapter.filterList(newContacts);

            }
        });


//========************=============***************testing code ===================*****************========//
//========************=============***************testing code ===================*****************========//
//========************=============***************testing code ===================*****************========//


        TagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bottomSheetDialog.show(getActivity().getSupportFragmentManager(),"Tag");
                bottomSheetDialog.show();
            }
        });


        Deletebtn = view.findViewById(R.id.deleteDoc);
        Deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docname.setText(null);
                uri = null;
                doc_image.setVisibility(View.GONE);
                Deletebtn.setVisibility(View.GONE);
            }
        });
        //--------------------------final post upload button click----------------------//
        //--------------------------final post upload button click----------------------//
        //--------------------------final post upload button click----------------------//

        PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Error_text.setText("");

                validateHeadNText();
                if (isAllFieldCheckedTextMedia == true) {
                    if (RelateSpin.getSelectedItem().toString().equals("Casual")){
                        if (uri == null) {
                            uploadData("none");
                        } else {
                            uploadAllDataToFirebase();
                        }
                    }else {
                        validateSpinner();
                        if (isAllFieldCheckedSpin == true){
                            if (uri == null) {
                                uploadData("none");

                            } else {
                                uploadAllDataToFirebase();
                            }
                        }

                    }


                }

            }
        });

        //check for tags in database present or not at Users node------------------------//


        return view;
    }


    private void uploadAllDataToFirebase() {
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please Wait..");
        pd.setMessage("Uploading File");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        reference = storageReference.child("PostedFiles/" + "(" + String.valueOf(System.currentTimeMillis()) + ")");

        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri data = uriTask.getResult();
                uploadData(String.valueOf(data));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String url) {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        if (uri == null){
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Please Wait..");
            pd.setMessage("Uploading Post");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        if (PersonTagsClone != null) {
            HashMap haspmap = new HashMap<>();
            haspmap.put("timeStamp", timestamp);
            haspmap.put("UserId",CurrentUserId);
            haspmap.put("RelatedTo", RelateSpin.getSelectedItem().toString());
            haspmap.put("CompanyTo", autoCompleteTextView.getText().toString());
            haspmap.put("ctcValue",CtcNumber.getText().toString());
            haspmap.put("ctcType",ctcSpin.getSelectedItem().toString());
            haspmap.put("JobProfile",JobProfile.getText().toString());
            haspmap.put("TextBoxData", Articlewritebox.getText().toString());
            haspmap.put("PersonTags", PersonTagsClone);
            haspmap.put("FileName",fileName);
            haspmap.put("FileType",FileType);
            haspmap.put("PostURL", url);
            if (RelateSpin.getSelectedItem().toString().equals("Placement") || RelateSpin.getSelectedItem().toString().equals("Internship")){
                // check comanyspin autocomplete name contains user input or not

                node = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestamp);
                CompanykRef = FirebaseDatabase.getInstance().getReference("allCompanies").child(RelateSpin.getSelectedItem().toString()).child(autoCompleteTextView.getText().toString().toLowerCase().trim()).child(timestamp);

            }else {
                node = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestamp);
                CompanykRef = FirebaseDatabase.getInstance().getReference("allCompanies").child(RelateSpin.getSelectedItem().toString()).child(autoCompleteTextView.getText().toString().toLowerCase().trim()).child(timestamp);

            }
            node.setValue(haspmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (CompanykRef != null){
                        CompanykRef.setValue(timestamp);
                    }
                    userDataRef = FirebaseDatabase.getInstance().getReference("UserPostData").child(CurrentUserId).child(timestamp);
                    userDataRef.setValue(timestamp);
                    pd.dismiss();
                    Toast.makeText(getContext(), "File Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                    docname.setVisibility(View.GONE);
                    moveToNewActivity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), title, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (RelateSpin.getSelectedItem().toString().equals("Placement") || RelateSpin.getSelectedItem().toString().equals("Internship")){

                node = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestamp);
                CompanykRef = FirebaseDatabase.getInstance().getReference("allCompanies").child(RelateSpin.getSelectedItem().toString()).child(autoCompleteTextView.getText().toString().toLowerCase().trim()).child(timestamp);

            }else {
                node = FirebaseDatabase.getInstance().getReference("POSTFiles").child(timestamp);
                CompanykRef = FirebaseDatabase.getInstance().getReference("allCompanies").child(RelateSpin.getSelectedItem().toString()).child(autoCompleteTextView.getText().toString().toLowerCase().trim()).child(timestamp);

            }

            HashMap haspmap = new HashMap<>();

            haspmap.put("timeStamp", timestamp);
            haspmap.put("UserId",CurrentUserId);
            haspmap.put("RelatedTo", RelateSpin.getSelectedItem().toString());
            haspmap.put("CompanyTo", autoCompleteTextView.getText().toString());
            haspmap.put("ctcValue",CtcNumber.getText().toString());
            haspmap.put("ctcType",ctcSpin.getSelectedItem().toString());
            haspmap.put("JobProfile",JobProfile.getText().toString());
            haspmap.put("TextBoxData", Articlewritebox.getText().toString());
            haspmap.put("FileName",fileName);
            haspmap.put("FileType",FileType);
            haspmap.put("PostURL", url);

            node.setValue(haspmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (CompanykRef != null){
                        CompanykRef.setValue(timestamp);
                    }
                    userDataRef = FirebaseDatabase.getInstance().getReference("UserPostData").child(CurrentUserId).child(timestamp);
                    userDataRef.setValue(timestamp);
                    pd.dismiss();
                    Toast.makeText(getContext(), "File Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                    docname.setVisibility(View.GONE);
                    moveToNewActivity();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), title, Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    private void validateSpinner() {
        if (autoCompleteTextView.getText().toString().isEmpty() || autoCompleteTextView.getText().toString().equals("")||CtcNumber.getText().toString().length() == 0 || ctcSpin.getSelectedItem().toString().equals("none") || JobProfile.getText().toString().length() ==0) {
            Error_text.setText("Please fill up all fields");
            isAllFieldCheckedSpin = false;
        } else {
            isAllFieldCheckedSpin = true;
        }
    }

    private void validateHeadNText() {
        //-----------------------------Article text box-------------------------------//
        article_box_Value = Articlewritebox.getText().toString();
        if (article_box_Value.length() == 0 || autoCompleteTextView.getText().toString().length() == 0) {
            Articlewritebox.setError("Please write experience/thoughts here");
            autoCompleteTextView.setError("Please enter related interest");
        } else {
            isAllFieldCheckedTextMedia = true;
        }
    }

    private void validateDocuments() {
        if (docname.length() == 0 || doc_image == null) {
            Toast.makeText(getContext(), "Please Write text or add media", Toast.LENGTH_SHORT).show();
            isAllFieldCheckedTextMedia = false;
        } else {
            isAllFieldCheckedTextMedia = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    uri = data.getData();
                    doc_image = (ImageView) view.findViewById(R.id.Doc_img);
                    doc_image.setImageURI(uri);
                    doc_image.setVisibility(View.VISIBLE);
                    Deletebtn.setVisibility(View.VISIBLE);
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
//                    File file = new File(SiliCompressor.with(getActivity()).compress(FileUtils.getPath(getActivity(), uri), new File(getActivity().getCacheDir(), "temp")));
//                    uriCompressed = Uri.fromFile(file);
//                    String path = file.getAbsolutePath();
                    extension = path.substring(path.lastIndexOf("."));

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getApplication().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                title = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                docname.setText(title);

                                fileName = title;
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {

                    }
//                    getExtensionFromUri(uri);
                    FileType = "image";
                }

                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    doc_image = (ImageView) view.findViewById(R.id.Doc_img);
                    doc_image.setImageResource(R.drawable.ic_baseline_video_library_24);
                    doc_image.setVisibility(View.VISIBLE);
                    Deletebtn.setVisibility(View.VISIBLE);
                    docname.setText("");
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName;
                    extension = path.substring(path.lastIndexOf("."));
                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getApplication().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                title = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                docname.setText(title);

                                fileName = title;
                            }

                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        Cursor cursor = null;
                        cursor = getActivity().getApplication().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = myFile.getName();
                            docname.setVisibility(View.VISIBLE);
                            docname.setText(displayName);
                        }
                    }
//                    getExtensionFromUri(uri);
                    FileType = "video";
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    doc_image = (ImageView) view.findViewById(R.id.Doc_img);
                    getExtensionFromUri(uri);
                    if (FileType.equals("pdf")){
                        doc_image.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
                    }else if(FileType.equals("ppt") || FileType.equals("vnd.openxmlformats-officedocument.presentationml.presentation") ||FileType.equals("vnd.ms-powerpoint")){
                        doc_image.setImageResource(R.drawable.ppt);
                    }else if(FileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || FileType.equals("xlsx") || FileType.equals("application/vnd.ms-excel")){
                        doc_image.setImageResource(R.drawable.excel);
                    }else if (FileType.equals("application/msword") || FileType.equals("docx") || FileType.equals("doc") || FileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                        doc_image.setImageResource(R.drawable.word);
                    }else {
                        doc_image.setImageResource(R.drawable.zip);
                    }

                    doc_image.setVisibility(View.VISIBLE);
                    Deletebtn.setVisibility(View.VISIBLE);

                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    extension = path.substring(path.lastIndexOf("."));
                    String displayName = null;
                    int displaysize = 0;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getApplication().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                displaysize = cursor.getColumnIndex(OpenableColumns.SIZE);

                                docname.setVisibility(View.VISIBLE);
                                docname.setText(displayName);
                                fileName = displayName;

                                getExtensionFromUri(uri);
                                k = cursor.getLong(displaysize);

                                checksize((int) k);

                            }

                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        Cursor cursor = null;
                        cursor = getActivity().getApplication().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = myFile.getName();
                            docname.setVisibility(View.VISIBLE);
                            docname.setText(displayName);
                            k = cursor.getLong(displaysize);
                            fileName = displayName;

                            checksize((int) k);
                        }
                    }

                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void getExtensionFromUri(Uri uri) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        FileType =  mimeTypeMap.getExtensionFromMimeType(getActivity().getContentResolver().getType(uri));
    }

    private void checksize(int displaysize) {
        long fixsize = 3355444;
        if (displaysize < fixsize) {

            checkForUpload = true;
            Toast.makeText(getContext(), checkForUpload.toString(), Toast.LENGTH_SHORT).show();
        } else {

            docname.setText("Please compress your file size <= 3 mb");
            checkForUpload = false;


        }
    }
    private void moveToNewActivity () {

        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);

    }

}