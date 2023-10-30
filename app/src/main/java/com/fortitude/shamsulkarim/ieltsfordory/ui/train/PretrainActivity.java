package com.fortitude.shamsulkarim.ieltsfordory.ui.train;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import com.fortitude.shamsulkarim.ieltsfordory.ui.NewSettingActivity;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class PretrainActivity extends AppCompatActivity implements View.OnClickListener, com.suke.widget.SwitchButton.OnCheckedChangeListener, PurchasesUpdatedListener {


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

    // Billing
    private BillingClient billingClient;

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

      //  learnedWordTextView = findViewById(R.id.start_training_learned_word_text);
       // leftWordTextView = findViewById(R.id.start_training_left_word_text);

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

            this.startActivity(new Intent(this,  NewSettingActivity.class));
        }

        // Billing
        if(v == purchaseButton){

            initiatePurchase();

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
            startActivity(new Intent(this, NewSettingActivity.class));
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

    // Billing
    public void setBillingClient(){

        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    List<String> skuList = new ArrayList<>();
                    skuList.add("test_product");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(@NotNull BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    // Process the result.


                                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                                        for(SkuDetails skuDetails : skuDetailsList){

                                            purchaseButton.setText(skuDetails.getPrice());
                                            //Toast.makeText(PretrainActivity.this,billingResult.getResponseCode()+" Getting Data for setting price"+skuDetailsList.size(),Toast.LENGTH_SHORT).show();

                                        }
                                    }


                                }
                            });
                    //Toast.makeText(PretrainActivity.this,"BILLING | startConnection | RESULT OK",Toast.LENGTH_SHORT).show();
                }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){

                    Toast.makeText(getApplicationContext(),"Please sign in to Google Play Store",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(PretrainActivity.this,"BILLING | startConnection | RESULT: $billingResponseCodexx"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                //Toast.makeText(PretrainActivity.this,"BILLING | onBillingServiceDisconnected | DISCONNECTED",Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {

            for (Purchase purchase : purchases) {

                handlePurchase(purchase,billingClient);

               // Toast.makeText(this, "Handle Purchase", Toast.LENGTH_SHORT).show();
            }
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(this, "Purchase cancelled", Toast.LENGTH_SHORT).show();


        } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE){
            // Handle any other error codes.
            Toast.makeText(this, "Please check your internet connection"+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();

        }
        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){

            Toast.makeText(this, "You already own this product, go to settings to restore it", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "Other error "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();


    }


    private void initiatePurchase(){
        List<String> skuList = new ArrayList<> ();
        skuList.add("test_product");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NotNull BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {


                        if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {

                            // if everything is ok

                            for(SkuDetails skuDetails : skuDetailsList){

                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build();

                                int responseCode = billingClient.launchBillingFlow(PretrainActivity.this, billingFlowParams).getResponseCode();
                                Log.i("Billing Response Code","Billing response code: "+responseCode);
                            }

                        }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){

                            Toast.makeText(getApplicationContext(),"Please sign in to Google Play Store",Toast.LENGTH_SHORT).show();
                        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED){

                            Toast.makeText(getApplicationContext(),"Play Store service is not connected now",Toast.LENGTH_SHORT).show();

                        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE){

                            Toast.makeText(getApplicationContext(),"Service unavailable",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.i("Billing Response Code","Billing response code: "+billingResult.getResponseCode());
                           // Toast.makeText(PretrainActivity.this,"BILLING | startConnection | RESULT: $billingResponseCode"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();

                        }
                    }


                });
    }

    private void handlePurchase(Purchase purchase, BillingClient billingClient){

        if( purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){

            if (!purchase.isAcknowledged()) {

                if(!sp.contains("purchase")){
                    sp.edit().putBoolean("purchase",true).apply();
                    purchaseCardView.setVisibility(View.GONE);
                    purchaseThankYou.setVisibility(View.VISIBLE);
                }

                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {

                        //Toast.makeText(PretrainActivity.this,"On Acknowledge",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(billingClient != null){
            billingClient.endConnection();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        if(!sp.contains("purchase")){

            setBillingClient();

        }
    }
}

