package com.fortitude.shamsulkarim.ieltsfordory.ui.train;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseAdvance;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseIntermediate;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
//import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import mehdi.sakout.fancybuttons.FancyButton;

public class NewTrain extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener, NewTrainRecyclerView.TrainAdapterCallback {


    private VocabularyRepository repository;
    private View topBackground,trainCircle1,trainCircle2,trainCircle3,trainCircle4;
    private CardView answerCard1, answerCard2, answerCard3, answerCard4, wordCard;
    private TextView wordView, answerView1, answerView2, answerView3, answerView4;
    private FloatingActionButton fab;
    private TextToSpeech tts;
    private FancyButton speak;
    private int lastMistake = 13;
    private int mistakes;
    private int wordsPerSession;
    private int FIVE_WORD_SIZE;
    private int showCycle;
    private int quizCycle;
    private int languageId;
    private int repeatPerSession;
    private int totalCycle;
    private int progressCount;
    private int cb ;
    private SharedPreferences sp;
    private ArrayList<Word> words, fiveWords,fiveWordsCopy,questionWords;
    private RoundCornerProgressBar progress1;
    private RecyclerView recyclerView;
    private NewTrainRecyclerView adapter;
    private String level;
    private boolean IsWrongAnswer = true;
    private int[] mistakeCollector;
    private boolean  alreadyclicked = true;
    private boolean progress = false;
    private int totalMistakeCount, totalCorrects;
    private boolean soundState = true;
    boolean isWhichvocbularyToText = false;

    public String[] items;
    public boolean[] checkedItems;

    private JustLearnedDatabaseBeginner justLearnedDatabaseBeginner;
    private JustLearnedDatabaseIntermediate justLearnedDatabaseIntermediate;
    private JustLearnedDatabaseAdvance justLearnedDatabaseAdvance;
    private FirebaseStorage storage;
    public File localFile = null;
    public String audioPath= null;
    private ProgressBar progressBar, adLoading;

    // UI





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_new_train);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimary));
        // This code reports to Crashlytics of connection
        // Boolean connected = ConnectivityHelper.isConnectedToNetwork(this);

        repository = new VocabularyRepository(this);

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        level = sp.getString("level","NOTHING");

        languageId = sp.getInt("language",0);
        soundState = sp.getBoolean("soundState",true);
        int noshowads = sp.getInt("noshowads", 0);
        //initializeAds();
        if(!sp.contains("totalWrongCount"+level)){

            sp.edit().putInt("totalWrongCount"+level,0).apply();



        }else {
            totalMistakeCount = sp.getInt("totalWrongCount"+level,0);
            totalCorrects =  sp.getInt("totalCorrects",0);
            cb = sp.getInt("cb",0);


        }




        tts = new TextToSpeech(this, this);


                initialization();
                initializingWords();
                showWords(showCycle);

        mistakeCollector = new int[fiveWords.size()];

        Arrays.fill(mistakeCollector, 0);
        sp.edit().putInt("fiveWordSize",fiveWords.size()).apply();

        noshowads++;

        sp.edit().putInt("noshowads", noshowads).apply();

        updateLearnedDatabase();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }



    }

    public void next(View v){
        speak.setVisibility(View.INVISIBLE);

        Timer T=new Timer();

        if(!progress){

            progress = true;


            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            progressCount++;
                            fab.setProgress(progressCount,true);



                            if( progressCount == 5){
                                progressCount = 0;
                                alreadyclicked = true;
                                progress = false;
                                cancel();
                            }
                        }
                    });
                }
            }, 1000, 1000);


        }


