package dev.xinto.argos.util

import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.systemTimeZone

actual value class FormattedLocalDateTime(private val instant: Instant) {
    actual override fun toString(): String {
        return NSDateFormatter().apply {
            dateFormat = formatString
            timeZone = NSTimeZone.systemTimeZone
        }.stringFromDate(instant.toNSDate())
    }
}