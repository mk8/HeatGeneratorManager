package com.torosoft.heater.heatermanager.Converters

import android.arch.persistence.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimestampTypeConverter {
    companion object {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        if (value != null) {
            try {
                return dateFormat.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        } else {
            return null
        }
    }

    @TypeConverter
    fun toTimestamp(value: Date?): String? {
        if (value != null) {
            return dateFormat.format(value)
        } else {
            return null
        }
    }


}