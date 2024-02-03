package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.DatabaseInitializer;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.TaskListener;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;


public class SplashScreen extends AppCompatActivity {

    private TextView progressText;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        Drawable background = ContextCompat.getDrawable(this,R.drawable.gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(android.R.color.transparent));
        window.setNavigationBarColor(getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);


        progressText = findViewById(R.id.textView20);


        //----------------------------------------

        // This method initializes default settings
        initialize();

        //---------------------------------------

        // This methods Initializes database at the first launch of the app
        // after first launch it sends you to MainActivity.java activity.

        Handler handler = new Handler();

        handler.postDelayed(this::initializeOrMainActivity,1500L);




        //initializeOrMainActivity();

        //-----------------------------------------------------------------

    }

    private void initializeOrMainActivity(){


        if(sp.contains("home")){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {


            createDatabase();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            sp.edit().putInt("DarkMode",0).apply();


        }

    }


    // This methods initializes the databases and when it

    private void createDatabase(){

       // DatabaseAsyncTask databaseTask = new DatabaseAsyncTask();
       // databaseTask.execute(10);


        DatabaseInitializer dbInitializer = new DatabaseInitializer(this, new TaskListener() {
            @Override
            public void onComplete() {

                if(BuildConfig.FLAVOR.equalsIgnoreCase("pro")){

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                }else {

                    startActivity(new Intent(getApplicationContext(), StartTrial.class));
                    finish();
                }




            }

            @Override
            public void onProgress() {

                progressText.setText("We are preparing the app...");
            }

            @Override
            public void onFailed() {

            }
        });

        dbInitializer.execute();


    }

    private void initialize(){
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        //Default Settings Initializtion

        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",3).apply();



        }
        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",3).apply();


        }
        // Default Dark themes
        setupAppTheme();




        ProgressBar progressBar = findViewById(R.id.spin_splash_screen);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);





        sp.edit().putString("advanceFavNum","").apply();
        sp.edit().putString("advanceLearnedNum","0").apply();
        sp.edit().putString("beginnerFavNum","").apply();
        sp.edit().putString("beginnerLearnedNum","0").apply();
        sp.edit().putString("intermediateFavNum", "").apply();
        sp.edit().putString("intermediateLearnedNum","0").apply();

        if(!sp.contains("skip")){


            sp.edit().putBoolean("skip",false).apply();
        }


        if(!sp.contains("favoriteCountProfile")){

            sp.edit().putInt("favoriteCountProfile",0).apply();


        }

        setupDefaultVocabulary();
        setupDefaultLanguage();
        //--------------------------------------------------------------





    }

    private void setupAppTheme(){

        if(!sp.contains("DarkMode")){

            sp.edit().putInt("DarkMode",0).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {

            int darkMode = sp.getInt("DarkMode",0);

            switch(darkMode) {

                case 1:

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;

                case 2:

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;

                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }


        }
    }

    private void setupDefaultVocabulary(){

        if(!sp.contains("home")){

            sp.edit().putBoolean("isIELTSActive",true).apply();
            sp.edit().putBoolean("isTOEFLActive",true).apply();
            sp.edit().putBoolean("isSATActive",true).apply();
            sp.edit().putBoolean("isGREActive",true).apply();



        }
    }

    private void setupDefaultLanguage(){

        if(!sp.contains("home")){


            sp.edit().putString("secondlanguage","english").apply();
        }

    }
}