//
//
//        if(!alreadyclicked && !next){
//
//            next = true;
//
//
//            T.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//
//                            nextCount++;
////                            fab.setProgress(nextCount,true);
//
//                            if( nextCount == 5){
//                                nextCount = 0;
//                                alreadyclicked = true;
//                                cancel();
//                            }
//                        }
//                    });
//                }
//            }, 1000, 1000);
//
//
//        }
//
//
//
        if( alreadyclicked){
            toNextWord(v);
            alreadyclicked = false;
        }







    }

    public void toNextWord(View view){
        if (showCycle >= FIVE_WORD_SIZE){

            // hide all the views after the learning stage
            //------------------------------


            if(!isWhichvocbularyToText){
                whichVocabularyToTest(view);
                isWhichvocbularyToText = true;
            }else {


            answerCard1.setVisibility(View.VISIBLE);
            answerCard2.setVisibility(View.VISIBLE);
            answerCard3.setVisibility(View.VISIBLE);
            answerCard4.setVisibility(View.VISIBLE);
            speak.setVisibility(View.INVISIBLE);

                if(fiveWords.size()>0){

                    if(sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){
                        String combineBothLanguage = fiveWords.get(quizCycle).getWord()+"\n"+fiveWords.get(quizCycle).getWordSL();
                        final ForegroundColorSpan lowColor = new ForegroundColorSpan(getColor(R.color.secondary_text_color));
                        SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);
                        spanWord.setSpan(lowColor,fiveWords.get(quizCycle).getWord().length(),1+fiveWords.get(quizCycle).getWordSL().length()+fiveWords.get(quizCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanWord.setSpan(new RelativeSizeSpan(0.4f), fiveWords.get(quizCycle).getWord().length(),1+fiveWords.get(quizCycle).getWordSL().length()+fiveWords.get(quizCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wordView.setText(spanWord);

                    }else {
                        wordView.setText(fiveWords.get(quizCycle).getWord());
                    }




                }


                quizWords(quizCycle, view);
            }


            //------------------------------







        }

        showWords(showCycle);

    }
    @Override
    public void onClick(View v) {

        if( v== speak){

            String word = wordView.getText().toString();
            if(showCycle < FIVE_WORD_SIZE+1){

                word = wordView.getText().toString();

//                tts.setLanguage(Locale.US);
//                tts.speak(word, TextToSpeech.QUEUE_ADD, null);
                downloadAudio(word);

                if(showCycle == FIVE_WORD_SIZE){
                    showCycle++;
                }

            } //                word = wordViewMiddle.getText().toString();


//            tts.setLanguage(Locale.US);
//            tts.speak(word, TextToSpeech.QUEUE_ADD, null);
            downloadAudio(word);

        }


        if( v == answerCard1 || v == answerCard2 || v == answerCard3 ||  v == answerCard4 ){



            if( quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){
                quizWords(quizCycle, v);
            }

        }


    }

    private void showWords(int showCycle){

        Handler handler = new Handler();

            if( showCycle < FIVE_WORD_SIZE){

                try{

                    if(sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){

                        String combineBothLanguage = fiveWords.get(showCycle).getWord()+"\n"+fiveWords.get(showCycle).getWordSL();
                        final ForegroundColorSpan lowColor = new ForegroundColorSpan(getColor(R.color.secondary_text_color));
                        SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);
                        spanWord.setSpan(lowColor,fiveWords.get(showCycle).getWord().length(),1+fiveWords.get(showCycle).getWordSL().length()+fiveWords.get(showCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanWord.setSpan(new RelativeSizeSpan(0.6f), fiveWords.get(showCycle).getWord().length(),1+fiveWords.get(showCycle).getWordSL().length()+fiveWords.get(showCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);




                        wordView.setText(spanWord);
                    }else {

                        wordView.setText(fiveWords.get(showCycle).getWord());
                    }


                }catch (NullPointerException i){
                    Log.i("Error","Quiz Cycle: "+quizCycle+" ShowCycle: "+showCycle);
 
                }

//                wordViewMiddle.setText(fiveWords.get(showCycle).getWord());


                if(showCycle == 0){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DefExamAnimation();
                        }
                    }, 200L);
                }else {


                    DefExamAnimation();
                }


                adapter = new NewTrainRecyclerView(this,fiveWords.get(showCycle), this);
                recyclerView.setAdapter(adapter);
                this.showCycle++;
                progress1.setProgress(quizCycle+showCycle);
            }



    }


    private void quizWords(int quizCycle, View v){



        if(quizCycle == 0){

            answerCardAnimation();
        }
        if(showCycle >= FIVE_WORD_SIZE|| quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){



            //hideViews();
            answerCard1.setVisibility(View.VISIBLE);
            answerCard2.setVisibility(View.VISIBLE);
            answerCard3.setVisibility(View.VISIBLE);
            answerCard4.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            fab.animate().scaleX(0f).scaleY(0f).setDuration(350L).setInterpolator(new AnticipateOvershootInterpolator());


            checkingAnswer(v);
            cycleQuiz();
        }


    }

    private void initialization(){
        //Typeface comfortaRegular = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa-Regular.ttf");



        topBackground = findViewById(R.id.top_background);
        trainCircle1 = findViewById(R.id.train_circle1);
        trainCircle2 = findViewById(R.id.train_circle2);
        trainCircle3 = findViewById(R.id.train_circle3);
        trainCircle4 = findViewById(R.id.train_circle4);
        wordCard = findViewById(R.id.wordCard);
        recyclerView = findViewById(R.id.train_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        wordsPerSession = sp.getInt("wordsPerSession",5);
        repeatPerSession = sp.getInt("repeatationPerSession",5);
        wordView = findViewById(R.id.train_word);
        answerCard1 = findViewById(R.id.answer_card1);
        answerCard2 = findViewById(R.id.answer_card2);
        answerCard3 = findViewById(R.id.answer_card3);
        answerCard4 = findViewById(R.id.answer_card4);
        answerView1 = findViewById(R.id.train_answer_text1);
        answerView2 = findViewById(R.id.train_answer_text2);
        answerView3 = findViewById(R.id.train_answer_text3);
        answerView4 = findViewById(R.id.train_answer_text4);
        speak = findViewById(R.id.train_speaker_icon);
        speak.setVisibility(View.INVISIBLE);
        storage = FirebaseStorage.getInstance();


        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);


        adLoading = findViewById(R.id.spin_ad_loading);
        Sprite threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);
        adLoading.setVisibility(View.GONE);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();

        //theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int colorPrimaryDark = typedValue.data;
       // theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int colorPrimary= typedValue.data;
        //theme.resolveAttribute(R.attr.colorPrimarySurface, typedValue, true);
        @ColorInt int colorPrimarySurface= typedValue.data;



        fab = findViewById(R.id.train_fab);
        fab.setMax(5);
        progress1 = findViewById(R.id.progress_1);

        progress1.setSecondaryProgressColor(colorPrimaryDark);
        progress1.setProgressColor(colorPrimary);
        progress1.setProgressBackgroundColor(colorPrimarySurface);

        justLearnedDatabaseBeginner = new JustLearnedDatabaseBeginner(this);
        justLearnedDatabaseIntermediate = new JustLearnedDatabaseIntermediate(this);
        justLearnedDatabaseAdvance = new JustLearnedDatabaseAdvance(this);



        fiveWords = new ArrayList<>();
        fiveWordsCopy = new ArrayList<>();
        questionWords = new ArrayList<>();

        answerCard1.setAlpha(0f);
        answerCard2.setAlpha(0f);
        answerCard3.setAlpha(0f);
        answerCard4.setAlpha(0f);

        answerCard1.setY(100f);
        answerCard2.setY(100f);
        answerCard3.setY(100f);
        answerCard4.setY(100f);


        answerCard1.setOnClickListener(this);
        answerCard2.setOnClickListener(this);
        answerCard3.setOnClickListener(this);
        answerCard4.setOnClickListener(this);
        speak.setOnClickListener(this);

        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);

    }

    private  void initializingWords(){

        words = new ArrayList<>();


        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        if(!sp.contains(level)){
            sp.edit().putInt(level,0).apply();
        }


        getWords();


       // wordsPerSession += countWords;

        if( wordsPerSession > words.size()){
            wordsPerSession = words.size();
        }



        if(wordsPerSession <= words.size()){
            for ( int k = 0; k < wordsPerSession;k++) {

                fiveWords.add(words.get(k));


            }
        }


        sp.edit().putInt("fiveWordSize",fiveWords.size()).apply();
        FIVE_WORD_SIZE = fiveWords.size();
        progress1.setMax(FIVE_WORD_SIZE+(FIVE_WORD_SIZE*repeatPerSession));
        progress1.setSecondaryProgress(FIVE_WORD_SIZE);



    }

    private void answerCardAnimation(){


                answerCard1.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
                answerCard2.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
                answerCard3.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
                answerCard4.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());



    }

    private void cycleQuiz(){


        if(this.quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){

            ArrayList<Word> answers = gettingAnswer();
            if(!IsWrongAnswer){
                IsWrongAnswer = true;

            }


            if( languageId == 0){

                answerView1.setText(answers.get(0).getTranslation());
                answerView2.setText(answers.get(1).getTranslation());
                answerView3.setText(answers.get(2).getTranslation());
                answerView4.setText(answers.get(3).getTranslation());

            }else {
                answerView1.setText(answers.get(0).getExtra());
                answerView2.setText(answers.get(1).getExtra());
                answerView3.setText(answers.get(2).getExtra());
                answerView4.setText(answers.get(3).getExtra());

            }




        }

    }

    private ArrayList<Word> gettingAnswer() {
        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }

        ArrayList<Word> answers = new ArrayList<>();


        if( this.quizCycle <=(FIVE_WORD_SIZE*repeatPerSession)-1){


        Word word = fiveWords.get(this.quizCycle);

            Collections.shuffle(questionWords);
        for (int i = 0; i < 4;i++) {

            answers.add(questionWords.get(i));

        }

        if( !answers.contains(word)){

            answers.set(0,word);

        }

        Collections.shuffle(answers);

        }
        return answers;
    }

    private void checkingAnswer(final View v){

        MediaPlayer correctAudio = MediaPlayer.create(this, R.raw.correct);
        MediaPlayer incorrectAudio = MediaPlayer.create(this, R.raw.incorrect);




        String answer;


        if(this.quizCycle < (FIVE_WORD_SIZE*repeatPerSession)){

            if(languageId == 0){
                answer = fiveWords.get(quizCycle).getTranslation();
            }
            else {
                answer = fiveWords.get(quizCycle).getExtra();

            }




        if(v == answerCard1){

            if(answerView1.getText().toString().equalsIgnoreCase(answer)){
                StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();

                applyCorrectColor();
                this.quizCycle++;

                this.totalCycle++;

                if(soundState){

                    correctAudio.start();
                }
                totalCorrects++;

                answerCardAnimation2();

            }else {
                applyWrongColor();

                if(soundState){

                    incorrectAudio.start();
                }

                mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;

                wrongAnswerAnimation();

                mistakes++;
                totalMistakeCount++;

                if( lastMistake == quizCycle){
                    StyleableToast.makeText(this, "wrong answer again",10, R.style.wrong_again).show();


                }else {


                    
                    lastMistake = quizCycle;
                    if(mistakes <= 3){

                        StyleableToast.makeText(this, "Wrong answer", 10, R.style.wrong).show();

                    }
                    if(mistakes >= 4){
                        StyleableToast.makeText(this, "Oh no! wrong answer", 10, R.style.MyToast).show();

                    }
                }








        } }
        if(v == answerCard2) {

             if (answerView2.getText().toString().equalsIgnoreCase(answer)) {
                 applyCorrectColor();
                 StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();
                 this.quizCycle++;

                 this.totalCycle++;
                 if(soundState){

                     correctAudio.start();
                 }
                 totalCorrects++;

                 answerCardAnimation2();

             } else {

                 applyWrongColor();
                 mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;
                 wrongAnswerAnimation();
                 if(soundState){

                     incorrectAudio.start();
                 }


                 mistakes++;
                 totalMistakeCount++;

                 if( lastMistake == quizCycle){
                     StyleableToast.makeText(this, "wrong answer again", 10, R.style.wrong_again).show();


                 }else {

                     lastMistake = quizCycle;
                     if(mistakes <= 3){

                         StyleableToast.makeText(this, "Wrong answer!", 10, R.style.wrong).show();

                     }
                     if(mistakes >= 4){
                         StyleableToast.makeText(this, "Oh no! wrong answer", 10, R.style.MyToast).show();

                     }
                 }


             }
         }
        if(v == answerCard3){

            if(answerView3.getText().toString().equalsIgnoreCase(answer)){
                applyCorrectColor();
                StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();
                this.quizCycle++;

                this.totalCycle++;
                if(soundState){

                    correctAudio.start();
                }
                totalCorrects++;

                answerCardAnimation2();

            }else {
                applyWrongColor();
                if(soundState){

                    incorrectAudio.start();
                }

                mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;

                wrongAnswerAnimation();


                mistakes++;
                totalMistakeCount++;

                if( lastMistake == quizCycle){
                    StyleableToast.makeText(this, "Wrong answer again", 10, R.style.wrong_again).show();


                }else {

                    lastMistake = quizCycle;
                    if(mistakes <= 3){

                        StyleableToast.makeText(this, "Wrong answer!", 10, R.style.wrong).show();

                    }
                    if(mistakes >= 4){
                        StyleableToast.makeText(this, "Oh no! wrong answer", 10, R.style.MyToast).show();

                    }
                }

            }


        }
        if(v == answerCard4){

            if(answerView4.getText().toString().equalsIgnoreCase(answer)){
                applyCorrectColor();
                StyleableToast.makeText(this, "Correct!", 10, R.style.correct).show();
                this.quizCycle++;

                this.totalCycle++;
                answerCardAnimation2();
                if(soundState){

                    correctAudio.start();
                }
                totalCorrects++;

                cycleQuiz();



            }else {
                applyWrongColor();
                if(soundState){

                    incorrectAudio.start();
                }


                mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;

                wrongAnswerAnimation();


                mistakes++;
                totalMistakeCount++;

                if( lastMistake == quizCycle){
                    StyleableToast.makeText(this, "wrong answer again?", 10, R.style.wrong_again).show();


                }else {

                    lastMistake = quizCycle;
                    if(mistakes <= 3){

                        StyleableToast.makeText(this, "Wrong answer", 10, R.style.wrong).show();

                    }
                    if(mistakes >= 4){
                        StyleableToast.makeText(this, "Oh no! Wrong answer", 10, R.style.MyToast).show();

                    }
                }

            }


        }

        }



        progress1.setProgress(totalCycle+showCycle);

        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }
