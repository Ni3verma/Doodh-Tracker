package com.andryoga.doodh.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andryoga.doodh.MainActivity
import com.andryoga.doodh.MyApplication
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class LogReminderWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance()
        val entryForToday = (applicationContext as? MyApplication)?.doodhDao?.getDoodhRecordForDay(
            day = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR)
        )?.firstOrNull()
        if (entryForToday != null) {
            // have already logged milk for today, no need to show a reminder notification
            return Result.success()
        }

        showNotification("Daily Milk Reminder", "Hey, did you buy milk today?")
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "daily_milk_log"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Log Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationId = 1001
        // Intent for "Yes" and clicking notification → open app
        val yesIntent = Intent(applicationContext, MainActivity::class.java)
        val yesPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for "No" → dismiss notification
        val dismissIntent =
            Intent(applicationContext, NotificationDismissReceiver::class.java).apply {
                putExtra("notification_id", notificationId)
            }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Sound, vibration, lights
            .setContentIntent(yesPendingIntent) // clicking the body opens app
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Yes", yesPendingIntent)
            .addAction(android.R.drawable.ic_delete, "No", dismissPendingIntent)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, notification)
        }
    }
}
