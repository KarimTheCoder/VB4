package com.fortitude.shamsulkarim.ieltsfordory.Practice;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
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
import com.fortitude.shamsulkarim.ieltsfordory.NewTrain;
import com.fortitude.shamsulkarim.ieltsfordory.TrainFinishedActivity;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.Word;
import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import mehdi.sakout.fancybuttons.FancyButton;

public class Practice extends AppCompatActivity  implements View.OnClickListener, TextToSpeech.OnInitListener, NewTrainRecyclerView.TrainAdapterCallback {

    private String[] IELTSwordArray,IELTSwordsArraySL, IELTStranslationArray,IELTStranslationArraySL, IELTSgrammarArray,IELTSextra, IELTSpronunArray, IELTSexample1array,IELTSexample1arraySL, IELTSexample2Array,IELTSexample2ArraySL, IELTSexample3Array,IELTSexample3ArraySL, IELTSvocabularyType;
    private String[] TOEFLwordArray, TOEFLtranslationArray, TOEFLgrammarArray, TOEFLpronunArray, TOEFLexample1array, TOEFLexample2Array, TOEFLexample3Array, TOEFLvocabularyType;
    private String[] TOEFLwordArraySL, TOEFLtranslationArraySL,TOEFLexample1ArraySL, TOEFLexample2ArraySL, TOEFLexample3ArraySL;

    private String[] SATwordArray, SATtranslationArray, SATgrammarArray, SATpronunArray, SATexample1array, SATexample2Array, SATexample3Array, SATvocabularyType;
    private String[] SATwordArraySL, SATtranslationArraySL, SATexample1ArraySL, SATexample2ArraySL, SATexample3ArraySL;

    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;
    private String[] GREwordArraySL, GREtranslationArraySL, GREexample1ArraySL, GREexample2ArraySL, GREexample3ArraySL;

    private List<String> IELTSlearnedDatabase, TOEFLlearnedDatabase, SATlearnedDatabase, GRElearnedDatabase;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private List<String> ieltsFavPosition, toeflFavPosition, satFavPosition, greFavPosition;
    private int[] IELTSposition, TOEFLposition, SATposition, GREposition;
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;
    private List<Word> words = new ArrayList<>();
    private List<Word> fiveWords, buttonQuestion;
    private SharedPreferences sp;
    private int color;
    private CardView wordCard, answerCard1, answerCard2, answerCard3, answerCard4;
    private TextView wordView, answerView1, answerView2, answerView3, answerView4;
    private FloatingActionButton fab;
    private FancyButton speak;
    private View trainCircle1, trainCircle2, trainCircle3, trainCircle4;
    private RoundCornerProgressBar progress1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int showCycle = 0;
    private int quizCycle = 0;
    private TextToSpeech tts;
    private int FIVE_WORD_SIZE = 0;
    private boolean IsWrongAnswer = true;
    private int lastMistake = 13;
    private int mistakes = 0;
    private int[] mistakeCollector;
    private int repeatPerSession = 5;
    private int totalCycle = 0;
    private int wordsPerSession = 5;
    private int languageId;
    private boolean soundState = true;
    private int favoriteWrongs,totalCorrects;
    private int progressCount = 0;
    private boolean next  = true;
    private boolean  alreadyclicked = true;
    private boolean progress = false;
    private int cb = 0;
    boolean isAdShown1 = false;
    // DATABASE
    private IELTSWordDatabase IELTSdatabase;
    private TOEFLWordDatabase TOEFLdatabase;
    private SATWordDatabase SATdatabase;
    private GREWordDatabase GREdatabase;
    private List<String> bWord,aWord,iWord,greWord;
    private List<Integer> bWordDatabasePosition, aWordDatabasePosition, iWordDatabasePosition, greWordDatabasePosition;

    private PublisherInterstitialAd mPublisherInterstitialAd;
    ProgressBar progressBar, adLoading;
    private String secondLanguage = "english";
    //----------------------
    private View topBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_new_train);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        // This code reports to Crashlytics of connection
        boolean connected = ConnectivityHelper.isConnectedToNetwork(this);

//
//        this.startActivity(new Intent(this, PracticeFinished.class));
//        this.finish();






        initializingSQLDatabase();
        initialization();
        initializeAds();
        gettingResources();
        addingLearnedDatabase();

        getFavoriteWordRes();
        addFavoriteWord();

        addingNewWords();

