package com.fortitude.shamsulkarim.ieltsfordory.utility.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Utility to schedule or cancel periodic daily practice reminders using AndroidX WorkManager.
 */
object ReminderScheduler {

    private const val UNIQUE_WORK_NAME = "daily_practice_reminder"

    /**
     * Schedules a recurring 24-hour reminder at the specified [hour] (0-23) and [minute] (0-59).
     */
    fun scheduleReminder(context: Context, hour: Int, minute: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time is earlier than now, schedule for tomorrow
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }

    /**
     * Cancels any scheduled daily practice reminders.
     */
    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
}
