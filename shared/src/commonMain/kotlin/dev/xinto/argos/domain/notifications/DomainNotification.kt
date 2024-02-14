package dev.xinto.argos.domain.notifications

import dev.xinto.argos.util.FormattedLocalDateTime

data class DomainNotification(
    val id: String,
    val text: String,
    val alertDate: FormattedLocalDateTime,
    val seen: Boolean
)