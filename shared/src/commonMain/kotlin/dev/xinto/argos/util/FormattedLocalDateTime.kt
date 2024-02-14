package dev.xinto.argos.util

import kotlinx.datetime.Instant

expect value class FormattedLocalDateTime(private val instant: Instant) {

    override fun toString(): String
}

internal val FormattedLocalDateTime.formatString
    get() = "d MMMM yyyy, HH:mm"

fun Instant.asFormattedLocalDateTime() = FormattedLocalDateTime(this)