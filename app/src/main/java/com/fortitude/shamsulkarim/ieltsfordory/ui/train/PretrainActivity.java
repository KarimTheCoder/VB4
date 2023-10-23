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

    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private TextView levelTextView;
    private FancyButton startTrainButton;
    private RoundCornerProgressBar progressBar;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
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
        init();
        initDatabase();
        initUIElement();
        setActivityTitle();
        checkSpanishState();
        checkTooEasyState();



    }

    private void init(){

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);




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
    private void initDatabase(){

        IELTSdatabase = new IELTSWordDatabase(this);
        TOEFLdatabase = new TOEFLWordDatabase(this);
        SATdatabase = new SATWordDatabase(this);
        GREdatabase = new GREWordDatabase(this);
    }

    private int getPercentageNumber(int percentage, int number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return (int)beginnerNum;

    }
   //----------------------------------------------------


    // Below these returns lengths of wordlist size
    private int getIELTSLength(){

        if(isIeltsChecked){
            return getResources().getStringArray(R.array.IELTS_words).length;
        }else {
            return 0;
        }


    }

    private int getTOEFLLength(){

        if(isToeflChecked){
            return getResources().getStringArray(R.array.TOEFL_words).length;
        }else {

            return 0;
        }


    }

    private int getSATLength(){

        if(isSatChecked){
            return getResources().getStringArray(R.array.SAT_words).length;
        }else {
            return 0;
        }

    }

    private int getGRELength(){

        if(isGreChecked){
            return getResources().getStringArray(R.array.GRE_words).length;

        }else {
            return 0;
        }

    }
    //----------------------------------------------------------------




    // This returns IELTS BEGINNER length

    private int  getIELTSBeginnerLearnedLength(){
        int size = 0;
        int i = 0;
        int beginnerPercentage = getBeginnerIELTSPercentage();
        Cursor res = IELTSdatabase.getData();


        while (res.moveToNext()){


            if (i == beginnerPercentage){

                break;
            }
            if(res.getString(3).equalsIgnoreCase("true")){

                size++;

            }
            i++;
        }

        IELTSdatabase.close();
        res.close();

        return size;

    }
    private int getBeginnerIELTSPercentage(){

        return getPercentageNumber(30,getIELTSLength());

    }

    //------------------------------------------------------


    // This returns TOEFL Beginner length

    private int  getTOEFLBeginnerLearnedLength(){
        int size = 0;
        int i = 0;
        int beginnerPercentage = getBeginnerTOEFLPercentage();
        Cursor res = TOEFLdatabase.getData();

        while (res.moveToNext()){

            if (i == beginnerPercentage){
                break;
            }

            if(res.getString(3).equalsIgnoreCase("true")){
                size++;
            }
            i++;
        }

        TOEFLdatabase.close();
        res.close();

        return size;

    }
    private int getBeginnerTOEFLPercentage(){

        return getPercentageNumber(30,getTOEFLLength());

    }

    //------------------------------------------------------


    // This returns SAT Beginner length

    private int  getSATBeginnerLearnedLength(){
        int size = 0;
        int i = 0;

        int beginnerPercentage = getBeginnerSATPercentage();

        Cursor res = SATdatabase.getData();


        while (res.moveToNext()){

            if (i >= beginnerPercentage){
                break;
            }

            if(res.getString(3).equalsIgnoreCase("true")){


                size++;

            }
            i++;
        }

        SATdatabase.close();
        res.close();

        return size;

    }
    private int getBeginnerSATPercentage(){

        return getPercentageNumber(30,getSATLength());

    }

    //------------------------------------------------------

    // This returns GRE Beginner length

    private int  getGREBeginnerLearnedLength(){
        int size = 0;
        int i = 0;
        int beginnerPercentage = getBeginnerGREPercentage();

        Cursor res = GREdatabase.getData();


        while (res.moveToNext()){


            if (i == beginnerPercentage){
                break;
            }


            if(res.getString(3).equalsIgnoreCase("true")){

                size++;

            }
            i++;
        }

        GREdatabase.close();
        res.close();

        return size;

    }
    private int getBeginnerGREPercentage(){

        return getPercentageNumber(30,getGRELength());

    }

    //------------------------------------------------------

