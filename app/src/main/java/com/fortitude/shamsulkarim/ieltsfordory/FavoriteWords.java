package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.github.clans.fab.FloatingActionButton;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteWords extends Fragment implements FavoriteRecyclerViewAdapter.AdapterCallback, TextToSpeech.OnInitListener{

    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;
    private FloatingActionButton fab;
    private FavoriteRecyclerViewAdapter adapter;
    static public final List<Word> words = new ArrayList<>();
    private float fabY;
    private SharedPreferences sp;
    private List<String> ieltsFavWords, toeflFavWords, satFavWords, greFavWords;
    private List<Integer> ieltsDatabasePosition, toeflDatabasePosition, satDatabasePosition, greDatabasePosition;
    private boolean isFabOptionOn = false;
    private TextToSpeech tts;
    private final String RECYCLER_VIEW_POSITION = "recyclerview_last_pos";
    private int lastRecyclerViewPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite_words,container,false);
        Window window = Objects.requireNonNull(getActivity()).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getContext().getColor(R.color.colorPrimary));

        tts = new TextToSpeech(getContext(), this);
        fab = v.findViewById(R.id.fab_favorite);
        fab.setColorNormal(getContext().getColor(R.color.colorPrimary));
        fab.setColorPressed(getContext().getColor(R.color.colorPrimaryDark));

        fabY = fab.getY();

        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        initializingSQLDatabase(v);


        TextView noFavorite =  v.findViewById(R.id.havenotlearned);
        ImageView noFavoriteImage =  v.findViewById(R.id.no_favorite_image);
        FloatingSearchView sv =  v.findViewById(R.id.mSearch);

        Toolbar toolbar= v.findViewById(R.id.favoriteToolbar);
        toolbar.setTitle("FAVORITE");
        toolbar.setTitleTextColor(getContext().getColor(R.color.beginnerS));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        ieltsFavWords = new ArrayList<>();
        toeflFavWords = new ArrayList<>();
        satFavWords = new ArrayList<>();
        greFavWords = new ArrayList<>();

        ieltsDatabasePosition = new ArrayList<>();
        toeflDatabasePosition = new ArrayList<>();
        satDatabasePosition = new ArrayList<>();
        greDatabasePosition = new ArrayList<>();
        getFavoriteWordRes();
        addFavoriteWord();


        int favoriteWordSize = words.size();

        if(favoriteWordSize >= 1){
          noFavorite.setVisibility(View.INVISIBLE);
            noFavoriteImage.setVisibility(View.INVISIBLE);

        }else {
            noFavorite.setVisibility(View.VISIBLE);
            noFavoriteImage.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView =  v.findViewById(R.id.recycler_view_favorite_words);
        recyclerView.setHasFixedSize(true);
        adapter = new FavoriteRecyclerViewAdapter(getContext(), words,this);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        lastRecyclerViewPosition = sp.getInt(RECYCLER_VIEW_POSITION,0);
        recyclerView.scrollToPosition(lastRecyclerViewPosition);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastRecyclerViewPosition = layoutManager.findFirstVisibleItemPosition();

            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");
                    fabAnimation(false);

                    if(isFabOptionOn){
                        fab.animate().rotation(-20f);
                        isFabOptionOn = false;

                    }


                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    fabAnimation(true);


                } else {

                    System.out.println("No Vertical Scrolled");
                }
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(words.size() < 5){

                    StyleableToast.makeText(view.getContext(), "At least five words needed", 10, R.style.favorite).show();


                }else {

                    sp.edit().putString("practice","favorite").apply();
                    Intent intent = new Intent(getContext(),Practice.class);
                    Objects.requireNonNull(getContext()).startActivity(intent);

                }

            }
        });

        //SEARCH

        sv.setDimBackground(false);
        sv.setShowSearchKey(true);





        sv.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {


            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                adapter.getFilter().filter(newQuery);

            }




        });

        return v;
    }

