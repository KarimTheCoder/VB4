package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.Word;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class TrainFinishedWordRecyclerView extends RecyclerView.Adapter<TrainFinishedWordRecyclerView.WordViewHolder>  {

    private List<Word> words;
    private final static int WORD_VIEW_TYPE = 0;
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;



    public TrainFinishedWordRecyclerView(Context context, List<Word> words){

        this.words = words;
        ieltsWordDatabase = new IELTSWordDatabase(context);
        toeflWordDatabase = new TOEFLWordDatabase(context);
        satWordDatabase = new SATWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);


    }


    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_finished_word_recycler_view,parent,false);

        WordViewHolder viewHolder = new WordViewHolder(view);
        return viewHolder;



    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {

        holder.word.setText(words.get(position).getWord());

        if(words.get(position).getIsFavorite().equalsIgnoreCase("true")){
            holder.favorite.setIconResource(R.drawable.favorite_icon_active);
        }

    }

    public int getItemViewType(int position) {

        return WORD_VIEW_TYPE;


    }
    @Override
    public int getItemCount() {
        return words.size();
    }



    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        FancyButton unlearnButton, favorite;
        Word mostMistakenWord;
        TextView word;

        public WordViewHolder(View itemView) {
            super(itemView);


            unlearnButton = itemView.findViewById(R.id.train_finished_recycler_view_unlearn);
            favorite = itemView.findViewById(R.id.train_finished_recycler_view_favorite);
            word = itemView.findViewById(R.id.train_finished_recycler_view_word);

            favorite.setOnClickListener(this);
            unlearnButton.setOnClickListener(this);


        }


        private void setUnlearn(){

            if(mostMistakenWord.vocabularyType.equalsIgnoreCase("IELTS")){

                if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                    mostMistakenWord.setIsLearned("false");
                    ieltsWordDatabase.updateLearned(mostMistakenWord.position+"","false");
                    unlearnButton.setText("Learn");



                }else {

                    mostMistakenWord.setIsLearned("true");
                    unlearnButton.setText("Unlearn");
                    ieltsWordDatabase.updateLearned(mostMistakenWord.position+"","true");
                }


            }

            if(mostMistakenWord.vocabularyType.equalsIgnoreCase("TOEFL")){

                if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                    mostMistakenWord.setIsLearned("false");
                    unlearnButton.setText("Learn");
                    toeflWordDatabase.updateLearned(mostMistakenWord.position+"","false");

                }else {
                    mostMistakenWord.setIsLearned("true");
                    unlearnButton.setText("Unlearn");
                    toeflWordDatabase.updateLearned(mostMistakenWord.position+"","true");
                }


            }

            if(mostMistakenWord.vocabularyType.equalsIgnoreCase("SAT")){

                if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                    mostMistakenWord.setIsLearned("false");
                    satWordDatabase.updateLearned(mostMistakenWord.position+"","false");
                    unlearnButton.setText("Learn");

                }else {
                    mostMistakenWord.setIsLearned("true");
                    unlearnButton.setText("Unlearn");
                    satWordDatabase.updateLearned(mostMistakenWord.position+"","true");
                }


            }

            if(mostMistakenWord.vocabularyType.equalsIgnoreCase("GRE")){

                if(mostMistakenWord.isLearned.equalsIgnoreCase("true")){

                    mostMistakenWord.setIsLearned("false");
                    unlearnButton.setText("Learn");
                    greWordDatabase.updateLearned(mostMistakenWord.position+"","false");

                }else {

                    mostMistakenWord.setIsLearned("true");
                    unlearnButton.setText("Unlearn");
                    greWordDatabase.updateLearned(mostMistakenWord.position+"","true");
                }


            }


        }
        @Override
        public void onClick(View v) {

            mostMistakenWord = words.get(getAdapterPosition());

            String vocabularyType = words.get(getAdapterPosition()).vocabularyType;
            String isFavorite = words.get(getAdapterPosition()).isFavorite;
            String isLearned = words.get(getAdapterPosition()).isLearned;
            int databasePosition = words.get(getAdapterPosition()).position;

            //Toast.makeText(v.getContext(),vocabularyType,Toast.LENGTH_SHORT).show();


            if( v == unlearnButton){


                setUnlearn();
            }

            if( v == favorite){

                if(isFavorite.equalsIgnoreCase("true")){

                    if(vocabularyType.equalsIgnoreCase("IELTS")){

                        favorite.setIconResource(R.drawable.favorite_icon);
                        words.get(getAdapterPosition()).setIsFavorite("false");
                        ieltsWordDatabase.updateFav(""+databasePosition,"false");


                    }

                    if( vocabularyType.equalsIgnoreCase("TOEFL")){

                        favorite.setIconResource(R.drawable.favorite_icon);
                        words.get(getAdapterPosition()).setIsFavorite("false");
                        toeflWordDatabase.updateFav(""+databasePosition,"false");

                    }

                    if( vocabularyType.equalsIgnoreCase("SAT")){

                        favorite.setIconResource(R.drawable.favorite_icon);
                        words.get(getAdapterPosition()).setIsFavorite("false");
                        satWordDatabase.updateFav(""+databasePosition,"false");

                    }

                    if( vocabularyType.equalsIgnoreCase("GRE")){

                        favorite.setIconResource(R.drawable.favorite_icon);
                        words.get(getAdapterPosition()).setIsFavorite("false");
                        greWordDatabase.updateFav(""+databasePosition,"false");

                    }


                }else {

                    if(vocabularyType.equalsIgnoreCase("IELTS")){

                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        words.get(getAdapterPosition()).setIsFavorite("true");
                        ieltsWordDatabase.updateFav(""+databasePosition,"true");


                    }

                    if( vocabularyType.equalsIgnoreCase("TOEFL")){

                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        words.get(getAdapterPosition()).setIsFavorite("true");
                        toeflWordDatabase.updateFav(""+databasePosition,"true");

                    }

                    if( vocabularyType.equalsIgnoreCase("SAT")){

                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        words.get(getAdapterPosition()).setIsFavorite("true");
                        satWordDatabase.updateFav(""+databasePosition,"true");

                    }

                    if( vocabularyType.equalsIgnoreCase("GRE")){

                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        words.get(getAdapterPosition()).setIsFavorite("true");
                        greWordDatabase.updateFav(""+databasePosition,"true");

                    }
                }
            }



        }
    }



}
