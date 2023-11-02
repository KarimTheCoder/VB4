package com.fortitude.shamsulkarim.ieltsfordory.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by sk on 12/30/16.
 */

public class WordRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    public  ArrayList<Object> words;
    private final ArrayList<Object> filterList;
    private CustomFilter filter;
    private final Context context;
    private final static int WORD_VIEW_TYPE = 0;
    private final static int AD_VIEW_TYPE = 1;
    private  SharedPreferences sp;
    private int favoriteCount;
    private  int favoriteLevel;

    public String audioPath= null;
    public File localFile = null;
    private  FirebaseStorage storage;
    private VocabularyRepository repository;
    private WordAdapterCallback wordAdapterCallback;

    public WordRecyclerViewAdapter(Context context, ArrayList<Object> words, WordAdapterCallback wordAdapterCallback) {
        this.context = context;

        init(context, wordAdapterCallback);
        initDatabase(context);
        favoriteWordChecker();

        this.words = words;
        this.filterList = words;
    }

    private void initDatabase(Context context) {
        repository = new VocabularyRepository(context);
    }

    private void init(Context context, WordAdapterCallback wordAdapterCallback) {
        try{
            this.wordAdapterCallback = wordAdapterCallback;
        }catch (ClassCastException e){
            Log.e("WordAdapter init",e.getMessage());
        }

        storage = FirebaseStorage.getInstance();
        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        favoriteLevel = sp.getInt("prevWordSelection",0);

        if(!sp.contains("favoriteCountProfile")){

            sp.edit().putInt("favoriteCountProfile",0).apply();


        }else {

            favoriteCount = sp.getInt("favoriteCountProfile",0);

        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType){


            case WORD_VIEW_TYPE:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_language,parent,false);

                return new WordViewHolder(view);

            case AD_VIEW_TYPE:

                default:
                    View ad = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout,parent,false);
                    return new NativeExpressAdViewHolder(ad);

        }







    }

    public void onDestroy(){

    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        int viewType = getItemViewType(position);

        switch (viewType){

            case WORD_VIEW_TYPE:
//
                Word word = (Word) words.get(position);
                WordViewHolder wordViewHolder = (WordViewHolder)holder;


                if(((Word) words.get(position)).isFavorite.equalsIgnoreCase("true")){

                    wordViewHolder.favorite.setIconResource(R.drawable.ic_favorite_icon_active);

                }else {

                    wordViewHolder.favorite.setIconResource(R.drawable.ic_favorite_icon);

                }
//                //------------------------------------------------------------
//

                wordViewHolder.translationView.setText(word.getTranslation());
                wordViewHolder.wordView.setText(word.getPronun());
                wordViewHolder.grammarView.setText(word.getGrammar());
                wordViewHolder.exampleView1.setText(word.getExample1());

                break;

            case AD_VIEW_TYPE:

                default:
//
//                    NativeExpressAdViewHolder nativeExpressHolder =
//                            (NativeExpressAdViewHolder) holder;
//                    NativeExpressAdView adView =
//                            (NativeExpressAdView) words.get(position);
//                    ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
//
//                    if (adCardView.getChildCount() > 0) {
//                        adCardView.removeAllViews();
//                    }
//                    if (adView.getParent() != null) {
//                        ((ViewGroup) adView.getParent()).removeView(adView);
//                    }
//
//                    // Add the Native Express ad to the native express ad view.
//                    adCardView.addView(adView);

        }





    }

    private void favoriteWordChecker(){

    }


    public int getItemViewType(int position) {

//        if(connected){
//            return (position %12 == 0)? AD_VIEW_TYPE: WORD_VIEW_TYPE;
//        }else {



            return WORD_VIEW_TYPE;
//        }

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }

        return filter;
    }

