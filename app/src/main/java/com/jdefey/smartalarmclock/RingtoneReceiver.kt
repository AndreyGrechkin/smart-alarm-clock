package com.jdefey.smartalarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class RingtoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmReceiver.ALARM_STOP) RingtoneAlarm.stopRingtone()
    }
}