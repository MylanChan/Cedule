package cedule.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtils {
    private fun replenishZero(target: Int): String {
        val targetInString = target.toString()

        if (2 - targetInString.length > 0) {
            return "0".repeat(2 - targetInString.length) + target
        }
        return targetInString
    }

    fun toTimeNotation(seconds: Int): String {
        val hrs = (seconds / 3600.0).toInt()
        val min = seconds / 60 - hrs * 60
        val sec = seconds - hrs * 3600 - min * 60

        if (hrs > 0) {
            return replenishZero(hrs) + ":" + replenishZero(min) + ":" + replenishZero(sec)
        }
        return replenishZero(min) + ":" + replenishZero(sec)
    }

    fun toTimeString(ms: Long): String {
        val hour = TimeUnit.MILLISECONDS.toHours(ms).toInt()
        val min = (TimeUnit.MILLISECONDS.toMinutes(ms) - hour * 60).toInt()

        return replenishZero(hour) + ":" + replenishZero(min)
    }

    fun setMidNight(calendar: Calendar): Calendar {
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        return calendar
    }

    fun toDateString(ms: Long): String {
        val date = Calendar.getInstance()
        date.timeInMillis = ms

        val month = SimpleDateFormat("MMM", Locale.ENGLISH).format(ms) // e.g. Feb
        return date[Calendar.DAY_OF_MONTH].toString() + " " + month + " " + date[Calendar.YEAR]
    }

    fun getTodayMidnight(): Long {
        val calendar = Calendar.getInstance()
        setMidNight(calendar)

        return calendar.timeInMillis
    }

    fun getAllDatesForYear(year: Int): List<DateInfo> {
        val dateList = mutableListOf<DateInfo>()

        val calendar = setMidNight(Calendar.getInstance())
        calendar.set(year, Calendar.JANUARY, 1)

        val weekdayFormat = SimpleDateFormat("EEE", Locale.ENGLISH) // e.g. Mon
        val dayFormat = SimpleDateFormat("dd", Locale.ENGLISH)      // e.g. 01
        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)   // e.g. Jan

        while (calendar.get(Calendar.YEAR) == year) {
            dateList.add(
                DateInfo(
                    weekday = weekdayFormat.format(calendar.time),
                    day = dayFormat.format(calendar.time).toInt(),
                    month = monthFormat.format(calendar.time),
                    calendar.timeInMillis
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dateList
    }
}

data class DateInfo(
    val weekday: String,
    val day: Int,
    val month: String,
    val timestamp: Long
)