//------------------- INNER CLASS--------------------------------------------------------------

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextToSpeech.OnInitListener{


        final TextView wordView;
        final TextView translationView;
        final TextView grammarView;
        final TextView exampleView1;
        final FancyButton favorite;
        final FancyButton speaker;
        final TextToSpeech tts;
        final CardView cardView;
        final Boolean isVoicePronunciation;
        final ProgressBar progressBar;


        public WordViewHolder(View itemView) {
            super(itemView);


            Typeface ABeeZee = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Regular.ttf");
            Typeface ABeeZeeItalic  = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Italic.ttf");

            wordView = itemView.findViewById(R.id.favorite_card_word);
            translationView = itemView.findViewById(R.id.favorite_card_translation);
            grammarView = itemView.findViewById(R.id.card_grammar);
            exampleView1 = itemView.findViewById(R.id.card_example1);

            speaker =  itemView.findViewById(R.id.favorite_speaker);
            cardView = itemView.findViewById(R.id.recycler_view_card);
            cardView.setPreventCornerOverlap(false);
            tts = new TextToSpeech(itemView.getContext(), this);
            progressBar = itemView.findViewById(R.id.spin_kit);
            Sprite doubleBounce = new Wave();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.INVISIBLE);
            isVoicePronunciation = sp.getBoolean("pronunState",true);
            favorite = itemView.findViewById(R.id.favorite);
            favorite.setIconResource(R.drawable.ic_favorite_icon);
            favorite.setTag(null);

            favorite.setOnClickListener(this);
            speaker.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

           // Toast.makeText(context,""+((Word) words.get(getAdapterPosition())).vocabularyType,Toast.LENGTH_SHORT).show();

            Word word = (Word) words.get(getAdapterPosition());
            int wordPos = ((Word) words.get(getAdapterPosition())).position;


            if(view == speaker){


                String wordName = word.getWord().toLowerCase();

                if (ConnectivityHelper.isConnectedToNetwork(context) && isVoicePronunciation) {
                    //Show the connected screen
                    downloadAudio(wordName);
                    //Toast.makeText(context,"Connectedssss",Toast.LENGTH_LONG).show();

                } else {
                    //Show disconnected screen
                    //Toast.makeText(context,"Not connected",Toast.LENGTH_LONG).show();
//                    tts.setLanguage(Locale.US);
//                    tts.speak(wordName, TextToSpeech.QUEUE_ADD, null);
                    try {
                        wordAdapterCallback.onMethodCallback(wordName);
                    } catch (ClassCastException e) {
                        // do something
                        Log.e("WordAdapterCallback", Objects.requireNonNull(e.getMessage()));
                    }
                }

            }


            if(view.getId() == R.id.favorite){

                if(favorite.getTag() == null){

                    favoriteCount++;
                    sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();

                    if(((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("IELTS")){
                      //  ieltsWordDatabase.updateFav(wordPos+"","True");

                        repository.updateIELTSFavoriteState(wordPos+"","True");

                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("TOEFL")){
                        //toeflWordDatabase.updateFav(wordPos+"","True");
                        repository.updateTOEFLFavoriteState(wordPos+"","True");

                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("SAT")){
                        //satWordDatabase.updateFav(wordPos+"","True");
                        repository.updateSATFavoriteState(wordPos+"","True");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("GRE")){
                        //greWordDatabase.updateFav(wordPos+"","True");
                        repository.updateGREFavoriteState(wordPos+"","True");
                    }

                    favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    favorite.setTag(R.drawable.ic_favorite_icon_active);

                }
                else {

                    if(favoriteCount > 0){

                        favoriteCount--;
                        sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();

                    }

                    if(((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("IELTS")){
                       // ieltsWordDatabase.updateFav(wordPos+"","false");
                        repository.updateIELTSFavoriteState(wordPos+"","false");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("TOEFL")){
                        //toeflWordDatabase.updateFav(wordPos+"","false");
                        repository.updateTOEFLFavoriteState(wordPos+"","false");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("SAT")){
                        //satWordDatabase.updateFav(wordPos+"","false");
                        repository.updateSATFavoriteState(wordPos+"","false");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("GRE")){
                        //greWordDatabase.updateFav(wordPos+"","false");

                        repository.updateGREFavoriteState(wordPos+"","false");
                        
                    }


                    favorite.setIconResource(R.drawable.ic_favorite_icon);
                    favorite.setTag(null);


                }




            }



        }

        public void downloadAudio(String wordName){

            progressBar.setVisibility(View.VISIBLE);

            wordName = wordName.toLowerCase();
            StorageReference gsReference = storage.getReferenceFromUrl("gs://fir-userauthentication-f751c.appspot.com/audio/" + wordName + ".mp3");

            try{
                localFile = File.createTempFile("Audio","mp3");
            }catch (IOException e){
                e.printStackTrace();
            }

            gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(context , localFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    audioPath = localFile.getAbsolutePath();

                    MediaPlayer mp = new MediaPlayer();
                    //Toast.makeText(context,audioPath,Toast.LENGTH_LONG).show();
                    try{

                        mp.setDataSource(audioPath);
                        mp.prepare();
                        mp.start();
                        speaker.setEnabled(false);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                speaker.setEnabled(true);
                              //  Toast.makeText(context,"play finished", Toast.LENGTH_LONG).show();
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }



                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                 //   Toast.makeText(context,"Completed",Toast.LENGTH_LONG).show();
                    speaker.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });

        }

        @Override
        public void onInit(int status) {

        }
    }

    // Native Express ad ----------------------------------------------------------------------------


    public static class NativeExpressAdViewHolder extends RecyclerView.ViewHolder{


        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface WordAdapterCallback{
        void onMethodCallback(String word);
    }

}