//----------------------------------------------------------------------------------------------------


    // This returns IELTS intermediate length

    private int  getIELTSIntermediateLearnedLength(){
        int beginnerLength= getBeginnerIELTSPercentage();
        int intermediateLength = getIntermediateIELTSPercentage();
        int size = 0;
        int i = 0;

        Cursor res = IELTSdatabase.getData();

        while (res.moveToNext()){
            if (i >= beginnerLength+intermediateLength){
                break;
            }

            if(i >= beginnerLength){

                if(res.getString(3).equalsIgnoreCase("true")){
                    size++;
                }
            }
            i++;
        }

        IELTSdatabase.close();
        res.close();

        return size;

    }



    private int getIntermediateIELTSPercentage(){

        return getPercentageNumber(40,getIELTSLength());

    }

    //------------------------------------------------------

    // This returns TOEFL intermediate length

    private int  getTOEFLIntermediateLearnedLength(){
        int size = 0;
        int i = 0;
        int intermediateLength = getIntermediateTOEFLPercentage();
        int beginnerLength = getBeginnerTOEFLPercentage();

        Cursor res = TOEFLdatabase.getData();


        while (res.moveToNext()){

            if (i >= beginnerLength+intermediateLength){
                break;
            }

            if(i >= beginnerLength){

                if(res.getString(3).equalsIgnoreCase("true")){
                    size++;
                }
            }
            i++;
        }

        TOEFLdatabase.close();
        res.close();

        return size;

    }
    private int getIntermediateTOEFLPercentage(){

        return getPercentageNumber(40,getTOEFLLength());

    }

    //------------------------------------------------------


    // This returns SAT intermediate length

    private int  getSATIntermediateLearnedLength(){
        int size = 0;
        int i = 0;
        int intermediateLength = getIntermediateSATPercentage();
        int beginnerLength = getBeginnerSATPercentage();

            Cursor res = SATdatabase.getData();


        while (res.moveToNext()){

            if (i >= beginnerLength+intermediateLength){
                break;
            }

            if(i >= beginnerLength){

                if(res.getString(3).equalsIgnoreCase("true")){
                    size++;
                }
            }
            i++;
        }

        SATdatabase.close();
        res.close();

        return size;

    }
    private int getIntermediateSATPercentage(){

        return getPercentageNumber(40,getSATLength());

    }

    //------------------------------------------------------

    // This returns GRE intermediate length

    private int  getGREIntermediateLearnedLength(){
        int size = 0;
        int i = 0;
        int intermediateLength = getIntermediateGREPercentage();
        int beginnerLength = getBeginnerGREPercentage();

        Cursor res = GREdatabase.getData();


        while (res.moveToNext()){

            if (i >= beginnerLength+intermediateLength){
                break;
            }

            if(i >= beginnerLength){

                if(res.getString(3).equalsIgnoreCase("true")){
                    size++;
                }
            }

            i++;
        }

        GREdatabase.close();
        res.close();

        return size;

    }
    private int getIntermediateGREPercentage(){

        return getPercentageNumber(40,getGRELength());

    }

    //------------------------------------------------------




    private int getIELTSAdvanceLearnedLenth(){

        Cursor res = IELTSdatabase.getData();

        int x =  getBeginnerIELTSPercentage() + getIntermediateIELTSPercentage();
        int i = 0;
        int size = 0;


        while(res.moveToNext()){


            if(res.getString(3).equalsIgnoreCase("true")){

                if(i >= x){

                    if(i == getIELTSLength()){
                        break;
                    }
                    size++;

                }


            }
            i++;
        }
        res.close();
        IELTSdatabase.close();

        return size;

    }

    private int getTOEFLAdvanceLearnedLenth(){

        Cursor res = TOEFLdatabase.getData();

        int x =  getBeginnerTOEFLPercentage()+getIntermediateTOEFLPercentage();
        int i = 0;
        int size = 0;


        while(res.moveToNext()){


            if(res.getString(3).equalsIgnoreCase("true")){

                if(i >= x){

                    if(i == getTOEFLLength()){
                        break;
                    }
                    size++;
                }


            }
            i++;
        }

        res.close();
        TOEFLdatabase.close();

        return size;

    }

    private int getSATAdvanceLearnedLenth(){

        Cursor res = SATdatabase.getData();

        int x =  getBeginnerSATPercentage()+getIntermediateSATPercentage();
        int i = 0;
        int size = 0;


        while(res.moveToNext()){


            if(res.getString(3).equalsIgnoreCase("true")){

                if(i >= x){

                    if(i == getSATLength()){
                        break;
                    }
                    size++;

                }


            }
            i++;
        }
        res.close();
        SATdatabase.close();

        return size;

    }

    private int getGREAdvanceLearnedLenth(){

        Cursor res = GREdatabase.getData();

        int x =  getBeginnerGREPercentage()+getIntermediateGREPercentage();
        int i = 0;
        int size = 0;


        while(res.moveToNext()){


            if(res.getString(3).equalsIgnoreCase("true")){

                if(i >= x){

                    if(i == getGRELength()){
                        break;
                    }
                    size++;

                }


            }



            i++;
        }
        res.close();
        GREdatabase.close();
        return size;

    }

    private void setBeginnerLearnedwordsLengthTextView(){

        int i = getIELTSBeginnerLearnedLength();
        i = getTOEFLBeginnerLearnedLength()+i;
        i = getSATBeginnerLearnedLength()+i;
        i = getGREBeginnerLearnedLength()+i;

        int size = getBeginnerIELTSPercentage();
        size = getBeginnerTOEFLPercentage()+size;
        size = getBeginnerSATPercentage()+size;
        size = getBeginnerGREPercentage()+size;

        progressBar.setMax(size);
        progressBar.setProgress(i);
        progressCountTextview.setText(getString(R.string.pretrain_progress_text,i,size));

    }



    private void setIntermediateLearnedwordsLengthTextView(){

        int i = getIELTSIntermediateLearnedLength();
        i = getTOEFLIntermediateLearnedLength()+i;
        i = getSATIntermediateLearnedLength()+i;
        i = getGREIntermediateLearnedLength()+i;


        int size = getIntermediateIELTSPercentage();
        size = getIntermediateTOEFLPercentage()+size;
        size = getIntermediateSATPercentage()+size;
        size = getIntermediateGREPercentage()+size;


        progressBar.setMax(size);
        progressBar.setProgress(i);
        progressCountTextview.setText(getString(R.string.pretrain_progress_text,i,size));
    }




    private void setAdvanceLearnedwordsLengthTextView(){

        int i = 0;
        int size = 0;

        if(isIeltsChecked){
            i = getIELTSAdvanceLearnedLenth()+i;
            size = getBeginnerIELTSPercentage();

        }

        if( isToeflChecked){

            i = getTOEFLAdvanceLearnedLenth()+i;
            size = getBeginnerTOEFLPercentage()+size;

        }

        if(isSatChecked){
            i = getSATAdvanceLearnedLenth()+i;
            size = getBeginnerSATPercentage()+size;
        }

        if(isGreChecked){
            i = getGREAdvanceLearnedLenth()+i;
            size = getBeginnerGREPercentage()+size;
        }


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

