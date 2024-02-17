package com.fortitude.shamsulkarim.ieltsfordory.ui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.BuildConfig;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.fortitude.shamsulkarim.ieltsfordory.utility.signin.SignInAndSync;
import com.fortitude.shamsulkarim.ieltsfordory.utility.signin.SignInAndSyncCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.kyleduo.switchbutton.SwitchButton;
import java.util.Calendar;
import java.util.Date;
import mehdi.sakout.fancybuttons.FancyButton;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, SignInAndSyncCallback {

    private SignInAndSync signInAndSync;
    private FancyButton save,spanish;
    private Spinner wps, rps,imageQualitySpinner, themeSpinner;
    private int wordsPerSession,repeatationPerSession;
    private SharedPreferences sp;
    private SwitchButton soundSwitch, pronunciationSwitch;
    private boolean switchState = true;
    private boolean pronunciationState = true;
    private ProgressBar progressBar;
    private CheckBox ieltsCheckbox, toeflCheckbox, satCheckbox, greCheckbox;
    private boolean isSignedIn;
    private CardView privacyPolicy;
    private CardView restorePurchaseCard;

    private FancyButton signIn;
    private TextView userName, userDetail;
    private boolean connected;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_setting);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.toolbar_background_color));

        // This code reports to Crashlytics of connection
        boolean connected = ConnectivityHelper.isConnectedToNetwork(this);
        signInAndSync = new SignInAndSync(this,this);

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

                        Toast.makeText(SettingActivity.this, "Ielts unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(SettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        ieltsCheckbox.setChecked(true);

                    }


                }else {


                    sp.edit().putBoolean("isIELTSActive", true).apply();

                    Toast.makeText(SettingActivity.this, "IELTS checked",Toast.LENGTH_SHORT).show();

                }


            }
        });


        toeflCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!toeflCheckbox.isChecked()){


                    if( ieltsCheckbox.isChecked() || satCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isTOEFLActive", false).apply();

                        Toast.makeText(SettingActivity.this, "TOEFL unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(SettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        toeflCheckbox.setChecked(true);


                    }





                }else {

                    sp.edit().putBoolean("isTOEFLActive", true).apply();

                    Toast.makeText(SettingActivity.this, "TOEFL checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

        satCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!satCheckbox.isChecked()){

                    if( ieltsCheckbox.isChecked() || toeflCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isSATActive", false).apply();

                        Toast.makeText(SettingActivity.this, "SAT unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(SettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        satCheckbox.setChecked(true);


                    }



                }else {



                    sp.edit().putBoolean("isSATActive", true).apply();

                    Toast.makeText(SettingActivity.this, "SAT checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

        greCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!greCheckbox.isChecked()){

                    if( ieltsCheckbox.isChecked() || toeflCheckbox.isChecked() || satCheckbox.isChecked()){


                        sp.edit().putBoolean("isGREActive", false).apply();

                        Toast.makeText(SettingActivity.this, "GRE unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(SettingActivity.this, "At least select one", Toast.LENGTH_SHORT).show();
                        greCheckbox.setChecked(true);

                    }



                }else {

                    sp.edit().putBoolean("isGREActive", true).apply();

                    Toast.makeText(SettingActivity.this, "GRE checked",Toast.LENGTH_SHORT).show();


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


        activityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {

                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    Intent data = result.getData();
                                    int requestCode = result.getResultCode();

                                    // Do something with the data

                                    signInAndSync.authenticateUser( data);

                                }



                            }
                        }
                );
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
        progressBar =  findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        // Checking Network Connection

        connected = ConnectivityHelper.isConnectedToNetwork(this);


        signIn = findViewById(R.id.ns_sign_in);
        signIn.setBackgroundColor(getColor(R.color.card_background_color));
        signIn.setTextColor(getColor(R.color.primary_text_color));

        userName = findViewById(R.id.ns_username);
        userDetail = findViewById(R.id.user_detail);

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

       sp.getInt("language",0);



    }

    private void checkboxActions(){

        ieltsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ieltsCheckbox.isChecked()){

                    Toast.makeText(SettingActivity.this,"ielst checked", Toast.LENGTH_SHORT).show();
                }else {


                    Toast.makeText(SettingActivity.this,"ielst unchecked", Toast.LENGTH_SHORT).show();
                }



            }
        });




    }

    private void signIn() {

        if(connected){
            Intent signInIntent = signInAndSync.getSignInIntent();
            //int RC_SIGN_IN = signInAndSync.getRC_SIGN_IN();
            //startActivityForResult(signInIntent, RC_SIGN_IN);

            Toast.makeText(this,"Signing in initiated",Toast.LENGTH_SHORT).show();
            activityResultLauncher.launch(signInIntent);

        }else {

            Toast.makeText(this,"Please connect to the internet",Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {

        progressStatus(false);
        // Firebase sign out
        signInAndSync.getmAuth().signOut();


        // Google sign out
        signInAndSync.getSignInClient().signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);

                        progressStatus(true);
                        Toast.makeText(getApplicationContext(),"Sign-out complete!",Toast.LENGTH_SHORT).show();

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


        }else {
            userName.setText(getString(R.string.doggo));
            userDetail.setText(getString(R.string.sign_in_description));
            signIn.setText(getString(R.string.sign_in));
            isSignedIn = false;

            sp.edit().putBoolean("isSignedIn",isSignedIn).apply();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signInAndSync.authenticateUser(data);
    }


    public void onClick(View v) {
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

            if( signInAndSync.getmAuth().getCurrentUser() == null){

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


            Uri appUrl = Uri.parse("https://banglish1.wixsite.com/vbprivacypolicy");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);
        }

        if( v == restorePurchaseCard){
            Toast.makeText(this,"Restore is unavailable",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        sp.edit().putBoolean("isSignedIn",isSignedIn).apply();

        // Check if user is signed in (non-null) and update UI accordingly.
        if (signInAndSync.getmAuth().getCurrentUser() !=null){
            FirebaseUser currentUser = signInAndSync.getmAuth().getCurrentUser();
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

                if(trialStatus.equalsIgnoreCase("ended") && BuildConfig.FLAVOR.equalsIgnoreCase("free")){

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

    @Override
    public void updateUI() {

        updateUI(signInAndSync.getmAuth().getCurrentUser());
    }

    @Override
    public void progressStatus(boolean isCompleted) {

        if(isCompleted){


            progressBar.setVisibility(View.INVISIBLE);
        }else {

            progressBar.setVisibility(View.VISIBLE);
        }

    }
}


