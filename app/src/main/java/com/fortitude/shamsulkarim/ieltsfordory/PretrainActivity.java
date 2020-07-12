package com.fortitude.shamsulkarim.ieltsfordory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.NewSettingActivity;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;

import mehdi.sakout.fancybuttons.FancyButton;

public class PretrainActivity extends AppCompatActivity implements View.OnClickListener {

    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private TextView learnedWordTextView, leftWordTextView;
    private FancyButton startTrainButton;
    private RoundCornerProgressBar progressBar;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private SharedPreferences sp;
    private FancyButton getProButton;
    private CardView goProCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_training);

        init();
        initDatabase();
        initUIElement();
        setActivityTitle();



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

        Toolbar toolbar = findViewById(R.id.start_training_toolbar);
        setSupportActionBar(toolbar);


        if (level.equalsIgnoreCase("beginner")) {

            toolbar.setTitle("Beginner");
            setBeginnerLearnedwordsLengthTextView();
            setTextBeginnerTotalWordCount();

        }

        else if (level.equalsIgnoreCase("intermediate")) {

            toolbar.setTitle("Intermediate");
            setIntermediateLearnedwordsLengthTextView();


        }

        else if (level.equalsIgnoreCase("advance")) {

            setAdvanceLearnedwordsLengthTextView();
            toolbar.setTitle("Advance");

        }
    }

    private void initUIElement(){

        learnedWordTextView = findViewById(R.id.start_training_learned_word_text);
        leftWordTextView = findViewById(R.id.start_training_left_word_text);

        startTrainButton = findViewById(R.id.no_word_home);
        startTrainButton.setOnClickListener(this);

        progressBar = findViewById(R.id.start_training_progress);
        progressBar.setProgressColor(getResources().getColor(R.color.middleColor));
        progressBar.setProgressBackgroundColor(getResources().getColor(R.color.grey));

        goProCardView = findViewById(R.id.getPro);

        if(BuildConfig.FLAVOR.equalsIgnoreCase("pro")){
            goProCardView.setVisibility(View.GONE);
        }

        getProButton = findViewById(R.id.download_pro);
        getProButton.setOnClickListener(this);


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

        int x = size-i;
        progressBar.setMax(size);
        progressBar.setProgress(i);
        leftWordTextView.setText(x+" word left");
        learnedWordTextView.setText(i+" word learned");

    }

    private void setTextBeginnerTotalWordCount(){


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


        int x = size-i;
        progressBar.setMax(size);
        progressBar.setProgress(i);
        leftWordTextView.setText(x+" word left");
        learnedWordTextView.setText(i+" word learned");

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


        int x = size-i;

        progressBar.setMax(size);
        progressBar.setProgress(i);
        leftWordTextView.setText(x+" word left");
        learnedWordTextView.setText(i+" word learned");

    }

    private void setTextAdvanceTotalWordCount(){





    }


    @Override
    public void onClick(View v) {

        if(v == startTrainButton){

            // No words to learn required
            Intent intent = new Intent(this, NewTrain.class);
            this.startActivity(intent);
            this.finish();
        }

        if(v == getProButton){

            Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile_menu_settings) {
            startActivity(new Intent(this, NewSettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