//----------------------------------------------------------------------------------------------------
    private void getFavoriteWordRes(){


        Cursor satRes = satWordDatabase.getData();
        Cursor ieltsRes = ieltsWordDatabase.getData();
        Cursor toeflRes = toeflWordDatabase.getData();
        Cursor greRes = greWordDatabase.getData();


        while (toeflRes.moveToNext()){

            toeflFavWords.add(toeflRes.getString(2));
            int pos =  toeflRes.getInt(0);
            toeflDatabasePosition.add(pos);


        }
        toeflRes.close();
        toeflWordDatabase.close();

        while (ieltsRes.moveToNext()){

            ieltsFavWords.add(ieltsRes.getString(2));
            int pos =  ieltsRes.getInt(0);
            ieltsDatabasePosition.add(pos);


        }

        ieltsRes.close();
        ieltsWordDatabase.close();

        while (satRes.moveToNext()){

            satFavWords.add(satRes.getString(2));
            int pos =  satRes.getInt(0);
            satDatabasePosition.add(pos);


        }
        satRes.close();
        satWordDatabase.close();
        while (greRes.moveToNext()){

            greFavWords.add(greRes.getString(2));
            int pos =  greRes.getInt(0);
            greDatabasePosition.add(pos);


        }
        greRes.close();
        greWordDatabase.close();
    }

    public   void addFavoriteWord(){

        words.clear();

        int toeflWordSize = toeflFavWords.size();
        int ieltsWordSize = ieltsFavWords.size();
        int satWordSize = satFavWords.size();
        int greWordSize  = greFavWords.size();

        String[] beginnerWordArray = getResources().getStringArray(R.array.IELTS_words);
        String[] beginnerTranslationArray = getResources().getStringArray(R.array.IELTS_translation);
        String[] beginnerPronunciationArray = getResources().getStringArray(R.array.IELTS_pronunciation);
        String[] beginnerGrammarArray = getResources().getStringArray(R.array.IELTS_grammar);
        String[] beginnerExampleArray1 = getResources().getStringArray(R.array.IELTS_example1);
        String[] beginnerExampleArray2 = getResources().getStringArray(R.array.IELTS_example2);
        String[] beginnerExampleArray3 = getResources().getStringArray(R.array.IELTS_example3);


        String[] intermediateWordArray = getResources().getStringArray(R.array.TOEFL_words);
        String[] intermediateTranslationArray = getResources().getStringArray(R.array.TOEFL_translation);
        String[] intermediatePronunciationArray = getResources().getStringArray(R.array.TOEFL_pronunciation);
        String[] intermediateGrammarArray = getResources().getStringArray(R.array.TOEFL_grammar);
        String[] intermediateExampleArray1 = getResources().getStringArray(R.array.TOEFL_example1);
        String[] intermediateExampleArray2 = getResources().getStringArray(R.array.TOEFL_example2);
        String[] intermediateExampleArray3 = getResources().getStringArray(R.array.TOEFL_example3);

        String[] advanceWordArray = getResources().getStringArray(R.array.SAT_words);
        String[] advanceTranslationArray = getResources().getStringArray(R.array.SAT_translation);
        String[] advancePronunciationArray = getResources().getStringArray(R.array.SAT_pronunciation);
        String[] advanceGrammarArray = getResources().getStringArray(R.array.SAT_grammar);
        String[] advanceExampleArray1 = getResources().getStringArray(R.array.SAT_example1);
        String[] advanceExampleArray2 = getResources().getStringArray(R.array.SAT_example2);
        String[] advanceExampleArray3 = getResources().getStringArray(R.array.SAT_example3);

        String[] greWordArray = getResources().getStringArray(R.array.GRE_words);
        String[] greTranslationArray  = getResources().getStringArray(R.array.GRE_translation);
        String[] grePronunciationArray = getResources().getStringArray(R.array.GRE_pronunciation);
        String[] greGrammarArray = getResources().getStringArray(R.array.GRE_grammar);
        String[] greExample1 = getResources().getStringArray(R.array.GRE_example1);
        String[] greExample2 = getResources().getStringArray(R.array.GRE_example2);
        String[] greExample3 = getResources().getStringArray(R.array.GRE_example3);

        for(int i = 0; i < satWordSize; i++){


            if(satFavWords.get(i).equalsIgnoreCase("True")){

                Word word = new Word(advanceWordArray[i],advanceTranslationArray[i],"",advancePronunciationArray[i],advanceGrammarArray[i],advanceExampleArray1[i],advanceExampleArray2[i], advanceExampleArray3[i], "SAT",satDatabasePosition.get(i),"",satFavWords.get(i));
                words.add(word);

            }

        }

        for(int i =0 ; i < ieltsWordSize; i++){

            if(ieltsFavWords.get(i).equalsIgnoreCase("True")){

                Word word = new Word(beginnerWordArray[i],beginnerTranslationArray[i],"",beginnerPronunciationArray[i],beginnerGrammarArray[i],beginnerExampleArray1[i],beginnerExampleArray2[i],beginnerExampleArray3[i], "IELTS",ieltsDatabasePosition.get(i),"",ieltsFavWords.get(i));

                words.add(word);
            }

        }

        for(int i =0 ; i < toeflWordSize; i++){

            if(toeflFavWords.get(i).equalsIgnoreCase("True")){
                Word word = new Word(intermediateWordArray[i],intermediateTranslationArray[i],"",intermediatePronunciationArray[i],intermediateGrammarArray[i],intermediateExampleArray1[i],intermediateExampleArray2[i],intermediateExampleArray3[i], "TOEFL",toeflDatabasePosition.get(i),"",toeflFavWords.get(i));
                words.add(word);

            }

        }

        //Toast.makeText(getContext(),"Words: "+greWordArray.length+" Translation: "+greTranslationArray.length+" Second Translation: "+intermediateSecondTranslation.length+" getPronunciation: "+grePronunciationArray.length+" Grammar: "+greGrammarArray.length+" Example1: "+greExample1.length+" Example2"+greExample2.length+" example3: "+greExample3.length+"GRE"+" Database Position: "+greDatabasePosition.size()+""+" FavWords: "+greFavWords.size(),Toast.LENGTH_LONG).show();

        for(int i =0 ; i < greWordSize; i++){

            if(greFavWords.get(i).equalsIgnoreCase("True")){
               Word word = new Word(greWordArray[i],greTranslationArray[i],"",grePronunciationArray[i],greGrammarArray[i],greExample1[i],greExample2[i],greExample3[i], "GRE",greDatabasePosition.get(i),"",greFavWords.get(i));
                words.add(word);

            }

        }


    }

    protected void fabAnimation(boolean isVisible) {
        if (isVisible) {
            fab.animate().cancel();
            fab.animate().translationY(fabY);
        } else {
            fab.animate().cancel();
            fab.animate().translationY(fabY + 500);
        }
    }




    private void initializingSQLDatabase(View v){

        ieltsWordDatabase = new IELTSWordDatabase(v.getContext());
        satWordDatabase = new SATWordDatabase(v.getContext());
        toeflWordDatabase = new TOEFLWordDatabase(v.getContext());
        greWordDatabase = new GREWordDatabase(v.getContext());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.onDestroy();
        if(tts != null ){
            tts.stop();
            tts.shutdown();
        }

        sp.edit().putInt(RECYCLER_VIEW_POSITION,lastRecyclerViewPosition).apply();


    }

    @Override
    public void onMethodCallback(String wordName) {

        if(tts != null){

            tts.setLanguage(Locale.US);
            tts.speak(wordName, TextToSpeech.QUEUE_FLUSH, null,"TTS");

        }
        Toast.makeText(getContext(),"Hello there, this is a callback",Toast.LENGTH_LONG).show();
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
