package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.PretrainActivity;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Calendar;
import java.util.Date;

import az.plainpie.PieView;

/**
 * Created by Shamsul Karim on 13-Dec-16.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {


    private VocabularyRepository repository;

    // UI
    private TextView trialStatusTextView;
    private CardView advanceCard,intermediateCard,beginnerCard;
    private PieView advancePie, intermediatePie, beginnerPie;
    private SharedPreferences sp;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment,container,false);
        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getContext().getColor(R.color.primary_background_color));

        repository = new VocabularyRepository(requireContext());

        if(!sp.contains("home")){
            sp.edit().putBoolean("home",true).apply();
        }

        advanceCard = v.findViewById(R.id.advance_card_home);
        intermediateCard = v.findViewById(R.id.intermediate_card_home);
        beginnerCard = v.findViewById(R.id.beginner_card_home);
        advanceCard.setPreventCornerOverlap(false);
        intermediateCard.setPreventCornerOverlap(false);
        beginnerCard.setPreventCornerOverlap(false);
        advancePie = v.findViewById(R.id.advance_pie);
        intermediatePie = v.findViewById(R.id.intermediate_pie);
        beginnerPie = v.findViewById(R.id.profile_pie_view);

        advanceCard.setOnClickListener(this);
        intermediateCard.setOnClickListener(this);
        beginnerCard.setOnClickListener(this);


        setProgress();


        freeVersion(v);

        return v;
    }

    private void freeVersion(View v){


        if(!BuildConfig.FLAVOR.equalsIgnoreCase("pro")){

            trialStatusTextView =v.findViewById(R.id.trial_status);

            checkTrialStatus();

        }





    }


    @Override
    public void onClick(View v) {


        if(v == advanceCard){

            sp.edit().putString("level","advance").apply();
            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));


        }

        if(v ==intermediateCard){

            sp.edit().putString("level","intermediate").apply();
            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));


        }

        if(v == beginnerCard){

            sp.edit().putString("level","beginner").apply();
            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));


        }
    }





    private void setProgress() {

        setBeginnerProgress();
        setIntermediateProgress();
        setAdvanceProgress();


    }

    private void setAdvanceProgress() {
        int learned;
        int percentage;
        int totalAdvCount = repository.getTotalAdvanceCount();

        learned = repository.getAdvanceLearnedCount();
        percentage = (learned*100)/totalAdvCount;

        advancePie.setMaxPercentage(100);
        advancePie.setPercentage(percentage);
    }

    private void setIntermediateProgress() {
        int percentage;
        int learned;
        int totalInterCount = repository.getTotalIntermediateCount();

        learned = repository.getIntermediateLearnedCount();
        percentage = (learned*100)/totalInterCount;

        intermediatePie.setMaxPercentage(100);
        intermediatePie.setPercentage(percentage);
    }

    private void setBeginnerProgress() {
        int totalBeginnerCount = repository.getTotalBeginnerCount();
        int learned = repository.getBeginnerLearnedCount();
        int percentage = (learned*100)/totalBeginnerCount;

        beginnerPie.setMaxPercentage(100);
        beginnerPie.setPercentage(percentage);
    }


    // Check Trial State
    private void checkTrialStatus(){

        String trialStatus;

        if(sp.contains("trial_end_date")){

            Date today = Calendar.getInstance().getTime();

            long endMillies = sp.getLong("trial_end_date",0) ;
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;

            //Toast.makeText(getContext(),"Days Left: "+ TimeUnit.DAYS.convert(leftMillies,TimeUnit.MILLISECONDS),Toast.LENGTH_SHORT).show();

            if(!sp.contains("purchase")){

                if(leftMillies >=0){

                    trialStatus = "active";

                }
                else {

                    showTrialFinished();

                    trialStatus = "ended";
                }
                trialStatusTextView.setText(getString(R.string.trial_mode,trialStatus));
            }else {

                trialStatus = "PREMIUM+";
                trialStatusTextView.setText(trialStatus);
                trialStatusTextView.setTextColor(getContext().getColor(R.color.green));
            }


        }





    }


    private void showTrialFinished(){

        if(!sp.contains("home_fragment_trail_end")){

            new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.red)
                    .setButtonsColorRes(R.color.secondary_text_color)
                    .setIcon(R.drawable.ic_information)
                    .setIconTintColor(getContext().getColor(R.color.primary_text_color_white))
                    .setNegativeButton("Continue with basic", new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Continue with Basic", Toast.LENGTH_SHORT).show();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            sp.edit().putInt("DarkMode",0).apply();
                        }
                    })
                    .setTitle(R.string.trial_ended)
                    .setMessage(R.string.trial_ended_description)
                    .setNeutralButtonColor(getContext().getColor(R.color.green))
                    .setNeutralButton("Upgrade", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            v.getContext().startActivity(new Intent(v.getContext(), PretrainActivity.class));
                            //Toast.makeText(getContext(), "positive clicked", Toast.LENGTH_SHORT).show();
                        }
                    })


                    .show();

            sp.edit().putBoolean("home_fragment_trail_end",true).apply();

        }

    }

}
