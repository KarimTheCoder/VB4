package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.FragmentProfileFragmentBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.settings.SettingActivity;
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.AlarmReceiver;
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.LocalData;
import com.fortitude.shamsulkarim.ieltsfordory.utility.notification.NotificationScheduler;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.cketti.mailto.EmailIntentBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileFragmentBinding binding;
    private final String TAG = "RemindMe";
    private LocalData localData;
    private AppPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(requireContext().getColor(R.color.toolbar_background_color));
        // Inflate the layout for this fragment
        binding = FragmentProfileFragmentBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // ----------------------------

        initialization();

        // -------------------------------

        try {
            if (activity != null) {
                activity.setSupportActionBar(binding.profileToolbar);
            }
        } catch (NullPointerException i) {
            i.printStackTrace();
        }

        return v;
    }

    private void initialization() {

        prefs = AppPreferences.get(requireContext());
        localData = new LocalData(requireContext());

        int hour = localData.get_hour();
        int min = localData.get_min();
        binding.setAlarm.setEnabled(false);
        binding.alarmTime.setText(getFormatedTime(hour, min));

        binding.fbCard.setOnClickListener(v -> {
            Uri appUrl = Uri.parse("https://www.facebook.com/FortitudeLearn/");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            startActivity(rateApp);
        });

        binding.instagramCard.setOnClickListener(v -> {
            Uri appUrl = Uri.parse("https://www.instagram.com/fortitudelearn/");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            startActivity(rateApp);
        });

        if (!prefs.contains(AppPreferences.KEY_WORDS_PER_SESSION)) {
            prefs.setWordsPerSession(5);
        } else {
            prefs.getWordsPerSession();
        }

        if (!prefs.contains(AppPreferences.KEY_REPEATATION_PER_SESSION)) {
            prefs.setRepeatationPerSession(5);
        } else {
            prefs.getRepeatationPerSession();
        }

        binding.profileRateCard.setOnClickListener(v -> {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("free")) {
                Uri appUrl = Uri
                        .parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                startActivity(rateApp);

            } else if (BuildConfig.FLAVOR.equalsIgnoreCase("huawei")) {
                Uri appUrl = Uri.parse(
                        "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                startActivity(rateApp);
            } else {
                Uri appUrl = Uri
                        .parse("https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro");
                Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
                startActivity(rateApp);
            }
        });

        binding.bugReportCard.setOnClickListener(v -> {
            try {
                EmailIntentBuilder.from(requireActivity().getBaseContext())
                        .to("fortitudedevs@gmail.com")
                        .subject("VB4 - FL: " + BuildConfig.FLAVOR + " VN: " + BuildConfig.VERSION_NAME + " VC: "
                                + BuildConfig.VERSION_CODE)
                        .body("")
                        .start();
            } catch (NullPointerException ignored) {

            }
        });

        if (!localData.getReminderStatus()) {
            binding.setAlarm.setAlpha(0.4f);
        } else {
            binding.setAlarm.setAlpha(1f);
        }

        // -------------SET DEFAULT alaarm

        if (!prefs.isDefaultAlarmSet()) {
            // Todo fix alarm
            // binding.alarmSwitch.setChecked(true);
            // localData.setReminderStatus(true);
            // NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, 18, 0);
            // binding.alarmTime.setText(getFormatedTime(hour, min));
            // binding.setAlarm.setAlpha(1f);
            // prefs.setDefaultAlarmSet(true);
        }

        binding.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localData.setReminderStatus(isChecked);
                if (isChecked) {
                    Toast.makeText(getContext(), "Reminder unavailable for now", Toast.LENGTH_SHORT).show();
                    // Log.d(TAG, "onCheckedChanged: true");
                    // NotificationScheduler.setReminder(getContext(), AlarmReceiver.class,
                    // localData.get_hour(), localData.get_min());
                    // binding.setAlarm.setAlpha(1f);
                } else {
                    Toast.makeText(getContext(), "Reminder unavailable for now", Toast.LENGTH_SHORT).show();
                    // Log.d(TAG, "onCheckedChanged: false");
                    // NotificationScheduler.cancelReminder(getContext(), AlarmReceiver.class);
                    // binding.setAlarm.setAlpha(0.4f);
                }
            }
        });

        binding.setAlarm.setOnClickListener(view -> {
            if (localData.getReminderStatus())
                showTimePickerDialog(localData.get_hour(), localData.get_min());
        });
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        try {
            requireActivity().getMenuInflater().inflate(R.menu.profile_toolbar_menus, menu);
        } catch (NullPointerException i) {
            i.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile_menu_settings) {
            try {
                requireActivity().startActivity(new Intent(getActivity().getBaseContext(), SettingActivity.class));
            } catch (NullPointerException i) {
                i.printStackTrace();
            }
        } else if (item.getItemId() == R.id.menu_item_share) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("free")) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilder";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
            } else if (BuildConfig.FLAVOR.equalsIgnoreCase("huawei")) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://appgallery.cloud.huawei.com/ag/n/app/C102022895?locale=en_GB&source=appshare&subsource=C102022895";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
            } else {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id=com.fortitude.apps.vocabularybuilderPro";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Learn vocabulary using this app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /// SET NOTIFICATION

    private void showTimePickerDialog(int h, int m) {
        TimePickerDialog builder = new TimePickerDialog(getContext(), R.style.DialogTheme,
                (timePicker, hour, min) -> {
                    Log.d(TAG, "onTimeSet: hour " + hour);
                    Log.d(TAG, "onTimeSet: min " + min);
                    localData.set_hour(hour);
                    localData.set_min(min);
                    binding.alarmTime.setText(getFormatedTime(hour, min));
                    NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, localData.get_hour(),
                            localData.get_min());
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

    public Locale getCurrentLocale() {
        return getResources().getConfiguration().getLocales().get(0);
    }
}
