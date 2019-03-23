package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

public class NoWordLeft extends AppCompatActivity implements View.OnClickListener{

    TextView congratulation, completedSection, soProud;
    ImageView noWordImage;
    SharedPreferences sp;
    FancyButton home, reset;
    String level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_no_word_left);

        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        level = sp.getString("level", "beginner");

        initialization();


    }

    private  void initialization(){
        Typeface comfortaRegular = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa-Regular.ttf");
        Typeface comfoftaBold = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa-Bold.ttf");

        congratulation = (TextView)findViewById(R.id.congratulation);
        completedSection = (TextView)findViewById(R.id.completed_section);
        soProud = (TextView)findViewById(R.id.so_proud);
        noWordImage = (ImageView) findViewById(R.id.no_word_image);
        home = (FancyButton)findViewById(R.id.no_word_home);
        reset = (FancyButton)findViewById(R.id.reset);

        congratulation.setTypeface(comfoftaBold);
        completedSection.setTypeface(comfortaRegular);
        soProud.setTypeface(comfortaRegular);

        if(level.equalsIgnoreCase("beginner")){
            home.setTextColor(getResources().getColor(R.color.beginnerP));
            home.setBorderColor(getResources().getColor(R.color.beginnerP));
            reset.setBorderColor(getResources().getColor(R.color.beginnerP));
            reset.setTextColor(getResources().getColor(R.color.beginnerP));
            congratulation.setTextColor(getResources().getColor(R.color.beginnerP));

            noWordImage.setImageResource(R.drawable.beginner_no_words);

        }

        if(level.equalsIgnoreCase("intermediate")){
            home.setTextColor(getResources().getColor(R.color.intermedateP));
            home.setBorderColor(getResources().getColor(R.color.intermedateP));
            reset.setBorderColor(getResources().getColor(R.color.intermedateP));
            reset.setTextColor(getResources().getColor(R.color.intermedateP));
            congratulation.setTextColor(getResources().getColor(R.color.intermedateP));

            noWordImage.setImageResource(R.drawable.intermediate_no_words);

        }

        if(level.equalsIgnoreCase("advance")){

            home.setTextColor(getResources().getColor(R.color.advanceP));
            home.setBorderColor(getResources().getColor(R.color.advanceP));
            reset.setBorderColor(getResources().getColor(R.color.advanceP));
            reset.setTextColor(getResources().getColor(R.color.advanceP));
            noWordImage.setImageResource(R.drawable.advance_no_words);
            congratulation.setTextColor(getResources().getColor(R.color.advanceP));

        }

    }



    public void home_nomorewords(View view){
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
        this.finish();


    }


    public void reset(View view){


        sp.edit().putInt(level,0).apply();

        Intent intent = new Intent(this, StartTrainingActivity.class);
        this.startActivity(intent);
        this.finish();




    }

    @Override
    public void onClick(View v) {

    }
}
