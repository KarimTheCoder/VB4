package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.WordRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewWordFragment extends Fragment implements AdapterView.OnItemSelectedListener, WordRecyclerViewAdapter.WordAdapterCallback, TextToSpeech.OnInitListener{

    private String[] IELTSwordArray, IELTStranslationArray, IELTSgrammarArray, IELTSpronunArray, IELTSexample1array, IELTSexample2Array, IELTSexample3Array, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;


    private VocabularyRepository repository;

    private List<String> ieltsFavPosition, toeflFavPosition, satFavPosition, greFavPosition;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;


    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private final ArrayList<Object> words = new ArrayList<>();
    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private SharedPreferences sp;
    private Spinner spinner;
    private int beginnerPos;
    private TextToSpeech tts;
    public NewWordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(requireContext().getColor(R.color.colorPrimary));
        View v = inflater.inflate(R.layout.fragment_new_word,container,false);

        repository = new VocabularyRepository(requireContext());

        sp = requireContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        gettingResources();
        getfavoriteDatabasePosition();
        tts = new TextToSpeech(getContext(), this);
        Toolbar toolbar = v.findViewById(R.id.word_toolbar);
        toolbar.setTitle("WORDS");
        toolbar.setTitleTextColor(getContext().getColor(R.color.beginnerS));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        FloatingSearchView sv = v.findViewById(R.id.mSearch);


        sp.getInt("language", 0);
        spinner = v.findViewById(R.id.word_spinner);
                spinner.setOnItemSelectedListener(this);
                setSpinnerAdapter();
                int selection = 0;

                if (!sp.contains("prevWordSelection")) {



                    sp.edit().putInt("prevWordSelection", 0).apply();
                    sp.edit().putInt("wordBeginnerScrollPosition",0).apply();

                } else {

                    selection = sp.getInt("prevWordSelection", 0);
                    beginnerPos = sp.getInt("wordBeginnerScrollPosition",0);
                }

                if(selection == 0){


                    recyclerView = v.findViewById(R.id.new_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    getBeginnerWordData();

                }
                if( selection == 1){


                    recyclerView = v.findViewById(R.id.new_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    getIntermediateWordData();

                }

                if( selection == 2){


                    recyclerView = v.findViewById(R.id.new_recycler_view);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    getAdvanceWordData();

                }

               recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                   @Override
                   public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                       super.onScrolled(recyclerView, dx, dy);

                       beginnerPos +=dy;

                       sp.edit().putInt("wordBeginnerScrollPosition",beginnerPos).apply();
                   }
               });



                sp.getInt("language",0);
                spinner.setSelection(selection);


//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//
//
//                        if (i == 0) {
//
//
//                            sp.edit().putInt("prevWordSelection", 0).apply();
//
//                            recyclerView = (RecyclerView) getView().findViewById(R.id.new_recycler_view);
//
//                            layoutManager = new LinearLayoutManager(getContext());
//                            recyclerView.setLayoutManager(layoutManager);
//                            recyclerView.setHasFixedSize(true);
//                            getBeginnerWordData();
//                            Crashlytics.setString("Word Fragment","Beginner");
//
//                        }
//                        if (i == 1) {
//
//
//                            sp.edit().putInt("prevWordSelection", 1).apply();
//
//                            recyclerView = (RecyclerView) getView().findViewById(R.id.new_recycler_view);
//                            layoutManager = new LinearLayoutManager(getContext());
//                            recyclerView.setLayoutManager(layoutManager);
//                            recyclerView.setHasFixedSize(true);
//                            getIntermediateWordData();
//                            Crashlytics.setString("Word Fragment","Intermediate");
//                        }
//
//                        if (i == 2) {
//
//                            sp.edit().putInt("prevWordSelection", 2).apply();
//
//                            recyclerView = (RecyclerView) getView().findViewById(R.id.new_recycler_view);
//                            layoutManager = new LinearLayoutManager(getContext());
//                            recyclerView.setLayoutManager(layoutManager);
//                            recyclerView.setHasFixedSize(true);
//                            getAdvanceWordData();
//                            Crashlytics.setString("Word Fragment","Advance");
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });



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

        //addIELTSwords(0,IELTSbeginnerNumber);
        //addTOEFLwords(0,TOEFLbeginnerNumber);
        //addSATwords(0,SATbeginnerNumber);
        //addGREwords(0,GREbeginnerNumber);



        words.addAll(repository.getBeginnerVocabulary());






        // all words


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

