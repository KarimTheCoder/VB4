package com.fortitude.shamsulkarim.ieltsfordory;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.Practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.Fab;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.gordonwong.materialsheetfab.MaterialSheetFab;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class LearnedWords extends Fragment implements View.OnClickListener{


    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Object> words = new ArrayList<>();
    private boolean isShowingFabOption = false;
    private List<Word> practiceWords = new ArrayList<>();
    private boolean connected = false;
    private List<String> IELTSlearnedDatabase, TOEFLlearnedDatabase, SATlearnedDatabase, GRElearnedDatabase;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private TextView havenotlearned;
    private ImageView noLearnedImage;
    private List<String> beginnerFav;
    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private SharedPreferences sp;
    private Spinner spinner;
    private MaterialSheetFab materialSheetFab;
    private Fab fab;
    private int selection;
    private FancyButton startLearning;
    private String level;
    private int languageId;
    private FloatingSearchView sv;

    private List<String> bWord,aWord,iWord;
    private List<String> ieltsFavPosition, toeflFavPosition, satFavPosition, greFavPosition;
    private TextView item1, item2, item3;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learned_words,container,false);

        initialization(v);
        gettingResources();
        getfavoriteDatabasePosition();
        addingLearnedDatabase();





        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
 @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");
                    fabAnimation(false);

                    if(isShowingFabOption){

//                        fam.animate().rotation(-20f);
                        isShowingFabOption = false;

                    }


                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    fabAnimation(true);


                } else {

                    System.out.println("No Vertical Scrolled");
                }
            }
        });

        // Spinner

        if (!sp.contains("prevLearnedSelection")) {



            sp.edit().putInt("prevLearnedSelection", 0).apply();

        } else {

            selection = sp.getInt("prevLearnedSelection", 0);
        }


        if(selection == 0){
            level = "beginner";






           // beginnerWordInitialization();
           getBeginnerWordData();
        }
        if( selection == 1){

            level = "intermediate";

           getIntermediateWordData();
        }

        if( selection == 2){
            level = "advance";

            getAdvanceWordData();
        }
        spinner.setSelection(selection);




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if (i == 0) {


                    sp.edit().putInt("prevLearnedSelection",0).apply();
                   // beginnerWordInitialization();
                   getBeginnerWordData();
                }
                if (i == 1) {


                    sp.edit().putInt("prevLearnedSelection",1).apply();
                    getIntermediateWordData();
                }

                if (i == 2) {

                    sp.edit().putInt("prevLearnedSelection",2).apply();
                    getAdvanceWordData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //SEARCH
        settingUpSearch();


        return v;

        //---------------------------------------------------------------------------------------------------
    }

    // INITIALIZATION

    private void initialization(View v){

        setUpFab(v);

        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);



        languageId = sp.getInt("language",0);
        beginnerFav = new ArrayList<>();
        havenotlearned = (TextView)v.findViewById(R.id.havenotlearned);
        havenotlearned.setVisibility(View.INVISIBLE);
        noLearnedImage = (ImageView)v.findViewById(R.id.no_learned_image);
        noLearnedImage.setVisibility(View.INVISIBLE);
        spinner = (Spinner)v.findViewById(R.id.word_spinner);
        startLearning = (FancyButton)v.findViewById(R.id.nl_start_learning);
        startLearning.setOnClickListener(this);
        startLearning.setBackgroundResource(R.drawable.gradient);
        startLearning.setBorderColor(getResources().getColor(R.color.colorPrimaryDark));
        startLearning.setFocusBackgroundColor(getResources().getColor(R.color.colorPrimary));
        sv= (FloatingSearchView) v.findViewById(R.id.mSearch);

        toolbar = (Toolbar)v.findViewById(R.id.learned_toolbar);
        toolbar.setTitle("LEARNED");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view_learned_words);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        bWord = new ArrayList<>();
        aWord = new ArrayList<>();
        iWord = new ArrayList<>();
        ieltsFavPosition = new ArrayList<>();
        toeflFavPosition = new ArrayList<>();
        satFavPosition = new ArrayList<>();
        greFavPosition = new ArrayList<>();



    }

    private void settingUpSearch(){

        sv.setDimBackground(false);
        sv.setShowSearchKey(true);
        sv.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                spinner.setVisibility(View.VISIBLE);
            }
        });
        sv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                spinner.setVisibility(View.VISIBLE);
            }
        });
        sv.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed() {

            }
        });
        sv.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                spinner.setVisibility(View.VISIBLE);
            }
        });


        sv.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {


            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                adapter.getFilter().filter(newQuery);
                spinner.setVisibility(View.INVISIBLE);
            }




        });



    }


    /// setup fab


    private void setUpFab(View v){


        fab = v.findViewById(R.id.fab);
        fab.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        View sheetView = v.findViewById(R.id.fab_sheet);
        View overlay = v.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.colorPrimary);
        int fabColor = getResources().getColor(R.color.beginnerS);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        item1 = v.findViewById(R.id.advance_fab);
        item2 = v.findViewById(R.id.intermediate_fab);
        item3 = v.findViewById(R.id.beginner_fab);

        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
    }

    ///// Spinner--------------------------


    protected void fabAnimation(boolean isVisible) {
        if (isVisible) {

            fab.animate().translationY(0f);

        } else {

            fab.animate().translationY(60f+500);

        }
    }



    @Override
    public void onClick(View view) {


        if(view == item1){


            sp.edit().putString("practice","learned").apply();
            sp.edit().putString("level","advance").apply();


            if(words.size() >= 5){
                getContext().startActivity(new Intent(getContext(), Practice.class));


            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }
        }

        if(view == item2){



            sp.edit().putString("practice","learned").apply();
            sp.edit().putString("level","intermediate").apply();

            if(words.size() >= 5){
                getContext().startActivity(new Intent(getContext(), Practice.class));


            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }

        }



        if(view == item3){



            sp.edit().putString("practice","learned").apply();
            sp.edit().putString("level","beginner").apply();


            getBeginnerWordData();
            //int wordObjectsSize = wordObjects.size();

            //for(int i =0; i < wordObjectsSize; i++){

            //    Word word = (Word) wordObjects.get(i);
            //    practiceWords.add(word);

            //}

            if(words.size() >= 5){
                getContext().startActivity(new Intent(getContext(), Practice.class));

                isShowingFabOption = false;

            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

                isShowingFabOption = false;
            }
        }


        if(view == startLearning){

            if(level.equalsIgnoreCase("beginner")){


                sp.edit().putString("level","beginner").apply();
                view.getContext().startActivity(new Intent(view.getContext(), StartTrainingActivity.class));

            }

            if(level.equalsIgnoreCase("intermediate")){

                sp.edit().putString("level","intermediate").apply();
                view.getContext().startActivity(new Intent(view.getContext(), StartTrainingActivity.class));

            }

            if(level.equalsIgnoreCase("advance")){

                sp.edit().putString("level","advance").apply();
                view.getContext().startActivity(new Intent(view.getContext(), StartTrainingActivity.class));

            }


        }

    }



    //---------------------------------------

    private void getBeginnerWordData(){

        words.clear();

        double IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        double TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        double SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        double GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        double IELTSbeginnerNumber = 0;
        double TOEFLbeginnerNumber = 0;
        double SATbeginnerNumber = 0;
        double GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSbeginnerNumber = getPercentageNumber(30d, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLbeginnerNumber = getPercentageNumber(30d, TOEFLwordSize);

        }

        if(isSatChecked){

            SATbeginnerNumber = getPercentageNumber(30d, SATwordSize);

        }

        if(isGreChecked){


            GREbeginnerNumber = getPercentageNumber(30d, GREwordSize);

        }

        addIELTSwords(0d,IELTSbeginnerNumber);
        addTOEFLwords(0d,TOEFLbeginnerNumber);
        addSATwords(0d,SATbeginnerNumber);
        addGREwords(0d,GREbeginnerNumber);

        if(words.size() <= 0){
            havenotlearned.setVisibility(View.VISIBLE);
            noLearnedImage.setVisibility(View.VISIBLE);
            startLearning.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }else {

            havenotlearned.setVisibility(View.INVISIBLE);
            noLearnedImage.setVisibility(View.INVISIBLE);
            startLearning.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);

        }

        adapter = new WordRecyclerViewAdapter(getContext(), words);
        recyclerView.setAdapter(adapter);


    }
    private void getIntermediateWordData(){

        words.clear();

        double IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        double TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        double SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        double GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        double IELTSintermediateNumber = 0;
        double TOEFLintermediateNumber = 0;
        double SATintermediateNumber = 0;
        double GREintermediateNumber = 0;

        double IELTSbeginnerNumber = 0;
        double TOEFLbeginnerNumber = 0;
        double SATbeginnerNumber = 0;
        double GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSintermediateNumber = getPercentageNumber(40d, IELTSwordSize);
            IELTSbeginnerNumber = getPercentageNumber(30d, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLintermediateNumber = getPercentageNumber(40d, TOEFLwordSize);
            TOEFLbeginnerNumber = getPercentageNumber(30d, TOEFLwordSize);

        }

        if(isSatChecked){

            SATintermediateNumber = getPercentageNumber(40d, SATwordSize);
            SATbeginnerNumber = getPercentageNumber(30d, SATwordSize);

        }

        if(isGreChecked){


            GREintermediateNumber = getPercentageNumber(40d, GREwordSize);
            GREbeginnerNumber = getPercentageNumber(30d, GREwordSize);

        }

        addIELTSwords(IELTSbeginnerNumber,IELTSbeginnerNumber+IELTSintermediateNumber);
        addTOEFLwords(TOEFLbeginnerNumber,TOEFLbeginnerNumber+TOEFLintermediateNumber);
        addSATwords(SATbeginnerNumber,SATbeginnerNumber+SATintermediateNumber);
        addGREwords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);

        if(words.size() <= 0){
            havenotlearned.setVisibility(View.VISIBLE);
            noLearnedImage.setVisibility(View.VISIBLE);
            startLearning.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }else {

            havenotlearned.setVisibility(View.INVISIBLE);
            noLearnedImage.setVisibility(View.INVISIBLE);
            startLearning.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);

        }

        adapter = new WordRecyclerViewAdapter(getContext(), words);
        recyclerView.setAdapter(adapter);

    }
    private void getAdvanceWordData(){

        words.clear();

        double IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        double TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        double SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        double GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        double IELTSintermediateNumber = 0;
        double TOEFLintermediateNumber = 0;
        double SATintermediateNumber = 0;
        double GREintermediateNumber = 0;

        double IELTSbeginnerNumber = 0;
        double TOEFLbeginnerNumber = 0;
        double SATbeginnerNumber = 0;
        double GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSintermediateNumber = getPercentageNumber(40d, IELTSwordSize);
            IELTSbeginnerNumber = getPercentageNumber(30d, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLintermediateNumber = getPercentageNumber(40d, TOEFLwordSize);
            TOEFLbeginnerNumber = getPercentageNumber(30d, TOEFLwordSize);

        }

        if(isSatChecked){

            SATintermediateNumber = getPercentageNumber(40d, SATwordSize);
            SATbeginnerNumber = getPercentageNumber(30d, SATwordSize);

        }

        if(isGreChecked){


            GREintermediateNumber = getPercentageNumber(40d, GREwordSize);
            GREbeginnerNumber = getPercentageNumber(30d, GREwordSize);

        }

        addIELTSwords(IELTSbeginnerNumber+IELTSintermediateNumber,IELTSwordSize);
        addTOEFLwords(TOEFLbeginnerNumber+TOEFLintermediateNumber,TOEFLwordSize);
        addSATwords(SATbeginnerNumber+SATintermediateNumber,SATwordSize);
        addGREwords(GREbeginnerNumber+GREintermediateNumber,GREwordSize);

        if(words.size() <= 0){
            havenotlearned.setVisibility(View.VISIBLE);
            noLearnedImage.setVisibility(View.VISIBLE);
            startLearning.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }else {

            havenotlearned.setVisibility(View.INVISIBLE);
            noLearnedImage.setVisibility(View.INVISIBLE);
            startLearning.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);

        }

        adapter = new WordRecyclerViewAdapter(getContext(), words);
        recyclerView.setAdapter(adapter);

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

            IELTSlearnedDatabase.add(beginnerRes.getString(3));

        }

        while (TOEFLres.moveToNext()) {

            TOEFLlearnedDatabase.add(TOEFLres.getString(3));

        }

        while (SATres.moveToNext()) {

            SATlearnedDatabase.add(SATres.getString(3));

        }

        while (GREres.moveToNext()) {

            GRElearnedDatabase.add(GREres.getString(3));

        }


    }

    private  void addIELTSwords(double startPoint,double IELTSbeginnerNumber){



        if(isIeltsChecked){
            for(int i = (int) startPoint; i  < IELTSbeginnerNumber; i++){


                if( IELTSlearnedDatabase.get(i).equalsIgnoreCase("true")){

                    words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], IELTSlearnedDatabase.get(i),ieltsFavPosition.get(i)));
                }

            }

        }


    }

    private  void addTOEFLwords(double startPoint, double TOEFLbeginnerNumber){


        if(isToeflChecked){


            for(int i = (int) startPoint; i < TOEFLbeginnerNumber; i++){


                if( TOEFLlearnedDatabase.get(i).equalsIgnoreCase("true")) {
                    words.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], TOEFLlearnedDatabase.get(i),toeflFavPosition.get(i)));


                }
            }
        }



    }

    private void addSATwords (double startPoint ,double SATbeginnerNumber){


        if(isSatChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){


                if( SATlearnedDatabase.get(i).equalsIgnoreCase("true")) {

                    words.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], SATlearnedDatabase.get(i),satFavPosition.get(i)));

                }

            }

        }





    }

    private void addGREwords (double startPoint ,double SATbeginnerNumber){


        if(isGreChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){



                if( GRElearnedDatabase.get(i).equalsIgnoreCase("true")) {

                    words.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], GRElearnedDatabase.get(i),greFavPosition.get(i)));
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

        TOEFLdatabase = new TOEFLWordDatabase(getContext());
        SATdatabase = new SATWordDatabase(getContext());
        IELTSdatabase = new IELTSWordDatabase(getContext());
        GREdatabase = new GREWordDatabase(getContext());

        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);

        IELTSlearnedDatabase = new ArrayList<>();
        TOEFLlearnedDatabase = new ArrayList<>();
        SATlearnedDatabase = new ArrayList<>();
        GRElearnedDatabase = new ArrayList<>();

    }

    private void getfavoriteDatabasePosition(){

        Cursor ieltsRes = IELTSdatabase.getData();
        Cursor toeflRes = TOEFLdatabase.getData();
        Cursor satRes = SATdatabase.getData();
        Cursor greRes = GREdatabase.getData();

        while (ieltsRes.moveToNext()){


            ieltsFavPosition.add(ieltsRes.getString(2));



        }

        while (toeflRes.moveToNext()){

            toeflFavPosition.add(toeflRes.getString(2));
        }

        while(satRes.moveToNext()){

            satFavPosition.add(satRes.getString(2));

        }

        while (greRes.moveToNext()){


            greFavPosition.add(greRes.getString(2));
        }






    }


}
