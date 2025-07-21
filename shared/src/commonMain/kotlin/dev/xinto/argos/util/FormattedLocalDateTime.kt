package dev.xinto.argos.util

import kotlinx.datetime.Instant

expect class FormattedLocalDateTime(instant: Instant) {

    val relativeDateTime: String
    val fullDateTime: String

}

internal val FormattedLocalDateTime.fullDatetimeFormat
    get() = "d MMMM yyyy, HH:mm"

internal val FormattedLocalDateTime.relativeDatetimeTodayFormat
    get() = "HH:mm"

internal val FormattedLocalDateTime.relativeDatetimeThisweekFormat
    get() = "EEEE"

internal val FormattedLocalDateTime.relativeDatetimePastFormat
    get() = "dd.MM.yy"

fun Instant.asFormattedLocalDateTime() = FormattedLocalDateTime(this)