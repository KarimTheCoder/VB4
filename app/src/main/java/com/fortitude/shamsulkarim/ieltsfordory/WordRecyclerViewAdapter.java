package com.fortitude.shamsulkarim.ieltsfordory;

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
import android.widget.Toast;

import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by sk on 12/30/16.
 */

public class WordRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    ArrayList<Object> words,filterList;
    CustomFilter filter;
    List<String> beginnerFav = new ArrayList<>();
    Context context;
    final static int WORD_VIEW_TYPE = 0;
    final static int AD_VIEW_TYPE = 1;
    SharedPreferences sp;
    String languageName[] = {"","spanish","hindi","bengali"};
    int languageId;
    int favoriteCount;
    boolean connected = false;
    int favoriteLevel;

    IELTSWordDatabase ieltsWordDatabase;
    TOEFLWordDatabase toeflWordDatabase;
    SATWordDatabase satWordDatabase;
    GREWordDatabase greWordDatabase;
    public String audioPath= null;
    public File localFile = null;
    StorageReference gsReference;
    private FirebaseStorage storage;
    private WordAdapterCallback wordAdapterCallback;

    public WordRecyclerViewAdapter(Context context, ArrayList<Object> words, WordAdapterCallback wordAdapterCallback) {
        this.context = context;

        try{
            this.wordAdapterCallback = wordAdapterCallback;
        }catch (ClassCastException e){
            Log.e("WordAdapter init",e.getMessage());
        }

        storage = FirebaseStorage.getInstance();
        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        favoriteLevel = sp.getInt("prevWordSelection",0);
//        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            //we are connected to a network
//            connected = true;
//        }
//        else{
//            connected = false;
//        }

        if(!sp.contains("favoriteCountProfile")){

            sp.edit().putInt("favoriteCountProfile",0).apply();


        }else {

            favoriteCount = sp.getInt("favoriteCountProfile",0);

        }

        languageId = sp.getInt("language",0);
        ieltsWordDatabase = new IELTSWordDatabase(context);
        toeflWordDatabase = new TOEFLWordDatabase(context);
        satWordDatabase = new SATWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);
        favoriteWordChecker();
        this.words = words;
        this.filterList = words;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType){


            case WORD_VIEW_TYPE:
                if(languageId == 0){
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_language,parent,false);
                }else {

                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_language,parent,false);
                }
                WordViewHolder viewHolder = new WordViewHolder(view);
                return viewHolder;

            case AD_VIEW_TYPE:

                default:
                    View ad = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout,parent,false);
                    return new NativeExpressAdViewHolder(ad);

        }







    }

    public void onDestroy(){
        ieltsWordDatabase.close();
        toeflWordDatabase.close();
        satWordDatabase.close();
        greWordDatabase.close();
        Toast.makeText(context,"ON Dititsljdfjf",Toast.LENGTH_LONG).show();
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
                if(languageId>= 1){

                    wordViewHolder.translationView.setText(word.getTranslation());
                    wordViewHolder.secondTranslation.setText(word.getExtra());
                }else{

                    wordViewHolder.translationView.setText(word.getTranslation());
                }
                if(languageId == 1){


                    wordViewHolder.secondLanguage.setText(languageName[1]);
                }

                if(languageId == 2){


                    wordViewHolder.secondLanguage.setText(languageName[2]);
                }
                if(languageId == 3){


                    wordViewHolder.secondLanguage.setText(languageName[3]);
                }


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



        beginnerFav.clear();


        if( favoriteLevel == 0){
            Cursor beginnerRes = ieltsWordDatabase.getData();


            while (beginnerRes.moveToNext()){

                beginnerFav.add(beginnerRes.getString(2));

            }
            beginnerRes.close();
        }

        if( favoriteLevel == 1){

            Cursor beginnerRes = toeflWordDatabase.getData();


            while (beginnerRes.moveToNext()){

                beginnerFav.add(beginnerRes.getString(2));

            }
            beginnerRes.close();
        }

        if( favoriteLevel == 2){

                Cursor beginnerRes = satWordDatabase.getData();


                while (beginnerRes.moveToNext()){

                    beginnerFav.add(beginnerRes.getString(2));


                }
            beginnerRes.close();
        }



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




        TextView wordView,translationView, grammarView, exampleView1, secondTranslation, secondLanguage, englishLanguage;
        FancyButton favorite, speaker;
        TextToSpeech tts;
        CardView cardView;
        Boolean isVoicePronunciation = true;
        ProgressBar progressBar;


        public WordViewHolder(View itemView) {
            super(itemView);


            Typeface ABeeZee = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Regular.ttf");
            Typeface ABeeZeeItalic  = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Italic.ttf");

            englishLanguage = itemView.findViewById(R.id.card_language);
            wordView = itemView.findViewById(R.id.favorite_card_word);
            translationView = itemView.findViewById(R.id.favorite_card_translation);
            grammarView = itemView.findViewById(R.id.card_grammar);
            exampleView1 = itemView.findViewById(R.id.card_example1);
            secondTranslation =  itemView.findViewById(R.id.card_translation_extra);
            secondLanguage = itemView.findViewById(R.id.card_language_extra);
            speaker =  itemView.findViewById(R.id.favorite_speaker);
            cardView = itemView.findViewById(R.id.recycler_view_card);
            cardView.setPreventCornerOverlap(false);
            tts = new TextToSpeech(itemView.getContext(), this);
            progressBar = itemView.findViewById(R.id.spin_kit);
            Sprite doubleBounce = new Wave();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.INVISIBLE);

          //  progressBar = itemView.findViewById(R.id.spin_kit);
          //  progressBar.setVisibility(View.INVISIBLE);


            isVoicePronunciation = sp.getBoolean("pronunState",true);

//            wordView.setTypeface(ABeeZee);
//            translationView.setTypeface(ABeeZee);
//            grammarView.setTypeface(ABeeZee);
//            exampleView1.setTypeface(ABeeZee);

            if(languageId == 1){
                secondLanguage.setTypeface(ABeeZeeItalic);
                englishLanguage.setTypeface(ABeeZeeItalic);
                secondTranslation.setTypeface(ABeeZee);
            }



            favorite = itemView.findViewById(R.id.favorite);
            favorite.setIconResource(R.drawable.ic_favorite_icon);
            favorite.setTag(null);

            favorite.setOnClickListener(this);
            speaker.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

           // Toast.makeText(context,""+((Word) words.get(getAdapterPosition())).vocabularyType,Toast.LENGTH_SHORT).show();

            int position = 1+getAdapterPosition();
            Word word = (Word) words.get(getAdapterPosition());
            int wordPos = ((Word) words.get(getAdapterPosition())).position;
            int listPos = word.getNumber();

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

                    beginnerFav.set(listPos,"True");

                    if(((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("IELTS")){
                        ieltsWordDatabase.updateFav(wordPos+"","True");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("TOEFL")){
                        toeflWordDatabase.updateFav(wordPos+"","True");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("SAT")){
                        satWordDatabase.updateFav(wordPos+"","True");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("GRE")){
                        greWordDatabase.updateFav(wordPos+"","True");
                    }

                    favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    favorite.setTag(R.drawable.ic_favorite_icon_active);

                }
                else {

                    if(favoriteCount > 0){

                        favoriteCount--;
                        sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();

                    }
                    beginnerFav.set(listPos,"False");

                    if(((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("IELTS")){
                        ieltsWordDatabase.updateFav(wordPos+"","false");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("TOEFL")){
                        toeflWordDatabase.updateFav(wordPos+"","false");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("SAT")){
                        satWordDatabase.updateFav(wordPos+"","false");
                    }

                    if( ((Word) words.get(getAdapterPosition())).vocabularyType.equalsIgnoreCase("GRE")){
                        greWordDatabase.updateFav(wordPos+"","false");
                    }


                    favorite.setIconResource(R.drawable.ic_favorite_icon);
                    favorite.setTag(null);


                }




            }



        }

        public void downloadAudio(String wordName){

            progressBar.setVisibility(View.VISIBLE);

            wordName = wordName.toLowerCase();
            gsReference = storage.getReferenceFromUrl("gs://fir-userauthentication-f751c.appspot.com/audio/"+wordName+".mp3");

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
                    progressBar.setVisibility(View.INVISIBLE);;
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
