package dev.xinto.argos.util

import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@JvmInline
actual value class FormattedLocalDateTime(private val instant: Instant) {
    actual override fun toString(): String {
        return SimpleDateFormat(formatString, Locale.getDefault())
            .format(Date(instant.toEpochMilliseconds()))
    }
}