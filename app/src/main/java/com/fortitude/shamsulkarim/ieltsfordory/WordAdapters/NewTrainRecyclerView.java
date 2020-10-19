package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.Images;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.Word;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import de.cketti.mailto.EmailIntentBuilder;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by karim on 7/1/17.
 */

public class NewTrainRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String[] examples  = new String[3];
    private final Word word;
    private Bitmap bitmap;
    private final Context ctx;
    private final String[] imageQualityArray = {"High","Medium","Low"};
    private final String[] imageFolderName = {"High","Medium","Low"};
    private final static int DEFINATION_VIEW = 0;
    private final static int EXAMPLE_VIEW = 1;
    private final static int SERVER_IMAGE_VIEW = 2;
    private final static int AD_LAYOUT = 3;
    private final int languageId;
    private final SharedPreferences sp;
    private final boolean connected;
    private final IELTSWordDatabase ieltsWordDatabase;
    private final TOEFLWordDatabase toeflWordDatabasee;
    private final SATWordDatabase satWordDatabase;
    private final GREWordDatabase greWordDatabase;
    private TrainAdapterCallback trainAdapterCallback;
    private final StorageReference storageRef;
    private final int cb;
    private final Context context;

    public NewTrainRecyclerView(Context context, Word word, TrainAdapterCallback trainAdapterCallback){

        try{
            this.trainAdapterCallback = trainAdapterCallback;
        }catch (ClassCastException e){
            Log.e("TrainAdapterCallback", e.getMessage());
        }

        Images images = new Images();
        this.context = context;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://fir-userauthentication-f751c.appspot.com");





        this.word = word;
        this.ctx = context;
        sp = ctx.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        cb = sp.getInt("cb",0);
        languageId = sp.getInt("language",0);
        String level = sp.getString("level", "NOTHING");
        examples[0] = word.getExample1();
        examples[1] = word.getExample2();
        examples[2] = word.getExample3()+"\n"+word.getExample3SL();

        ieltsWordDatabase = new IELTSWordDatabase(context);
        toeflWordDatabasee = new TOEFLWordDatabase(context);
        satWordDatabase = new SATWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);
        connected = isOnline();
        //Toast.makeText(ctx,"ONLINE: "+connected,Toast.LENGTH_LONG).show();

    }
    private boolean isOnline() {

        return ConnectivityHelper.isConnectedToNetwork(ctx);

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        ieltsWordDatabase.close();
        toeflWordDatabasee.close();
        satWordDatabase.close();
        greWordDatabase.close();
        Toast.makeText(context,"On Detached Recyclerview",Toast.LENGTH_SHORT).show();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view;
        if(viewType == DEFINATION_VIEW){
            if(languageId >0 ){

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_defination_layout,parent,false);

            }
            else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_english_only,parent,false);
            }
            DefinationAdapter viewHolder = new DefinationAdapter(view);

            return viewHolder;
        }

        if( viewType == AD_LAYOUT){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout,parent,false);
            BannerAdLayout bannerAdLayout = new BannerAdLayout(view);
            return bannerAdLayout;

        }


        if(viewType == SERVER_IMAGE_VIEW){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_from_server,parent,false);
            ImageViewHolder imageViewHolder = new ImageViewHolder(view);
            return imageViewHolder;


        }

        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_examples,parent,false);
            ExampleAdapter exampleAdapter = new ExampleAdapter(view);
            return exampleAdapter;
        }



    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType){


            case DEFINATION_VIEW:

                SpannableStringBuilder spanEx1 = new SpannableStringBuilder(word.getExample1()+"\n"+word.getExample1SL());

                // Span to set text color to some RGB value
                final ForegroundColorSpan fcs = new ForegroundColorSpan(ctx.getResources().getColor(R.color.primary_text_color));
                final ForegroundColorSpan lowColor = new ForegroundColorSpan(ctx.getResources().getColor(R.color.third_text_color));
                spanEx1.setSpan(fcs,0,word.getExample1().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                SpannableStringBuilder spanEx2 = new SpannableStringBuilder(word.getExample2()+"\n"+word.getExample2SL());
                spanEx2.setSpan(fcs,0,word.getExample2().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                SpannableStringBuilder spanDef = new SpannableStringBuilder(word.getTranslation()+"\n"+word.getTranslationSL());
                spanDef.setSpan(lowColor,word.getTranslation().length(),1+word.getTranslation().length()+word.getTranslationSL().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                SpannableStringBuilder spanWord = new SpannableStringBuilder(word.getWord()+"\n"+word.getWordSL());
                spanWord.setSpan(lowColor,word.getWord().length(),1+word.getWordSL().length()+word.getWord().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanWord.setSpan(new RelativeSizeSpan(0.8f), word.getWord().length(),1+word.getWordSL().length()+word.getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                DefinationAdapter definationAdapter = (DefinationAdapter) holder;

                if(word.getIsFavorite().equalsIgnoreCase("true")){

                    definationAdapter.favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                }

                if(!sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){




                    definationAdapter.translation.setText(word.getTranslation());
                    definationAdapter.pronunciation.setText(word.getPronun());
                    definationAdapter.example1.setText(word.getExample1());
                    definationAdapter.example2.setText(word.getExample2());
//                    definationAdapter.example3.setText(word.getExample3());
                    definationAdapter.grammar.setText(word.getGrammar());
//                    definationAdapter.spanish.setText(word.getExtra());
                }else {
                    definationAdapter.example1.setText(spanEx1);
                    definationAdapter.example2.setText(spanEx2);
                  //  definationAdapter.example3.setText(word.getExample3());
                    definationAdapter.pronunciation.setText(word.getPronun());
                    definationAdapter.grammar.setText(word.getGrammar());
                    definationAdapter.translation.setText(spanDef);
                }


                break;


            case SERVER_IMAGE_VIEW:

                int imageQualityNum = sp.getInt("imageQuality",1);

                String imageQualityString = imageFolderName[imageQualityNum];
                imageQualityString = imageQualityString.toLowerCase();

                final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;


                if(connected){
//                    DownloadImage downloadImage = new DownloadImage(imageViewHolder.image,imageViewHolder.imageDownloadProgress,"aquatic");
//                    downloadImage.execute("aquatic");
                    String wordName;

                    if(word.vocabularyType.equalsIgnoreCase("TOEFL")){
                         wordName = word.getWord();
                    }else {
                         wordName = word.getWord().toLowerCase();
                    }

                    imageViewHolder.imageState.setImageResource(R.drawable.waiting_for_image);
                    imageViewHolder.imageStateText.setText(R.string.waitingForImage);
                    imageViewHolder.imageText.setText(" ");

                    final ForegroundColorSpan fcss = new ForegroundColorSpan(ctx.getResources().getColor(R.color.primary_text_color));
                    final SpannableStringBuilder spanEx3 = new SpannableStringBuilder(word.getExample3()+"\n"+word.getExample3SL());
                    spanEx3.setSpan(fcss,0,word.getExample3().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    storageRef.child(imageQualityString+"/"+wordName+".png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {


                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Use the bytes to display the image
                            //String path= Environment.getExternalStorageDirectory()+"/"+editTextName.getText().toString();
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageViewHolder.image.setImageBitmap(bitmap);

                            imageViewHolder.imageState.setImageResource(0);
                            if(sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){
                                imageViewHolder.imageText.setText(spanEx3);
                            }else {
                                imageViewHolder.imageText.setText(word.getExample3());
                            }

                            imageViewHolder.imageStateText.setText(" ");



//                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(),
//                        imageView.getHeight(), false));
                        }});


                }else {
                    imageViewHolder.imageText.setText(" ");
                    imageViewHolder.imageState.setImageResource(R.drawable.no_internet);
                    imageViewHolder.imageStateText.setText(R.string.noInternet);
                }



                break;

            case EXAMPLE_VIEW:
                ExampleAdapter exampleAdapter = (ExampleAdapter)holder;

                if(sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){
                    exampleAdapter.example.setText(examples[position-1]);
                }else {
                    exampleAdapter.example.setText(word.getExample3());
                }

        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {

        if( position == 0){

            return DEFINATION_VIEW;
        }else if( position == 1) {

            return SERVER_IMAGE_VIEW;
        }
        else {

            return AD_LAYOUT;
        }
    }



    public class DefinationAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        public String audioPath= null;
        public File localFile = null;
        ProgressBar progressBar;
        TextView translation, spanish,languageName, grammar, pronunciation, example1,example2;
        FancyButton speak, favorite;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        String wordName = word.getWord().toLowerCase();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://fir-userauthentication-f751c.appspot.com/audio/"+wordName+".mp3");
        Boolean isVoicePronunciation;

        //Typeface comfortaRegular;


        public DefinationAdapter(View itemView) {
            super(itemView);
            //comfortaRegular = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Comfortaa-Bold.ttf");

            translation = itemView.findViewById(R.id.translation_train);
            spanish = itemView.findViewById(R.id.spanish_translation_train);
            languageName = itemView.findViewById(R.id.spanish);
            grammar = itemView.findViewById(R.id.grammar_train);
            pronunciation = itemView.findViewById(R.id.pronunciation_train);
            speak = itemView.findViewById(R.id.train_speaker_icon);
            example1 = itemView.findViewById(R.id.example1);
            example2 = itemView.findViewById(R.id.example2);
            progressBar = itemView.findViewById(R.id.spin_kit);
            Sprite doubleBounce = new Wave();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.INVISIBLE);
            favorite = itemView.findViewById(R.id.train_favorite_icon);
            isVoicePronunciation = sp.getBoolean("pronunState",true);
            favorite.setOnClickListener(this);
            speak.setOnClickListener(this);
            speak.setDisableBackgroundColor(ctx.getResources().getColor(R.color.primary_background_color));
            speak.setDisableBorderColor(ctx.getResources().getColor(R.color.primary_background_color));




            if(languageId >0){
               // spanish.setTypeface(comfortaRegular);
            }

           // translation.setTypeface(comfortaRegular);

            if(languageId == 1){
                languageName.setText("Spanish");

            }
            if(languageId == 2){

                languageName.setText("Hindi");
            }
            if(languageId == 3){

                languageName.setText("Bangla");
            }
            if( ConnectivityHelper.isConnectedToNetwork(itemView.getContext()) && isVoicePronunciation){
                speak.setEnabled(false);
                downloadAudio();
            }else {
                speak.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(ctx,"Internet required for real human pronunciation.",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onClick(View v) {

            if(v == speak){

                MediaPlayer mp = new MediaPlayer();
                //Toast.makeText(ctx,audioPath,Toast.LENGTH_LONG).show();


                if (ConnectivityHelper.isConnectedToNetwork(ctx) && isVoicePronunciation) {
                    //Show the connected screen
                    try{

                        if(audioPath != null){
                            mp.setDataSource(audioPath);
                            mp.prepare();
                            mp.start();
                        }else {
                            Log.i("Audio","Audio Path: "+audioPath);
                        }


                        speak.setEnabled(false);

                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                speak.setEnabled(true);
                               // Toast.makeText(ctx,"play finished", Toast.LENGTH_LONG).show();
                                Log.i("Audio","Audio Path: "+audioPath);
                            }
                        });
                    }catch (NullPointerException | IOException e){
                        e.printStackTrace();
                    }

                } else {
                    //Show disconnected screen
                    //Toast.makeText(ctx,"Not connected",Toast.LENGTH_LONG).show();
//                    tts.setLanguage(Locale.US);
//                    tts.speak(wordName, TextToSpeech.QUEUE_ADD, null);
                    try{
                        trainAdapterCallback.onMethodCallback(wordName);
                    }catch (ClassCastException e){
                        Log.e("TrainAdapterCallback", e.getMessage());
                    }
                }

            }

            if( v == favorite){

                if(word.vocabularyType.equalsIgnoreCase("IELTS")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        ieltsWordDatabase.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                        word.setIsFavorite("true");

                    }else {

                        ieltsWordDatabase.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.ic_favorite_icon);
                        word.setIsFavorite("false");
                    }
                }



                if(word.vocabularyType.equalsIgnoreCase("TOEFL")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        toeflWordDatabasee.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                        word.setIsFavorite("true");
                    }else {

                        toeflWordDatabasee.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.ic_favorite_icon);
                        word.setIsFavorite("false");
                    }
                }

                if(word.vocabularyType.equalsIgnoreCase("SAT")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        satWordDatabase.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                        word.setIsFavorite("true");
                    }else {

                        satWordDatabase.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.ic_favorite_icon);
                        word.setIsFavorite("false");
                    }
                }

                if(word.vocabularyType.equalsIgnoreCase("GRE")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        greWordDatabase.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                        word.setIsFavorite("true");

                    }else {

                        greWordDatabase.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.ic_favorite_icon);
                        word.setIsFavorite("false");
                    }
                }

            }




        }

        public void downloadAudio(){
            progressBar.setVisibility(View.VISIBLE);

            try{
                localFile = File.createTempFile("Audio","mp3");
            }catch (IOException e){
                e.printStackTrace();
            }

            gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(ctx, localFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    audioPath = localFile.getAbsolutePath();



                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    //Toast.makeText(ctx,"Completed",Toast.LENGTH_LONG).show();
                    speak.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }

    }

    public static class ExampleAdapter extends RecyclerView.ViewHolder{

        TextView example;
        //Typeface comfortaRegular;

        public ExampleAdapter(View itemView) {
            super(itemView);

           // comfortaRegular = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Comfortaa-Regular.ttf");
            example = itemView.findViewById(R.id.train_example);
          //  example.setTypeface(comfortaRegular);


        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image, imageState, imageQualitySetting;
        TextView imageText,imageStateText, imageQualitySettingText, reportmistake;


        public ImageViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.server_image);

            imageState = itemView.findViewById(R.id.server_image_state);
            imageText = itemView.findViewById(R.id.image_text);
            imageStateText =  itemView.findViewById(R.id.state_text);
            reportmistake = itemView.findViewById(R.id.train_reportmistake);
            imageQualitySetting = itemView.findViewById(R.id.image_quality);
            imageQualitySettingText = itemView.findViewById(R.id.image_quality_text);
            reportmistake.setOnClickListener(this);
            imageQualitySetting.setOnClickListener(this);


            if(!sp.contains("imageQuality")){
                sp.edit().putInt("imageQuality",1).apply();

            }

            int imageQualityNum = sp.getInt("imageQuality",1);

            imageQualitySettingText.setText(imageQualityArray[imageQualityNum]);


        }


        @Override
        public void onClick(View v) {
            if(v == imageQualitySetting){

                if(!sp.contains("imageQuality")){
                    sp.edit().putInt("imageQuality",1).apply();

                }else {

                    int imageQualityNum = 1+sp.getInt("imageQuality",1);

                    if(imageQualityNum == 3){
                        imageQualityNum = 0;
                        sp.edit().putInt("imageQuality",imageQualityNum).apply();
                    }else {
                        sp.edit().putInt("imageQuality",imageQualityNum).apply();
                    }



                }


                int imageQualityNum = sp.getInt("imageQuality",1);

                imageQualitySettingText.setText(imageQualityArray[imageQualityNum]);


            }

            if (v == reportmistake){

                try{
                    EmailIntentBuilder.from(ctx)
                            .to("fortitudedevs@gmail.com")
                            .subject("Mistake found! APP: VB4"+" FL: "+ BuildConfig.FLAVOR+" VC: "+BuildConfig.VERSION_CODE+" VN: "+BuildConfig.VERSION_NAME)
                            .body("Word: "+word.getWord()+"\nDefinition: "+word.getTranslation()
                                    +"\nExample 1: "+word.example1
                                    +"\nExample 2: "+word.example2
                                    +"\nExample 3: "+word.example3
                                    +"\nVocabulary type: "+word.vocabularyType
                                    +"\nPOS: "+word.grammar
                                    +"\nSyllable: "+word.pronun
                                    +"\nPosition: "+word.position
                                    +"\nPlease describe the mistake here: "
                            )
                            .start();
                }catch (NullPointerException i){

                    Log.i("RepostMistake", Objects.requireNonNull(i.getMessage()));

                }
            }
        }
    }

    private class BannerAdLayout extends  RecyclerView.ViewHolder{

        private AdView mAdView;

        public BannerAdLayout(View itemView) {
            super(itemView);
            Context context = itemView.getContext();

            if( cb != 1){

             //   showNativeAd();
            }

        }
    }

    public interface TrainAdapterCallback{

        void onMethodCallback(String word);

    }
}
