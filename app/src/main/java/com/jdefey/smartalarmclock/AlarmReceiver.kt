package com.jdefey.smartalarmclock

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jdefey.smartalarmclock.R


private const val CHANNEL_ID: String = "id_of_channel"

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_ACTION = "W_ACTION"
        const val ALARM_INPUT_KEY = "W_INPUT"
        const val ALARM_STOP = "stop"

        fun createIntent(context: Context, message: String): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                action = ALARM_ACTION
                putExtra(ALARM_INPUT_KEY, message)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ALARM_ACTION) {
            return
        }
        RingtoneAlarm.ringtone(context)
        val text = "Будильник!!!  ${intent.extractInput()}"
        context.createNotificationChannel(CHANNEL_ID)
        context.getNotificationManager()
            .notify(text.hashCode(), createNotification(context, text))
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun stopAlarm(context: Context): PendingIntent? {
        val intent = Intent(context, RingtoneReceiver::class.java).apply {
            action = ALARM_STOP
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                33,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                33,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(context: Context, text: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Alarm")
            .setContentText(text)
            .addAction(R.drawable.ic_explore, "STOP", stopAlarm(context))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(context.getPendingIntent(MainActivity::class.java), true)
            .build()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun Context.getPendingIntent(classZ: Class<out Activity>): PendingIntent {
        val fullScreenIntent = Intent(this, classZ)
        return PendingIntent.getActivity(
            this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun Context.createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = descriptionText
            val notificationManager = getNotificationManager()
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun Context.getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun Intent.extractInput(): String {
        return getStringExtra(ALARM_INPUT_KEY) ?: "Empty"
    }
}
