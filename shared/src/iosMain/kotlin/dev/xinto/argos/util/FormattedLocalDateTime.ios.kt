package dev.xinto.argos.util

import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitWeekOfYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.now
import platform.Foundation.systemTimeZone

actual class FormattedLocalDateTime actual constructor(private val instant: Instant) {

    actual val relativeDateTime: String
        get() {
            val date = instant.toNSDate()
            val dateNow = NSDate.now()
            val calendar = NSCalendar.currentCalendar()
            val dateFormatter = NSDateFormatter().apply {
                timeZone = NSTimeZone.systemTimeZone
                dateFormat = when {
                    calendar.isDateInToday(date) -> relativeDatetimeTodayFormat
                    calendar.isDate(date, equalToDate = dateNow, toUnitGranularity = NSCalendarUnitWeekOfYear) -> relativeDatetimeThisweekFormat
                    else -> relativeDatetimePastFormat
                }
            }

            return dateFormatter.stringFromDate(date)
        }

    actual val fullDateTime: String
        get() {
            val dateFormatter = NSDateFormatter().apply {
                dateFormat = fullDatetimeFormat
                timeZone = NSTimeZone.systemTimeZone
            }
            return dateFormatter.stringFromDate(instant.toNSDate())
        }

}