package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import az.plainpie.PieView;

/**
 * Created by Shamsul Karim on 13-Dec-16.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{


    // UI
    private TextView trialStatusTextView;
    private CardView advanceCard,intermediateCard,beginnerCard;
    private PieView advancePie, intermediatePie, beginnerPie;
    private SharedPreferences sp;
    //private RoundCornerProgressBar homeProgress;

    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;
    private List<String> IELTSlearnedDatabase, TOEFLlearnedDatabase, SATlearnedDatabase, GRElearnedDatabase;

    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private ArrayList<Word> words = new ArrayList<>();
    private int learnedCount, totalWordCount, totalLearned;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.home_fragment,container,false);
        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        Window window = Objects.requireNonNull(getActivity()).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.primary_background_color));

        if(!sp.contains("home")){
            sp.edit().putBoolean("home",true).apply();
        }



        initialization();
        addingLearnedDatabase();

        advanceCard = v.findViewById(R.id.advance_card_home);
        intermediateCard = v.findViewById(R.id.intermediate_card_home);
        beginnerCard = v.findViewById(R.id.beginner_card_home);
        advanceCard.setPreventCornerOverlap(false);
        intermediateCard.setPreventCornerOverlap(false);
        beginnerCard.setPreventCornerOverlap(false);
        trialStatusTextView =v.findViewById(R.id.trial_status);
        advancePie = v.findViewById(R.id.advance_pie);
        intermediatePie = v.findViewById(R.id.intermediate_pie);
        beginnerPie = v.findViewById(R.id.profile_pie_view);
     //   homeProgress = v.findViewById(R.id.home_progress);



        advanceCard.setOnClickListener(this);
        intermediateCard.setOnClickListener(this);
        beginnerCard.setOnClickListener(this);


        getWords();



        checkTrialStatus();
        return v;
    }


    @Override
    public void onClick(View v) {


        if(v == advanceCard){

            sp.edit().putString("level","advance").apply();
            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));


        }

        if(v ==intermediateCard){

            sp.edit().putString("level","intermediate").apply();
            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));


        }

        if(v == beginnerCard){

            sp.edit().putString("level","beginner").apply();
            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));


        }
    }

    private void setPercentageAndProgress(){

        int advaceWordSize = getResources().getStringArray(R.array.SAT_words).length;
        int intermediateWordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int beginnerWordSize = getResources().getStringArray(R.array.IELTS_words).length;

        int advanceWordLearnedSize = sp.getInt("advance",0);
        int intermediateLearnedSize = sp.getInt("intermediate",0);
        int beginnerLearnedSize = sp.getInt("beginner",0);

        float advancePercentage = (advanceWordLearnedSize*100)/advaceWordSize;
        float intermediatePercentage = (intermediateLearnedSize*100)/intermediateWordSize;
        float beginnerPercentage = (beginnerLearnedSize*100)/beginnerWordSize;

        advancePie.setMaxPercentage(100);
        advancePie.setPercentage(advancePercentage);

        intermediatePie.setMaxPercentage(100);
        intermediatePie.setPercentage(intermediatePercentage);

        beginnerPie.setMaxPercentage(100);
        beginnerPie.setPercentage(beginnerPercentage);

//        homeProgress.setProgressBackgroundColor(getResources().getColor(R.color.deepGrey));
//        homeProgress.setProgressColor(getResources().getColor(R.color.colorPrimary));
//        homeProgress.setMax(advaceWordSize+intermediateWordSize+beginnerWordSize);
//        homeProgress.setProgress(advanceWordLearnedSize+intermediateLearnedSize+beginnerLearnedSize);






    }


    private void initialization(){


        IELTSdatabase = new IELTSWordDatabase(getContext());
        TOEFLdatabase = new TOEFLWordDatabase(getContext());
        SATdatabase = new SATWordDatabase(getContext());
        GREdatabase = new GREWordDatabase(getContext());

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


    private void getWords() {

        double IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        double TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        double SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        double GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        double IELTSbeginnerNumber = 0;
        double IELTSintermediateNumber = 0;
        double TOEFLbeginnerNumber = 0;
        double TOEFLintermediateNumber = 0;
        double SATbeginnerNumber = 0;
        double SATintermediateNumber = 0;
        double GREbeginnerNumber = 0;
        double GREintermediateNumber = 0;

        if(isIeltsChecked){
            IELTSbeginnerNumber = getPercentageNumber(30d, IELTSwordSize);
            IELTSintermediateNumber = getPercentageNumber(40d, IELTSwordSize);

            totalWordCount = getResources().getStringArray(R.array.IELTS_words).length;
        }

        if(isToeflChecked){

             TOEFLbeginnerNumber = getPercentageNumber(30d, TOEFLwordSize);
             TOEFLintermediateNumber = getPercentageNumber(40d, TOEFLwordSize);

             totalWordCount = totalWordCount+ getResources().getStringArray(R.array.TOEFL_words).length;
        }

        if(isSatChecked){

             SATbeginnerNumber = getPercentageNumber(30d, SATwordSize);
             SATintermediateNumber = getPercentageNumber(40d, SATwordSize);
             totalWordCount = totalWordCount+ getResources().getStringArray(R.array.SAT_words).length;

        }

        if(isGreChecked){


             GREbeginnerNumber = getPercentageNumber(30d, GREwordSize);
             GREintermediateNumber = getPercentageNumber(40d, GREwordSize);

             totalWordCount = totalWordCount+ getResources().getStringArray(R.array.GRE_words).length;
        }







            addIELTSwords(0d,IELTSbeginnerNumber);
            addTOEFLwords(0d,TOEFLbeginnerNumber);
            addSATwords(0d,SATbeginnerNumber);
            addGREwords(0d,GREbeginnerNumber);

            float i = (float)IELTSbeginnerNumber+(float)TOEFLbeginnerNumber+(float)SATbeginnerNumber+(float)GREbeginnerNumber;
            float learned = i-words.size();
            learnedCount =+ (int)learned;
            float percentage = (learned*100)/i;

            beginnerPie.setMaxPercentage(100);
            beginnerPie.setPercentage(percentage);






            words.clear();
            addIELTSwords(IELTSbeginnerNumber,IELTSintermediateNumber+IELTSbeginnerNumber);
            addTOEFLwords(TOEFLbeginnerNumber,TOEFLbeginnerNumber+TOEFLintermediateNumber);
            addSATwords(SATbeginnerNumber,SATbeginnerNumber+SATintermediateNumber);
            addGREwords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);

            i = (float)IELTSintermediateNumber+(float)TOEFLintermediateNumber+(float)SATintermediateNumber+(float)GREintermediateNumber;
            learned = i-words.size();
            learnedCount =+ (int) learned;
            percentage = (learned*100)/i;

            intermediatePie.setMaxPercentage(100);
            intermediatePie.setPercentage(percentage);






            words.clear();
            addIELTSwords(IELTSintermediateNumber+IELTSbeginnerNumber, IELTSwordSize);
            addTOEFLwords(TOEFLintermediateNumber+TOEFLbeginnerNumber, TOEFLwordSize);
            addSATwords(SATintermediateNumber+SATbeginnerNumber, SATwordSize);
            addGREwords(GREintermediateNumber+GREbeginnerNumber,GREwordSize);

            i = (int)IELTSbeginnerNumber+(int)TOEFLbeginnerNumber+(int)SATbeginnerNumber+(int)GREbeginnerNumber;

            learned = i-words.size();
            learnedCount =+ (int)learned;
            percentage = (learned*100)/i;


         //   Toast.makeText(getContext(),"percentage: "+percentage+ " i: "+i,Toast.LENGTH_LONG).show();

            advancePie.setMaxPercentage(100);
            advancePie.setPercentage(percentage);
            words.clear();


//        homeProgress.setProgressBackgroundColor(getResources().getColor(R.color.deepGrey));
//        homeProgress.setProgressColor(getResources().getColor(R.color.colorPrimary));
//        homeProgress.setMax(totalWordCount);
//        homeProgress.setProgress(totalLearned);



    }


    private double getPercentageNumber(double percentage, double number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return beginnerNum;

    }

    private void addingLearnedDatabase() {

        Cursor beginnerRes = IELTSdatabase.getData();
        Cursor TOEFLres = TOEFLdatabase.getData();
        Cursor SATres = SATdatabase.getData();
        Cursor GREres = GREdatabase.getData();

        while (beginnerRes.moveToNext()) {



            if(beginnerRes.getString(3).equalsIgnoreCase("True")){
                totalLearned++;
            }
            IELTSlearnedDatabase.add(beginnerRes.getString(3));

        }

        while (TOEFLres.moveToNext()) {

            if(TOEFLres.getString(3).equalsIgnoreCase("True")){
                totalLearned++;
            }
            TOEFLlearnedDatabase.add(TOEFLres.getString(3));

        }

        while (SATres.moveToNext()) {

            if(SATres.getString(3).equalsIgnoreCase("True")){
                totalLearned++;
            }
            SATlearnedDatabase.add(SATres.getString(3));

        }

        while (GREres.moveToNext()) {

            if(GREres.getString(3).equalsIgnoreCase("True")){
                totalLearned++;
            }

            GRElearnedDatabase.add(GREres.getString(3));

        }


    }

    private  void addIELTSwords(double startPoint,double IELTSbeginnerNumber){



        if(isIeltsChecked){
            for(int i = (int) startPoint; i  < IELTSbeginnerNumber; i++){


                try{
                    if( IELTSlearnedDatabase.get(i).equalsIgnoreCase("false")){

                        words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], IELTSlearnedDatabase.get(i),""));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }


    }

    private  void addTOEFLwords(double startPoint, double TOEFLbeginnerNumber){


        if(isToeflChecked){


            for(int i = (int) startPoint; i < TOEFLbeginnerNumber; i++){

                try{

                    if( TOEFLlearnedDatabase.get(i).equalsIgnoreCase("false")) {
                        words.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], TOEFLlearnedDatabase.get(i),""));


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }



    }

    private void addSATwords (double startPoint ,double SATbeginnerNumber){


        try{

            if(isSatChecked){

                for(int i = (int) startPoint; i < SATbeginnerNumber; i++){


                    try{

                        if( SATlearnedDatabase.get(i).equalsIgnoreCase("false")) {

                            words.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], SATlearnedDatabase.get(i),""));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void addGREwords (double startPoint ,double SATbeginnerNumber){


        if(isGreChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){


                try{

                    if( GRElearnedDatabase.get(i).equalsIgnoreCase("false")) {

                        words.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], GRElearnedDatabase.get(i),""));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
    }



    // Check Trial State
    private void checkTrialStatus(){

        String trialStatus;

        if(sp.contains("trial_end_date")){

            Date today = Calendar.getInstance().getTime();

            long endMillies = sp.getLong("trial_end_date",0) ;
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;

            Toast.makeText(getContext(),"Days Left: "+ TimeUnit.DAYS.convert(leftMillies,TimeUnit.MILLISECONDS),Toast.LENGTH_SHORT).show();

            if(!sp.contains("purchase")){
                if(leftMillies >=0){

                    trialStatus = "active";
                    trialStatusTextView.setText("Trial Mode: "+trialStatus);

                }
                else {

                    showTrialFinished();

                    trialStatus = "ended";
                    trialStatusTextView.setText("Trial Mode: "+trialStatus);
                }
            }else {

                trialStatus = "PREMIUM+";
                trialStatusTextView.setText(trialStatus);
                trialStatusTextView.setTextColor(getResources().getColor(R.color.green));
            }


        }





    }


    private void showTrialFinished(){

        if(!sp.contains("home_fragment_trail_end")){

            new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.red)
                    .setButtonsColorRes(R.color.secondary_text_color)
                    .setIcon(R.drawable.ic_information)
                    .setIconTintColor(getResources().getColor(R.color.primary_text_color_white))
                    .setNegativeButton("Continue with basic", new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Continue with Basic", Toast.LENGTH_SHORT).show();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            sp.edit().putInt("DarkMode",0).apply();
                        }
                    })
                    .setTitle(R.string.trial_ended)
                    .setMessage(R.string.trial_ended_description)
                    .setNeutralButtonColor(getResources().getColor(R.color.green))
                    .setNeutralButton("Upgrade", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "positive clicked", Toast.LENGTH_SHORT).show();
                        }
                    })


                    .show();

            sp.edit().putBoolean("home_fragment_trail_end",true).apply();

        }

    }
}
