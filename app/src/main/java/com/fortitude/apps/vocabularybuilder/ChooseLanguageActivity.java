package com.fortitude.apps.vocabularybuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fortitude.shamsulkarim.ieltsfordory.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.SplashScreen;

import mehdi.sakout.fancybuttons.FancyButton;

public class ChooseLanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private FancyButton yes, no;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language2);

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == yes){

                sp.edit().putString("secondlanguage","spanish").apply();
                startActivity(new Intent(this, SplashScreen.class));
            }

        if( v == no){
            sp.edit().putString("secondlanguage","english").apply();
            startActivity(new Intent(this, SplashScreen.class));

        }

    }
}
