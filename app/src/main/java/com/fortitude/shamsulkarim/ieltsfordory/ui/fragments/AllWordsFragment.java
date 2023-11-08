package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.fortitude.shamsulkarim.ieltsfordory.adapters.WordRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllWordsFragment extends Fragment implements AdapterView.OnItemSelectedListener, WordRecyclerViewAdapter.WordAdapterCallback, TextToSpeech.OnInitListener{


    private VocabularyRepository repository;
    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private final ArrayList<Object> words = new ArrayList<>();
    private SharedPreferences sp;
    private Spinner spinner;
    private int beginnerPos;
    private TextToSpeech tts;
    public AllWordsFragment() {
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

        tts = new TextToSpeech(getContext(), this);
        Toolbar toolbar = v.findViewById(R.id.word_toolbar);
        toolbar.setTitle("WORDS");
        toolbar.setTitleTextColor(requireContext().getColor(R.color.beginnerS));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }




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
                spinner.setSelection(selection);




        //SEARCH
        searchRecyclerview(v);

        return v;
    }

    private void searchRecyclerview(View v) {
        FloatingSearchView sv = v.findViewById(R.id.mSearch);

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


    //---------------------------------------

    private void getBeginnerWordData(){

        words.clear();
        words.addAll(repository.getBeginnerVocabulary());
        adapter = new WordRecyclerViewAdapter(getContext(), words,this);
        recyclerView.setAdapter(adapter);


    }
    private void getIntermediateWordData(){

        words.clear();

        words.addAll(repository.getIntermediateVocabulary());
        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        recyclerView.setAdapter(adapter);

    }
    private void getAdvanceWordData(){

        words.clear();
        words.addAll(repository.getAdvanceVocabulary());
        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        recyclerView.setAdapter(adapter);

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
