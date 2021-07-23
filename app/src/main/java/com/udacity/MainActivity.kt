package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var input: TextInputEditText
    private lateinit var notificationManager: NotificationManager
    private var downloadID: Long = 0
    private var Url: String = ""
    private var fileTitle = "-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        input = findViewById(R.id.custom_url)
        custom_button.setOnClickListener {

            createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_title)
            )

            val customUrlInput = input.text.toString()
            var isValid = false

            if (URLUtil.isValidUrl(customUrlInput)) {
                isValid = true
            }

            if (radio_group.checkedRadioButtonId == -1 && !isValid) {
                custom_button.checked(false)
                custom_button.setProgressAndButtonState(0.0, ButtonState.Clicked)
            } else {
                custom_button.checked(true)

                when {
                    isValid -> {
                        Url = customUrlInput
                        downloadName = "Custom download"
                        download(Url)
                        custom_button.setProgressAndButtonState(0.0, ButtonState.Loading)
                        fileTitle = "Custom file"
                    }
                    radio_group.radio_glide.isChecked -> {
                        Url = URL_GLIDE
                        downloadName = "Glide - GitHub"
                        download(Url)
                        custom_button.setProgressAndButtonState(0.0, ButtonState.Loading)
                        input.isEnabled = false
                        fileTitle = "Glide from Github"
                    }
                    radio_group.radio_udacity.isChecked -> {
                        Url = URL_UDACITY
                        downloadName = "Udacity - Starter"
                        download(Url)
                        custom_button.setProgressAndButtonState(0.0, ButtonState.Loading)
                        input.isEnabled = false
                        fileTitle = "Udacity - Starter"
                    }
                    radio_group.radio_retrofit.isChecked -> {
                        Url = URL_RETROFIT
                        downloadName = "Retrofit - GitHub"
                        download(Url)
                        custom_button.setProgressAndButtonState(0.0, ButtonState.Loading)
                        input.isEnabled = false
                        fileTitle = "Retrofit - GitHub"
                    }
                }
            }
        }//End onclick
    }//End onCreate

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {

                    val success =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = success == DownloadManager.STATUS_SUCCESSFUL

                    sendNotification(isSuccess, fileTitle) // message
                }

                custom_button.hasCompletedDownload()
                radioGroupDisabledEnabled(true)
                input.text = null
                input.isEnabled = true
            }
        }
    }

    private fun download(downloadLink: String) {

        radioGroupDisabledEnabled(false)
        input.isEnabled = false
        val request =
            DownloadManager.Request(Uri.parse(downloadLink))
                .setTitle(downloadName)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    companion object {
        private var downloadName = "name"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "ready"

            notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(isSuccess: Boolean, downloadTitle: String) {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(
            downloadTitle,
            this, isSuccess
        )
    }

    fun radioGroupDisabledEnabled(enable: Boolean) {
        radio_group.check(-1)
        for (i in 0 until radio_group.childCount) {
            radio_group.getChildAt(i).isEnabled = enable
        }
    }
}
