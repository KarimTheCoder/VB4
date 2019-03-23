package com.fortitude.shamsulkarim.ieltsfordory;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.Practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.github.clans.fab.FloatingActionButton;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteWords extends Fragment  {



    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private FavoriteRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    static public List<Word> words = new ArrayList<>();
    private float fabY;
    SharedPreferences sp;

    List<String> ieltsFavWords, toeflFavWords, satFavWords, greFavWords;
    List<Integer> ieltsDatabasePosition, toeflDatabasePosition, satDatabasePosition, greDatabasePosition;
    boolean isFabOptionOn = false;
    TextView noFavorite;
    ImageView noFavoriteImage;
    private FloatingSearchView sv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite_words,container,false);

        fab = (FloatingActionButton)v.findViewById(R.id.fab_favorite);
        fab.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));

        fabY = fab.getY();

        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        initializingSQLDatabase(v);


        noFavorite = (TextView)v.findViewById(R.id.havenotlearned);
        noFavoriteImage = (ImageView)v.findViewById(R.id.no_favorite_image);
        sv= (FloatingSearchView) v.findViewById(R.id.mSearch);

        Toolbar toolbar= (Toolbar)v.findViewById(R.id.favoriteToolbar);
        toolbar.setTitle("FAVORITE");
        toolbar.setTitleTextColor(getResources().getColor(R.color.beginnerS));
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

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view_favorite_words);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new FavoriteRecyclerViewAdapter(getContext(), words);
        recyclerView.setAdapter(adapter);




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {



            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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

                    getContext().startActivity(intent);

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
            int pos = (Integer) toeflRes.getInt(0);
            toeflDatabasePosition.add(pos);

        }

        while (ieltsRes.moveToNext()){

            ieltsFavWords.add(ieltsRes.getString(2));
            int pos = (Integer) ieltsRes.getInt(0);
            ieltsDatabasePosition.add(pos);

        }

        while (satRes.moveToNext()){

            satFavWords.add(satRes.getString(2));
            int pos = (Integer) satRes.getInt(0);
            satDatabasePosition.add(pos);

        }

        while (greRes.moveToNext()){

            greFavWords.add(greRes.getString(2));
            int pos = (Integer) greRes.getInt(0);
            greDatabasePosition.add(pos);

        }

    }

    public   void addFavoriteWord(){

        words.clear();
        int languageId = sp.getInt("language",0);


        String[] beginnerSecondTranslation = new String[getResources().getStringArray(R.array.IELTS_words).length];
        String[] intermediateSecondTranslation = new String[getResources().getStringArray(R.array.TOEFL_words).length];
        String[] advanceSecondTranslation = new String[getResources().getStringArray(R.array.SAT_words).length];

        if(languageId == 0){

            for(int i = 0; i < getResources().getStringArray(R.array.IELTS_words).length; i++){


                beginnerSecondTranslation[i] = "";
            }

        }

        if(languageId == 1){

            beginnerSecondTranslation = getResources().getStringArray(R.array.beginner_spanish);
            intermediateSecondTranslation = getResources().getStringArray(R.array.intermediate_spanish);
            advanceSecondTranslation = getResources().getStringArray(R.array.advance_spanish);
        }
        if(languageId == 3){

            beginnerSecondTranslation = getResources().getStringArray(R.array.beginner_bengali);
            intermediateSecondTranslation = getResources().getStringArray(R.array.intermediate_bengali);
            advanceSecondTranslation = getResources().getStringArray(R.array.advance_bengali);
        }
        if(languageId == 2){

            beginnerSecondTranslation = getResources().getStringArray(R.array.beginner_hindi);
            intermediateSecondTranslation = getResources().getStringArray(R.array.intermediate_hindi);
            advanceSecondTranslation = getResources().getStringArray(R.array.advance_hindi);
        }

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




}
