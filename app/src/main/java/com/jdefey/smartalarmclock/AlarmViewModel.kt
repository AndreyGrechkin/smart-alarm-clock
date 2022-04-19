package com.jdefey.smartalarmclock

import androidx.lifecycle.ViewModel
import com.jdefey.smartalarmclock.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun getSunrise() {
        repository.timeSunRise()
    }

    fun getEditLocation(city: String) {
        if (city == "") return
        else repository.editCityLocation(city)
    }

    fun setAlarm(hour: Long, minute: Long, timeRise: Long) {
        repository.setAlarm(hour, minute, timeRise)
    }
}