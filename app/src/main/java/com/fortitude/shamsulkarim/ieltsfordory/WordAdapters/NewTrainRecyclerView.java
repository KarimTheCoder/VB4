package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortitude.shamsulkarim.ieltsfordory.Images;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.Word;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Locale;

import de.cketti.mailto.EmailIntentBuilder;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by karim on 7/1/17.
 */

public class NewTrainRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TextToSpeech.OnInitListener{

    private String[] examples  = new String[3];
    private Word word;
    Bitmap bitmap;
    private Context ctx;
    private String[] imageQualityArray = {"High","Medium","Low"};
    private String[] imageFolderName = {"High","Medium","Low"};
    private final static int DEFINATION_VIEW = 0;
    private final static int EXAMPLE_VIEW = 1;
    private final static int SERVER_IMAGE_VIEW = 2;
    private final static int AD_LAYOUT = 3;
    private static int pos;
    private int languageId;
    private SharedPreferences sp;
    private boolean connected;
    private TextToSpeech tts;
    private String level;
    private Images images;
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabasee;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;


    private FirebaseStorage storage;
    private StorageReference storageRef;
    private int cb;

    public NewTrainRecyclerView(Context context, Word word){
        images = new Images();
        tts = new TextToSpeech(context, this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://fir-userauthentication-f751c.appspot.com");





        this.word = word;
        this.ctx = context;
        sp = ctx.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        cb = sp.getInt("cb",0);
        languageId = sp.getInt("language",0);
        level = sp.getString("level","NOTHING");
        examples[0] = word.getExample1();
        examples[1] = word.getExample2();
        examples[2] = word.getExample3();

        ieltsWordDatabase = new IELTSWordDatabase(context);
        toeflWordDatabasee = new TOEFLWordDatabase(context);
        satWordDatabase = new SATWordDatabase(context);
        greWordDatabase = new GREWordDatabase(context);
        connected = isOnline();

    }
    protected boolean isOnline() {

        try{

            ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }

        }catch (NullPointerException e){

            Toast.makeText(ctx,"Network error "+e.toString(),Toast.LENGTH_SHORT).show();
        }

        return false;

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
        pos = position;

        switch (viewType){


            case DEFINATION_VIEW:

                DefinationAdapter definationAdapter = (DefinationAdapter) holder;

                if(word.getIsFavorite().equalsIgnoreCase("true")){

                    definationAdapter.favorite.setIconResource(R.drawable.favorite_icon_active);
                }

                if(languageId >0){




                    definationAdapter.translation.setText(word.getTranslation());
                    definationAdapter.pronunciation.setText(word.getPronun());
                    definationAdapter.example1.setText(word.getExample1());
                    definationAdapter.example2.setText(word.getExample2());
                   // definationAdapter.example3.setText(word.getExample3());
                    definationAdapter.grammar.setText(word.getGrammar());
                    definationAdapter.spanish.setText(word.getExtra());
                }else {
                    definationAdapter.example1.setText(word.getExample1());
                    definationAdapter.example2.setText(word.getExample2());
                   // definationAdapter.example3.setText(word.getExample3());
                    definationAdapter.pronunciation.setText(word.getPronun());
                    definationAdapter.grammar.setText(word.getGrammar());
                    definationAdapter.translation.setText(word.getTranslation());
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

                    storageRef.child(imageQualityString+"/"+wordName+".png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {


                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Use the bytes to display the image
                            //String path= Environment.getExternalStorageDirectory()+"/"+editTextName.getText().toString();
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageViewHolder.image.setImageBitmap(bitmap);

                            imageViewHolder.imageState.setImageResource(0);
                            imageViewHolder.imageText.setText(examples[2]);
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

                exampleAdapter.example.setText(examples[position-1]);
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

    @Override
    public void onInit(int status) {

    }

    public class DefinationAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView translation, spanish,languageName, grammar, pronunciation, example1,example2, example3;
        FancyButton speak, favorite;

        //Typeface comfortaRegular;


        public DefinationAdapter(View itemView) {
            super(itemView);
            //comfortaRegular = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Comfortaa-Bold.ttf");

            translation = (TextView)itemView.findViewById(R.id.translation_train);
            spanish = (TextView)itemView.findViewById(R.id.spanish_translation_train);
            languageName = (TextView)itemView.findViewById(R.id.spanish);
            grammar = (TextView)itemView.findViewById(R.id.grammar_train);
            pronunciation = (TextView)itemView.findViewById(R.id.pronunciation_train);
            speak = (FancyButton)itemView.findViewById(R.id.train_speaker_icon);
            example1 = (TextView)itemView.findViewById(R.id.example1);
            example2 = (TextView)itemView.findViewById(R.id.example2);
 //           example3 = itemView.findViewById(R.id.example3);
            favorite = itemView.findViewById(R.id.train_favorite_icon);

            favorite.setOnClickListener(this);
            speak.setOnClickListener(this);


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

        }

        @Override
        public void onClick(View v) {

            if(v == speak){

                tts.setLanguage(Locale.US);
                tts.speak(word.getWord(), TextToSpeech.QUEUE_ADD, null);
            }

            if( v == favorite){

                if(word.vocabularyType.equalsIgnoreCase("IELTS")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        ieltsWordDatabase.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        word.setIsFavorite("true");

                    }else {

                        ieltsWordDatabase.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.favorite_icon);
                        word.setIsFavorite("false");
                    }
                }



                if(word.vocabularyType.equalsIgnoreCase("TOEFL")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        toeflWordDatabasee.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        word.setIsFavorite("true");
                    }else {

                        toeflWordDatabasee.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.favorite_icon);
                        word.setIsFavorite("false");
                    }
                }

                if(word.vocabularyType.equalsIgnoreCase("SAT")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        satWordDatabase.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        word.setIsFavorite("true");
                    }else {

                        satWordDatabase.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.favorite_icon);
                        word.setIsFavorite("false");
                    }
                }

