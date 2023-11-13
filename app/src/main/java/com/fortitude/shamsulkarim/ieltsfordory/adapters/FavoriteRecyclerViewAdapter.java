package com.fortitude.shamsulkarim.ieltsfordory.adapters;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
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

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
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
import java.util.List;
import java.util.Objects;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by sk on 1/1/17.
 */

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.WordViewHolder>  implements Filterable {

    public String audioPath= null;
    public File localFile = null;
    private final List<Boolean> isFav = new ArrayList<>();
    public List<Word> words,filterList;
    private CustomFilterFavorite filter;
    private final Context context;

    private final VocabularyRepository repository;

    private final SharedPreferences sp;
    private int favoriteCount;
    private final FirebaseStorage storage;
    private final AdapterCallback adapterCallback;

    public FavoriteRecyclerViewAdapter(Context context, List<Word> words, AdapterCallback adapterCallback) {

        try{
            this.adapterCallback = adapterCallback;

        }catch (ClassCastException e){
            throw new ClassCastException();
        }

        this.context = context;
        repository = new VocabularyRepository(context);

        storage = FirebaseStorage.getInstance();
        this.words = words;
        this.filterList = words;

        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        if(!sp.contains("favoriteCountProfile")){

            sp.edit().putInt("favoriteCountProfile",0).apply();

        }else {

            favoriteCount = sp.getInt("favoriteCountProfile",0);

        }



        addFav();
    }






    private void addFav(){

        for(int i = 0; i < words.size(); i++){

            isFav.add(true);



        }



    }


    @Override
    public @NotNull WordViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_favorite_one_language,parent,false);
        return new WordViewHolder(view);
    }




    public void onBindViewHolder(@NotNull WordViewHolder holder, int position) {


        Word word = words.get(position);
        holder.favorite.setIconResource(R.drawable.ic_favorite_icon_active);


        holder.translationView.setText(word.getTranslation());
        holder.wordView.setText(word.getPronun());
        holder.grammarView.setText(word.getGrammar());
        holder.exampleView1.setText(word.getExample1());
        holder.exampleView2.setText(word.getExample2());
        holder.exampleView3.setText(word.getExample3());


    }



    public void onDestroy(){

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


    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView wordView;
        final TextView translationView;
        final TextView grammarView;
        final TextView exampleView1;
        final TextView exampleView2;
        final TextView exampleView3;
        final FancyButton favorite;
        final FancyButton speaker;
        final CardView cardView;
        final ProgressBar progressBar;





        public WordViewHolder(View itemView) {
            super(itemView);
            wordView = itemView.findViewById(R.id.favorite_card_word);
            translationView = itemView.findViewById(R.id.favorite_card_translation);
            grammarView = itemView.findViewById(R.id.favorite_card_grammar);
            progressBar = itemView.findViewById(R.id.spin_kit);
            Sprite doubleBounce = new Wave();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.INVISIBLE);

            exampleView1 = itemView.findViewById(R.id.favorite_card_example1);
            exampleView2 = itemView.findViewById(R.id.favorite_card_example2);
            exampleView3 = itemView.findViewById(R.id.favorite_card_example3);
            speaker =  itemView.findViewById(R.id.favorite_speaker);
            cardView = itemView.findViewById(R.id.recycler_view_card);
            cardView.setPreventCornerOverlap(false);

            sp.getBoolean("pronunState",true);
            speaker.setDisableBackgroundColor(Color.parseColor("#cccccc"));
            speaker.setDisableBorderColor(Color.parseColor("#ffffff"));


            favorite =  itemView.findViewById(R.id.favorite_favorite);

            favorite.setOnClickListener(this);
            speaker.setOnClickListener(this);



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
                   // Toast.makeText(context,audioPath,Toast.LENGTH_LONG).show();
                    try{

                        mp.setDataSource(audioPath);
                        mp.prepare();
                        mp.start();
                        speaker.setEnabled(false);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                speaker.setEnabled(true);
                               // Toast.makeText(context,"play finished", Toast.LENGTH_LONG).show();
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }



                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                   // Toast.makeText(context,"Completed",Toast.LENGTH_LONG).show();
                    speaker.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });

        }
        @Override
        public void onClick(View view) {

            Word word = words.get(getBindingAdapterPosition());

            if( view == speaker){


                String wordName = words.get(getBindingAdapterPosition()).getWord().toLowerCase();



                if (ConnectivityHelper.isConnectedToNetwork(context)) {
                    //Show the connected screen
                    downloadAudio(wordName);
                    //Toast.makeText(context,"Connectedssss",Toast.LENGTH_LONG).show();

                } else {

                    //Show disconnected screen
                    try {
                        adapterCallback.onMethodCallback(wordName);
                    } catch (ClassCastException e) {
                        // do something
                        Log.e("AdapterCallback", Objects.requireNonNull(e.getMessage()));
                    }

                    Toast.makeText(context,"No internet",Toast.LENGTH_LONG).show();



                }




            }

            if(view.getId() == R.id.favorite_favorite){


                if(word.isFavorite.equalsIgnoreCase("True")){


                    if(favoriteCount > 0){

                        favoriteCount--;
                        sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();

                    }

                    if(word.vocabularyType.equalsIgnoreCase("IELTS")){

                        repository.updateIELTSFavoriteState(words.get(getBindingAdapterPosition()).position+"","False");

                    }

                    if(word.vocabularyType.equalsIgnoreCase("TOEFL")){
                        repository.updateTOEFLFavoriteState(words.get(getBindingAdapterPosition()).position+"","False");


                    }

                    if(word.vocabularyType.equalsIgnoreCase("SAT")){

                        repository.updateSATFavoriteState(words.get(getBindingAdapterPosition()).position+"","False");



                    }

                    if(word.vocabularyType.equalsIgnoreCase("GRE")){
                        repository.updateGREFavoriteState(words.get(getBindingAdapterPosition()).position+"","False");

                    }





                }else {
                    favoriteCount++;
                    sp.edit().putInt("favoriteCountProfile",favoriteCount).apply();


                    isFav.set(getBindingAdapterPosition(),true);
                    favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    favorite.setTag(null);

                    if(words.get(getBindingAdapterPosition()).level.equalsIgnoreCase("IELTS")){

                        repository.updateIELTSFavoriteState(words.get(getBindingAdapterPosition()).position+"","True");

                    }

                    if(words.get(getBindingAdapterPosition()).level.equalsIgnoreCase("TOEFL")){
                        repository.updateTOEFLFavoriteState(words.get(getBindingAdapterPosition()).position+"","True");

                    }

                    if(words.get(getBindingAdapterPosition()).level.equalsIgnoreCase("SAT")){
                        repository.updateSATFavoriteState(words.get(getBindingAdapterPosition()).position+"","True");



                    }
                    if(words.get(getBindingAdapterPosition()).level.equalsIgnoreCase("GRE")){

                        repository.updateGREFavoriteState(words.get(getBindingAdapterPosition()).position+"","True");


                    }

                }


                words.remove(getBindingAdapterPosition());
                isFav.remove(getBindingAdapterPosition());
                notifyItemRemoved(getBindingAdapterPosition());}
        }



    }


    public  interface AdapterCallback{

        void onMethodCallback(String word);
    }




}
