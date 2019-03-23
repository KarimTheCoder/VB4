package com.fortitude.shamsulkarim.ieltsfordory;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewWordFragment extends Fragment {

    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;

    private List<String> ieltsFavPosition, toeflFavPosition, satFavPosition, greFavPosition;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;


    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Object> words = new ArrayList<>();
    private List<String> beginnerFav;
    Toolbar toolbar;
    IELTSWordDatabase IELTSdatabase;
    TOEFLWordDatabase TOEFLdatabase;
    SATWordDatabase SATdatabase;
    GREWordDatabase GREdatabase;
    SharedPreferences sp;
    boolean connected = false;
    int languageId;
    Spinner spinner;
    int beginnerPos;
    private FloatingSearchView sv;

    public NewWordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_new_word,container,false);

        sp = getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        gettingResources();
        getfavoriteDatabasePosition();

        toolbar = (Toolbar)v.findViewById(R.id.word_toolbar);
        toolbar.setTitle("WORDS");
        toolbar.setTitleTextColor(getResources().getColor(R.color.beginnerS));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        sv= (FloatingSearchView) v.findViewById(R.id.mSearch);



                languageId = sp.getInt("language",0);
                beginnerFav = new ArrayList<>();
                spinner = (Spinner)v.findViewById(R.id.word_spinner);
                int selection = 0;

                if (!sp.contains("prevWordSelection")) {



                    sp.edit().putInt("prevWordSelection", 0).apply();
                    sp.edit().putInt("wordBeginnerScrollPosition",0).apply();

                } else {

                    selection = sp.getInt("prevWordSelection", 0);
                    beginnerPos = sp.getInt("wordBeginnerScrollPosition",0);
                }

                if(selection == 0){


                    recyclerView = (RecyclerView) v.findViewById(R.id.new_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    getBeginnerWordData();

                }
                if( selection == 1){


                    recyclerView = (RecyclerView) v.findViewById(R.id.new_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    getIntermediateWordData();

                }

                if( selection == 2){


                    recyclerView = (RecyclerView) v.findViewById(R.id.new_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    getAdvanceWordData();

                }

               recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                   @Override
                   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                       super.onScrolled(recyclerView, dx, dy);

                       beginnerPos +=dy;

                       sp.edit().putInt("wordBeginnerScrollPosition",beginnerPos).apply();
                   }
               });



                languageId = sp.getInt("language",0);
                spinner.setSelection(selection);


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {




                        if (i == 0) {


                            sp.edit().putInt("prevWordSelection", 0).apply();

                            recyclerView = (RecyclerView) getView().findViewById(R.id.new_recycler_view);

                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setHasFixedSize(true);
                            getBeginnerWordData();

                        }
                        if (i == 1) {


                            sp.edit().putInt("prevWordSelection", 1).apply();

                            recyclerView = (RecyclerView) getView().findViewById(R.id.new_recycler_view);
                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setHasFixedSize(true);
                            getIntermediateWordData();
                        }

                        if (i == 2) {

                            sp.edit().putInt("prevWordSelection", 2).apply();

                            recyclerView = (RecyclerView) getView().findViewById(R.id.new_recycler_view);
                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setHasFixedSize(true);
                            getAdvanceWordData();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });



        //SEARCH

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
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                //FILTER AS YOU TYPE
//                adapter.getFilter().filter(query);
//                return false;
//            }
//        });




        return v;
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

        adapter = new WordRecyclerViewAdapter(getContext(), words);
        recyclerView.setAdapter(adapter);

    }


    private double getPercentageNumber(double percentage, double number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return beginnerNum;

    }

    private  void addIELTSwords(double startPoint,double IELTSbeginnerNumber){



        if(isIeltsChecked){
            for(int i = (int) startPoint; i  < IELTSbeginnerNumber; i++){



                words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], "",ieltsFavPosition.get(i)));


            }

        }


    }

    private  void addTOEFLwords(double startPoint, double TOEFLbeginnerNumber){


        if(isToeflChecked){


            for(int i = (int) startPoint; i < TOEFLbeginnerNumber; i++){

                words.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], "",toeflFavPosition.get(i)));



            }
        }



    }

    private void addSATwords (double startPoint ,double SATbeginnerNumber){


        if(isSatChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){


                words.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], "",satFavPosition.get(i)));


            }

        }





    }

    private void addGREwords (double startPoint ,double SATbeginnerNumber){


        if(isGreChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){

                words.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], "",greFavPosition.get(i)));


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

        ieltsFavPosition = new ArrayList<>();
        toeflFavPosition = new ArrayList<>();
        satFavPosition = new ArrayList<>();
        greFavPosition = new ArrayList<>();

        TOEFLdatabase = new TOEFLWordDatabase(getContext());
        SATdatabase = new SATWordDatabase(getContext());
        IELTSdatabase = new IELTSWordDatabase(getContext());
        GREdatabase = new GREWordDatabase(getContext());

        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);



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
