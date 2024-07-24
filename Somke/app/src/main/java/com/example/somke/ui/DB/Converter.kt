package com.example.somke.ui.DB

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Converter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.JAPAN)

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
        timeFormat.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
    }

    @TypeConverter
    fun fromDate(value: String?): Date? {
        return value?.let { dateFormat.parse(it) }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return date?.let { dateFormat.format(it) }
    }

    @TypeConverter
    fun fromTime(value: String?): Date? {
        return value?.let { timeFormat.parse(it) }
    }

    @TypeConverter
    fun timeToString(date: Date?): String? {
        return date?.let { timeFormat.format(it) }
    }
}