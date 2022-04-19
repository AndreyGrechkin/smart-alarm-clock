package com.jdefey.smartalarmclock

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

object RingtoneAlarm {
    lateinit var ringtone: Ringtone
    fun stopRingtone() {
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }

    fun ringtone(context: Context) {
        val notificationUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, notificationUri)
        ringtone.play()
    }
}