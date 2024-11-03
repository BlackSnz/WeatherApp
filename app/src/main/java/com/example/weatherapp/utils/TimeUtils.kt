package com.example.weatherapp.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object TimeUtils {

    fun unixToDayOfWeek(unixTime: Long): String {
        val instant = Instant.ofEpochSecond(unixTime)
        val dayOfWeek = instant.atZone(ZoneId.systemDefault()).dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    fun unixTimeToIsoTime(unixTime: String): String {
        val unixTimestamp = unixTime.toLong()
        val date = java.util.Date(unixTimestamp * 1000L)
        val sdf = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }
}