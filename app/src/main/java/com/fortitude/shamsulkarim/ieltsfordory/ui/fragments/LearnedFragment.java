package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.PretrainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.WordRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.Fab;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearnedFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,TextToSpeech.OnInitListener, WordRecyclerViewAdapter.WordAdapterCallback {


    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;
    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private final ArrayList<Object> words = new ArrayList<>();
    private boolean isShowingFabOption = false;
    private List<String> IELTSlearnedDatabase, TOEFLlearnedDatabase, SATlearnedDatabase, GRElearnedDatabase;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private TextView havenotlearned;
    private ImageView noLearnedImage;
    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private SharedPreferences sp;
    private Spinner spinner;
    private Fab fab;
    private int selection;
    private FancyButton startLearning;
    private String level;
    private FloatingSearchView sv;
    private TextToSpeech tts;
    private List<String> ieltsFavPosition, toeflFavPosition, satFavPosition, greFavPosition;
    private TextView item1, item2, item3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learned_words,container,false);

        Window window = Objects.requireNonNull(getActivity()).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getContext().getColor(R.color.colorPrimary));

        initialization(v);
        gettingResources();
        getfavoriteDatabasePosition();
        addingLearnedDatabase();





        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
 @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
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




//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        //SEARCH
        settingUpSearch();


        return v;

        //---------------------------------------------------------------------------------------------------
    }

    // INITIALIZATION

    private void initialization(View v){

        setUpFab(v);

        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        tts = new TextToSpeech(getContext(),this);
        sp.getInt("language", 0);
        havenotlearned = v.findViewById(R.id.havenotlearned);
        havenotlearned.setVisibility(View.INVISIBLE);
        noLearnedImage = v.findViewById(R.id.no_learned_image);
        noLearnedImage.setVisibility(View.INVISIBLE);
        setSpinner(v);

        startLearning = v.findViewById(R.id.nl_start_learning);
        startLearning.setOnClickListener(this);
        startLearning.setBackgroundResource(R.drawable.gradient);
        startLearning.setBorderColor(getContext().getColor(R.color.colorPrimaryDark));
        startLearning.setFocusBackgroundColor(getContext().getColor(R.color.colorPrimary));
        sv= v.findViewById(R.id.mSearch);

        Toolbar toolbar = v.findViewById(R.id.learned_toolbar);
        toolbar.setTitle("LEARNED");
        toolbar.setTitleTextColor(getContext().getColor(R.color.colorPrimary));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);

        recyclerView = v.findViewById(R.id.recycler_view_learned_words);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

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
        fab.setColorNormal(getContext().getColor(R.color.colorPrimary));
        fab.setColorPressed(getContext().getColor(R.color.colorPrimaryDark));
        View sheetView = v.findViewById(R.id.fab_sheet);
        View overlay = v.findViewById(R.id.overlay);
        int sheetColor = getContext().getColor(R.color.colorPrimary);
        int fabColor = getContext().getColor(R.color.beginnerS);
        MaterialSheetFab materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
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
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), Practice.class));



            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }
        }

        if(view == item2){



            sp.edit().putString("practice","learned").apply();
            sp.edit().putString("level","intermediate").apply();

            if(words.size() >= 5){
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), Practice.class));



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
                Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), Practice.class));

            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }
            isShowingFabOption = false;
        }


        if(view == startLearning){

            if(level.equalsIgnoreCase("beginner")){


                sp.edit().putString("level","beginner").apply();
                Objects.requireNonNull(view).getContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));

            }

            if(level.equalsIgnoreCase("intermediate")){

                sp.edit().putString("level","intermediate").apply();
                Objects.requireNonNull(view).getContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));

            }

            if(level.equalsIgnoreCase("advance")){

                sp.edit().putString("level","advance").apply();
                Objects.requireNonNull(view).getContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));

            }


        }

    }



    //---------------------------------------

    private void getBeginnerWordData(){

        words.clear();

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSbeginnerNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int SATbeginnerNumber = 0;
        int GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSbeginnerNumber = getPercentageNumber(30, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLbeginnerNumber = getPercentageNumber(30, TOEFLwordSize);

        }

        if(isSatChecked){

            SATbeginnerNumber = getPercentageNumber(30, SATwordSize);

        }

        if(isGreChecked){


            GREbeginnerNumber = getPercentageNumber(30, GREwordSize);

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

        adapter = new WordRecyclerViewAdapter(getContext(), words,this);
        recyclerView.setAdapter(adapter);


    }
    private void getIntermediateWordData(){

        words.clear();

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSintermediateNumber = 0;
        int TOEFLintermediateNumber = 0;
        int SATintermediateNumber = 0;
        int GREintermediateNumber = 0;

        int IELTSbeginnerNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int SATbeginnerNumber = 0;
        int GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSintermediateNumber = getPercentageNumber(40, IELTSwordSize);
            IELTSbeginnerNumber = getPercentageNumber(30, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLintermediateNumber = getPercentageNumber(40, TOEFLwordSize);
            TOEFLbeginnerNumber = getPercentageNumber(30, TOEFLwordSize);

        }

        if(isSatChecked){

            SATintermediateNumber = getPercentageNumber(40, SATwordSize);
            SATbeginnerNumber = getPercentageNumber(30, SATwordSize);

        }

        if(isGreChecked){


            GREintermediateNumber = getPercentageNumber(40, GREwordSize);
            GREbeginnerNumber = getPercentageNumber(30, GREwordSize);

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

        adapter = new WordRecyclerViewAdapter(getContext(), words,this);
        recyclerView.setAdapter(adapter);

    }
    private void getAdvanceWordData(){

        words.clear();

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSintermediateNumber = 0;
        int TOEFLintermediateNumber = 0;
        int SATintermediateNumber = 0;
        int GREintermediateNumber = 0;

        int IELTSbeginnerNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int SATbeginnerNumber = 0;
        int GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSintermediateNumber = getPercentageNumber(40, IELTSwordSize);
            IELTSbeginnerNumber = getPercentageNumber(30, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLintermediateNumber = getPercentageNumber(40, TOEFLwordSize);
            TOEFLbeginnerNumber = getPercentageNumber(30, TOEFLwordSize);

        }

        if(isSatChecked){

            SATintermediateNumber = getPercentageNumber(40, SATwordSize);
            SATbeginnerNumber = getPercentageNumber(30, SATwordSize);

        }

        if(isGreChecked){


            GREintermediateNumber = getPercentageNumber(40, GREwordSize);
            GREbeginnerNumber = getPercentageNumber(30, GREwordSize);

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

        adapter = new WordRecyclerViewAdapter(getContext(), words,this);
        recyclerView.setAdapter(adapter);

    }


    private int getPercentageNumber(int percentage, int number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return (int) beginnerNum;

    }

    private void addingLearnedDatabase() {

        Cursor beginnerRes = IELTSdatabase.getData();
        Cursor TOEFLres = TOEFLdatabase.getData();
        Cursor SATres = SATdatabase.getData();
        Cursor GREres = GREdatabase.getData();

        while (beginnerRes.moveToNext()) {

            IELTSlearnedDatabase.add(beginnerRes.getString(3));

        }
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
        ieltsRes.close();

        while (toeflRes.moveToNext()){

            toeflFavPosition.add(toeflRes.getString(2));
        }
        toeflRes.close();

        while(satRes.moveToNext()){

            satFavPosition.add(satRes.getString(2));

        }
        satRes.close();

        while (greRes.moveToNext()){


            greFavPosition.add(greRes.getString(2));
        }

        greRes.close();
    }



    private void setSpinner(View v){

        spinner = v.findViewById(R.id.word_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.spinner_options, R.layout.settings_spinner);
        adapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        spinner.setAdapter(adapter);
    }


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.onDestroy();
        if(tts != null ){
            tts.stop();
            tts.shutdown();
        }

    }

    @Override
    public void onMethodCallback(String word) {

        if(tts != null){

            tts.setLanguage(Locale.US);
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null,"TTS");

        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int ttsLang = tts.setLanguage(Locale.US);

            if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!");
                Toast.makeText(getContext(), "Please install English Language on your Text-to-Speech engine.\nSend us an email if you need help", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("TTS", "Language Supported.");
            }
            Log.i("TTS", "Initialization success.");
        } else {
            Log.e("TTS", "TTS not initialized");
            Toast.makeText(getContext(), "Please install Google Text-to-Speech on your phone. \nSend us an email if you need help", Toast.LENGTH_SHORT).show();
        }
    }
}