                if(word.vocabularyType.equalsIgnoreCase("GRE")){


                    if(word.getIsFavorite().equalsIgnoreCase("false")){



                        greWordDatabase.updateFav(word.position+"","true");
                        favorite.setIconResource(R.drawable.favorite_icon_active);
                        word.setIsFavorite("true");

                    }else {

                        greWordDatabase.updateFav(word.position+"","false");
                        favorite.setIconResource(R.drawable.favorite_icon);
                        word.setIsFavorite("false");
                    }
                }

            }




        }
    }

    public class ExampleAdapter extends RecyclerView.ViewHolder{

        TextView example;
        //Typeface comfortaRegular;

        public ExampleAdapter(View itemView) {
            super(itemView);

           // comfortaRegular = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Comfortaa-Regular.ttf");
            example = (TextView)itemView.findViewById(R.id.train_example);
          //  example.setTypeface(comfortaRegular);


        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image, imageState, imageQualitySetting;
        TextView imageText,imageStateText, imageQualitySettingText, reportmistake;


        public ImageViewHolder(View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.server_image);

            imageState = (ImageView)itemView.findViewById(R.id.server_image_state);
            imageText = (TextView)itemView.findViewById(R.id.image_text);
            imageStateText = (TextView) itemView.findViewById(R.id.state_text);
            reportmistake = itemView.findViewById(R.id.train_reportmistake);
            imageQualitySetting = (ImageView)itemView.findViewById(R.id.image_quality);
            imageQualitySettingText = (TextView)itemView.findViewById(R.id.image_quality_text);
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
                            .subject("Fortitude Vocabulary Builder")
                            .body("Word: "+word.getWord()+"\nDefinition: "+word.getTranslation()
                                    +"\nExample 1: "+word.example1
                                    +"\nExample 2: "+word.example2
                                    +"\nExample 3: "+word.example3
                                    +"\nVocabulary type: "+word.vocabularyType
                                    +"\nPosition: "+word.position
                            )
                            .start();
                }catch (NullPointerException i){

                }
            }
        }
    }

    private class BannerAdLayout extends  RecyclerView.ViewHolder{

        private AdView mAdView;
        private Context context;
        private View view;

        public BannerAdLayout(View itemView) {
            super(itemView);
            context = itemView.getContext();
            view = itemView;

            if( cb != 1){

             //   showNativeAd();
            }

        }



    }
}
