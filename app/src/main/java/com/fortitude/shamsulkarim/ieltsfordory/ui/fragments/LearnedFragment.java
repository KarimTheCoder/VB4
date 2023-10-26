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
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
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

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearnedFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,TextToSpeech.OnInitListener, WordRecyclerViewAdapter.WordAdapterCallback {


    private VocabularyRepository repository;
    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private final ArrayList<Object> words = new ArrayList<>();
    private boolean isShowingFabOption = false;
    private TextView havenotlearned;
    private ImageView noLearnedImage;
    private SharedPreferences sp;
    private Spinner spinner;
    private Fab fab;
    private int selection;
    private FancyButton startLearning;
    private String level;
    private FloatingSearchView sv;
    private TextToSpeech tts;
    private TextView item1, item2, item3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learned_words,container,false);

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getContext().getColor(R.color.colorPrimary));


        repository = new VocabularyRepository(requireContext());
        initialization(v);


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
                requireContext().startActivity(new Intent(getContext(), Practice.class));



            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }
        }

        if(view == item2){



            sp.edit().putString("practice","learned").apply();
            sp.edit().putString("level","intermediate").apply();

            if(words.size() >= 5){
                requireContext().startActivity(new Intent(getContext(), Practice.class));



            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }

        }



        if(view == item3){



            sp.edit().putString("practice","learned").apply();
            sp.edit().putString("level","beginner").apply();


            getBeginnerWordData();

            if(words.size() >= 5){
                requireContext().startActivity(new Intent(getContext(), Practice.class));

            }else {
                Toast.makeText(getContext(),"There must be atleast five words", Toast.LENGTH_SHORT).show();

            }
            isShowingFabOption = false;
        }


        if(view == startLearning){

            if(level.equalsIgnoreCase("beginner")){


                sp.edit().putString("level","beginner").apply();
                requireView().getContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));

            }

            if(level.equalsIgnoreCase("intermediate")){

                sp.edit().putString("level","intermediate").apply();
                requireView().getContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));

            }

            if(level.equalsIgnoreCase("advance")){

                sp.edit().putString("level","advance").apply();
                requireView().getContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));

            }


        }

    }



    //---------------------------------------

    private void getBeginnerWordData(){

        words.clear();
        words.addAll(repository.getBeginnerLearnedWords());


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
        words.addAll(repository.getIntermediateLearnedWords());


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
        words.addAll(repository.getAdvanceLearnedWords());


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








    private void setSpinner(View v){

        spinner = v.findViewById(R.id.word_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
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