//        addIELTSwords(IELTSbeginnerNumber,IELTSbeginnerNumber+IELTSintermediateNumber);
//        addTOEFLwords(TOEFLbeginnerNumber,TOEFLbeginnerNumber+TOEFLintermediateNumber);
//        addSATwords(SATbeginnerNumber,SATbeginnerNumber+SATintermediateNumber);
//        addGREwords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);




        words.addAll(repository.getIntermediateVocabulary());
        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
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

//        addIELTSwords(IELTSbeginnerNumber+IELTSintermediateNumber,IELTSwordSize);
//        addTOEFLwords(TOEFLbeginnerNumber+TOEFLintermediateNumber,TOEFLwordSize);
//        addSATwords(SATbeginnerNumber+SATintermediateNumber,SATwordSize);
//        addGREwords(GREbeginnerNumber+GREintermediateNumber,GREwordSize);


        words.addAll(repository.getAdvanceVocabulary());
        Log.i("All words","count: "+words.size());


        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        recyclerView.setAdapter(adapter);

    }


    private int getPercentageNumber(int percentage, int number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return (int)beginnerNum;

    }

    private  void addIELTSwords(int startPoint,int IELTSbeginnerNumber){



        if(isIeltsChecked){
            for(int i = startPoint; i  < IELTSbeginnerNumber; i++){



                words.add(new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], "",ieltsFavPosition.get(i)));


            }

        }


    }

    private  void addTOEFLwords(int startPoint, int TOEFLbeginnerNumber){


        if(isToeflChecked){


            for(int i = startPoint; i < TOEFLbeginnerNumber; i++){

                words.add(new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], "",toeflFavPosition.get(i)));



            }
        }



    }

    private void addSATwords (int startPoint ,int SATbeginnerNumber){


        if(isSatChecked){

            for(int i = startPoint; i < SATbeginnerNumber; i++){


                words.add(new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], "",satFavPosition.get(i)));


            }

        }





    }

    private void addGREwords (int startPoint ,int SATbeginnerNumber){


        if(isGreChecked){

            for(int i = startPoint; i < SATbeginnerNumber; i++){

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
        ieltsRes.close();
        IELTSdatabase.close();

        while (toeflRes.moveToNext()){

            toeflFavPosition.add(toeflRes.getString(2));
        }
        toeflRes.close();
        TOEFLdatabase.close();

        while(satRes.moveToNext()){

            satFavPosition.add(satRes.getString(2));

        }
        satRes.close();
        SATdatabase.close();

        while (greRes.moveToNext()){


            greFavPosition.add(greRes.getString(2));
        }
        greRes.close();
        GREdatabase.close();

    }


    private void setSpinnerAdapter(){

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.spinner_options, R.layout.settings_spinner);
        spinnerAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        spinner.setAdapter(spinnerAdapter);


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        if (i == 0) {


            sp.edit().putInt("prevWordSelection", 0).apply();

            recyclerView = requireView().findViewById(R.id.new_recycler_view);

            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            getBeginnerWordData();

        }
        if (i == 1) {


            sp.edit().putInt("prevWordSelection", 1).apply();

            recyclerView = requireView().findViewById(R.id.new_recycler_view);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            getIntermediateWordData();

        }

        if (i == 2) {

            sp.edit().putInt("prevWordSelection", 2).apply();

            recyclerView = requireView().findViewById(R.id.new_recycler_view);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
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