//        if(true){
//            answerCard1.setVisibility(View.VISIBLE);
//            answerCard2.setVisibility(View.VISIBLE);
//            answerCard3.setVisibility(View.VISIBLE);
//            answerCard4.setVisibility(View.VISIBLE);
//
//            fab.setVisibility(View.INVISIBLE);
//
//            quizWords(quizCycle, fab);
//
//        }else {
//            showWords(showCycle);
//        }

        showWords(showCycle);

        mistakeCollector = new int[fiveWords.size()];
        sp.edit().putInt("favoriteWordCount",fiveWords.size()).apply();
        languageId = sp.getInt("language",0);
        soundState = sp.getBoolean("soundState",true);
        totalCorrects = sp.getInt("totalCorrects",0);
        cb = sp.getInt("cb",0);



        if(!sp.contains("favoriteWrongs")){

            sp.edit().putInt("favoriteWrongs",0).apply();


        }else {

            favoriteWrongs = sp.getInt("favoriteWrongs",0);

        }

        for(int i = 0; i < mistakeCollector.length; i ++){

            mistakeCollector[i] = 0;

        }



    }
    //----------------------------------------------------------------------------------------------


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


        if( alreadyclicked){

            if (showCycle >= FIVE_WORD_SIZE){


                // isAdShown1 = showInterstitialAd(isAdShown1);
                answerCard1.setVisibility(View.VISIBLE);
                answerCard2.setVisibility(View.VISIBLE);
                answerCard3.setVisibility(View.VISIBLE);
                answerCard4.setVisibility(View.VISIBLE);
                quizWords(quizCycle, v);

            }

            showWords(showCycle);


            alreadyclicked = false;
            next = false;
        }




    }


    @Override
    public void onClick(View v) {

        if( v== speak){
            String word ="";

            if(showCycle < FIVE_WORD_SIZE+1){

                word = wordView.getText().toString();

                if(showCycle == FIVE_WORD_SIZE){
                    showCycle++;
                }

            }else {

                word = wordView.getText().toString();

            }
            tts.setLanguage(Locale.US);
            tts.speak(word, TextToSpeech.QUEUE_ADD, null);

        }


        if( v == answerCard1 || v == answerCard2 || v == answerCard3 ||  v == answerCard4 ){


            if( quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){
                quizWords(quizCycle, v);

            }

        }

    }



    //---------- Showing Words

    private void showWords(int showCycle){


        Handler handler = new Handler();

        if( showCycle < FIVE_WORD_SIZE){

            wordView.setText(fiveWords.get(showCycle).getWord());

            if(sp.getString("secondlanguage","english").equalsIgnoreCase("spanish")){
                String combineBothLanguage = fiveWords.get(showCycle).getWord()+"\n"+fiveWords.get(showCycle).getWordSL();
                final ForegroundColorSpan lowColor = new ForegroundColorSpan(Color.parseColor("#8c979a"));
                SpannableStringBuilder spanWord = new SpannableStringBuilder(combineBothLanguage);


             //   Toast.makeText(this, "Word length: "+fiveWords.get(showCycle).getWordSL(), Toast.LENGTH_LONG).show();
               spanWord.setSpan(lowColor,fiveWords.get(showCycle).getWord().length(),1+fiveWords.get(showCycle).getWordSL().length()+fiveWords.get(showCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
               spanWord.setSpan(new RelativeSizeSpan(0.4f), fiveWords.get(showCycle).getWord().length(),1+fiveWords.get(showCycle).getWordSL().length()+fiveWords.get(showCycle).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordView.setText(spanWord);

            }else {
                wordView.setText(fiveWords.get(showCycle).getWord());
            }
           // wordViewMiddle.setText(fiveWords.get(showCycle).getWord());

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


            adapter = new NewTrainRecyclerView(this,fiveWords.get(showCycle),this);
            recyclerView.setAdapter(adapter);
            this.showCycle++;
            progress1.setProgress(quizCycle+showCycle);
        }else {

            answerCard1.setVisibility(View.VISIBLE);
            answerCard2.setVisibility(View.VISIBLE);
            answerCard3.setVisibility(View.VISIBLE);
            answerCard4.setVisibility(View.VISIBLE);
            quizWords(quizCycle, fab);
        }



    }

    //--------- Quizzing

    private void quizWords(int quizCycle, View v){


        if(quizCycle == 0){

            answerCardAnimation();

        }
        if(showCycle >=FIVE_WORD_SIZE|| quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){

            //wordView.setVisibility(View.INVISIBLE);
            answerCard1.setVisibility(View.VISIBLE);
            answerCard2.setVisibility(View.VISIBLE);
            answerCard3.setVisibility(View.VISIBLE);
            answerCard4.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            speak.setVisibility(View.INVISIBLE);
            fab.animate().scaleX(0f).scaleY(0f).setDuration(350L).setInterpolator(new AnticipateOvershootInterpolator());


            checkingAnswer(v);
            cycleQuiz(quizCycle,v);
        }


    }
    private void cycleQuiz(int quizCycle, View v){


        if(this.quizCycle <=(FIVE_WORD_SIZE*repeatPerSession)-1){

            ArrayList<Word> answers = gettingAnswer();
            if(!IsWrongAnswer){
                IsWrongAnswer = true;

            }else {
               // wordViewMiddle.setVisibility(View.VISIBLE);
                wordView.setText(fiveWords.get(this.quizCycle).getWord());
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

        ArrayList<Word> answers = new ArrayList<>();
        List<Word> words = new ArrayList<>();
        words.addAll(fiveWords) ;
        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }


        if( this.quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){


            Word word = fiveWords.get(this.quizCycle);

            Collections.shuffle(words);
            for (int i = 0; i < 4;i++) {

                answers.add(words.get(i));

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


        if(this.quizCycle < (FIVE_WORD_SIZE*repeatPerSession)){

            String answer;
            if(languageId == 0){
                answer = fiveWords.get(quizCycle).getTranslation().toString();
            }else {
                answer = fiveWords.get(quizCycle).getExtra().toString();

            }


            if(v == answerCard1){


                if(answerView1.getText().toString().equalsIgnoreCase(answer)){
                    applyCorrectColor();
                    if(soundState){

                        correctAudio.start();
                    }
                    totalCorrects++;

                    StyleableToast.makeText(this, "Correct!", 5, R.style.correct).show();

                    this.quizCycle++;
                    totalCycle++;

                    answerCardAnimation2();

                }else {

                    applyWrongColor();
                    if(soundState){

                        incorrectAudio.start();
                    }
                    mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;
                    //wordViewMiddle.setText("");
                    speak.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();

                    mistakes++;

                    if( lastMistake == quizCycle){
                        StyleableToast.makeText(this, "wrong answer again", 5, R.style.wrong_again).show();


                    }else {

                        lastMistake = quizCycle;
                        if(mistakes <= 3){

                            StyleableToast.makeText(this, "Wrong Answer", 5, R.style.wrong).show();

                        }
                        if(mistakes >= 4){
                            StyleableToast.makeText(this, "Oh no! wrong answer", 5, R.style.MyToast).show();

                        }
                    }








                } }

            if(v == answerCard2) {


                if (answerView2.getText().toString().equalsIgnoreCase(answer)) {
                    applyCorrectColor();
                    if(soundState){

                        correctAudio.start();
                    }
                    totalCorrects++;

                    StyleableToast.makeText(this, "Correct!", 5, R.style.correct).show();
                    this.quizCycle++;
                    totalCycle++;

                    answerCardAnimation2();

                } else {

                    applyWrongColor();
                    if(soundState){

                        incorrectAudio.start();
                    }
                   // wordViewMiddle.setText("");
                    speak.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();

                    mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;
                    mistakes++;

                    if( lastMistake == quizCycle){
                        StyleableToast.makeText(this, "wrong answer again", 5, R.style.wrong_again).show();


                    }else {

                        lastMistake = quizCycle;
                        if(mistakes <= 3){

                            StyleableToast.makeText(this, "Wrong answer", 5, R.style.wrong).show();

                        }
                        if(mistakes >= 4){
                            StyleableToast.makeText(this, "Oh no! wrong answer", 5, R.style.MyToast).show();

                        }
                    }


                }
            }

            if(v == answerCard3){


                if(answerView3.getText().toString().equalsIgnoreCase(answer)){
                    applyCorrectColor();
                    if(soundState){

                        correctAudio.start();
                    }
                    totalCorrects++;

                    StyleableToast.makeText(this, "Correct!", 5, R.style.correct).show();
                    this.quizCycle++;
                    totalCycle++;

                    answerCardAnimation2();

                }else {
                    applyWrongColor();
                    if(soundState){

                        incorrectAudio.start();
                    }
                   // wordViewMiddle.setText("");
                    speak.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();

                    mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;
                    mistakes++;

                    if( lastMistake == quizCycle){
                        StyleableToast.makeText(this, "Wrong answer again", 5, R.style.wrong_again).show();


                    }else {

                        lastMistake = quizCycle;
                        if(mistakes <= 3){

                            StyleableToast.makeText(this, "Wrong answer", 5, R.style.wrong).show();

                        }
                        if(mistakes >= 4){
                            StyleableToast.makeText(this, "Oh no! wrong answer", 5, R.style.MyToast).show();

                        }
                    }

                }


            }
            if(v == answerCard4){


                if(answerView4.getText().toString().equalsIgnoreCase(answer)){
                    if(soundState){

                        correctAudio.start();
                    }
                    applyCorrectColor();
                    totalCorrects++;

                    StyleableToast.makeText(this, "Correct!", 5, R.style.correct).show();
                    this.quizCycle++;
                    totalCycle++;
                    answerCardAnimation2();

                    cycleQuiz(quizCycle, v);



                }else {
                    applyWrongColor();

                    if(soundState){

                        incorrectAudio.start();
                    }
                //    wordViewMiddle.setText("");
                    speak.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();


                    mistakeCollector[quizCycle] = mistakeCollector[quizCycle]+1;
                    mistakes++;

                    if( lastMistake == quizCycle){
                        StyleableToast.makeText(this, "wrong answer again?", 5, R.style.wrong_again).show();


                    }else {

                        lastMistake = quizCycle;
                        if(mistakes <= 3){

                            StyleableToast.makeText(this, "Wrong answer", 5, R.style.wrong).show();

                        }
                        if(mistakes >= 4){
                            StyleableToast.makeText(this, "Oh no! Wrong answer", 5, R.style.MyToast).show();

                        }
                    }

                }


            }

        }
        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }


        progress1.setProgress(totalCycle+showCycle);
        sp.edit().putInt("mistakeFavorite",mistakes).apply();
        sp.edit().putInt("favoriteWrongs",mistakes+favoriteWrongs).apply();
        sp.edit().putInt("totalCorrects",totalCorrects).apply();

        if (totalCycle == (FIVE_WORD_SIZE*repeatPerSession)){

//            int pos = getMostMistakenWord(mistakeCollector);
//
//            if(pos != -1){
//
//                String word = fiveWords.get(pos).getPronun();
//                String def = fiveWords.get(pos).getTranslation();
//                String spanish = fiveWords.get(pos).getExtra();
//                String example = fiveWords.get(pos).getExample2();
//
//                StringBuilder mostMistakenWord = new StringBuilder("shit"+"+"+word+"+"+def+"+"+spanish+"+"+example);
//                sp.edit().putString("MostMistakenWord",mostMistakenWord.toString()).apply();
//
//
//            }else {
//                sp.edit().putString("MostMistakenWord","no").apply();
//
//            }



            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Todo

                    hideViews();
                    adLoading.setVisibility(View.VISIBLE);
                    showInterstitialAd(false);

//                    Practice.this.startActivity(new Intent(getApplicationContext(), PracticeFinished.class));
//                    Practice.this.finish();
                }
            },200L);



        }


    }

    // --------- Initializing

    private void initialization(){


        topBackground = findViewById(R.id.top_background);
        recyclerView = (RecyclerView)findViewById(R.id.train_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        // if true this will set showCycle to maximum and skip definition session


        wordsPerSession = sp.getInt("wordsPerSession",5);
        repeatPerSession = sp.getInt("repeatationPerSession",5);

        // DATABASE
        bWord = new ArrayList<>();
        aWord = new ArrayList<>();
        iWord = new ArrayList<>();
        greWord = new ArrayList<>();
        bWordDatabasePosition = new ArrayList<>();
        aWordDatabasePosition = new ArrayList<>();
        iWordDatabasePosition = new ArrayList<>();
        greWordDatabasePosition = new ArrayList<>();


        color = R.color.practiceColor;
        buttonQuestion = new ArrayList<>();

        tts = new TextToSpeech(this, this);

        wordCard =    (CardView)findViewById(R.id.wordCard);
        wordView =    (TextView)findViewById(R.id.train_word);
        answerCard1 = (CardView)findViewById(R.id.answer_card1);
        answerCard2 = (CardView)findViewById(R.id.answer_card2);
        answerCard3 = (CardView)findViewById(R.id.answer_card3);
        answerCard4 = (CardView)findViewById(R.id.answer_card4);
        answerView1 = (TextView)findViewById(R.id.train_answer_text1);
        answerView2 = (TextView)findViewById(R.id.train_answer_text2);
        answerView3 = (TextView)findViewById(R.id.train_answer_text3);
        answerView4 = (TextView)findViewById(R.id.train_answer_text4);
        speak =       (FancyButton)findViewById(R.id.train_speaker_icon);
        speak.setVisibility(View.INVISIBLE);
       // wordViewMiddle = (TextView)findViewById(R.id.train_word_middle);
        fab = (FloatingActionButton)findViewById(R.id.train_fab);
        fab.setMax(5);
        progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        trainCircle1 = (View)findViewById(R.id.train_circle1);
        trainCircle2 = (View)findViewById(R.id.train_circle2);
        trainCircle3 = (View)findViewById(R.id.train_circle3);
        trainCircle4 = (View)findViewById(R.id.train_circle4);



        progress1.setProgressColor(getResources().getColor(R.color.colorPrimary));
        progress1.setSecondaryProgressColor(getResources().getColor(R.color.colorPrimaryDark));
        progress1.setProgressBackgroundColor(getResources().getColor(R.color.primary_text_color_white));
        fab.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        fiveWords = new ArrayList<>();

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
        progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);
        adLoading = findViewById(R.id.spin_ad_loading);
        Sprite threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);
        adLoading.setVisibility(View.GONE);




//        wordViewMiddle.setVisibility(View.INVISIBLE);

      //  wordView.setTypeface(comfortaRegular);
    }
    private void addingNewWords() {

        fiveWords = new ArrayList<>();
        fiveWords.clear();
        String practice = sp.getString("practice","learned");

        if(practice.equalsIgnoreCase("favorite")){
            fiveWords = words;

            for(int i = 0; i < fiveWords.size(); i++){

                fiveWords.get(i).setSeen(false);


            }

            if(true){
                showCycle = fiveWords.size();
            }
        }



        if(practice.equalsIgnoreCase("learned")){
            getLearnedWords();

            for(int i = 0; i < fiveWords.size(); i++){

                fiveWords.get(i).setSeen(false);



            }


        }

        sp.edit().putInt("favoriteWordCount",fiveWords.size()).apply();

        FIVE_WORD_SIZE = fiveWords.size();
        progress1.setMax(FIVE_WORD_SIZE+(FIVE_WORD_SIZE*repeatPerSession));
        progress1.setSecondaryProgress(FIVE_WORD_SIZE);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
    }

    // ------- Animation -------------------------------
    private void answerCardAnimation(){

        answerCard1.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
        answerCard2.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
        answerCard3.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());
        answerCard4.animate().translationY(0f).alpha(1f).setDuration(500L).setInterpolator(new AccelerateDecelerateInterpolator());

    }
    private void answerCardAnimation2(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;
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
      //  wordViewMiddle.setText("");
      //  wordViewMiddle.setVisibility(View.INVISIBLE);
        answerCard1.setVisibility(View.INVISIBLE);
        answerCard2.setVisibility(View.INVISIBLE);
        answerCard3.setVisibility(View.INVISIBLE);
        answerCard4.setVisibility(View.INVISIBLE);
        speak.setVisibility(View.INVISIBLE);
        wordView.setVisibility(View.VISIBLE);

        wordView.setText(fiveWords.get(quizCycle).getWord());
    //    wordViewMiddle.setText(fiveWords.get(quizCycle).getWord());


        DefExamAnimation();
        adapter = new NewTrainRecyclerView(this,fiveWords.get(quizCycle),this);
        recyclerView.setAdapter(adapter);


    }
    private void DefExamAnimation(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;
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


    @Override
    public void onBackPressed() {


        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_leave)
                .setTitle("Do you want to leave this session, "+sp.getString("userName","Boo")+"?")
                .setMessage("Leaving this session will make you lose your progress")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Practice.this.startActivity(new Intent(Practice.this,MainActivity.class));
                        Practice.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();

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

    private void initializingSQLDatabase(){

        ieltsWordDatabase = new IELTSWordDatabase(this);
        satWordDatabase = new SATWordDatabase(this);
        toeflWordDatabase = new TOEFLWordDatabase(this);
        greWordDatabase = new GREWordDatabase(this);
    }

    private void getFavoriteWordRes(){


        Cursor aRes = satWordDatabase.getData();
        Cursor bRes = ieltsWordDatabase.getData();
        Cursor iRes = toeflWordDatabase.getData();
        Cursor greRes = greWordDatabase.getData();

        while (aRes.moveToNext()){

            aWord.add(aRes.getString(2));
            int pos = (Integer) aRes.getInt(0);
            aWordDatabasePosition.add(pos);

        }

        while (bRes.moveToNext()){

            bWord.add(bRes.getString(2));
            int pos = (Integer) bRes.getInt(0);
            bWordDatabasePosition.add(pos);

        }

        while (iRes.moveToNext()){

            iWord.add(iRes.getString(2));
            int pos = (Integer) iRes.getInt(0);
            iWordDatabasePosition.add(pos);

        }

        while(greRes.moveToNext()){

            greWord.add(greRes.getString(2));
            int pos = (Integer)greRes.getInt(0);
            greWordDatabasePosition.add(pos);

        }



    }

    public   void addFavoriteWord(){

        words.clear();
        int languageId = sp.getInt("language",0);


        String[] beginnerSecondTranslation = new String[getResources().getStringArray(R.array.IELTS_words).length];
        String[] intermediateSecondTranslation = new String[getResources().getStringArray(R.array.TOEFL_words).length];
        String[] advanceSecondTranslation = new String[getResources().getStringArray(R.array.SAT_words).length];

        if(languageId == 0){

            for(int i = 0; i < getResources().getStringArray(R.array.IELTS_words).length; i++){


                beginnerSecondTranslation[i] = "";
            }

        }

        if(languageId == 1){

            beginnerSecondTranslation = getResources().getStringArray(R.array.beginner_spanish);
            intermediateSecondTranslation = getResources().getStringArray(R.array.intermediate_spanish);
            advanceSecondTranslation = getResources().getStringArray(R.array.advance_spanish);
        }
        if(languageId == 3){

            beginnerSecondTranslation = getResources().getStringArray(R.array.beginner_bengali);
            intermediateSecondTranslation = getResources().getStringArray(R.array.intermediate_bengali);
            advanceSecondTranslation = getResources().getStringArray(R.array.advance_bengali);
        }
        if(languageId == 2){

            beginnerSecondTranslation = getResources().getStringArray(R.array.beginner_hindi);
            intermediateSecondTranslation = getResources().getStringArray(R.array.intermediate_hindi);
            advanceSecondTranslation = getResources().getStringArray(R.array.advance_hindi);
        }

        int aWordSize = aWord.size();
        int bWordSize = bWord.size();
        int iWordSize = iWord.size();
        int greWordSize = greWord.size();

        String[] beginnerWordArray = getResources().getStringArray(R.array.IELTS_words);
        String[] beginnerTranslationArray = getResources().getStringArray(R.array.IELTS_translation);
        String[] beginnerPronunciationArray = getResources().getStringArray(R.array.IELTS_pronunciation);
        String[] beginnerGrammarArray = getResources().getStringArray(R.array.IELTS_grammar);
        String[] beginnerExampleArray1 = getResources().getStringArray(R.array.IELTS_example1);
        String[] beginnerExampleArray2 = getResources().getStringArray(R.array.IELTS_example2);
        String[] beginnerExampleArray3 = getResources().getStringArray(R.array.IELTS_example3);

//        Toast.makeText(this,"IELTS: "+beginnerWordArray.length
//                +"\nIELTS trans:"+beginnerTranslationArray.length
//                +"\nIELTS pron:"+beginnerPronunciationArray.length
//                +"\nIELTS gram:"+beginnerGrammarArray.length
//                +"\nIELTS ex1:"+beginnerExampleArray1.length
//                +"\nIELTS ex2:"+beginnerExampleArray2.length
//                +"\nIELTS ex3:"+beginnerExampleArray3.length,Toast.LENGTH_LONG).show();

        String[] intermediateWordArray = getResources().getStringArray(R.array.TOEFL_words);
        String[] intermediateTranslationArray = getResources().getStringArray(R.array.TOEFL_translation);
        String[] intermediatePronunciationArray = getResources().getStringArray(R.array.TOEFL_pronunciation);
        String[] intermediateGrammarArray = getResources().getStringArray(R.array.TOEFL_grammar);
        String[] intermediateExampleArray1 = getResources().getStringArray(R.array.TOEFL_example1);
        String[] intermediateExampleArray2 = getResources().getStringArray(R.array.TOEFL_example2);
        String[] intermediateExampleArray3 = getResources().getStringArray(R.array.TOEFL_example3);

//        Toast.makeText(this,"TOEFL: "+intermediateWordArray.length
//                +"\nTOEFL trans:"+intermediateTranslationArray.length
//                +"\nTOEFL pron:"+intermediatePronunciationArray.length
//                +"\nTOEFL gram:"+intermediateGrammarArray.length
//                +"\nTOEFL ex1:"+intermediateExampleArray1.length
//                +"\nTOEFL ex2:"+intermediateExampleArray2.length
//                +"\nTOEFL ex3:"+intermediateExampleArray3.length,Toast.LENGTH_LONG).show();

        String[] advanceWordArray = getResources().getStringArray(R.array.SAT_words);
        String[] advanceTranslationArray = getResources().getStringArray(R.array.SAT_translation);
        String[] advancePronunciationArray = getResources().getStringArray(R.array.SAT_pronunciation);
        String[] advanceGrammarArray = getResources().getStringArray(R.array.SAT_grammar);
        String[] advanceExampleArray1 = getResources().getStringArray(R.array.SAT_example1);
        String[] advanceExampleArray2 = getResources().getStringArray(R.array.SAT_example2);
        String[] advanceExampleArray3 = getResources().getStringArray(R.array.SAT_example3);

//        Toast.makeText(this,"SAT: "+advanceWordArray.length
//                +"\nSAT trans:"+advanceTranslationArray.length
//                +"\nSAT pron:"+advancePronunciationArray.length
//                +"\nSAT gram:"+advanceGrammarArray.length
//                +"\nSAT ex1:"+advanceExampleArray1.length
//                +"\nSAT ex2:"+advanceExampleArray2.length
//                +"\nSAT ex3:"+advanceExampleArray3.length,Toast.LENGTH_LONG).show();

        String[] greWordArray = getResources().getStringArray(R.array.GRE_words);
        String[] greTranslationArray = getResources().getStringArray(R.array.GRE_translation);
        String[] grePronunciationArray = getResources().getStringArray(R.array.GRE_pronunciation);
        String[] greGrammarArray = getResources().getStringArray(R.array.GRE_grammar);
        String[] greExampleArray1 = getResources().getStringArray(R.array.GRE_example1);
        String[] greExampleArray2 = getResources().getStringArray(R.array.GRE_example2);
        String[] greExampleArray3 = getResources().getStringArray(R.array.GRE_example3);

//        Toast.makeText(this,"GRE: "+greWordArray.length
//                +"\nGRE trans:"+greTranslationArray.length
//                +"\nGRE pron:"+grePronunciationArray.length
//                +"\nGRE gram:"+greGrammarArray.length
//                +"\nGRE ex1:"+greExampleArray1.length
//                +"\nGRE ex2:"+greExampleArray2.length
//                +"\nGRE ex3:"+greExampleArray3.length,Toast.LENGTH_LONG).show();

        for(int i = 0; i < aWordSize; i++){






            if(aWord.get(i).equalsIgnoreCase("True")){

                Word word = new Word(advanceWordArray[i],advanceTranslationArray[i],advanceSecondTranslation[i],advancePronunciationArray[i],advanceGrammarArray[i],advanceExampleArray1[i],advanceExampleArray2[i], advanceExampleArray3[i],"SAT",aWordDatabasePosition.get(i),"False", "True");

                if(!secondLanguage.equalsIgnoreCase("english")){

                    word.setWordSL(SATwordArraySL[i]);
                    word.setTranslationSL(SATtranslationArraySL[i]);
                    word.setExample1SL(SATexample1ArraySL[i]);
                    word.setExample2SL(SATexample2ArraySL[i]);
                    word.setExample3SL(SATexample3ArraySL[i]);

                }else {

                    word.setWordSL("");
                    word.setTranslationSL("");
                    word.setExample1SL("");
                    word.setExample2SL("");
                    word.setExample3SL("");

                }

                words.add(word);

            }

        }

        for(int i =0 ; i < bWordSize; i++){

            if(bWord.get(i).equalsIgnoreCase("True")){


                Word word = new Word(beginnerWordArray[i],beginnerTranslationArray[i],beginnerSecondTranslation[i],beginnerPronunciationArray[i],beginnerGrammarArray[i],beginnerExampleArray1[i],beginnerExampleArray2[i],beginnerExampleArray3[i],"IELTS",bWordDatabasePosition.get(i),"False","True");

                if(!secondLanguage.equalsIgnoreCase("english")){


                    word.setWordSL(IELTSwordsArraySL[i]);
                    word.setTranslationSL(IELTStranslationArraySL[i]);
                    word.setExample1SL(IELTSexample1arraySL[i]);
                    word.setExample2SL(IELTSexample2ArraySL[i]);
                    word.setExample3SL(IELTSexample3ArraySL[i]);

                }else {

                    word.setWordSL("");
                    word.setTranslationSL("");
                    word.setExample1SL("");
                    word.setExample2SL("");
                    word.setExample3SL("");

                }

                words.add(word);
            }

        }

        for(int i =0 ; i < iWordSize; i++){

            if(iWord.get(i).equalsIgnoreCase("True")){

                Word word = new Word(intermediateWordArray[i],intermediateTranslationArray[i],intermediateSecondTranslation[i],intermediatePronunciationArray[i],intermediateGrammarArray[i],intermediateExampleArray1[i],intermediateExampleArray2[i],intermediateExampleArray3[i],"TOEFL",iWordDatabasePosition.get(i),"False","True");

                if(!secondLanguage.equalsIgnoreCase("english")){

                    word.setWordSL(TOEFLwordArraySL[i]);
                    word.setTranslationSL(TOEFLtranslationArraySL[i]);
                    word.setExample1SL(TOEFLexample1ArraySL[i]);
                    word.setExample2SL(TOEFLexample2ArraySL[i]);
                    word.setExample3SL(TOEFLexample3ArraySL[i]);
                }else {

                    word.setWordSL("");
                    word.setTranslationSL("");
                    word.setExample1SL("");
                    word.setExample2SL("");
                    word.setExample3SL("");

                }

                words.add(word);

            }

        }

        for(int i =0 ; i < greWordSize; i++){

            if(greWord.get(i).equalsIgnoreCase("True")){

                Word word = new Word(greWordArray[i],greTranslationArray[i],"",grePronunciationArray[i],greGrammarArray[i],greExampleArray1[i],greExampleArray2[i],greExampleArray3[i],"GRE",greWordDatabasePosition.get(i),"False","True");

                if(!secondLanguage.equalsIgnoreCase("english")){

                    word.setWordSL(SATwordArraySL[i]);
                    word.setTranslationSL(SATtranslationArraySL[i]);
                    word.setExample1SL(SATexample1ArraySL[i]);
                    word.setExample2SL(SATexample2ArraySL[i]);
                    word.setExample3SL(SATexample3ArraySL[i]);

                }else {

                    word.setWordSL("");
                    word.setTranslationSL("");
                    word.setExample1SL("");
                    word.setExample2SL("");
                    word.setExample3SL("");

                }

                words.add(word);

            }

        }

        //Toast.makeText(this,words.get(0).getWord(),Toast.LENGTH_SHORT).show();



    }

    public List<Word> getLearnedWords(){

        List<Word> getWords = new ArrayList<>();
        String level = sp.getString("level","beginner");
        int languageId = sp.getInt("language",0);

        int LearnedCount = sp.getInt(level,0);

        if(level.equalsIgnoreCase("beginner")){
//
//            String[]  beginnerWordArray= getResources().getStringArray(R.array.IELTS_words);
//            String[] beginnerTranslationArray = getResources().getStringArray(R.array.IELTS_translation);
//            String[] beginnerPronunciationArray = getResources().getStringArray(R.array.IELTS_pronunciation);
//            String[] beginnerGrammarArray = getResources().getStringArray(R.array.IELTS_grammar);
//            String[] beginnerExampleArray1 = getResources().getStringArray(R.array.IELTS_example1);
//            String[] beginnerExampleArray2 = getResources().getStringArray(R.array.IELTS_example2);
//            String[] beginnerExampleArray3 = getResources().getStringArray(R.array.IELTS_example3);
//            String[] extraArray = new String[getResources().getStringArray(R.array.IELTS_words).length];
//
//            if(languageId == 1){
//
//                extraArray = getResources().getStringArray(R.array.beginner_spanish);
//            }
//            if(languageId == 2){
//
//
//                extraArray = getResources().getStringArray(R.array.beginner_hindi);
//            }
//            if(languageId == 3){
//
//                extraArray = getResources().getStringArray(R.array.beginner_bengali);
//
//            }
//
//
//            for(int i = 0 ; i < LearnedCount; i++){
//
//                getWords.add(new Word(beginnerWordArray[i],beginnerTranslationArray[i],extraArray[i],beginnerPronunciationArray[i],beginnerGrammarArray[i],beginnerExampleArray1[i], beginnerExampleArray2[i],beginnerExampleArray3[i],"beginner",0,i));
//
//            }

            getBeginnerWordData();



        }

        if(level.equalsIgnoreCase("intermediate")){


//            String[]  beginnerWordArray= getResources().getStringArray(R.array.TOEFL_words);
//            String[] beginnerTranslationArray = getResources().getStringArray(R.array.TOEFL_translation);
//            String[] beginnerPronunciationArray = getResources().getStringArray(R.array.TOEFL_pronunciation);
//            String[] beginnerGrammarArray = getResources().getStringArray(R.array.TOEFL_grammar);
//            String[] beginnerExampleArray1 = getResources().getStringArray(R.array.TOEFL_example1);
//            String[] beginnerExampleArray2 = getResources().getStringArray(R.array.TOEFL_example2);
//            String[] beginnerExampleArray3 = getResources().getStringArray(R.array.TOEFL_example3);
//            String[] extraArray = new String[getResources().getStringArray(R.array.TOEFL_words).length];
//
//            if(languageId == 1){
//
//                extraArray = getResources().getStringArray(R.array.intermediate_spanish);
//            }
//            if(languageId == 2){
//
//
//                extraArray = getResources().getStringArray(R.array.intermediate_hindi);
//            }
//            if(languageId == 3){
//
//                extraArray = getResources().getStringArray(R.array.intermediate_bengali);
//
//            }
//
//
//            for(int i = 0 ; i < LearnedCount; i++) {
//
//                getWords.add(new Word(beginnerWordArray[i], beginnerTranslationArray[i], extraArray[i], beginnerPronunciationArray[i], beginnerGrammarArray[i], beginnerExampleArray1[i], beginnerExampleArray2[i], beginnerExampleArray3[i], "beginner", 0, i));
//
//            }

            getIntermediateWordData();

        }


        if(level.equalsIgnoreCase("advance")){


//            String[]  beginnerWordArray= getResources().getStringArray(R.array.SAT_words);
//            String[] beginnerTranslationArray = getResources().getStringArray(R.array.SAT_translation);
//            String[] beginnerPronunciationArray = getResources().getStringArray(R.array.SAT_pronunciation);
//            String[] beginnerGrammarArray = getResources().getStringArray(R.array.SAT_grammar);
//            String[] beginnerExampleArray1 = getResources().getStringArray(R.array.SAT_example1);
//            String[] beginnerExampleArray2 = getResources().getStringArray(R.array.SAT_example2);
//            String[] beginnerExampleArray3 = getResources().getStringArray(R.array.SAT_example3);
//            String[] extraArray = new String[getResources().getStringArray(R.array.SAT_words).length];
//
//            if(languageId == 1){
//
//                extraArray = getResources().getStringArray(R.array.advance_spanish);
//            }
//            if(languageId == 2){
//
//
//                extraArray = getResources().getStringArray(R.array.advance_hindi);
//            }
//            if(languageId == 3){
//
//                extraArray = getResources().getStringArray(R.array.advance_bengali);
//
//            }
//
//
//            for(int i = 0 ; i < LearnedCount; i++) {
//
//                getWords.add(new Word(beginnerWordArray[i], beginnerTranslationArray[i], extraArray[i], beginnerPronunciationArray[i], beginnerGrammarArray[i], beginnerExampleArray1[i], beginnerExampleArray2[i], beginnerExampleArray3[i], "beginner", 0, i));
//
//            }

            getAdvanceWordData();

        }


        return getWords;

    }


    //---------------------------------------

    private void getBeginnerWordData(){

         fiveWords.clear();

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSbeginnerNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int SATbeginnerNumber = 0;
        int GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSbeginnerNumber = (int)getPercentageNumber(30d, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLbeginnerNumber = (int)getPercentageNumber(30d, TOEFLwordSize);

        }

        if(isSatChecked){

            SATbeginnerNumber = (int)getPercentageNumber(30d, SATwordSize);

        }

        if(isGreChecked){


            GREbeginnerNumber =(int) getPercentageNumber(30d, GREwordSize);

        }

        addIELTSwords(0,IELTSbeginnerNumber);
        addTOEFLwords(0,TOEFLbeginnerNumber);
        addSATwords(0,SATbeginnerNumber);
        addGREwords(0,GREbeginnerNumber);


    }

    private void getIntermediateWordData(){

        words.clear();

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSintermediateNumber = 0;
        int TOEFLintermediateNumber = 0;
        int SATintermediateNumber = 0;
        int GREintermediateNumber = 0;

        int IELTSbeginnerNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int SATbeginnerNumber = 0;
        int GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSintermediateNumber = (int)getPercentageNumber(40d, IELTSwordSize);
            IELTSbeginnerNumber = (int)getPercentageNumber(30d, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLintermediateNumber = (int) getPercentageNumber(40d, TOEFLwordSize);
            TOEFLbeginnerNumber = (int) getPercentageNumber(30d, TOEFLwordSize);

        }

        if(isSatChecked){

            SATintermediateNumber = (int) getPercentageNumber(40d, SATwordSize);
            SATbeginnerNumber = (int) getPercentageNumber(30d, SATwordSize);

        }

        if(isGreChecked){


            GREintermediateNumber = (int)getPercentageNumber(40d, GREwordSize);
            GREbeginnerNumber = (int) getPercentageNumber(30d, GREwordSize);

        }

        addIELTSwords(IELTSbeginnerNumber,IELTSbeginnerNumber+IELTSintermediateNumber);
        addTOEFLwords(TOEFLbeginnerNumber,TOEFLbeginnerNumber+TOEFLintermediateNumber);
        addSATwords(SATbeginnerNumber,SATbeginnerNumber+SATintermediateNumber);
        addGREwords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);

    }

    private void getAdvanceWordData(){

        words.clear();

        int IELTSwordSize = getResources().getStringArray(R.array.IELTS_words).length;
        int TOEFLwordSize = getResources().getStringArray(R.array.TOEFL_words).length;
        int SATwordSize = getResources().getStringArray(R.array.SAT_words).length;
        int GREwordSize = getResources().getStringArray(R.array.GRE_words).length;

        int IELTSintermediateNumber = 0;
        int TOEFLintermediateNumber = 0;
        int SATintermediateNumber = 0;
        int GREintermediateNumber = 0;

        int IELTSbeginnerNumber = 0;
        int TOEFLbeginnerNumber = 0;
        int SATbeginnerNumber = 0;
        int GREbeginnerNumber = 0;




        if(isIeltsChecked){
            IELTSintermediateNumber = (int)getPercentageNumber(40d, IELTSwordSize);
            IELTSbeginnerNumber = (int)getPercentageNumber(30d, IELTSwordSize);

        }

        if(isToeflChecked){

            TOEFLintermediateNumber =(int) getPercentageNumber(40d, TOEFLwordSize);
            TOEFLbeginnerNumber =(int) getPercentageNumber(30d, TOEFLwordSize);

        }

        if(isSatChecked){

            SATintermediateNumber =(int) getPercentageNumber(40d, SATwordSize);
            SATbeginnerNumber =(int) getPercentageNumber(30d, SATwordSize);

        }

        if(isGreChecked){


            GREintermediateNumber =(int) getPercentageNumber(40d, GREwordSize);
            GREbeginnerNumber = (int) getPercentageNumber(30d, GREwordSize);

        }

        addIELTSwords(IELTSbeginnerNumber+IELTSintermediateNumber,IELTSwordSize);
        addTOEFLwords(TOEFLbeginnerNumber+TOEFLintermediateNumber,TOEFLwordSize);
        addSATwords(SATbeginnerNumber+SATintermediateNumber,SATwordSize);
        addGREwords(GREbeginnerNumber+GREintermediateNumber,GREwordSize);

    }

    private double getPercentageNumber(double percentage, double number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return beginnerNum;

    }

    private void addingLearnedDatabase() {

        Cursor beginnerRes = IELTSdatabase.getData();
        Cursor TOEFLres = TOEFLdatabase.getData();
        Cursor SATres = SATdatabase.getData();
        Cursor GREres = GREdatabase.getData();

        while (beginnerRes.moveToNext()) {

            IELTSlearnedDatabase.add(beginnerRes.getString(3));

        }

        while (TOEFLres.moveToNext()) {

            TOEFLlearnedDatabase.add(TOEFLres.getString(3));

        }

        while (SATres.moveToNext()) {

            SATlearnedDatabase.add(SATres.getString(3));

        }

        while (GREres.moveToNext()) {

            GRElearnedDatabase.add(GREres.getString(3));

        }


    }

    private  void addIELTSwords(int startPoint,int IELTSbeginnerNumber){





        if(isIeltsChecked){

            for(int i = (int) startPoint; i < IELTSbeginnerNumber; i++){


                if( IELTSlearnedDatabase.get(i).equalsIgnoreCase("true")){


                    Word newWord = new Word(IELTSwordArray[i], IELTStranslationArray[i],"", IELTSpronunArray[i], IELTSgrammarArray[i], IELTSexample1array[i], IELTSexample2Array[i], IELTSexample3Array[i],IELTSvocabularyType[i],IELTSposition[i], IELTSlearnedDatabase.get(i),ieltsFavPosition.get(i));


                    if(!secondLanguage.equalsIgnoreCase("english")){
                //        Toast.makeText(this,"added to newWord",Toast.LENGTH_LONG).show();

                        newWord.setWordSL(IELTSwordsArraySL[i]);
                        newWord.setTranslationSL(IELTStranslationArraySL[i]);
                        newWord.setExample1SL(IELTSexample1arraySL[i]);
                        newWord.setExample2SL(IELTSexample2ArraySL[i]);
                        newWord.setExample3SL(IELTSexample3ArraySL[i]);


                    }else {

                        newWord.setWordSL("");
                        newWord.setTranslationSL("");
                        newWord.setExample1SL("");
                        newWord.setExample2SL("");
                        newWord.setExample3SL("");

                    }


                    fiveWords.add(newWord);
                }

            }

        }


    }

    private  void addTOEFLwords(int startPoint, int TOEFLbeginnerNumber){

//        Toast.makeText(this,IELTSexample1arraySL[0]+": "+secondLanguage,Toast.LENGTH_LONG).show();

        if(isToeflChecked){


            for(int i = (int) startPoint; i < TOEFLbeginnerNumber; i++){


                if( TOEFLlearnedDatabase.get(i).equalsIgnoreCase("true")) {


                    Word newWord = new Word(TOEFLwordArray[i], TOEFLtranslationArray[i], "", TOEFLpronunArray[i], TOEFLgrammarArray[i], TOEFLexample1array[i], TOEFLexample2Array[i], TOEFLexample3Array[i], TOEFLvocabularyType[i], TOEFLposition[i], TOEFLlearnedDatabase.get(i),toeflFavPosition.get(i));

                    if(!secondLanguage.equalsIgnoreCase("english")){

                        newWord.setWordSL(TOEFLwordArraySL[i]);
                        newWord.setTranslationSL(TOEFLtranslationArraySL[i]);
                        newWord.setExample1SL(TOEFLexample1ArraySL[i]);
                        newWord.setExample2SL(TOEFLexample2ArraySL[i]);
                        newWord.setExample3SL(TOEFLexample3ArraySL[i]);


                    }else {

                        newWord.setWordSL("");
                        newWord.setTranslationSL("");
                        newWord.setExample1SL("");
                        newWord.setExample2SL("");
                        newWord.setExample3SL("");

                    }


                    fiveWords.add(newWord);



                }
            }
        }



    }

    private void addSATwords (int startPoint ,int SATbeginnerNumber){


       // Toast.makeText(this,IELTSexample1arraySL[0]+": "+secondLanguage,Toast.LENGTH_LONG).show();

        if(isSatChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){


                if( SATlearnedDatabase.get(i).equalsIgnoreCase("true")) {

                    Word newWord = new Word(SATwordArray[i], SATtranslationArray[i], "", SATpronunArray[i], SATgrammarArray[i], SATexample1array[i], SATexample2Array[i], SATexample3Array[i], SATvocabularyType[i], SATposition[i], SATlearnedDatabase.get(i),satFavPosition.get(i));


                    if(!secondLanguage.equalsIgnoreCase("english")){

                        newWord.setWordSL(SATwordArraySL[i]);
                        newWord.setTranslationSL(SATtranslationArraySL[i]);
                        newWord.setExample1SL(SATexample1ArraySL[i]);
                        newWord.setExample2SL(SATexample2ArraySL[i]);
                        newWord.setExample3SL(SATexample3ArraySL[i]);


                    }else {

                        newWord.setWordSL("");
                        newWord.setTranslationSL("");
                        newWord.setExample1SL("");
                        newWord.setExample2SL("");
                        newWord.setExample3SL("");

                    }


                    fiveWords.add(newWord);

                }

            }

        }





    }

    private void addGREwords (int startPoint ,int SATbeginnerNumber){


     //   Toast.makeText(this,IELTSexample1arraySL[0]+": "+secondLanguage,Toast.LENGTH_LONG).show();
        if(isGreChecked){

            for(int i = (int) startPoint; i < SATbeginnerNumber; i++){



                if( GRElearnedDatabase.get(i).equalsIgnoreCase("true")) {

                    Word newWord = new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], GRElearnedDatabase.get(i),greFavPosition.get(i));

                    if(!secondLanguage.equalsIgnoreCase("english")){

                        newWord.setWordSL(GREwordArraySL[i]);
                        newWord.setTranslationSL(GREtranslationArraySL[i]);
                        newWord.setExample1SL(GREexample1ArraySL[i]);
                        newWord.setExample2SL(GREexample2ArraySL[i]);
                        newWord.setExample3SL(GREexample3ArraySL[i]);


                    }else {

                        newWord.setWordSL("");
                        newWord.setTranslationSL("");
                        newWord.setExample1SL("");
                        newWord.setExample2SL("");
                        newWord.setExample3SL("");

                    }


                    fiveWords.add(newWord);

                }

            }

        }





    }

    private void gettingResources()  {



        IELTSwordArray = getResources().getStringArray(R.array.IELTS_words);
        IELTStranslationArray = getResources().getStringArray(R.array.IELTS_translation);
        IELTSgrammarArray = getResources().getStringArray(R.array.IELTS_grammar);
        IELTSpronunArray = getResources().getStringArray(R.array.IELTS_pronunciation);
        IELTSexample1array = getResources().getStringArray(R.array.IELTS_example1);
        IELTSexample2Array = getResources().getStringArray(R.array.IELTS_example2);
        IELTSexample3Array = getResources().getStringArray(R.array.IELTS_example3);
        IELTSvocabularyType = getResources().getStringArray(R.array.IELTS_level);
        IELTSposition = getResources().getIntArray(R.array.IELTS_position);


        TOEFLwordArray = getResources().getStringArray(R.array.TOEFL_words);
        TOEFLtranslationArray = getResources().getStringArray(R.array.TOEFL_translation);
        TOEFLgrammarArray = getResources().getStringArray(R.array.TOEFL_grammar);
        TOEFLpronunArray = getResources().getStringArray(R.array.TOEFL_pronunciation);
        TOEFLexample1array = getResources().getStringArray(R.array.TOEFL_example1);
        TOEFLexample2Array = getResources().getStringArray(R.array.TOEFL_example2);
        TOEFLexample3Array = getResources().getStringArray(R.array.TOEFL_example3);
        TOEFLvocabularyType = getResources().getStringArray(R.array.TOEFL_level);
        TOEFLposition = getResources().getIntArray(R.array.TOEFL_position);

        SATwordArray = getResources().getStringArray(R.array.SAT_words);
        SATtranslationArray = getResources().getStringArray(R.array.SAT_translation);
        SATgrammarArray = getResources().getStringArray(R.array.SAT_grammar);
        SATpronunArray = getResources().getStringArray(R.array.SAT_pronunciation);
        SATexample1array = getResources().getStringArray(R.array.SAT_example1);
        SATexample2Array = getResources().getStringArray(R.array.SAT_example2);
        SATexample3Array = getResources().getStringArray(R.array.SAT_example3);
        SATvocabularyType = getResources().getStringArray(R.array.SAT_level);
        SATposition = getResources().getIntArray(R.array.SAT_position);


        GREwordArray = getResources().getStringArray(R.array.GRE_words);
        GREtranslationArray = getResources().getStringArray(R.array.GRE_translation);
        GREgrammarArray = getResources().getStringArray(R.array.GRE_grammar);
        GREpronunArray = getResources().getStringArray(R.array.GRE_pronunciation);
        GREexample1array = getResources().getStringArray(R.array.GRE_example1);
        GREexample2array = getResources().getStringArray(R.array.GRE_example2);
        GREexample3Array = getResources().getStringArray(R.array.GRE_example3);
        GREvocabularyType = getResources().getStringArray(R.array.GRE_level);
        GREposition = getResources().getIntArray(R.array.GRE_position);

        secondLanguage = sp.getString("secondlanguage","english");


        if(!secondLanguage.equalsIgnoreCase("english")){

            //This method initializes Spanish translation resources.
           // Toast.makeText(this,"Spanish translation init",Toast.LENGTH_LONG).show();
            addSpanishTranslation();
        }




        TOEFLdatabase = new TOEFLWordDatabase(this);
        SATdatabase = new SATWordDatabase(this);
        IELTSdatabase = new IELTSWordDatabase(this);
        GREdatabase = new GREWordDatabase(this);

        ieltsFavPosition = new ArrayList<>();
        toeflFavPosition = new ArrayList<>();
        satFavPosition = new ArrayList<>();
        greFavPosition = new ArrayList<>();

        getfavoriteDatabasePosition();


        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);

        IELTSlearnedDatabase = new ArrayList<>();
        TOEFLlearnedDatabase = new ArrayList<>();
        SATlearnedDatabase = new ArrayList<>();
        GRElearnedDatabase = new ArrayList<>();

    }

    private void getfavoriteDatabasePosition(){

        Cursor ieltsRes = IELTSdatabase.getData();
        Cursor toeflRes = TOEFLdatabase.getData();
        Cursor satRes = SATdatabase.getData();
        Cursor greRes = GREdatabase.getData();

        while (ieltsRes.moveToNext()){


            ieltsFavPosition.add(ieltsRes.getString(2));



        }

        while (toeflRes.moveToNext()){

            toeflFavPosition.add(toeflRes.getString(2));
        }

        while(satRes.moveToNext()){

            satFavPosition.add(satRes.getString(2));

        }

        while (greRes.moveToNext()){


            greFavPosition.add(greRes.getString(2));
        }






    }
    private Boolean showInterstitialAd(Boolean isAdShown){

        if(mPublisherInterstitialAd.isLoaded()){

            if(!isAdShown){

                if( cb != 1){

                    mPublisherInterstitialAd.show();
                }

                isAdShown = true;
            }

        }else {

            //Toast.makeText(this,"Ad is not loaded yet",Toast.LENGTH_SHORT).show();
            Practice.this.startActivity(new Intent(getApplicationContext(), PracticeFinished.class));
            Practice.this.finish();

        }

        return isAdShown;
    }



    private void addSpanishTranslation(){

        ///IELTS Translations----
        IELTSwordsArraySL = getResources().getStringArray(R.array.IELTS_words_sp);

        IELTStranslationArraySL = getResources().getStringArray(R.array.IELTS_translation_sp);
        IELTSexample1arraySL = getResources().getStringArray(R.array.IELTS_example1_sp);
        IELTSexample2ArraySL = getResources().getStringArray(R.array.IELTS_example2_sp);
        IELTSexample3ArraySL = getResources().getStringArray(R.array.IELTS_example3_sp);



        //TOEFL Second Language Arrays
        TOEFLwordArraySL = getResources().getStringArray(R.array.TOEFL_words_sp);
        TOEFLtranslationArraySL = getResources().getStringArray(R.array.TOEFL_translation_sp);
        TOEFLexample1ArraySL = getResources().getStringArray(R.array.TOEFL_example1_sp);
        TOEFLexample2ArraySL = getResources().getStringArray(R.array.TOEFL_example2_sp);
        TOEFLexample3ArraySL = getResources().getStringArray(R.array.TOEFL_example3_sp);

        // SAT second Language Arrays
        SATwordArraySL = getResources().getStringArray(R.array.SAT_words_sp);
        SATtranslationArraySL = getResources().getStringArray(R.array.SAT_translation_sp);
        SATexample1ArraySL = getResources().getStringArray(R.array.SAT_example1_sp);
        SATexample2ArraySL = getResources().getStringArray(R.array.SAT_example2_sp);
        SATexample3ArraySL = getResources().getStringArray(R.array.SAT_example3_sp);

        // GRE Second Language Arrays
        GREwordArraySL = getResources().getStringArray(R.array.GRE_words_sp);
        GREtranslationArraySL = getResources().getStringArray(R.array.GRE_translation_sp);
        GREexample1ArraySL = getResources().getStringArray(R.array.GRE_example1_sp);
        GREexample2ArraySL = getResources().getStringArray(R.array.GRE_example2_sp);
        GREexample3ArraySL = getResources().getStringArray(R.array.GRE_example3_sp);

    }


    private void initializeAds(){
        String trialStatus = checkTrialStatus();
        boolean isAdShow = getIsAdShow();
        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        if(isAdShow){


          if(BuildConfig.FLAVOR.equalsIgnoreCase("free") || BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){
                 mPublisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());
                 mPublisherInterstitialAd.setAdListener( new AdListener(){

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Practice.this.startActivity(new Intent(getApplicationContext(), PracticeFinished.class));
                Practice.this.finish();

                Toast.makeText(getApplicationContext(),"Sorry for the ad :(",Toast.LENGTH_SHORT).show();
            }

        });
          }


         }




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
        Toast.makeText(this,"Hello there, this is a callback",Toast.LENGTH_LONG).show();
    }

    private void applyWrongColor(){
        topBackground.setBackgroundColor(getResources().getColor(R.color.red));
        fab.setColorNormal(getResources().getColor(R.color.red));
        progress1.setProgressColor(getResources().getColor(R.color.red));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.red));
        trainCircle1.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
        trainCircle2.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
        trainCircle3.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
        trainCircle4.setBackground(ContextCompat.getDrawable(this,R.drawable.red_bottom_bar_dot));
    }
    private void applyCorrectColor(){
        topBackground.setBackgroundColor(getResources().getColor(R.color.green));
        fab.setColorNormal(getResources().getColor(R.color.green));
        progress1.setProgressColor(getResources().getColor(R.color.green));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        trainCircle1.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
        trainCircle2.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
        trainCircle3.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
        trainCircle4.setBackground(ContextCompat.getDrawable(this,R.drawable.green_bottom_bar_dot));
    }
}
