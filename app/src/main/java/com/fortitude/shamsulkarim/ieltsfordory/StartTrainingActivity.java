package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class StartTrainingActivity extends AppCompatActivity implements View.OnClickListener{

    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;
    private int learnedWordCount, totalWordCount;
    private FancyButton getPro;

    private TextView learnedWordText, leftWordText;
    private RoundCornerProgressBar progressBar;
    private Toolbar toolbar;
    private List<String> IELTSlearnedDatabase, TOEFLlearnedDatabase, SATlearnedDatabase, GRElearnedDatabase;
    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private ArrayList<Word> words = new ArrayList<>();


    int i = 0;
    int totalMistakeCount;
    //    BillingProcessor bp;
    SharedPreferences sp;
    String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_training);


        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        level = sp.getString("level", "");
        learnedWordCount = sp.getInt(level, 0);

        if (!sp.contains("totalWrongCount" + level)) {

            sp.edit().putInt("totalWrongCount" + level, 0).apply();

        } else {
            totalMistakeCount = sp.getInt("totalWrongCount" + level, 0);

        }


        initialization();
        levelPicker(level);
        increaseNum();
        gettingResources();
        addingLearnedDatabase();
        getWords();



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


    public void onStartTraining(View view) {

        if(words.size() == 0){

            Toast.makeText(this,"No more words to learn",Toast.LENGTH_SHORT).show();
        }else {

            Intent intent = new Intent(this, NewTrain.class);
            this.startActivity(intent);
            this.finish();

        }

    }

    private void initialization() {

        IELTSdatabase = new IELTSWordDatabase(this);
        TOEFLdatabase = new TOEFLWordDatabase(this);
        SATdatabase = new SATWordDatabase(this);
        GREdatabase = new GREWordDatabase(this);

        getPro = findViewById(R.id.download_pro);
        getPro.setOnClickListener(this);



        toolbar = findViewById(R.id.start_training_toolbar);

        learnedWordText = findViewById(R.id.start_training_learned_word_text);
        leftWordText = findViewById(R.id.start_training_left_word_text);
        progressBar = findViewById(R.id.start_training_progress);

        progressBar.setProgressColor(getResources().getColor(R.color.middleColor));
        progressBar.setProgressBackgroundColor(getResources().getColor(R.color.grey));


        try {
            setSupportActionBar(toolbar);
        } catch (NullPointerException i) {

        }



        // bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlOSgGRFe8nOo3UfthotojrY4Vb9nBXYjqNHHR8ZabmMdpMVQWjG5SpuaLCQ1e+AjX+2Zsw6t9AHt9IGxIJuTmqlTd2sr7gCvtKwn6nIHvkGzOvYUsaVXteUoGEJP9lPMvja3vo0CxKXLwtCHxE1rCeKa1Wr1E/H0gMXH6eS91O0VRjzGBW/e+zgYm0ek/0g1bt9e7Vz6P3BfOj6DFhfzYAoaMhT2iw8kOr8yups6aAkkAprvyoUz78Au6u141y9LGYgEZK0mEXB83vOWetlrigJmGyfyrKXfGMZMIxcFdW49ZdjYBIlomw++9MEmMb1KChcTQldKl2BZ4ATRw5BCswIDAQAB", this);


    }

    private void levelPicker(String level) {

        if (level.equalsIgnoreCase("beginner")) {
            totalWordCount = getResources().getStringArray(R.array.IELTS_words).length;


            toolbar.setTitle("Beginner");



        } else if (level.equalsIgnoreCase("intermediate")) {

            totalWordCount = getResources().getStringArray(R.array.TOEFL_words).length;


            toolbar.setTitle("Intermediate");



        } else if (level.equalsIgnoreCase("advance")) {


            toolbar.setTitle("Advance");
            totalWordCount = getResources().getStringArray(R.array.SAT_words).length;



        }
    }

    private void increaseNum() {

        final Handler handler = new Handler();



    }


    @Override
    public void onClick(View v) {

        if(v == getPro){

            Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);
        }

    }