//        String combineBothLanguage = fiveWords.get(quizCycle).getWord()+"\n"+fiveWords.get(quizCycle).getWord();
//        final ForegroundColorSpan lowColor = new ForegroundColorSpan(Color.parseColor("#8c979a"));
//        SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);
//        spanWord.setSpan(lowColor,fiveWords.get(quizCycle).getWord().length(),1+fiveWords.get(quizCycle).getWordSL().length()+fiveWords.get(quizCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spanWord.setSpan(new RelativeSizeSpan(0.8f), fiveWords.get(quizCycle).getWord().length(),1+fiveWords.get(quizCycle).getWordSL().length()+fiveWords.get(quizCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        wordView.setText(fiveWords.get(quizCycle).getWord());

        if (totalCycle == (FIVE_WORD_SIZE*repeatPerSession)){


            int pos = getMostMistakenWord(mistakeCollector);
            fiveWords.clear();
            fiveWords.addAll(fiveWordsCopy);
            updateLearnedDatabase();
            updateJustlearnedDatabase(pos);


            sp.edit().putInt("NTmistakes",mistakes).apply();
            sp.edit().putInt("totalWrongCount"+level,totalMistakeCount).apply();
            sp.edit().putInt("totalCorrects",totalCorrects).apply();
            if(pos != -1){

                String word = fiveWords.get(pos).getPronun();
                String def = fiveWords.get(pos).getTranslation();
                String spanish = fiveWords.get(pos).getExtra();
                String example = fiveWords.get(pos).getExample2();

                sp.edit().putString("MostMistakenWord", "shit" + "+" + word + "+" + def + "+" + spanish + "+" + example).apply();



            }else {
                sp.edit().putString("MostMistakenWord","no").apply();

            }

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    hideViews();
                    adLoading.setVisibility(View.VISIBLE);
                    showInterstitialAd();



                    //NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
                    //NewTrain.this.finish();
                }
            }, 200L);



        }


    }

    private void DefExamAnimation(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height,0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float value = (float)valueAnimator.getAnimatedValue();
                recyclerView.setTranslationY(value/10);
            }
        });


        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();




    }

    private void answerCardAnimation2(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float height = dm.heightPixels;

        final ValueAnimator va = ValueAnimator.ofFloat(height,0);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float value = (float)valueAnimator.getAnimatedValue();
                answerCard1.setTranslationY(value/10);
                answerCard2.setTranslationY(value/10);
                answerCard3.setTranslationY(value/10);
                answerCard4.setTranslationY(value/10);
            }
        });


        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setInterpolator(new DecelerateInterpolator());
        va.setDuration(400L);
        va.start();




    }


    private void wrongAnswerAnimation(){
        IsWrongAnswer = false;

       fab.animate().scaleX(1f).scaleY(1f).setDuration(350L).setInterpolator(new AnticipateOvershootInterpolator());

        recyclerView.setVisibility(View.VISIBLE);
//
//        wordViewMiddle.setText("");
//        wordViewMiddle.setVisibility(View.INVISIBLE);
        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);
        wordView.setVisibility(View.VISIBLE);
        speak.setVisibility(View.INVISIBLE);



        wordView.setText(fiveWords.get(quizCycle).getWord());
