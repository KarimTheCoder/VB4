package com.fortitude.shamsulkarim.ieltsfordory;
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
    private final SATWordDatabase satWordDatabase;
    private final IELTSWordDatabase ieltsWordDatabase;
    private final TOEFLWordDatabase toeflWordDatabase;
    private final GREWordDatabase greWordDatabase;
    private final SharedPreferences sp;
    private final int languageId;
    private int favoriteCount;
    private final String[] languageName = {"","spanish","hindi","bengali"};
    private final FirebaseStorage storage;
    private final AdapterCallback adapterCallback;

    public FavoriteRecyclerViewAdapter(Context context, List<Word> words, AdapterCallback adapterCallback) {

        try{
            this.adapterCallback = adapterCallback;

        }catch (ClassCastException e){
            throw new ClassCastException();
        }

        this.context = context;
        storage = FirebaseStorage.getInstance();
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
    public @NotNull WordViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        if(languageId >= 1){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_favorite_two_language,parent,false);

        }else {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_favorite_one_language,parent,false);
        }


        return new WordViewHolder(view);
    }




    public void onBindViewHolder(@NotNull WordViewHolder holder, int position) {
        Word word = words.get(position);
        if(isFav.get(position)){


            holder.favorite.setIconResource(R.drawable.ic_favorite_icon_active);
        }else {
         holder.favorite.setIconResource(R.drawable.ic_favorite_icon);


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



    public void onDestroy(){

        ieltsWordDatabase.close();
        toeflWordDatabase.close();
        satWordDatabase.close();
        greWordDatabase.close();
        Toast.makeText(context,"Favorite OnDestory",Toast.LENGTH_SHORT).show();
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

        TextView wordView,translationView, grammarView, exampleView1,exampleView2,exampleView3,secondLanguageName,secondTranslation;
        FancyButton favorite, speaker;
        CardView cardView;
        ProgressBar progressBar;





        public WordViewHolder(View itemView) {
            super(itemView);

            //englishLanguage = (TextView)itemView.findViewById(R.id.favorite_second_language);
            secondLanguageName = (TextView)itemView.findViewById(R.id.favorite_second_language2);
            secondTranslation = (TextView)itemView.findViewById(R.id.favorite_second_translation);
            wordView = (TextView)itemView.findViewById(R.id.favorite_card_word);
            translationView = (TextView)itemView.findViewById(R.id.favorite_card_translation);
            grammarView = (TextView)itemView.findViewById(R.id.favorite_card_grammar);
            progressBar = itemView.findViewById(R.id.spin_kit);
            Sprite doubleBounce = new Wave();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.INVISIBLE);

            exampleView1 = (TextView)itemView.findViewById(R.id.favorite_card_example1);
            exampleView2 = (TextView)itemView.findViewById(R.id.favorite_card_example2);
            exampleView3 = (TextView)itemView.findViewById(R.id.favorite_card_example3);
            speaker = (FancyButton) itemView.findViewById(R.id.favorite_speaker);
            cardView = (CardView)itemView.findViewById(R.id.recycler_view_card);
            cardView.setPreventCornerOverlap(false);

            sp.getBoolean("pronunState",true);
            speaker.setDisableBackgroundColor(Color.parseColor("#cccccc"));
            speaker.setDisableBorderColor(Color.parseColor("#ffffff"));


            //speaker.setEnabled(false);

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

            Word word = words.get(getAdapterPosition());

            if( view == speaker){


                String wordName = words.get(getAdapterPosition()).getWord().toLowerCase();



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

                    favorite.setIconResource(R.drawable.ic_favorite_icon);
                    favorite.setTag(R.drawable.ic_favorite_icon);
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
                    favorite.setIconResource(R.drawable.ic_favorite_icon_active);
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



    }


    public  interface AdapterCallback{

        void onMethodCallback(String word);
    }




}
