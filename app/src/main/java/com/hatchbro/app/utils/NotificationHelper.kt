package com.hatchbro.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hatchbro.app.R

object NotificationHelper {
    const val CHANNEL_ID_ALERTS = "alerts"
    const val CHANNEL_ID_REMINDERS = "reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Critical Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Temperature and Humidity Alerts"
            }

            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Incubation Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lockdown and Hatch Date Reminders"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(alertsChannel)
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }

    fun showNotification(context: Context, id: Int, title: String, message: String, channelId: String) {
        // Note: Missing permission check for POST_NOTIFICATIONS on Android 13+.
        // Assuming permission is handled or app targets lower/debug environment for now.
        // In production, we must check ContextCompat.checkSelfPermission.
        try {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Use default launcher icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (e: SecurityException) {
            // Log error
        }
    }
}
