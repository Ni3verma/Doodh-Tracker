package com.andryoga.doodh.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.andryoga.doodh.BuildConfig
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {
    fun scheduleDailyLogReminder(context: Context) {
        val currentTime = Calendar.getInstance()
        val targetTime = if (BuildConfig.DEBUG) {
            Calendar.getInstance().apply {
                set(Calendar.SECOND, 10) // 5sec delay
            }
        } else {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 21) // 9 PM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(currentTime)) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        }


        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<LogReminderWork>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        // make it true to test notification
        if (false && BuildConfig.DEBUG) {
            WorkManager.getInstance(context).cancelAllWork()
        }
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_6pm_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}