package com.fortitude.shamsulkarim.ieltsfordory.utility.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {


    final String TAG = "AlarmReceiver";

@Override
public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction() != null && context != null) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
        // Set the alarm here.
        Log.d(TAG, "onReceive: BOOT_COMPLETED");
        LocalData localData = new LocalData(context);
        NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());
        return;
        }
        }

        Log.d(TAG, "onReceive: ");

        //Trigger the notification
        NotificationScheduler.showNotification(context, MainActivity.class,
        "Vocabulary Builder", "Come on! Let's learn some vocabulary");

        }


}
