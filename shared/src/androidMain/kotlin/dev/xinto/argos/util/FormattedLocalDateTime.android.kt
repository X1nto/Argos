package dev.xinto.argos.util

import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

actual class FormattedLocalDateTime actual constructor(private val instant: Instant) {

    actual val relativeDateTime: String
        get() {
            val dateNow = Calendar.getInstance()
            val date = Calendar.getInstance().apply {
                time = Date(instant.toEpochMilliseconds())
            }

            val isSameYear = dateNow.get(Calendar.YEAR) == date.get(Calendar.YEAR)
            val isSameWeek = dateNow.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)
            val isSameDay = dateNow.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

            val format = when {
                isSameYear && isSameDay -> relativeDatetimeTodayFormat
                isSameYear && isSameWeek -> relativeDatetimeThisweekFormat
                else -> relativeDatetimePastFormat
            }

            return SimpleDateFormat(format, Locale.getDefault()).format(date.time)
        }

    actual val fullDateTime: String
        get() {
            val simpleDateFormat = SimpleDateFormat(fullDatetimeFormat, Locale.getDefault())
            return simpleDateFormat.format(Date(instant.toEpochMilliseconds()))
        }

}