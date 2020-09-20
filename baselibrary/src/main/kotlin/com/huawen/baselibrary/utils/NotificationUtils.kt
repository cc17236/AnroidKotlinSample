package com.huawen.baselibrary.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * Created by LaoZhao on 2017/11/19.
 */

class NotificationUtils(context: Context) : ContextWrapper(context) {

    private var manager: NotificationManager? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        getManager().createNotificationChannel(channel)
    }

    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getChannelNotification(title: String, content: String): Notification.Builder {
        return Notification.Builder(applicationContext, id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
    }

    fun getNotification_25(title: String, content: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
    }

    fun sendNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel()
            val notification = getChannelNotification(title, content).build()
            getManager().notify(1, notification)
        } else {
            val notification = getNotification_25(title, content).build()
            getManager().notify(1, notification)
        }
    }

    companion object {
        val id = "channel_1"
        val name = "channel_name_1"

        init {

        }
    }
}  