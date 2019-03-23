package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by sk on 1/1/17.
 */

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.WordViewHolder>  implements Filterable {


     List<Boolean> isFav = new ArrayList<>();
     List<Word> words,filterList;
     CustomFilterFavorite filter;
     Context context;
     SATWordDatabase satWordDatabase;
     IELTSWordDatabase ieltsWordDatabase;
     TOEFLWordDatabase toeflWordDatabase;
     GREWordDatabase greWordDatabase;
     SharedPreferences sp;
     int languageId;
     int favoriteCount;
     String languageName[] = {"","spanish","hindi","bengali"};


    public FavoriteRecyclerViewAdapter(Context context, List<Word> words) {
        this.context = context;

        this.words = words;
        this.filterList = words;

        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        if(!sp.contains("favoriteCountProfile")){

            sp.edit().putInt("favoriteCountProfile",0).apply();


        }else {

            favoriteCount = sp.getInt("favoriteCountProfile",0);

        }

        languageId =sp.getInt("language",0);
        satWordDatabase = new SATWordDatabase(context);
        ieltsWordDatabase = new IELTSWordDatabase(context);
        toeflWordDatabase = new TOEFLWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);


        addFav();
    }



    private void addFav(){

        for(int i = 0; i < words.size(); i++){

            isFav.add(true);



        }



    }


    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(languageId >= 1){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_favorite_two_language,parent,false);

        }else {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_favorite_one_language,parent,false);
        }



        WordViewHolder viewHolder = new WordViewHolder(view);
        return viewHolder;
    }




    public void onBindViewHolder(WordViewHolder holder, int position) {

        int pos = position;

        Word word = words.get(pos);

        if(isFav.get(pos) == true){


            holder.favorite.setIconResource(R.drawable.favorite_icon_active);
        }else {
         holder.favorite.setIconResource(R.drawable.favorite_icon);


        }

        if(languageId == 1){


            holder.secondLanguageName.setText(languageName[1]);
        }

        if(languageId == 2){


            holder.secondLanguageName.setText(languageName[2]);
        }

        if(languageId == 3){


            holder.secondLanguageName.setText(languageName[3]);
        }




        if(languageId >= 1){

            holder.translationView.setText(word.getTranslation());
            holder.secondTranslation.setText(word.getExtra());


        }else {

            holder.translationView.setText(word.getTranslation());
        }

        holder.wordView.setText(word.getPronun());

        holder.grammarView.setText(word.getGrammar());
        holder.exampleView1.setText(word.getExample1());
        holder.exampleView2.setText(word.getExample2());
        holder.exampleView3.setText(word.getExample3());


    }


    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilterFavorite(filterList,this);
        }

        return filter;
    }


    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextToSpeech.OnInitListener{

        TextView wordView,translationView, grammarView, exampleView1,exampleView2,exampleView3,secondLanguageName,secondTranslation;
        FancyButton favorite, speaker;
        TextToSpeech tts;
        CardView cardView;


        public WordViewHolder(View itemView) {
            super(itemView);
            Typeface ABeeZee = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Regular.ttf");
            Typeface ABeeZeeItalic  = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ABeeZee-Italic.ttf");
            tts = new TextToSpeech(itemView.getContext(), this);

            //englishLanguage = (TextView)itemView.findViewById(R.id.favorite_second_language);
            secondLanguageName = (TextView)itemView.findViewById(R.id.favorite_second_language2);
            secondTranslation = (TextView)itemView.findViewById(R.id.favorite_second_translation);
            wordView = (TextView)itemView.findViewById(R.id.favorite_card_word);
            translationView = (TextView)itemView.findViewById(R.id.favorite_card_translation);
            grammarView = (TextView)itemView.findViewById(R.id.favorite_card_grammar);

            exampleView1 = (TextView)itemView.findViewById(R.id.favorite_card_example1);
            exampleView2 = (TextView)itemView.findViewById(R.id.favorite_card_example2);
            exampleView3 = (TextView)itemView.findViewById(R.id.favorite_card_example3);
            speaker = (FancyButton) itemView.findViewById(R.id.favorite_speaker);
            cardView = (CardView)itemView.findViewById(R.id.recycler_view_card);
            cardView.setPreventCornerOverlap(false);

//
//            wordView.setTypeface(ABeeZee);
//            translationView.setTypeface(ABeeZee);
//            grammarView.setTypeface(ABeeZee);
//
//            exampleView1.setTypeface(ABeeZee);
//
//
//            exampleView2.setTypeface(ABeeZee);
//            exampleView3.setTypeface(ABeeZee);
//
//            if(languageId == 1){
//                secondLanguageName.setTypeface(ABeeZeeItalic);
//                secondTranslation.setTypeface(ABeeZee);
////                secondLanguageName.setText("Spanish");
//            }
//            if(languageId == 2){
//                secondLanguageName.setText("Bangla");
//            }
//            if(languageId == 3){
//                secondLanguageName.setText("Hindi");
//            }

            favorite = (FancyButton) itemView.findViewById(R.id.favorite_favorite);
            favorite.setOnClickListener(this);
            speaker.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            Word word = words.get(getAdapterPosition());



            //Toast.makeText(view.getContext(),"level: "+words.get(getAdapterPosition()).level+ "DatabasePosition: "+words.get(getAdapterPosition()).databasePosition,Toast.LENGTH_SHORT).show();

            if( view == speaker){


                tts.setLanguage(Locale.US);
                tts.speak(word.getWord(), TextToSpeech.QUEUE_ADD, null);


            }

            if(view.getId() == R.id.favorite_favorite){


                if(word.isFavorite.equalsIgnoreCase("True")){

                    favorite.setIconResource(R.drawable.ic_favorite);
                    favorite.setTag(R.drawable.ic_favorite);
                    isFav.set(getAdapterPosition(),false);


                    if(favoriteCount > 0){

                        favoriteCount--;
                        sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();

                    }

                    if(word.vocabularyType.equalsIgnoreCase("IELTS")){

                        ieltsWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","False");

                    }

                    if(word.vocabularyType.equalsIgnoreCase("TOEFL")){
                        toeflWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","False");

                    }

                    if(word.vocabularyType.equalsIgnoreCase("SAT")){

                        satWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","False");


                    }

                    if(word.vocabularyType.equalsIgnoreCase("GRE")){

                        greWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","False");


                    }





                }else {
                    favoriteCount++;
                    sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();


                    isFav.set(getAdapterPosition(),true);
                    favorite.setIconResource(R.drawable.favorite_card_view);
                    favorite.setTag(null);

                    if(words.get(getAdapterPosition()).level.equalsIgnoreCase("IELTS")){

                        ieltsWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","True");
                    }

                    if(words.get(getAdapterPosition()).level.equalsIgnoreCase("TOEFL")){
                        toeflWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","True");

                    }

                    if(words.get(getAdapterPosition()).level.equalsIgnoreCase("SAT")){

                        satWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","True");


                    }
                    if(words.get(getAdapterPosition()).level.equalsIgnoreCase("GRE")){

                        greWordDatabase.updateFav(words.get(getAdapterPosition()).position+"","True");


                    }

                }




            }
        }

        @Override
        public void onInit(int status) {

        }
    }





}
