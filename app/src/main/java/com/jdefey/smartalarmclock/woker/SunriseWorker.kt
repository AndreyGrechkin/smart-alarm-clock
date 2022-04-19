package com.jdefey.smartalarmclock.woker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.jdefey.smartalarmclock.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import org.shredzone.commons.suncalc.SunTimes
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class SunriseWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result = coroutineScope {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val coordinate = inputData.getDoubleArray(COORDINATE)
        Log.d("MyTag", "Worker")
        val time = timeSunRise(coordinate).rise?.time
        val result = workDataOf(WORK_RESULT_KEY to time)
        NotificationHelper(context).createNotification(
            "Расвет",
            "Примерно в это время: ${sdf.format(time)} "
        )
        return@coroutineScope Result.success(result)
    }

    private fun timeSunRise(coordinate: DoubleArray?): SunTimes {
        val dateTime = Date()
        val zone = TimeZone.getDefault()
        return SunTimes.compute()
            .timezone(zone)
            .on(dateTime)
            .at(coordinate)
            .execute()
    }

    companion object {
        const val COORDINATE = "coordinate"
        const val WORK_RESULT_KEY = "result"
    }
}