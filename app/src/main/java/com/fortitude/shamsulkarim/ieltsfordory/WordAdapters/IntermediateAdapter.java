package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.CustomFilterIntermediate;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.Word;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by sk on 1/3/17.
 */

public class IntermediateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<String> isFav = new ArrayList<>();
    public ArrayList<Object> words,filterList;
    CustomFilterIntermediate filter;
    Context context;
    TOEFLWordDatabase db;
    final static int WORD_VIEW_TYPE = 0;
    final static int AD_VIEW_TYPE = 1;
    SharedPreferences sp;
    boolean connected;
    String languageName[] = {"","spanish","hindi","bengali"};
    int languageId, favoriteCount;



    public IntermediateAdapter(Context context, ArrayList<Object> words) {
        this.context = context;
        this.words = words;
        this.filterList = words;
        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

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



        db = new TOEFLWordDatabase(context);
        favoriteWordChecker();
    }



    private void favoriteWordChecker(){

        isFav.clear();
        Cursor res = db.getData();


        while (res.moveToNext()){

            isFav.add(res.getString(2));

        }


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




    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);



        switch (viewType){

            case WORD_VIEW_TYPE:

                Word word = (Word) words.get(position);
                WordViewHolder wordViewHolder = (WordViewHolder)holder;


                if(word.getFavNum() == 1){

                    wordViewHolder.favorite.setIconResource(R.drawable.favorite_card_view);

                }else {

                    wordViewHolder.favorite.setIconResource(R.drawable.ic_favorite);

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

                if(languageId>= 1){

                    wordViewHolder.translationView.setText(word.getTranslation());
                    wordViewHolder.secondTranslation.setText(word.getExtra());
                }else{

                    wordViewHolder.translationView.setText(word.getTranslation());
                }





                wordViewHolder.translationView.setText(word.getTranslation());


                wordViewHolder.wordView.setText(word.getWord());

                wordViewHolder.grammarView.setText(word.getGrammar());
                wordViewHolder.pronunciationView.setText(word.getPronun());
                wordViewHolder.exampleView1.setText(word.getExample1());
                break;


            case AD_VIEW_TYPE:

                default:

                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) words.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;

                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);


        }




    }


    public int getItemViewType(int position) {

        if(connected){
            return (position %12 == 0)? AD_VIEW_TYPE: WORD_VIEW_TYPE;
        }else {


            return WORD_VIEW_TYPE;
        }

    }
    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilterIntermediate(filterList,this);
        }

        return filter;
    }


    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextToSpeech.OnInitListener{

        TextView wordView,translationView, grammarView, pronunciationView, exampleView1, secondTranslation, secondLanguage, englishLanguage;
        FancyButton favorite, speaker;
        TextToSpeech tts;
        CardView cardView;


        public WordViewHolder(View itemView) {
            super(itemView);
            Typeface ABeeZee = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Regular.ttf");
            Typeface ABeeZeeItalic  = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Italic.ttf");


            englishLanguage = (TextView)itemView.findViewById(R.id.card_language);

            wordView = (TextView)itemView.findViewById(R.id.favorite_card_word);
            translationView = (TextView)itemView.findViewById(R.id.favorite_card_translation);
            grammarView = (TextView)itemView.findViewById(R.id.card_grammar);
            exampleView1 = (TextView)itemView.findViewById(R.id.card_example1);
            cardView = (CardView)itemView.findViewById(R.id.recycler_view_card);
            cardView.setPreventCornerOverlap(false);

            secondLanguage = (TextView)itemView.findViewById(R.id.card_language_extra);
            secondTranslation = (TextView)itemView.findViewById(R.id.card_translation_extra);
            speaker = (FancyButton) itemView.findViewById(R.id.favorite_speaker);
            tts = new TextToSpeech(itemView.getContext(), this);

            wordView.setTypeface(ABeeZee);
            translationView.setTypeface(ABeeZee);
            grammarView.setTypeface(ABeeZee);
            pronunciationView.setTypeface(ABeeZee);
            exampleView1.setTypeface(ABeeZee);

            if(languageId == 1){
                secondLanguage.setTypeface(ABeeZeeItalic);
                englishLanguage.setTypeface(ABeeZeeItalic);
                secondTranslation.setTypeface(ABeeZee);
            }

            favorite = (FancyButton) itemView.findViewById(R.id.favorite);
            favorite.setTag(null);

            favorite.setOnClickListener(this);
            speaker.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int position = 1+getAdapterPosition();
            Word word = (Word) words.get(getAdapterPosition());
            int wordPos = 1+word.getNumber();
            int listPos = word.getNumber();


            if(view == speaker){

                String wordtext = wordView.getText().toString();
                tts.setLanguage(Locale.US);
                tts.speak(wordtext, TextToSpeech.QUEUE_ADD, null);
            }



            if(view.getId() == R.id.favorite){

                if(favorite.getTag() == null){

                    isFav.set(listPos,"True");
                    db.updateFav(wordPos+"","True");
                    favorite.setIconResource(R.drawable.favorite_card_view);
                    favorite.setTag(R.drawable.favorite_card_view);
                    favoriteCount++;
                    sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();
                }
                else {

                    if(favoriteCount > 0){

                        favoriteCount--;
                        sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();

                    }
                    isFav.set(listPos,"False");

                    db.updateFav(wordPos+"","False");
                    favorite.setIconResource(R.drawable.ic_favorite);
                    favorite.setTag(null);


                }




            }




        }

        @Override
        public void onInit(int status) {

        }
    }


    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder{


        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }


}
