package com.jdefey.smartalarmclock.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jdefey.smartalarmclock.AlarmFragment
import com.jdefey.smartalarmclock.AlarmReceiver
import com.jdefey.smartalarmclock.MainActivity
import com.jdefey.smartalarmclock.location.PeriodicLocationProvider
import com.jdefey.smartalarmclock.woker.SunriseWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : Repository {

    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

    override fun editCityLocation(city: String) {
        val geocoder = Geocoder(context)
        val location = geocoder.getFromLocationName(city, 1)
        val latitude = location[0].latitude
        val longitude = location[0].longitude
        Log.d("MyTag", "location $latitude,  $longitude")
        val coordinate = doubleArrayOf(latitude, longitude)
        val workData = workDataOf(SunriseWorker.COORDINATE to coordinate)
        val workRequest = OneTimeWorkRequestBuilder<SunriseWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(AlarmFragment.UNIQUE, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override fun timeSunRise() {
        PeriodicLocationProvider(context = context).startLocationUpdates {
            val lat = it.getOrNull()?.latitude ?: 0.0
            val lng = it.getOrNull()?.longitude ?: 0.0
            val coordinate = doubleArrayOf(lat, lng)
            Log.d("MyTag", "coordinate $lat,  $lng")
            val workData = workDataOf(SunriseWorker.COORDINATE to coordinate)
            val workRequest = OneTimeWorkRequestBuilder<SunriseWorker>()
                .setInputData(workData)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(AlarmFragment.UNIQUE, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }

    override fun setAlarm(hour: Long, minute: Long, timeRise: Long) {
        val hourToMillisecond: Long = hour * 3600000
        val minuteToMillisecond: Long = minute * 60000
        val timeRiseAlarm = timeRise + hourToMillisecond + minuteToMillisecond
        val alarmClockInfo = AlarmManager.AlarmClockInfo(timeRiseAlarm, getAlarmInfoPendingIntent())
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = AlarmReceiver.createIntent(context, "Пора вставать!!!")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    1,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setAlarmClock(
                    alarmClockInfo,
                    pendingIntent
                )
            } else {
                val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0)
                alarmManager.setAlarmClock(
                    alarmClockInfo,
                    pendingIntent
                )
            }
        }
        Toast.makeText(
            context,
            "Будильник установлен на " + sdf.format(timeRiseAlarm),
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getAlarmInfoPendingIntent(): PendingIntent? {
        val alarmInfoIntent = Intent(context, MainActivity::class.java)
        alarmInfoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                0,
                alarmInfoIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                context,
                0,
                alarmInfoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}