package com.fortitude.shamsulkarim.ieltsfordory.ui.practice;

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

import androidx.activity.OnBackPressedCallback;
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
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.NewTrainRecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.NewTrain;
import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
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

    private VocabularyRepository repository;

    private List<Word> fiveWords;
    private SharedPreferences sp;
    private CardView wordCard, answerCard1, answerCard2, answerCard3, answerCard4;
    private TextView wordView, answerView1, answerView2, answerView3, answerView4;
    private FloatingActionButton fab;
    private FancyButton speak;
    private View trainCircle1, trainCircle2, trainCircle3, trainCircle4;
    private RoundCornerProgressBar progress1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private int showCycle = 0;
    private int quizCycle = 0;
    private TextToSpeech tts;
    private int FIVE_WORD_SIZE = 0;
    private boolean IsWrongAnswer = true;
    private int lastMistake = 13;
    private int mistakes = 0;
    private int repeatPerSession = 5;
    private int totalCycle = 0;
    private int languageId;
    private boolean soundState = true;
    private int favoriteWrongs,totalCorrects;
    private int progressCount = 0;
    private boolean  alreadyclicked = true;
    private boolean progress = false;

    ProgressBar progressBar, adLoading;

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
        window.setStatusBarColor(getColor(R.color.colorPrimary));

        repository = new VocabularyRepository(this);

        initialization();
        addingNewWords();
        showWords(showCycle);

        sp.edit().putInt("favoriteWordCount",fiveWords.size()).apply();
        languageId = sp.getInt("language",0);
        soundState = sp.getBoolean("soundState",true);
        totalCorrects = sp.getInt("totalCorrects",0);


        if(!sp.contains("favoriteWrongs")){

            sp.edit().putInt("favoriteWrongs",0).apply();


        }else {

            favoriteWrongs = sp.getInt("favoriteWrongs",0);

        }



        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                new LovelyStandardDialog(Practice.this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_leave)
                        .setTitle("Do you want to leave this session, " + sp.getString("userName", "Boo") + "?")
                        .setMessage("Leaving this session will make you lose your progress")
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Practice.this.startActivity(new Intent(Practice.this, MainActivity.class));
                                Practice.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();

            }
        });


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



                answerCard1.setVisibility(View.VISIBLE);
                answerCard2.setVisibility(View.VISIBLE);
                answerCard3.setVisibility(View.VISIBLE);
                answerCard4.setVisibility(View.VISIBLE);
                quizWords(quizCycle, v);

            }
            showWords(showCycle);
            alreadyclicked = false;
        }




    }


    @Override
    public void onClick(View v) {

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
            cycleQuiz();
        }


    }
    private void cycleQuiz(){


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
        List<Word> wordsAnswers = new ArrayList<>(fiveWords);
        if( quizCycle == FIVE_WORD_SIZE ){

            quizCycle = 0;
        }


        if( this.quizCycle <= (FIVE_WORD_SIZE*repeatPerSession)-1){


            Word word = fiveWords.get(this.quizCycle);

            Collections.shuffle(wordsAnswers);
            for (int i = 0; i < 4;i++) {

                answers.add(wordsAnswers.get(i));

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
                answer = fiveWords.get(quizCycle).getTranslation();
            }else {
                answer = fiveWords.get(quizCycle).getExtra();

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

                    cycleQuiz();



                }else {
                    applyWrongColor();

                    if(soundState){

                        incorrectAudio.start();
                    }
                //    wordViewMiddle.setText("");
                    speak.setVisibility(View.INVISIBLE);
                    wrongAnswerAnimation();
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






            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Todo

                    hideViews();
                    adLoading.setVisibility(View.VISIBLE);
                   // showInterstitialAd();

                    Practice.this.startActivity(new Intent(getApplicationContext(), PracticeFinished.class));
                    Practice.this.finish();
                }
            },200L);



        }


    }

    // --------- Initializing

    private void initialization(){


        topBackground = findViewById(R.id.top_background);
        recyclerView = findViewById(R.id.train_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        // if true this will set showCycle to maximum and skip definition session


        sp.getInt("wordsPerSession", 5);
        repeatPerSession = sp.getInt("repeatationPerSession",5);


        tts = new TextToSpeech(this, this);
        wordCard = findViewById(R.id.wordCard);
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
       // wordViewMiddle = (TextView)findViewById(R.id.train_word_middle);
        fab = findViewById(R.id.train_fab);
        fab.setMax(5);
        progress1 = findViewById(R.id.progress_1);
        trainCircle1 = findViewById(R.id.train_circle1);
        trainCircle2 = findViewById(R.id.train_circle2);
        trainCircle3 = findViewById(R.id.train_circle3);
        trainCircle4 = findViewById(R.id.train_circle4);



        progress1.setProgressColor(getColor(R.color.colorPrimary));
        progress1.setSecondaryProgressColor(getColor(R.color.colorPrimaryDark));
        progress1.setProgressBackgroundColor(getColor(R.color.primary_text_color_white));
        fab.setColorNormal(getColor(R.color.colorPrimary));
        fab.setColorPressed(getColor(R.color.colorPrimaryDark));
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
        String practice = sp.getString("practice","learned");

        if(practice.equalsIgnoreCase("favorite")){


            fiveWords.addAll(repository.getFavoriteWords());

            for(int i = 0; i < fiveWords.size(); i++){

                fiveWords.get(i).setSeen(false);


            }


                showCycle = fiveWords.size();

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

    public void getLearnedWords(){

        String level = sp.getString("level","beginner");
        sp.getInt("language",0);
        sp.getInt(level,0);

        if(level.equalsIgnoreCase("beginner")) {

            fiveWords.addAll(repository.getBeginnerLearnedWords());
            //getBeginnerWordData();
        }

        if(level.equalsIgnoreCase("intermediate")){

            fiveWords.addAll(repository.getIntermediateLearnedWords());
           // getIntermediateWordData();
        }


        if(level.equalsIgnoreCase("advance")){

            fiveWords.addAll(repository.getAdvanceLearnedWords());

            //getAdvanceWordData();

        }


    }


    //---------------------------------------






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
