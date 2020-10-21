package com.fortitude.shamsulkarim.ieltsfordory.wordAdapters;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.notification.AlarmReceiver;
import com.fortitude.shamsulkarim.ieltsfordory.notification.LocalData;
import com.fortitude.shamsulkarim.ieltsfordory.notification.NotificationScheduler;
import com.kyleduo.switchbutton.SwitchButton;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import de.cketti.mailto.EmailIntentBuilder;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class profile_fragment extends Fragment implements View.OnClickListener {

    private FancyButton setReminder;
    private CardView rateCardView, bugReport, fbCard,instagramCard;

    /// notification
    private final String TAG = "RemindMe";
    private LocalData localData;
    private TextView tvTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Window window = Objects.requireNonNull(getActivity()).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.toolbar_background_color));
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        Toolbar toolbar = v.findViewById(R.id.profile_toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();


        //----------------------------

        initialization(v);


        //-------------------------------


        try{
            activity.setSupportActionBar(toolbar);
        }catch (NullPointerException i) {
            i.printStackTrace();
        }

        return v;


    }

    private void initialization(View v){

        SharedPreferences sp = v.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        localData = new LocalData(Objects.requireNonNull(getContext()));
        SwitchButton reminderSwitch = v.findViewById(R.id.alarm_switch);
        tvTime = v.findViewById(R.id.alarm_time);
        int hour = localData.get_hour();
        int min = localData.get_min();
        setReminder = v.findViewById(R.id.set_alarm);
        tvTime.setText(getFormatedTime(hour, min));
        reminderSwitch.setChecked(localData.getReminderStatus());


        //Toast.makeText(v.getContext(),localData.getReminderStatus()+"",Toast.LENGTH_LONG).show();



        rateCardView = v.findViewById(R.id.profile_rate_card);
        bugReport = v.findViewById(R.id.bug_report_card);
        fbCard = v.findViewById(R.id.fb_card);
        instagramCard = v.findViewById(R.id.instagram_card);
        fbCard.setOnClickListener(this);
        instagramCard.setOnClickListener(this);


        if(!sp.contains("wordsPerSession")){

            sp.edit().putInt("wordsPerSession",5).apply();

        }else {

            sp.getInt("wordsPerSession",5);

        }

        if(!sp.contains("repeatationPerSession")){

            sp.edit().putInt("repeatationPerSession",5).apply();

        }else {

            sp.getInt("repeatationPerSession",5);

        }













        rateCardView.setOnClickListener(this);
        bugReport.setOnClickListener(this);


        if (!localData.getReminderStatus()){
            setReminder.setAlpha(0.4f);
        }else {
            setReminder.setAlpha(1f);
        }



        //-------------SET DEFAULT alaarm

        if(!sp.contains("defaultAlarm")){

            //reminderSwitch.setChecked(true);
            localData.setReminderStatus(true);
            NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, 18, 0);
            tvTime.setText(getFormatedTime(hour, min));
            setReminder.setAlpha(1f);
            sp.edit().putBoolean("defaultAlarm",true).apply();
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
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {

        try{
            Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.profile_toolbar_menus,menu);
        }catch (NullPointerException i ) {
            i.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.profile_menu_settings){

            try{

                Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity().getBaseContext(), NewSettingActivity.class));

            }catch (NullPointerException i) {
                i.printStackTrace();
            }
        }
        else if (item.getItemId() == R.id.menu_item_share){

            if(BuildConfig.FLAVOR.equalsIgnoreCase("free")){

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
            }
                else if(BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));


            }
            else{

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));

            }

        }
        return super.onOptionsItemSelected(item);

    }





    @Override
    public void onClick(View v) {

        if(v == bugReport){

            try{

                EmailIntentBuilder.from(Objects.requireNonNull(getActivity()).getBaseContext())
                        .to("fortitudedevs@gmail.com")
                        .subject("VB4 - FL: "+BuildConfig.FLAVOR+" VN: "+BuildConfig.VERSION_NAME+" VC: "+BuildConfig.VERSION_CODE)
                        .body("")
                        .start();
            }catch (NullPointerException ignored){

            }



        }

        if( v == rateCardView){

            if(BuildConfig.FLAVOR.equalsIgnoreCase("free")){
                Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);

            }
            else if(BuildConfig.FLAVOR.equalsIgnoreCase("huawei")){

                Uri appUrl = Uri.parse("https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }

            else {

                Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                this.startActivity(rateApp);
            }

        }

        if(v == fbCard){

            Uri appUrl = Uri.parse("https://www.facebook.com/FortitudeLearn/");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);

        }

        if( v == instagramCard){




            Uri appUrl = Uri.parse("https://www.instagram.com/fortitudelearn/");
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
            newDateString = sdf.format(Objects.requireNonNull(d));
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
