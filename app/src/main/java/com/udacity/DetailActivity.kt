package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file_name_content.text = intent.getStringExtra(TITLE)

        if(intent.hasExtra(SUCCESS))
        {
            if(intent.getBooleanExtra(SUCCESS,false))
            {
                status_content.text = this.getString(R.string.status_content_text_succ)
                status_content.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark_))
            }
            else
            {
                status_content.text = this.getString(R.string.status_content_text_fail)
            }
        }

        ok_button.setOnClickListener {
            if (isTaskRoot)
            {
                startActivity(Intent(this,MainActivity::class.java))
            }
            NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
            finish()
        }
    }
}