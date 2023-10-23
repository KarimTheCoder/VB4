package com.fortitude.shamsulkarim.ieltsfordory.ui;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.BuildConfig;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kyleduo.switchbutton.SwitchButton;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import mehdi.sakout.fancybuttons.FancyButton;

public class NewSettingActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, PurchasesUpdatedListener {


    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;
    private String ADVANCE_FAVORITE,ADVANCE_LEARNED, BEGINNER_FAVORITE, BEGINNER_LEARNED,INTERMEDIATE_FAVORITE,INTERMEDIATE_LEARNED, GRE_FAVORITE, GRE_LEARNED;
    private List<Integer> savedBeginnerFav, savedAdvanceFav,savedIntermediateFav, savedGreFav;
    private List<Integer> savedIeltsLearned, savedToeflLearned,savedSatLearned, savedGreLearned;
    private FancyButton save,spanish;
    private Spinner wps, rps,imageQualitySpinner, themeSpinner;
    private int wordsPerSession,repeatationPerSession;
    private SharedPreferences sp;
    private SwitchButton soundSwitch, pronunciationSwitch;
    private boolean switchState = true;
    private boolean pronunciationState = true;
    private ProgressDialog progressDialog;
    private CheckBox ieltsCheckbox, toeflCheckbox, satCheckbox, greCheckbox;
    private boolean isSignedIn;
    private CardView privacyPolicy;
    private CardView restorePurchaseCard;

    // google sign in
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FancyButton signIn;
    private TextView userName, userDetail;
    private DatabaseReference ref;
    private boolean connected;

