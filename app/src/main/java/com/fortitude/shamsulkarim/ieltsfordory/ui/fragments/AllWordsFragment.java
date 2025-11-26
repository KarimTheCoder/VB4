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


public class AllWordsFragment extends Fragment implements AdapterView.OnItemSelectedListener, WordRecyclerViewAdapter.WordAdapterCallback, TextToSpeech.OnInitListener{


    private VocabularyRepository repository;
    private RecyclerView recyclerView;
    private WordRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private final ArrayList<Object> words = new ArrayList<>();
    private SharedPreferences sp;
    private Spinner spinner;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private static final String KEY_PREV_SELECTION = "prevWordSelection";
    private static final String KEY_FIRST_POS = "wordFirstVisiblePos";
    private static final String KEY_FIRST_OFFSET = "wordFirstOffsetTop";
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

        setupRecycler(v);

        int selection = 0;
        if (!sp.contains(KEY_PREV_SELECTION)) {
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt(KEY_PREV_SELECTION, 0);
            ed.putInt(KEY_FIRST_POS, 0);
            ed.putInt(KEY_FIRST_OFFSET, 0);
            ed.apply();
        } else {
            selection = sp.getInt(KEY_PREV_SELECTION, 0);
        }

        showWordsForSelection(selection);
        attachScrollPersistence();
        restoreScrollPosition();
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
                if (hasFocus && sv.getQuery().isEmpty()) {
                    spinner.setVisibility(View.VISIBLE);
                }
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

        sv.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                adapter.getFilter().filter(newQuery);
                spinner.setVisibility(View.INVISIBLE);
            }
        });
    }


    //---------------------------------------

    private void setupRecycler(View v){
        recyclerView = v.findViewById(R.id.new_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        recyclerView.setAdapter(adapter);
    }

    private void showWordsForSelection(int selection){
        words.clear();
        if(selection == 0){
            words.addAll(repository.getBeginnerVocabulary());
        } else if(selection == 1){
            words.addAll(repository.getIntermediateVocabulary());
        } else if(selection == 2){
            words.addAll(repository.getAdvanceVocabulary());
        }
        adapter.notifyDataSetChanged();
    }

    private void attachScrollPersistence(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                int pos = lm.findFirstVisibleItemPosition();
                View first = lm.findViewByPosition(pos);
                int offset = first == null ? 0 : first.getTop();
                sp.edit()
                        .putInt(KEY_FIRST_POS, pos)
                        .putInt(KEY_FIRST_OFFSET, offset)
                        .apply();
            }
        });
    }

    private void restoreScrollPosition(){
        int pos = sp.getInt(KEY_FIRST_POS, 0);
        int offset = sp.getInt(KEY_FIRST_OFFSET, 0);
        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(pos, offset);
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
            sp.edit().putInt(KEY_PREV_SELECTION, 0).apply();
            showWordsForSelection(0);
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0,0);
        }
        if (i == 1) {
            sp.edit().putInt(KEY_PREV_SELECTION, 1).apply();
            showWordsForSelection(1);
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0,0);
        }

        if (i == 2) {
            sp.edit().putInt(KEY_PREV_SELECTION, 2).apply();
            showWordsForSelection(2);
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0,0);
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

        if(tts != null && ttsReady){

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
                ttsReady = false;
            } else {
                Log.i("TTS", "Language Supported.");
                ttsReady = true;
            }
            Log.i("TTS", "Initialization success.");
        } else {
            Log.e("TTS", "TTS not initialized");
            Toast.makeText(getContext(), "Please install Google Text-to-Speech on your phone. \nSend us an email if you need help", Toast.LENGTH_SHORT).show();
            ttsReady = false;
        }
    }
}
