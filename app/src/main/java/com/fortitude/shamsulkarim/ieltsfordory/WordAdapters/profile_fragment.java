package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;



import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.notification.AlarmReceiver;
import com.fortitude.shamsulkarim.ieltsfordory.notification.LocalData;
import com.fortitude.shamsulkarim.ieltsfordory.notification.NotificationScheduler;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import az.plainpie.PieView;
import de.cketti.mailto.EmailIntentBuilder;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class profile_fragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private FancyButton setReminder;
    private SharedPreferences sp;
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase satWordDatabase;
    private GREWordDatabase greWordDatabase;

    //Spinner
    private Spinner wps, rps,imageQualitySpinner;
    private int wordsPerSession,repeatationPerSession;

    private CardView rateCardView, bugReport, fbCard,instagramCard;
    private TextView leftTextView,learnedTextView;

    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;


    /// notification
    private String TAG = "RemindMe";
    private LocalData localData;
    private SwitchCompat reminderSwitch;
    private TextView tvTime;
    private int hour, min, totalWordCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        toolbar = v.findViewById(R.id.profile_toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();



        //----------------------------

        initialization(v);
        setProgress();

        //-------------------------------


        try{
            activity.setSupportActionBar(toolbar);
        }catch (NullPointerException i){

        }

        return v;


    }

    private void initialization(View v){

        sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
        isSatChecked =   sp.getBoolean("isSATActive", true);
        isGreChecked =   sp.getBoolean("isGREActive",true);

        ieltsWordDatabase = new IELTSWordDatabase(v.getContext());
        satWordDatabase = new SATWordDatabase(v.getContext());
        toeflWordDatabase = new TOEFLWordDatabase(v.getContext());
        greWordDatabase = new GREWordDatabase(v.getContext());

        localData = new LocalData(getContext());
        reminderSwitch = v.findViewById(R.id.alarm_switch);
        tvTime = v.findViewById(R.id.alarm_time);
        hour = localData.get_hour();
        min = localData.get_min();
        setReminder = v.findViewById(R.id.set_alarm);
        tvTime.setText(getFormatedTime(hour, min));
        reminderSwitch.setChecked(localData.getReminderStatus());
        leftTextView = v.findViewById(R.id.profile_left_text);
        learnedTextView = v.findViewById(R.id.profile_learned_text);

        rateCardView = v.findViewById(R.id.profile_rate_card);
        bugReport = v.findViewById(R.id.bug_report_card);
        fbCard = v.findViewById(R.id.fb_card);
        instagramCard = v.findViewById(R.id.instagram_card);
        fbCard.setOnClickListener(this);
        instagramCard.setOnClickListener(this);


        wps = (Spinner)v.findViewById(R.id.profile_wps_spinner);
        rps = (Spinner)v.findViewById(R.id.profile_rps_spinner);

        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",5).apply();
            wordsPerSession = 5;
        }else {

            wordsPerSession = sp.getInt("wordsPerSession",5);

        }
        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",5).apply();
            repeatationPerSession = 5;
        }else {

            repeatationPerSession = sp.getInt("repeatationPerSession",5);

        }



        if(repeatationPerSession == 5){


            rps.setSelection(0);
        }
        else if(repeatationPerSession == 4){


            rps.setSelection(1);
        }else {

            rps.setSelection(2);

        }

        if(wordsPerSession == 5){


            wps.setSelection(0);
        }
        else if(wordsPerSession == 4){


            wps.setSelection(1);
        }else {

            wps.setSelection(2);

        }



        wps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {




                if(i == 0){


                    wordsPerSession = 5;
                    sp.edit().putInt("wordsPerSession",wordsPerSession).apply();


                }if( i == 1){


                    wordsPerSession = 4;

                    sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
                }
                if(i == 2) {


                    wordsPerSession = 3;
                    sp.edit().putInt("wordsPerSession",wordsPerSession).apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {




                if(i == 0){


                    repeatationPerSession = 5;
                    sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();


                }if( i == 1){


                    repeatationPerSession = 4;
                    sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
                }if( i == 2){


                    repeatationPerSession = 3;
                    sp.edit().putInt("repeatationPerSession",repeatationPerSession).apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(isIeltsChecked){
            totalWordCount = getResources().getStringArray(R.array.IELTS_words).length;


        }

        if(isToeflChecked){

            totalWordCount = totalWordCount + getResources().getStringArray(R.array.TOEFL_words).length;

        }

        if(isSatChecked){

            totalWordCount = totalWordCount + getResources().getStringArray(R.array.SAT_words).length;


        }

        if( isGreChecked){

            totalWordCount = totalWordCount + getResources().getStringArray(R.array.GRE_words).length;

        }






        rateCardView.setOnClickListener(this);
        bugReport.setOnClickListener(this);


        if (!localData.getReminderStatus())
            setReminder.setAlpha(0.4f);


        //-------------SET DEFAULT alaarm

        if(!sp.contains("defaultAlarm")){

            reminderSwitch.setChecked(true);
            NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, 18, 0);
            tvTime.setText(getFormatedTime(hour, min));
            setReminder.setAlpha(1f);
        }


        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localData.setReminderStatus(isChecked);
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: true");
                    NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, localData.get_hour(), localData.get_min());
                    setReminder.setAlpha(1f);
                } else {
                    Log.d(TAG, "onCheckedChanged: false");
                    NotificationScheduler.cancelReminder(getContext(), AlarmReceiver.class);
                    setReminder.setAlpha(0.4f);
                }

            }
        });


        setReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localData.getReminderStatus())
                    showTimePickerDialog(localData.get_hour(), localData.get_min());
            }
        });






    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        try{
            getActivity().getMenuInflater().inflate(R.menu.profile_toolbar_menus,menu);

        }catch (NullPointerException i ){

        }






    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case R.id.profile_menu_settings:

                try{

                    getActivity().startActivity(new Intent(getActivity().getBaseContext(), NewSettingActivity.class));

                }catch (NullPointerException i){

                }


                break;

            case R.id.menu_item_share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;
        }


        return super.onOptionsItemSelected(item);


    }

    public void setProgress(){

        int learned = getLearnedWords();
        int left =totalWordCount - learned;
        int percentage = (learned*100)/totalWordCount;
        leftTextView.setText(left+" words left");
        learnedTextView.setText(learned+" words learned");

    }



    private int getLearnedWords() {
        int learnedCount = 0;


        Cursor ieltsRes = ieltsWordDatabase.getData();
        Cursor toeflRes = toeflWordDatabase.getData();
        Cursor satRes = satWordDatabase.getData();
        Cursor greRes = greWordDatabase.getData();



        while (ieltsRes.moveToNext()) {

            if (ieltsRes.getString(3).equalsIgnoreCase("true")) {

                if(isIeltsChecked){

                    learnedCount++;
                }



            }


        }

        while (toeflRes.moveToNext()) {

            if (toeflRes.getString(3).equalsIgnoreCase("true")) {

                if(isToeflChecked){

                    learnedCount++;
                }

            }
        }

        while (satRes.moveToNext()) {

            if (satRes.getString(3).equalsIgnoreCase("true")) {

                if(isSatChecked){

                    learnedCount++;
                }


            }
        }


        while (greRes.moveToNext()){

            if(greRes.getString(3).equalsIgnoreCase("true")){


                if(isGreChecked){

                    learnedCount++;
                }


            }

        }



        return learnedCount;
    }

    @Override
    public void onClick(View v) {

        if(v == bugReport){

            try{
                EmailIntentBuilder.from(getActivity().getBaseContext())
                        .to("fortitudedevs@gmail.com")
                        .subject("Fortitude Vocabulary Builder")
                        .body("")
                        .start();
            }catch (NullPointerException i){

            }



        }

        if( v == rateCardView){

            Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);
        }

        if(v == fbCard){

            Uri appUrl = Uri.parse("https://www.facebook.com/FortitudeVocab/notifications/");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);

        }

        if( v == instagramCard){

            Uri appUrl = Uri.parse("https://www.instagram.com/fortitudevocab/");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);
        }

    }



    /// SET NOTIFICATION

    private void showTimePickerDialog(int h, int m) {



        TimePickerDialog builder = new TimePickerDialog(getContext(), R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        Log.d(TAG, "onTimeSet: hour " + hour);
                        Log.d(TAG, "onTimeSet: min " + min);
                        localData.set_hour(hour);
                        localData.set_min(min);
                        tvTime.setText(getFormatedTime(hour, min));
                        NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, localData.get_hour(), localData.get_min());


                    }
                }, h, m, false);


        builder.show();

    }

    public String getFormatedTime(int h, int m) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h + ":" + m;
        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }
}