//        wordViewMiddle.setText(fiveWords.get(quizCycle).getWord());

        DefExamAnimation();
        adapter = new NewTrainRecyclerView(this,fiveWords.get(quizCycle), this);
        recyclerView.setAdapter(adapter);




    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int ttsLang = tts.setLanguage(Locale.US);

            if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!");
                Toast.makeText(this, "Please install English Language on your Text-to-Speech engine.\nSend us an email if you need help", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("TTS", "Language Supported.");
            }
            Log.i("TTS", "Initialization success.");
        } else {
            Log.e("TTS", "TTS not initialized");
            Toast.makeText(this, "Please install Google Text-to-Speech on your phone. \nSend us an email if you need help", Toast.LENGTH_SHORT).show();
        }

    }


    private int getMostMistakenWord(int[] list){

        int wordIndex = 0;
        int pos = -1;


        for(int i = 0; i < list.length; i++){

            int current = list[i];

            if( current> wordIndex){
                pos = i;

                wordIndex = current;

            }

        }

        return pos;




    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_leave)
                .setTitle("Do you want to leave this session, " + sp.getString("userName", "Boo") + "?")
                .setMessage("Leaving this session will make you lose your progress")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewTrain.this.startActivity(new Intent(NewTrain.this, MainActivity.class));
                        NewTrain.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void getWords(){

        if( level.equalsIgnoreCase("beginner")){
           words.addAll(repository.getBeginnerUnlearnedWords());
        }

        if( level.equalsIgnoreCase("intermediate")){
            words.addAll(repository.getIntermediateUnlearnedWords());
        }

        if( level.equalsIgnoreCase("advance")){
            words.addAll(repository.getAdvanceUnlearnedWords());
        }
    }




    private void updateLearnedDatabase(){

        for(int i = 0; i < fiveWords.size(); i++){

            Word word = fiveWords.get(i);

            if(word.vocabularyType.equalsIgnoreCase("IELTS")){
                repository.updateIELTSLearnState(word.position+"","true");
            }

            if( word.vocabularyType.equalsIgnoreCase("TOEFL")){
                repository.updateTOEFLLearnState(word.position+"", "true");
            }

            if(word.vocabularyType.equalsIgnoreCase("SAT")){
                repository.updateSATLearnState(word.position+"", "true");
            }

            if(word.vocabularyType.equalsIgnoreCase("GRE")){
                repository.updateGRELearnState(word.position+"", "true");
            }
        }
    }

    private void updateJustlearnedDatabase(int pos){

        justLearnedDatabaseBeginner.removeAll();
        justLearnedDatabaseIntermediate.removeAll();
        justLearnedDatabaseAdvance.removeAll();


        int j = 1;


        if(level.equalsIgnoreCase("beginner")){

            for(int i = 0; i < fiveWords.size(); i++){
                Word word = fiveWords.get(i);

                if(i == pos){

                    justLearnedDatabaseBeginner.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite(),"true");

                }



                justLearnedDatabaseBeginner.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite(),"false");

                j++;

            }
        }

        if(level.equalsIgnoreCase("intermediate")){


            for(int i = 0; i < fiveWords.size(); i++){
                Word word = fiveWords.get(i);

                if( i == pos){

                    justLearnedDatabaseIntermediate.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "true");


                }else {

                    justLearnedDatabaseIntermediate.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "false");

                }




                j++;

            }


        }

        if(level.equalsIgnoreCase("advance")){


            for(int i = 0; i < fiveWords.size(); i++){
                Word word = fiveWords.get(i);


                if(pos == i){

                    justLearnedDatabaseAdvance.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "true");


                }else {

                    justLearnedDatabaseAdvance.insertData(j,""+word.position,word.getWord(),word.getTranslation(),word.getExtra(),word.getPronun(),word.getGrammar(),word.getExample1(),word.getExample2(),word.getExample3(),word.vocabularyType,"true",word.isFavorite, "false");

                }



                j++;

            }


        }








    }

    private void showInterstitialAd(){

    }

    private void hideViews(){
        topBackground.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);
        progress1.setVisibility(View.INVISIBLE);
        wordCard.setVisibility(View.INVISIBLE);

    }

    private void whichVocabularyToTest(View v){

        final View view = v;
        fiveWordsCopy.addAll(fiveWords);

        final ArrayList<Word> userSelectedWords = new ArrayList<>(fiveWords);
        items = new String[fiveWords.size()];
        checkedItems = new boolean[fiveWords.size()];




        for(int i = 0; i < fiveWords.size(); i++){

            items[i] = fiveWords.get(i).getWord();
            checkedItems[i] = false;

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
        builder.setTitle("Which vocabularies do you want to test?");


        //this will checked the items when user open the dialog
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Toast.makeText(NewTrain.this, "Position: " + which + " Value: " + items[which] + " State: " + (isChecked ? "checked" : "unchecked"), Toast.LENGTH_LONG).show();

                checkedItems[which] = isChecked;


            }
        });


        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                fiveWords.clear();

                for(int i = 0; i < checkedItems.length; i++){


                    if(checkedItems[i]){
                        fiveWords.add(userSelectedWords.get(i));
                    }
                }

                if(fiveWords.size()>0){


                    progress1.setMax(userSelectedWords.size()+(fiveWords.size()*repeatPerSession));
                    wordView.setText(fiveWords.get(quizCycle).getWord());
                    quizWords(quizCycle, view);
                }else{

                    fiveWords.addAll(userSelectedWords);
                    updateJustlearnedDatabase(-1);
                    updateLearnedDatabase();

                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
                            NewTrain.this.finish();
                        }
                    }, 200L);
                }


                FIVE_WORD_SIZE = fiveWords.size();

                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();










