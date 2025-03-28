package com.abdelrahman_elshreif.sky_vibe.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Utility {
    object DateTimeUtil {
        @RequiresApi(Build.VERSION_CODES.O)
        fun convertUnixToDateTime(unixTime: Long): String {
            val instant = Instant.ofEpochSecond(unixTime)
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            return zonedDateTime.format(formatter)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertUnixToDateHour(unixTime: Long): String {
            val instant = Instant.ofEpochSecond(unixTime)
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("hh a")
            return zonedDateTime.format(formatter)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertUnixToDate(unixTime: Long): String {
            val instant = Instant.ofEpochSecond(unixTime)
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")
            return zonedDateTime.format(formatter)
        }

    }

    object TimeZoneUtil {
        fun convertTimezoneToCityLocation(timezone: String): String {
            return timezone.split("/")
                .reversed()
                .joinToString(" , ") { it.replace("_", " ") }
        }
    }


}
