package com.rootscare.utils

import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    private val TAG = "DateTimeUtils"
    val DAY_KEY = "DAY_KEY"
    val TIME_KEY = "TIME_KEY"

    fun getDayDependingOnJavaTime(s: String): String {
        val day = Integer.parseInt(s.trim { it <= ' ' })
        var dayTemp = ""
        when (day) {
            Calendar.SUNDAY -> {
                dayTemp = "Sun"
            }
            Calendar.MONDAY -> {
                dayTemp = "Mon"
            }
            Calendar.TUESDAY -> {
                dayTemp = "Tue"
            }
            Calendar.WEDNESDAY -> {
                dayTemp = "Wed"
            }
            Calendar.THURSDAY -> {
                dayTemp = "Thurs"
            }
            Calendar.FRIDAY -> {
                dayTemp = "Fri"
            }
            Calendar.SATURDAY -> {
                dayTemp = "Sat"
            }
        }
        return dayTemp
    }

    fun getDay(s: String): String {
        var dayTemp = ""
        when {
            s.trim { it <= ' ' } == "1" -> {
                dayTemp = "Mon"
            }
            s.trim { it <= ' ' } == "2" -> {
                dayTemp = "Tue"
            }
            s.trim { it <= ' ' } == "3" -> {
                dayTemp = "Wed"
            }
            s.trim { it <= ' ' } == "4" -> {
                dayTemp = "Thurs"
            }
            s.trim { it <= ' ' } == "5" -> {
                dayTemp = "Fri"
            }
            s.trim { it <= ' ' } == "6" -> {
                dayTemp = "Sat"
            }
            s.trim { it <= ' ' } == "7" -> {
                dayTemp = "Sun"
            }
        }
        return dayTemp
    }

    /* return as yyyy-MM-dd */
    fun getFormattedDate(date: Date): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val result = Date(calendar.time.time)
        Log.d(TAG, "getFormattedDate: " + dateFormat.format(result))
        return dateFormat.format(result)
    }

    /* return as given pattern */
    fun getFormattedDate(date: Date?, pattern: String?): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = date
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val result = Date(calendar.time.time)
        Log.d(TAG, "getFormattedDate: " + dateFormat.format(result))
        return dateFormat.format(result)
    }

    fun getDayOfWeekFromDate(stringDate: String?): String {
        var day = ""
        val c = Calendar.getInstance()
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date
        try {
            formatter.timeZone = TimeZone.getDefault()
            date = formatter.parse(stringDate)
            c.time = date
            when (c[Calendar.DAY_OF_WEEK]) {
                1 -> day = "Sunday"
                2 -> day = "Monday"
                3 -> day = "Tuesday"
                4 -> day = "Wednesday"
                5 -> day = "Thursday"
                6 -> day = "Friday"
                7 -> day = "Saturday"
            }
            return day
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

}