    // Billing
    private BillingClient billingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_setting);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.toolbar_background_color));

        // This code reports to Crashlytics of connection
        boolean connected = ConnectivityHelper.isConnectedToNetwork(this);


        privacyPolicy = findViewById(R.id.privacy_policy_card);
        CardView signInCardView = findViewById(R.id.user_status);
        restorePurchaseCard = findViewById(R.id.restore_card);

        privacyPolicy.setOnClickListener(this);
        restorePurchaseCard.setOnClickListener(this);

        if(BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){
            signInCardView.setVisibility(View.GONE);
        }




        initialization();
        checkboxActions();
        setSpinner();

        boolean isIeltsChecked = sp.getBoolean("isIELTSActive", true);
        boolean isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        boolean isSatChecked = sp.getBoolean("isSATActive", true);
        boolean isGreChecked = sp.getBoolean("isGREActive", true);

        ieltsCheckbox.setChecked(isIeltsChecked);
        toeflCheckbox.setChecked(isToeflChecked);
        satCheckbox.setChecked(isSatChecked);
        greCheckbox.setChecked(isGreChecked);

        ieltsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(!ieltsCheckbox.isChecked()){

                    if( toeflCheckbox.isChecked() || satCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isIELTSActive", false).apply();

                        Toast.makeText(NewSettingActivity.this, "Ielts unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(NewSettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        ieltsCheckbox.setChecked(true);

                    }


                }else {


                    sp.edit().putBoolean("isIELTSActive", true).apply();

                    Toast.makeText(NewSettingActivity.this, "IELTS checked",Toast.LENGTH_SHORT).show();

                }


            }
        });


        toeflCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!toeflCheckbox.isChecked()){


                    if( ieltsCheckbox.isChecked() || satCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isTOEFLActive", false).apply();

                        Toast.makeText(NewSettingActivity.this, "TOEFL unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(NewSettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        toeflCheckbox.setChecked(true);


                    }





                }else {

                    sp.edit().putBoolean("isTOEFLActive", true).apply();

                    Toast.makeText(NewSettingActivity.this, "TOEFL checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

        satCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!satCheckbox.isChecked()){

                    if( ieltsCheckbox.isChecked() || toeflCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isSATActive", false).apply();

                        Toast.makeText(NewSettingActivity.this, "SAT unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(NewSettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        satCheckbox.setChecked(true);


                    }



                }else {



                    sp.edit().putBoolean("isSATActive", true).apply();

                    Toast.makeText(NewSettingActivity.this, "SAT checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

        greCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!greCheckbox.isChecked()){

                    if( ieltsCheckbox.isChecked() || toeflCheckbox.isChecked() || satCheckbox.isChecked()){


                        sp.edit().putBoolean("isGREActive", false).apply();

                        Toast.makeText(NewSettingActivity.this, "GRE unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(NewSettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        greCheckbox.setChecked(true);

                    }



                }else {

                    sp.edit().putBoolean("isGREActive", true).apply();

                    Toast.makeText(NewSettingActivity.this, "GRE checked",Toast.LENGTH_SHORT).show();


                }


            }
        });



        soundSwitch =  findViewById(R.id.sound_switch);
        pronunciationSwitch = findViewById(R.id.pronun_switch);

        pronunciationSwitch.setOnClickListener(this);
        soundSwitch.setOnClickListener(this);

        switchState = sp.getBoolean("soundState", true);
        pronunciationState = sp.getBoolean("pronunState",true);

        pronunciationSwitch.setChecked(pronunciationState);
        soundSwitch.setChecked(switchState);

        imageQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {


                if(i == 0){



                    sp.edit().putInt("imageQuality",0).apply();


                }if( i == 1){




                    sp.edit().putInt("imageQuality",1).apply();
                }
                if(i == 2) {


                    sp.edit().putInt("imageQuality",2).apply();
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        wps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//                if(i == 0){
//
//
//                    wordsPerSession = 5;
//
//                    sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
//                    Crashlytics.setInt("Words Per Session",wordsPerSession);
//
//
//                }if( i == 1){
//
//
//                    wordsPerSession = 4;
//
//                    sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
//                    Crashlytics.setInt("Words Per Session",wordsPerSession);
//                }
//                if(i == 2) {
//
//
//                    wordsPerSession = 3;
//                    sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
//                    Crashlytics.setInt("Words Per Session",wordsPerSession);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        rps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//
//
//                if(i == 0){
//
//
//                    repeatationPerSession = 5;
//                    sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
//                    Crashlytics.setInt("Repetition Per Session",repeatationPerSession);
//
//
//                }if( i == 1){
//
//
//                    repeatationPerSession = 4;
//                    sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
//                    Crashlytics.setInt("Repetition Per Session",repeatationPerSession);
//                }if( i == 2){
//
//
//                    repeatationPerSession = 3;
//                    sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
//                    Crashlytics.setInt("Repetition Per Session",repeatationPerSession);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        if(mAuth.getCurrentUser() != null && connected){

            ref.child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {


                @Override
                public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {

//                if(dataSnapshot.exists()) {
                    String data = dataSnapshot.getValue(String.class);
                    String key  = dataSnapshot.getKey();

                   // Toast.makeText(getApplicationContext(),key+" "+ data, Toast.LENGTH_SHORT).show();

                    // if key equals to favorite
                    if( Objects.requireNonNull(key).equalsIgnoreCase("advanceFavCount") || key.equalsIgnoreCase("intermediateFavCount") || key.equalsIgnoreCase("beginnerFavCount")){

                        syncDatabasesIfFavDataChanged(data, key);

                    }


                    //if key equals to learned

                    if(key.equalsIgnoreCase("advanceLearnedCount") || key.equalsIgnoreCase("intermediateLearnedCount") || key.equalsIgnoreCase("beginnerLearnedCount")){

                        syncSPIfLearnedDataChanged(data, key);
                    }




                }

                @Override
                public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {


                }

                @Override
                public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {



                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {


                }
            });
        }

    }


    private void initialization(){

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.ns_toolbar);
        save = findViewById(R.id.ns_save);
        spanish = findViewById(R.id.language_spanish);
        imageQualitySpinner = findViewById(R.id.image_quality_spinner);
        toolbar.setTitle("Settings");
        ieltsCheckbox = findViewById(R.id.ielts_checkbox);
        toeflCheckbox = findViewById(R.id.toefl_checkbox);
        satCheckbox = findViewById(R.id.sat_checkbox);
        greCheckbox = findViewById(R.id.gre_checkbox);

        wps = findViewById(R.id.wps_spinner);
        rps = findViewById(R.id.profile_wps_spinner);
        themeSpinner = findViewById(R.id.theme_spinner);

        themeSpinner.setOnItemSelectedListener(this);
        wps.setOnItemSelectedListener(this);
        rps.setOnItemSelectedListener(this);
        setUpSpinnerAdapter();




        ///---------------

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        // Checking Network Connection

      connected = ConnectivityHelper.isConnectedToNetwork(this);


        signIn = findViewById(R.id.ns_sign_in);
        signIn.setBackgroundColor(getColor(R.color.card_background_color));
        signIn.setTextColor(getColor(R.color.primary_text_color));

        userName = findViewById(R.id.ns_username);
        userDetail = findViewById(R.id.user_detail);

        //Firebase and Google Sign-In
        // [START config_signin]
        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
        // [END config_signin]
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
       // mAuth = FirebaseAuth.getInstance();
        // firebase database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        //database
        ieltsWordDatabase = new IELTSWordDatabase(this);
        satWordDatabase = new SATWordDatabase(this);
        toeflWordDatabase = new TOEFLWordDatabase(this);
        greWordDatabase = new GREWordDatabase(this);


        save.setOnClickListener(this);
        spanish.setOnClickListener(this);
        signIn.setOnClickListener(this);

        if(sp.getString("secondlanguage","spanish").equalsIgnoreCase("spanish")){


            spanish.setBackgroundColor(getColor(R.color.colorPrimary));
            spanish.setTextColor(getColor(R.color.text_white_87));
        }else {
            spanish.setTextColor(getColor(R.color.primary_text_color));
            spanish.setBackgroundColor(getColor(R.color.card_background_color));
        }


    }

    private void setUpSpinnerAdapter(){

        // Words per second spinner adapter
        ArrayAdapter<CharSequence> wpsAdapter = ArrayAdapter.createFromResource(this,
                R.array.words_per_session_array, R.layout.settings_spinner);
        wpsAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        wps.setAdapter(wpsAdapter);

        // Repetition per second spinner adapter
        ArrayAdapter<CharSequence> rpsAdapter = ArrayAdapter.createFromResource(this,
                R.array.words_per_session_array, R.layout.settings_spinner);
        rpsAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        rps.setAdapter(rpsAdapter);


        // theme spinner adapter
        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(this,
                R.array.theme_options, R.layout.settings_spinner);
        themeAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        themeSpinner.setAdapter(themeAdapter);


    }
    private void setSpinner(){

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        if(!sp.contains("imageQuality")){
            sp.edit().putInt("imageQuality",1).apply();
        }

        int imageQualityInt = sp.getInt("imageQuality",1);

        imageQualitySpinner.setSelection(imageQualityInt);

        int darkMode = sp.getInt("DarkMode",0);
        themeSpinner.setSelection(darkMode);


        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",5).apply();
            wordsPerSession = 5;
        }else {

            wordsPerSession = sp.getInt("wordsPerSession",5);

        }
        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",5).apply();
            repeatationPerSession = 5;
        }else {

            repeatationPerSession = sp.getInt("repeatationPerSession",5);

        }



        if(repeatationPerSession == 25){

            rps.setSelection(0);
        }
        else if(repeatationPerSession == 20){

            rps.setSelection(1);
        }else if(repeatationPerSession == 15) {

            rps.setSelection(2);

        }else if(repeatationPerSession == 10){
            rps.setSelection(3);
        }
        else if(repeatationPerSession == 5){
            rps.setSelection(4);

        }else if(repeatationPerSession == 4){
            rps.setSelection(5);
        }
        else if(repeatationPerSession == 3){
            rps.setSelection(6);
        }


        if(wordsPerSession == 25){

            wps.setSelection(0);
        }
        else if(wordsPerSession == 20){

            wps.setSelection(1);
        }else if(wordsPerSession == 15) {

            wps.setSelection(2);

        }else if(wordsPerSession == 10){
            wps.setSelection(3);
        }
        else if(wordsPerSession == 5){
            wps.setSelection(4);

        }else if(wordsPerSession == 4){
            wps.setSelection(5);
        }
        else if(wordsPerSession == 3){
            wps.setSelection(6);
        }