//    @Override
//    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//        Toast.makeText(this, "Purchased", Toast.LENGTH_SHORT).show();
//        sp.edit().putInt("cb", 1).apply();
//    }
//
//    @Override
//    public void onPurchaseHistoryRestored() {
//
//    }
//
//    @Override
//    public void onBillingError(int errorCode, @Nullable Throwable error) {
//
//    }
//
//    @Override
//    public void onBillingInitialized() {
//
//    }


    private void getWords() {

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSbeginnerNumber = 0;
        int IELTSintermediateNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int TOEFLintermediateNumber = 0;
        int SATbeginnerNumber = 0;
        int SATintermediateNumber = 0;
        int GREbeginnerNumber = 0;
        int GREintermediateNumber = 0;

        int extraIeltsNuBeginner = 0;
        int extraIeltsIntermediate = 0;

        int extraToeflBeginner = 0;
        int extraToeflIntermediate = 0;

        int extraSatBeginner = 0;
        int extraSatIntermediate = 0;

        int extraGreBeginner = 0;
        int extraGreIntermediate = 0;

        Toast.makeText(this,"Ielts fdffdsf",Toast.LENGTH_LONG).show();

        if(isIeltsChecked){
            Toast.makeText(this,"Ielts checked",Toast.LENGTH_LONG).show();

            extraIeltsNuBeginner = 1;
            extraIeltsIntermediate = 2;

            IELTSbeginnerNumber = (int)getPercentageNumber(30, IELTSwordSize);
            IELTSintermediateNumber = (int)getPercentageNumber(40, IELTSwordSize);


        }

        if(isToeflChecked){
            extraToeflBeginner= 1;
            extraToeflIntermediate = 2;

            TOEFLbeginnerNumber = (int)getPercentageNumber(30, TOEFLwordSize);
            TOEFLintermediateNumber =(int) getPercentageNumber(40, TOEFLwordSize);
        }

        if(isSatChecked){

            extraSatBeginner = 1;
            extraSatIntermediate = 2;

            SATbeginnerNumber = (int)getPercentageNumber(30, SATwordSize);
            SATintermediateNumber = (int)getPercentageNumber(40, SATwordSize);
        }

        if(isGreChecked){

            extraGreBeginner = 1;
            extraGreIntermediate = 1;

            GREbeginnerNumber = (int) getPercentageNumber(30, GREwordSize);
            GREintermediateNumber = (int) getPercentageNumber(40, GREwordSize);
        }
        if( level.equalsIgnoreCase("beginner")){

            addIELTSwords(0,IELTSbeginnerNumber);
            addTOEFLwords(0,TOEFLbeginnerNumber);
            addSATwords(0,SATbeginnerNumber);
            addGREwords(0,GREbeginnerNumber);


            int i = extraSatBeginner+extraGreBeginner+extraToeflBeginner+extraIeltsNuBeginner+(int)IELTSbeginnerNumber+(int)TOEFLbeginnerNumber+(int)SATbeginnerNumber+(int)GREbeginnerNumber;



            learnedWordText.setText((i-words.size())+" words learned");
            leftWordText.setText(words.size()+" words left");
            progressBar.setMax(i);
            progressBar.setProgress(i-words.size());

        }




        if( level.equalsIgnoreCase("intermediate")){

            addIELTSwords(IELTSbeginnerNumber, +IELTSbeginnerNumber);

            int indermediateEndPoint= IELTSintermediateNumber+IELTSbeginnerNumber;
            Toast.makeText(this,"Interm: startPoint: "+IELTSbeginnerNumber+
                    "\nEndPoint: "+indermediateEndPoint,Toast.LENGTH_LONG).show();

            addTOEFLwords(TOEFLbeginnerNumber,TOEFLbeginnerNumber+TOEFLintermediateNumber);

            int TOEFLindermediateEndPoint= TOEFLbeginnerNumber+TOEFLintermediateNumber;
            Toast.makeText(this,"TOEFL Interm: startPoint: "+IELTSbeginnerNumber+
                    "\nEndPoint: "+indermediateEndPoint,Toast.LENGTH_LONG).show();
            addSATwords(SATbeginnerNumber,SATbeginnerNumber+SATintermediateNumber);
            addGREwords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);

            int i = extraSatIntermediate+extraGreIntermediate+extraToeflIntermediate+extraIeltsNuBeginner+(int)IELTSintermediateNumber+(int)TOEFLintermediateNumber+(int)SATintermediateNumber+(int)GREintermediateNumber;




            learnedWordText.setText((i-words.size())+" words learned");


            leftWordText.setText(words.size()+" words left");
            progressBar.setMax(i);
            progressBar.setProgress(i-words.size());

        }

        if( level.equalsIgnoreCase("advance")){

            addIELTSwords(IELTSintermediateNumber+IELTSbeginnerNumber, IELTSwordSize);

            int advanceStartPoint = IELTSintermediateNumber+IELTSbeginnerNumber;
            Toast.makeText(this,"ADV: startPoint: "+advanceStartPoint+
                    "\nEndPoint: "+IELTSwordSize,Toast.LENGTH_LONG).show();

            addTOEFLwords(TOEFLintermediateNumber+TOEFLbeginnerNumber, TOEFLwordSize);
            addSATwords(SATintermediateNumber+SATbeginnerNumber, SATwordSize);
            addGREwords(GREintermediateNumber+GREbeginnerNumber,GREwordSize);

            int i = extraSatBeginner+extraGreBeginner+extraToeflBeginner+extraIeltsNuBeginner+(int)IELTSbeginnerNumber+(int)TOEFLbeginnerNumber+(int)SATbeginnerNumber+(int)GREbeginnerNumber;




            learnedWordText.setText((i-words.size())+" words learned");
            leftWordText.setText(words.size()+" words left");
            progressBar.setMax(i);
            progressBar.setProgress(i-words.size());
        }


    }


    private int getPercentageNumber(int percentage, int number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return (int)beginnerNum;

    }

    private void addingLearnedDatabase()  {

        Cursor beginnerRes = IELTSdatabase.getData();
        Cursor TOEFLres = TOEFLdatabase.getData();
        Cursor SATres = SATdatabase.getData();
        Cursor GREres = GREdatabase.getData();

        IELTSlearnedDatabase.clear();
        TOEFLlearnedDatabase.clear();
        SATlearnedDatabase.clear();
        GRElearnedDatabase.clear();

        while (beginnerRes.moveToNext()) {

            IELTSlearnedDatabase.add(beginnerRes.getString(3));

        }
        //Closing databases
        beginnerRes.close();
        IELTSdatabase.close();

        while (TOEFLres.moveToNext()) {

            TOEFLlearnedDatabase.add(TOEFLres.getString(3));

        }

        TOEFLres.close();
        TOEFLdatabase.close();

        while (SATres.moveToNext()) {

            SATlearnedDatabase.add(SATres.getString(3));

        }
        SATres.close();
        SATdatabase.close();

        while (GREres.moveToNext()) {

            GRElearnedDatabase.add(GREres.getString(3));

        }
        GREres.close();
        GREdatabase.close();


    }

    private  void addIELTSwords(int startPoint,int IELTSbeginnerNumber){

        int IELTSLearnedDatabaseSize = IELTSlearnedDatabase.size();
        Toast.makeText(this,"IELTSBeginnerNumber: "+IELTSbeginnerNumber+" Learned Database size: "
        +IELTSLearnedDatabaseSize,Toast.LENGTH_LONG).show();

        if( IELTSbeginnerNumber > IELTSLearnedDatabaseSize){

            IELTSbeginnerNumber = IELTSLearnedDatabaseSize;
        }


            if(isIeltsChecked){
                for(int i = (int) startPoint; i  < IELTSbeginnerNumber; i++){


                    if( IELTSlearnedDatabase.get(i).equalsIgnoreCase("false")){

                        words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"",
                                IELTSpronunArray[i],IELTSgrammarArray[i], IELTSexample1array[i],
                                IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],
                                IELTSposition[i], IELTSlearnedDatabase.get(i),""));
                    }

                }

            }



    }

    private  void addTOEFLwords(int startPoint, int TOEFLbeginnerNumber){


        if(isToeflChecked){


            for(int i = (int) startPoint; i < TOEFLbeginnerNumber; i++){


                if( TOEFLlearnedDatabase.get(i).equalsIgnoreCase("false")) {
                    words.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], TOEFLlearnedDatabase.get(i),""));


                }
            }
        }



    }

    private void addSATwords (int startPoint ,int SATbeginnerNumber){


        if(isSatChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){


                if( SATlearnedDatabase.get(i).equalsIgnoreCase("false")) {

                    words.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], SATlearnedDatabase.get(i),""));

                }

            }

        }





    }

    private void addGREwords (int startPoint ,int SATbeginnerNumber){


        if(isGreChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){



                if( GRElearnedDatabase.get(i).equalsIgnoreCase("false")) {

                    words.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], GRElearnedDatabase.get(i),""));
                }

            }

        }





    }

    private void gettingResources() {


        IELTSwordArray = getResources().getStringArray(R.array.IELTS_words);
        IELTStranslationArray = getResources().getStringArray(R.array.IELTS_translation);
        IELTSgrammarArray = getResources().getStringArray(R.array.IELTS_grammar);
        IELTSpronunArray = getResources().getStringArray(R.array.IELTS_pronunciation);
        IELTSexample1array = getResources().getStringArray(R.array.IELTS_example1);
        IELTSexample2Array = getResources().getStringArray(R.array.IELTS_example2);
        IELTSexample3Array = getResources().getStringArray(R.array.IELTS_example3);
        IELTSvocabularyType = getResources().getStringArray(R.array.IELTS_level);
        IELTSposition = getResources().getIntArray(R.array.IELTS_position);


        TOEFLwordArray = getResources().getStringArray(R.array.TOEFL_words);
        TOEFLtranslationArray = getResources().getStringArray(R.array.TOEFL_translation);
        TOEFLgrammarArray = getResources().getStringArray(R.array.TOEFL_grammar);
        TOEFLpronunArray = getResources().getStringArray(R.array.TOEFL_pronunciation);
        TOEFLexample1array = getResources().getStringArray(R.array.TOEFL_example1);
        TOEFLexample2Array = getResources().getStringArray(R.array.TOEFL_example2);
        TOEFLexample3Array = getResources().getStringArray(R.array.TOEFL_example3);
        TOEFLvocabularyType = getResources().getStringArray(R.array.TOEFL_level);
        TOEFLposition = getResources().getIntArray(R.array.TOEFL_position);


        SATwordArray = getResources().getStringArray(R.array.SAT_words);
        SATtranslationArray = getResources().getStringArray(R.array.SAT_translation);
        SATgrammarArray = getResources().getStringArray(R.array.SAT_grammar);
        SATpronunArray = getResources().getStringArray(R.array.SAT_pronunciation);
        SATexample1array = getResources().getStringArray(R.array.SAT_example1);
        SATexample2Array = getResources().getStringArray(R.array.SAT_example2);
        SATexample3Array = getResources().getStringArray(R.array.SAT_example3);
        SATvocabularyType = getResources().getStringArray(R.array.SAT_level);
        SATposition = getResources().getIntArray(R.array.SAT_position);


        GREwordArray = getResources().getStringArray(R.array.GRE_words);
        GREtranslationArray = getResources().getStringArray(R.array.GRE_translation);
        GREgrammarArray = getResources().getStringArray(R.array.GRE_grammar);
        GREpronunArray = getResources().getStringArray(R.array.GRE_pronunciation);
        GREexample1array = getResources().getStringArray(R.array.GRE_example1);
        GREexample2array = getResources().getStringArray(R.array.GRE_example2);
        GREexample3Array = getResources().getStringArray(R.array.GRE_example3);
        GREvocabularyType = getResources().getStringArray(R.array.GRE_level);
        GREposition = getResources().getIntArray(R.array.GRE_position);

        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);

        IELTSlearnedDatabase = new ArrayList<>();
        TOEFLlearnedDatabase = new ArrayList<>();
        SATlearnedDatabase = new ArrayList<>();
        GRElearnedDatabase = new ArrayList<>();

    }



}