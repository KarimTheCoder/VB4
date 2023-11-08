package com.fortitude.shamsulkarim.ieltsfordory.ui.train;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.SettingActivity;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class PretrainActivity extends AppCompatActivity implements View.OnClickListener, com.suke.widget.SwitchButton.OnCheckedChangeListener {


    private VocabularyRepository repository;
    private TextView levelTextView;
    private FancyButton startTrainButton;
    private RoundCornerProgressBar progressBar;
    private SharedPreferences sp;


    // UI
    private FancyButton purchaseButton;
    private ImageButton settingsButton;
    private TextView progressCountTextview;
    private com.suke.widget.SwitchButton tooEasySwitch,spanishSwitch;
    private CardView purchaseCardView,purchaseThankYou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_start_training);

        repository = new VocabularyRepository(this);

        init();
        initUIElement();
        setActivityTitle();
        checkSpanishState();
        checkTooEasyState();



    }

    private void init(){
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

    }

    private void setActivityTitle() {


        String level = sp.getString("level", "");




        if (level.equalsIgnoreCase("beginner")) {

            levelTextView.setText(getString(R.string.beginner));
            setBeginnerLearnedwordsLengthTextView();

        }

        else if (level.equalsIgnoreCase("intermediate")) {

            levelTextView.setText(getString(R.string.intermediate));
            setIntermediateLearnedwordsLengthTextView();


        }

        else if (level.equalsIgnoreCase("advance")) {

            setAdvanceLearnedwordsLengthTextView();
            levelTextView.setText(getString(R.string.advance));

        }
    }

    private void initUIElement(){

        purchaseButton = findViewById(R.id.purchase_button);
        purchaseCardView = findViewById(R.id.purchase_card);
        if(sp.contains("purchase")){

            purchaseCardView.setVisibility(View.GONE);
        }
        purchaseThankYou = findViewById(R.id.purchase_thank_you);
        purchaseButton.setOnClickListener(this);
        spanishSwitch = findViewById(R.id.spanish_switch);
       tooEasySwitch = findViewById(R.id.too_easy_switch);
        spanishSwitch.setOnCheckedChangeListener(this);
        tooEasySwitch.setOnCheckedChangeListener(this);

        levelTextView = findViewById(R.id.start_training_level_textview);
        startTrainButton = findViewById(R.id.no_word_home);
        startTrainButton.setOnClickListener(this);
        settingsButton = findViewById(R.id.start_training_settings_button);
        settingsButton.setOnClickListener(this);

        progressBar = findViewById(R.id.start_training_progress);
        progressBar.setProgressColor(getColor(R.color.colorPrimary));
        progressBar.setProgressBackgroundColor(getColor(R.color.third_background_color));
        progressCountTextview = findViewById(R.id.progress_count_textview);


    }


    //------------------------------------------------------


    private void setBeginnerLearnedwordsLengthTextView(){

        int learnedCount = repository.getBeginnerLearnedCount();
        int totalBeginnerCount = repository.getTotalBeginnerCount();

        progressBar.setMax(totalBeginnerCount);
        progressBar.setProgress(learnedCount);
        progressCountTextview.setText(getString(R.string.pretrain_progress_text,learnedCount,totalBeginnerCount));

    }

    private void setIntermediateLearnedwordsLengthTextView(){

        int i = repository.getIntermediateLearnedCount();

        int size = repository.getTotalIntermediateCount();

        progressBar.setMax(size);
        progressBar.setProgress(i);
        progressCountTextview.setText(getString(R.string.pretrain_progress_text,i,size));
    }

    private void setAdvanceLearnedwordsLengthTextView(){

        int i = repository.getAdvanceLearnedCount();
        int size = repository.getTotalAdvanceCount();



        progressBar.setMax(size);
        progressBar.setProgress(i);
        progressCountTextview.setText(getString(R.string.pretrain_progress_text,i,size));
    }

   private void checkSpanishState(){

        String secondLang = sp.getString("secondlanguage","English");

       assert secondLang != null;
       boolean spanishSwitchState;
       spanishSwitchState = secondLang.equalsIgnoreCase("spanish");

       spanishSwitch.setChecked(spanishSwitchState);


   }

   private void checkTooEasyState(){
        boolean ieltsState = sp.getBoolean("isIELTSActive",true);
        boolean toeflState = sp.getBoolean("isTOEFLActive",true);

       tooEasySwitch.setChecked(!ieltsState && !toeflState);

   }

   private void changeEasyWord(boolean switchState){

        if(switchState){

            sp.edit().putBoolean("isIELTSActive", false).apply();
            sp.edit().putBoolean("isTOEFLActive", false).apply();
        }else {

            sp.edit().putBoolean("isIELTSActive", true).apply();
            sp.edit().putBoolean("isTOEFLActive", true).apply();
        }


   }


    @Override
    public void onClick(View v) {

        if(v == startTrainButton){

            // No words to learn required
            Intent intent = new Intent(this, NewTrain.class);
            this.startActivity(intent);
            this.finish();
        }

        if( v == settingsButton){

            this.startActivity(new Intent(this,  SettingActivity.class));
        }

        if( v == purchaseButton){

            Toast.makeText(this,"Currently upgrade is unavailable.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_training_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.profile_menu_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCheckedChanged(com.suke.widget.SwitchButton view, boolean isChecked) {

        if(view == tooEasySwitch){

            changeEasyWord(isChecked);

           // Toast.makeText(this,"is checked: "+isChecked,Toast.LENGTH_SHORT).show();
        }

        if(view == spanishSwitch){


            if(Objects.requireNonNull(sp.getString("secondlanguage", "english")).equalsIgnoreCase("spanish")){
               // Toast.makeText(this,"english",Toast.LENGTH_SHORT).show();
                sp.edit().putString("secondlanguage","english").apply();

            }else {
                sp.edit().putString("secondlanguage","spanish").apply();
                //Toast.makeText(this,"Spanish",Toast.LENGTH_SHORT).show();
            }




        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();



    }

    @Override
    protected void onResume() {
        super.onResume();

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

    }
}