// TODO: 12/5/2020 delete this line if everything works
//        if(wordsPerSession == 5){
//
//
//            wps.setSelection(0);
//        }
//        else if(wordsPerSession == 4){
//
//
//            wps.setSelection(1);
//        }else {
//
//            wps.setSelection(2);
//
//        }

       sp.getInt("language",0);



    }

    private void checkboxActions(){

        ieltsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ieltsCheckbox.isChecked()){

                    Toast.makeText(NewSettingActivity.this,"ielst checked", Toast.LENGTH_SHORT).show();
                }else {


                    Toast.makeText(NewSettingActivity.this,"ielst unchecked", Toast.LENGTH_SHORT).show();
                }



            }
        });




    }

    private void signIn() {

        if(connected){

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            Toast.makeText(this,"Signing in initiated",Toast.LENGTH_SHORT).show();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else {

            Toast.makeText(this,"Please connect to the internet",Toast.LENGTH_SHORT).show();
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();


        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {


        if( user != null){

            userName.setText(user.getDisplayName());
            sp.edit().putString("userName",user.getDisplayName()).apply();
            userDetail.setText(user.getEmail());
            signIn.setText("Sign out");
            isSignedIn = true;

            sp.edit().putBoolean("isSignedIn",isSignedIn).apply();
            Toast.makeText(this,"Successfully signed in.",Toast.LENGTH_LONG).show();




        }else {
            userName.setText(getString(R.string.doggo));
            userDetail.setText(getString(R.string.sign_in_description));
            signIn.setText(getString(R.string.sign_in));
            isSignedIn = false;

            sp.edit().putBoolean("isSignedIn",isSignedIn).apply();
            Toast.makeText(this,"Log in failed.",Toast.LENGTH_LONG).show();

        }
        //  hideProgressDialog();
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplicationContext(), "On activity result",Toast.LENGTH_SHORT).show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                progressDialog.show();
               // Toast.makeText(getApplicationContext(), "On activity result success",Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
              //  Toast.makeText(getApplicationContext(), "On activity result failed",Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient != null){
            billingClient.endConnection();
        }

        ieltsWordDatabase.close();
        toeflWordDatabase.close();
        satWordDatabase.close();
        greWordDatabase.close();


    }

    private void askToSync(){

        if(!NewSettingActivity.this.isFinishing()){
            new LovelyStandardDialog(this)
                    .setIcon(R.drawable.data_found)
                    .setTitle("Saved progress on the cloud found!")
                    .setMessage("Do you want to sync the progress?")
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            syncSQL();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

        }



    }

    public void getFirebase(){

        //Toast.makeText(NewSettingActivity.this,"get firebase",Toast.LENGTH_LONG).show();

        try{
            ref.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addChildEventListener(new ChildEventListener() {


                int i = 0;
                final String[] strData = new String[9];
                final HashMap<String,String> data = new HashMap<>();
                Boolean askOnce = false;

                @Override
                public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                    //Toast.makeText(getApplicationContext(),s+" : "+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();




                    if(dataSnapshot.exists() &&  i == 8){

                        progressDialog.cancel();
                        askOnce = true;
                        askToSync();

                    }


                    // getting data

                    String state = dataSnapshot.getValue(String.class);

                    strData[i] = state;
                    data.put(s,state);



                    if(strData[8] != null){

                        for(int j = 0; j < strData.length; j++){

                            if(j == 0){

                                GRE_FAVORITE = strData[0];

                            }
                            if(j == 1){

                                GRE_LEARNED = strData[1];

                            }
                            if(j == 2){

                                BEGINNER_FAVORITE = strData[2];


                            }
                            if(j == 3){


                                BEGINNER_LEARNED = strData[3];

                            }
                            if(j == 4){
                                sp.edit().putString("userName",strData[4]).apply();

                            }
                            if(j == 5){

                                ADVANCE_FAVORITE = strData[5];


                            }
                            if( j == 6){



                                ADVANCE_LEARNED = strData[6];

                            }

                            if( j == 7){



                                INTERMEDIATE_FAVORITE = strData[7];

                            }

                            if( j == 8){



                                INTERMEDIATE_LEARNED = strData[8];

                            }

                        }

                    }
                    i++;

                }


                @Override
                public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException n){

            Toast.makeText(this,"Reference exception",Toast.LENGTH_SHORT).show();

        }



    }

    private List<Integer> builderToNums(StringBuilder numBuilder){

        List<Integer> backToNums = new ArrayList<>();
        String string = numBuilder.toString();

        for(int i = 0; i < string.length();){

            if(string.substring(i, i+1).equalsIgnoreCase("1")){

                backToNums.add(1);
            }else {


                backToNums.add(0);

            }
            i = i+2;
        }

        return backToNums;
    }

    private void addingBuilderToNums(){

        savedAdvanceFav = new ArrayList<>();
        savedIntermediateFav = new ArrayList<>();
        savedBeginnerFav = new ArrayList<>();
        savedGreFav = new ArrayList<>();

        savedIeltsLearned = new ArrayList<>();
        savedToeflLearned = new ArrayList<>();
        savedSatLearned = new ArrayList<>();
        savedGreFav = new ArrayList<>();


        savedIeltsLearned = builderToNums(new StringBuilder(BEGINNER_LEARNED));
        savedToeflLearned = builderToNums(new StringBuilder(INTERMEDIATE_LEARNED));
        savedSatLearned =   builderToNums(new StringBuilder(ADVANCE_LEARNED));
        savedGreLearned =   builderToNums(new StringBuilder(GRE_LEARNED));

        savedBeginnerFav =  builderToNums(new StringBuilder(BEGINNER_FAVORITE));
        savedIntermediateFav = builderToNums(new StringBuilder(INTERMEDIATE_FAVORITE));
        savedAdvanceFav  = builderToNums(new StringBuilder(ADVANCE_FAVORITE));
        savedGreFav = builderToNums(new StringBuilder(GRE_FAVORITE));

//        sp.edit().putInt("intermediate", Integer.parseInt(INTERMEDIATE_LEARNED)).apply();
//        sp.edit().putInt("beginner", Integer.parseInt(BEGINNER_LEARNED)).apply();
//        sp.edit().putInt("advance", Integer.parseInt(ADVANCE_LEARNED)).apply();

    }

    private void syncSQL(){
        addingBuilderToNums();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //Toast.makeText(getApplicationContext(),"Syncingdfksfjlsj"+savedBeginnerFav.size(),Toast.LENGTH_SHORT).show();

                if(savedBeginnerFav.size() > 0){

                    for(int i = 0; i <savedBeginnerFav.size();i++){

                       // Toast.makeText(getApplicationContext(),""+savedBeginnerFav.get(i),Toast.LENGTH_SHORT).show();

                        if(savedBeginnerFav.get(i) == 1){

                            ieltsWordDatabase.updateFav(""+(i+1),"True");
                        }else {
                            ieltsWordDatabase.updateFav(""+(i+1),"False");
                        }



                    }


                }

                if(savedIntermediateFav.size() > 0){

                    for(int j = 0; j < savedIntermediateFav.size(); j++){

                        if(savedIntermediateFav.get(j)== 1){

                            toeflWordDatabase.updateFav(""+(j+1),"True");
                        }else {
                            toeflWordDatabase.updateFav(""+(j+1),"False");
                        }




                    }

                }

                if(savedAdvanceFav.size() > 0){

                    for(int k = 0; k < savedAdvanceFav.size(); k++){


                        if(savedAdvanceFav.get(k) == 1){

                            satWordDatabase.updateFav(""+(1+k),"True");
                        }else {

                            satWordDatabase.updateFav(""+(1+k),"False");
                        }




                    }

                }

                if(savedGreFav.size() > 0){

                    for(int k = 0; k < savedGreFav.size(); k++){


                        if(savedGreFav.get(k) == 1){

                            greWordDatabase.updateFav(""+(1+k),"True");
                        }else {

                            greWordDatabase.updateFav(""+(1+k),"False");
                        }




                    }

                }


                // Syncing Learned


                if(savedIeltsLearned.size() > 0){

                    for(int k = 0; k < savedIeltsLearned.size(); k++){


                        if(savedIeltsLearned.get(k) == 1){

                            ieltsWordDatabase.updateLearned(""+(1+k),"True");
                        }else {

                            ieltsWordDatabase.updateLearned(""+(1+k),"False");
                        }




                    }

                }


                if(savedToeflLearned.size() > 0){

                    for(int k = 0; k < savedToeflLearned.size(); k++){


                        if(savedToeflLearned.get(k) == 1){

                            toeflWordDatabase.updateLearned(""+(1+k),"True");
                        }else {

                            toeflWordDatabase.updateLearned(""+(1+k),"False");
                        }




                    }

                }

                if(savedSatLearned.size() > 0){

                    for(int k = 0; k < savedSatLearned.size(); k++){


                        if(savedSatLearned.get(k) == 1){

                            satWordDatabase.updateLearned(""+(1+k),"True");
                        }else {

                            satWordDatabase.updateLearned(""+(1+k),"False");
                        }




                    }

                }

                if(savedGreLearned.size() > 0){

                    for(int k = 0; k < savedGreLearned.size(); k++){


                        if(savedGreLearned.get(k) == 1){

                            greWordDatabase.updateLearned(""+(1+k),"True");
                        }else {

                            greWordDatabase.updateLearned(""+(1+k),"False");
                        }
                    }

                }




//                Toast.makeText(getApplicationContext(), "syncing sql...", Toast.LENGTH_SHORT).show();
            }
        }, 0L);






    }

    private void syncDatabasesIfFavDataChanged(String newData, String key){

        List<Integer> newDataList;





        // ADVANC FAVORITE COUNT

        if(key.equalsIgnoreCase("advanceFavCount")){

            int advanceSize = sp.getInt("advance",getResources().getStringArray(R.array.SAT_words).length);
            // cleaning database

            for(int i = 0; i < advanceSize; i++){
                satWordDatabase.updateFav(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    satWordDatabase.updateFav(""+adva,"True");



                }

            }

            //Toast.makeText(getApplicationContext(),"advance favorite synced", Toast.LENGTH_SHORT).show();
        }

        // INTERMEDIATE FAVORITE COUNT

        if(key.equalsIgnoreCase("intermediateFavCount")){

            int intermediateSize = sp.getInt("intermediate",getResources().getStringArray(R.array.TOEFL_words).length);
            // cleaning database

            for(int i = 0; i < intermediateSize; i++){
                toeflWordDatabase.updateFav(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    toeflWordDatabase.updateFav(""+adva,"True");



                }

            }
            //Toast.makeText(getApplicationContext(),"intermediate favorite synced", Toast.LENGTH_SHORT).show();

        }


        // BEGINNER FAVORITE COUNT

        if(key.equalsIgnoreCase("beginnerFavCount")){

            int beginnerSize = sp.getInt("beginner",getResources().getStringArray(R.array.TOEFL_words).length);
            // cleaning database

            for(int i = 0; i < beginnerSize; i++){
                ieltsWordDatabase.updateFav(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    ieltsWordDatabase.updateFav(""+adva,"True");



                }

            }
            //Toast.makeText(getApplicationContext(),"beginner favorite synced", Toast.LENGTH_SHORT).show();

        }





    }

    private void syncSPIfLearnedDataChanged(String data, String key){


        if(key.equalsIgnoreCase("advanceLearndCount")){

            int localAdvanceLearned = sp.getInt("advance",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("advance",firebaseSaved).apply();
               // Toast.makeText(getApplicationContext(),"Synced "+ key, Toast.LENGTH_SHORT).show();

            }

        }

        if(key.equalsIgnoreCase("intermediateLearnedCount")){

            int localAdvanceLearned = sp.getInt("intermediate",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("intermediate",firebaseSaved).apply();
                //Toast.makeText(getApplicationContext(),"Synced "+ key, Toast.LENGTH_SHORT).show();

            }

        }

        if(key.equalsIgnoreCase("beginnerLearnedCount")){

            int localAdvanceLearned = sp.getInt("beginner",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("beginner",firebaseSaved).apply();
               // Toast.makeText(getApplicationContext(),"Synced "+ key, Toast.LENGTH_SHORT).show();

            }

        }

    }

    // Google Sign in methods
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(getApplicationContext(), "Successfully signed in",Toast.LENGTH_SHORT).show();
                            getFirebase();
                            // askToSync();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Failed to sign in", task.getException());
                            Toast.makeText(NewSettingActivity.this, "Failed to sign in",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }
                        progressDialog.cancel();


                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    //    @Override
    public void onClick(View v) {
//
        if(v == soundSwitch){

            if(switchState){
                switchState = false;
                sp.edit().putBoolean("soundState",false).apply();


            }else {
                switchState = true;
                sp.edit().putBoolean("soundState",true).apply();
            }

        }

        if( v == pronunciationSwitch){

            if(pronunciationState){
                pronunciationState = false;
                sp.edit().putBoolean("pronunState",false).apply();

            }else {
                pronunciationState = true;
                sp.edit().putBoolean("pronunState",true).apply();
            }
        }

        if( v == signIn){

            if( mAuth.getCurrentUser() == null){

                signIn();
            }else {
                signOut();
            }

        }

        if( v == save){

            finish();
            startActivity(new Intent(this,MainActivity.class));
        }

        if( v== spanish){
            if(sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){

                sp.edit().putString("secondlanguage","english").apply();
                spanish.setBackgroundColor(getColor(R.color.card_background_color));
                spanish.setTextColor(getColor(R.color.primary_text_color));
            }else {
                sp.edit().putString("secondlanguage","spanish").apply();
                spanish.setTextColor(getColor(R.color.text_white_87));
                spanish.setBackgroundColor(getColor(R.color.colorPrimary));
            }


        }

        if( v == privacyPolicy){


            Uri appUrl = Uri.parse("https://banglish1.wixsite.com/fortitude");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);
        }

        if( v == restorePurchaseCard){
          //  getPreviousPurchases();

            Toast.makeText(this,"Restore process started...",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        sp.edit().putBoolean("isSignedIn",isSignedIn).apply();

       // Toast.makeText(this,"Is signed in: "+isSignedIn,Toast.LENGTH_LONG).show();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() !=null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        if( adapterView.getId() == wps.getId()){

            if(i == 0){

                wordsPerSession = 25;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();

            }else if( i == 1){

                wordsPerSession = 20;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();

            }else if(i == 2) {

                wordsPerSession = 15;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
            }else if(i == 3) {

                wordsPerSession = 10;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
            }else if(i == 4) {

                wordsPerSession = 5;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
            }else if(i == 5) {

                wordsPerSession = 4;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
            }else if(i == 6) {

                wordsPerSession = 3;
                sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
            }


        }

        if(adapterView.getId() == rps.getId()){

            if(i == 0){

                repeatationPerSession = 25;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();

            }if( i == 1){

                repeatationPerSession = 20;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();

            }if( i == 2){

                repeatationPerSession = 15;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
            }
            if( i == 3){

                repeatationPerSession = 10;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
            }
            if( i == 4){

                repeatationPerSession = 5;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
            }
            if( i == 5){

                repeatationPerSession = 4;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
            }
            if( i == 6){

                repeatationPerSession = 3;
                sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
            }

        }

        if(adapterView.getId() == themeSpinner.getId()){

            String trialStatus = checkTrialStatus();

            if(!sp.contains("purchase")){

                if(trialStatus.equalsIgnoreCase("ended")){

                    Toast.makeText(this,"Please upgrade to enjoy dark mode feature",Toast.LENGTH_SHORT).show();

                }else {

                    if(i == 0){

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        sp.edit().putInt("DarkMode",0).apply();
                    }else if(i == 1){

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        sp.edit().putInt("DarkMode",1).apply();
                    }else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        sp.edit().putInt("DarkMode",2).apply();
                    }

                }
            }else {


                if(i == 0){

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sp.edit().putInt("DarkMode",0).apply();
                }else if(i == 1){

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sp.edit().putInt("DarkMode",1).apply();
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    sp.edit().putInt("DarkMode",2).apply();
                }

            }





        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    private String checkTrialStatus(){

        String trialStatus = "ended";

        if(sp.contains("trial_end_date")){

            Date today = Calendar.getInstance().getTime();

            long endMillies = sp.getLong("trial_end_date",0) ;
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;

            if(leftMillies >=0){

                trialStatus = "active";

            }
            else {

                trialStatus = "ended";

            }

        }


        return trialStatus;


    }

    // Billing

//    private void getPreviousPurchases(){
//
//
//        billingClient = BillingClient.newBuilder(this)
//                .enablePendingPurchases()
//                .setListener(this)
//                .build();
//
//
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
//                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//
//                    Purchase.PurchasesResult results = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
//
//                    try{
//                        if(Objects.requireNonNull(results.getPurchasesList()).isEmpty()){
//                            Toast.makeText(getApplicationContext(),"Google tells us, you don't own Premium+",Toast.LENGTH_LONG).show();
//                        }else {
//
//                            for (Purchase purchase : Objects.requireNonNull(results.getPurchasesList())) {
//
//                                if(purchase.getSku().equalsIgnoreCase("test_product")){
//
//                                    if(!sp.contains("purchase")){
//
//                                        sp.edit().putBoolean("purchase",true).apply();
//                                        Toast.makeText(getApplicationContext(),"Product: "+purchase.getSku()+" restored",Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    }catch (NullPointerException i){
//                        Log.i("Billing Result", Objects.requireNonNull(i.getMessage()));
//                    }
//                }
//                else if( billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE){
//
//                    Toast.makeText(getApplicationContext(),"Please connect to the internet",Toast.LENGTH_SHORT).show();
//                }
//                else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
//
//                    Toast.makeText(getApplicationContext(),"Please sign in to Google Play Store",Toast.LENGTH_SHORT).show();
//
//                }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED){
//
//                    Toast.makeText(getApplicationContext(),"Play Store service is not connected now",Toast.LENGTH_SHORT).show();
//
//                }
//                else {
//                    Toast.makeText(getApplicationContext(),"BILLING | startConnection | RESULT: $billingResponseCode"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//                Toast.makeText(getApplicationContext(),"BILLING | onBillingServiceDisconnected | DISCONNECTED",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//
//
//    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}


