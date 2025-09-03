package dev.xinto.argos.util

import kotlin.time.Instant
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
            val isSameDay = dateNow.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

            val format = when {
                isSameYear && isSameDay -> "HH:mm"
                isSameYear -> "MMM dd"
                else -> "dd/MM/YYYY"
            }

            return SimpleDateFormat(format, Locale.getDefault()).format(date.time)
        }

    actual val fullDateTime: String
        get() {
            val simpleDateFormat = SimpleDateFormat(fullDatetimeFormat, Locale.getDefault())
            return simpleDateFormat.format(Date(instant.toEpochMilliseconds()))
        }

}