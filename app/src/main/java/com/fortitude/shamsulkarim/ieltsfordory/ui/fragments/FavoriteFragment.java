package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

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
import com.fortitude.shamsulkarim.ieltsfordory.adapters.FavoriteRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
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
public class FavoriteFragment extends Fragment implements FavoriteRecyclerViewAdapter.AdapterCallback, TextToSpeech.OnInitListener{

    private VocabularyRepository repository;
    private FloatingActionButton fab;
    private FavoriteRecyclerViewAdapter adapter;
    static public final List<Word> words = new ArrayList<>();
    private float fabY;
    private SharedPreferences sp;
    private boolean isFabOptionOn = false;
    private TextToSpeech tts;
    private final String RECYCLER_VIEW_POSITION = "recyclerview_last_pos";
    private int lastRecyclerViewPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite_words,container,false);
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getContext().getColor(R.color.colorPrimary));


        repository = new VocabularyRepository(requireContext());

        tts = new TextToSpeech(getContext(), this);
        fab = v.findViewById(R.id.fab_favorite);
        fab.setColorNormal(getContext().getColor(R.color.colorPrimary));
        fab.setColorPressed(getContext().getColor(R.color.colorPrimaryDark));

        fabY = fab.getY();

        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        TextView noFavorite =  v.findViewById(R.id.havenotlearned);
        ImageView noFavoriteImage =  v.findViewById(R.id.no_favorite_image);
        FloatingSearchView sv =  v.findViewById(R.id.mSearch);

        Toolbar toolbar= v.findViewById(R.id.favoriteToolbar);
        toolbar.setTitle("FAVORITE");
        toolbar.setTitleTextColor(getContext().getColor(R.color.beginnerS));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);



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
                    requireContext().startActivity(intent);

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


    public   void addFavoriteWord(){
        words.clear();
        words.addAll(repository.getFavoriteWord());
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