//
//        new LovelyChoiceDialog(this, R.style.CheckBoxTintTheme)
//                .setTopColorRes(R.color.colorPrimary)
//                .setTitle("Which vocabularies do you want to test?")
//                .setIcon(R.drawable.ic_stat_notification_icon)
//                .setItemsMultiChoice(items, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
//                    @Override
//                    public void onItemsSelected(List<Integer> positions, List<String> items) {
//
//                        fiveWords.clear();
//
//                        for(int i = 0; i < items.size(); i++){
//
//                            for(int x = 0; x < userSelectedWords.size(); x++){
//
//                                if(items.get(i).equalsIgnoreCase(userSelectedWords.get(x).getWord())){
//                                    fiveWords.add(userSelectedWords.get(x));
//                                }
//                            }
//                        }
//
//                        if(fiveWords.size()>0){
//
//
//                            progress1.setMax(userSelectedWords.size()+(fiveWords.size()*repeatPerSession));
//                            wordView.setText(fiveWords.get(quizCycle).getWord());
//                            quizWords(quizCycle, view);
//                        }else{
//
//                            fiveWords.addAll(userSelectedWords);
//                            updateJustlearnedDatabase(-1);
//                            updateLearnedDatabase();
//
//                            Handler handler = new Handler();
//
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    NewTrain.this.startActivity(new Intent(getApplicationContext(), TrainFinishedActivity.class));
//                                    NewTrain.this.finish();
//                                }
//                            }, 200L);
//                        }
//
//
//                        FIVE_WORD_SIZE = fiveWords.size();
//
//
//
//                    }
//
//                })
//                .setConfirmButtonText("Confirm")
//                .show();
//        unhideViews();
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
               // Toast.makeText(getApplicationContext() , localFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                audioPath = localFile.getAbsolutePath();

                MediaPlayer mp = new MediaPlayer();
               // Toast.makeText(getApplicationContext(),audioPath,Toast.LENGTH_LONG).show();
                try{

                    mp.setDataSource(audioPath);
                    mp.prepare();
                    mp.start();
                    speak.setEnabled(false);
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            speak.setEnabled(true);
                           // Toast.makeText(getApplicationContext(),"play finished", Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (IOException e){
                    e.printStackTrace();
                }



            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
               // Toast.makeText(getApplicationContext(),"Completed",Toast.LENGTH_LONG).show();
                speak.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);

            }
        });

    }

    private String checkTrialStatus(){

        String trialStatus = "ended";

        if(sp.contains("trial_end_date")){

            Date today = Calendar.getInstance().getTime();

            long endMillies = sp.getLong("trial_end_date",0) ;
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;

            if(leftMillies >=0){

                trialStatus = "active";

            }
            else {

                trialStatus = "ended";

            }

        }
        return trialStatus;
    }

    private boolean getIsAdShow(){

        boolean isAdShow = false;
        String trialStatus = checkTrialStatus();

        if(!sp.contains("premium")){

            if(trialStatus.equalsIgnoreCase("ended")){

                isAdShow = true;
            }
        }

        return isAdShow;

    }

    @Override
    public void onMethodCallback(String word) {
        if(tts != null){

            tts.setLanguage(Locale.US);
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null,"TTS");

        }

    }

    private void applyWrongColor(){
        topBackground.setBackgroundColor(getColor(R.color.red));
        fab.setColorNormal(getColor(R.color.red));
        progress1.setProgressColor(getColor(R.color.red));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.red));
        trainCircle1.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
        trainCircle2.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
        trainCircle3.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
        trainCircle4.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
    }
    private void applyCorrectColor(){
        topBackground.setBackgroundColor(getColor(R.color.green));
        fab.setColorNormal(getColor(R.color.green));

        progress1.setProgressColor(getColor(R.color.green));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.green));

        trainCircle1.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
        trainCircle2.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
        trainCircle3.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
        trainCircle4.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
    }
}




