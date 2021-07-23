
package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
const val TITLE = "title"
const val SUCCESS = "success"


fun NotificationManager.sendNotification(
        messageBody : String,
        applicationContext : Context,
        isSuccess : Boolean
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)

    contentIntent.putExtra(TITLE, messageBody)
    contentIntent.putExtra(SUCCESS, isSuccess)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val buttonPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        contentIntent,
        FLAGS
    )

    val bigPicture = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.letoltes)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            buttonPendingIntent
        )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setLargeIcon(bigPicture)
        .setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(bigPicture)
                .bigLargeIcon(null)
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
