package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

public class NoWordLeft extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sp;
    private String level;
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

        TextView congratulation = findViewById(R.id.congratulation);
        TextView completedSection = findViewById(R.id.completed_section);
        TextView soProud = findViewById(R.id.so_proud);
        ImageView noWordImage = findViewById(R.id.no_word_image);
        FancyButton home = findViewById(R.id.no_word_home);
        FancyButton reset = findViewById(R.id.reset);

        congratulation.setTypeface(comfoftaBold);
        completedSection.setTypeface(comfortaRegular);
        soProud.setTypeface(comfortaRegular);

        if(level.equalsIgnoreCase("beginner")){
            home.setTextColor(getColor(R.color.beginnerP));
            home.setBorderColor(getColor(R.color.beginnerP));
            reset.setBorderColor(getColor(R.color.beginnerP));
            reset.setTextColor(getColor(R.color.beginnerP));
            congratulation.setTextColor(getColor(R.color.beginnerP));

            noWordImage.setImageResource(R.drawable.beginner_no_words);

        }

        if(level.equalsIgnoreCase("intermediate")){
            home.setTextColor(getColor(R.color.intermedateP));
            home.setBorderColor(getColor(R.color.intermedateP));
            reset.setBorderColor(getColor(R.color.intermedateP));
            reset.setTextColor(getColor(R.color.intermedateP));
            congratulation.setTextColor(getColor(R.color.intermedateP));

            noWordImage.setImageResource(R.drawable.intermediate_no_words);

        }

        if(level.equalsIgnoreCase("advance")){

            home.setTextColor(getColor(R.color.advanceP));
            home.setBorderColor(getColor(R.color.advanceP));
            reset.setBorderColor(getColor(R.color.advanceP));
            reset.setTextColor(getColor(R.color.advanceP));
            noWordImage.setImageResource(R.drawable.advance_no_words);
            congratulation.setTextColor(getColor(R.color.advanceP));

        }

    }



    public void home_nomorewords(View view){
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
        this.finish();


    }


    public void reset(View view){


        sp.edit().putInt(level,0).apply();

        Intent intent = new Intent(this, PretrainActivity.class);
        this.startActivity(intent);
        this.finish();




    }

    @Override
    public void onClick(View v) {

    }
}
