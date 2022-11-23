package com.shrouk.locationtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0


fun NotificationManager.sendNotification(
    messageBody: String,
    title: String,
    bitmap:Int,

    applicationContext: Context
) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    contentIntent.putExtra("title", messageBody)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
    remoteViews.setImageViewResource(R.id.image, bitmap)
    remoteViews.setTextViewText(R.id.title, title)
    remoteViews.setTextViewText(R.id.message, messageBody)
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        "123456"
    )


        .setSmallIcon(androidx.loader.R.drawable.notification_bg)
        .setAutoCancel(false)
        .setOngoing(true)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setCustomContentView(remoteViews)

        .setPriority(NotificationCompat.PRIORITY_HIGH)
    //builder.addAction(com.google.android.material.R.drawable.notification_bg, "Details", contentPendingIntent)

    Log.e("TAG", "sendNotification: ")
    notify(NOTIFICATION_ID, builder.build())
}


/**
 * Cancels all notifications.
 */


fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "download"
        val descriptionText ="description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("123456", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}