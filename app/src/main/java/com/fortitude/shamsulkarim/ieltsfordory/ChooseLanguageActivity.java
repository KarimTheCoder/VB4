package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;



import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.ChooseLanguageAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;

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

        // This code reports to Crashlytics of connection
        Boolean connected = ConnectivityHelper.isConnectedToNetwork(this);

        startActivity(new Intent(this, ChooseVocabulary.class));
        skip = findViewById(R.id.skip);
        skip.setOnClickListener(this);

//
//        recyclerView = findViewById(R.id.choose_language_setting);
//        adapter = new ChooseLanguageAdapter();
//        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {

            //startActivity(new Intent(this, MainActivity.class));

    }
}
