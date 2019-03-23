package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.ChooseLanguageAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.LanguageAdapter;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import mehdi.sakout.fancybuttons.FancyButton;

public class ChooseLanguageActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    FancyButton skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_language);
        startActivity(new Intent(this, ChooseVocabulary.class));
        skip = findViewById(R.id.skip);
        skip.setOnClickListener(this);


        recyclerView = findViewById(R.id.choose_language_setting);
        adapter = new ChooseLanguageAdapter();
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

//        if( view == skip){
//            SharedPreferences sp = getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
//
//            sp.edit().putInt("language",0).apply();

            startActivity(new Intent(this, ChooseVocabulary.class));


//        }

    }
}
