package com.fortitude.shamsulkarim.ieltsfordory.utility.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity

/**
 * Modern AndroidX CoroutineWorker for triggering daily vocabulary practice notifications.
 * Battery-optimized and resilient across device reboots and Doze mode.
 */
class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "daily_reminders_channel"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        showPracticeNotification()
        return Result.success()
    }

    private fun showPracticeNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Practice Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to learn and review vocabulary daily"
                enableLights(true)
                lightColor = ContextCompat.getColor(context, R.color.colorPrimary)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Vocabulary Builder")
            .setContentText("Time for daily practice! Let's learn new words today.")
            .setSmallIcon(R.drawable.ic_stat_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
