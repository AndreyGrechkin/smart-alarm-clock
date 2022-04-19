package com.jdefey.smartalarmclock.repository

interface Repository {

    fun timeSunRise()
    fun editCityLocation(city: String)
    fun setAlarm(hour: Long, minute: Long, timeRise: Long)
}