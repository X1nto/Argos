package dev.xinto.argos.domain.notifications

import kotlinx.datetime.LocalDateTime

data class DomainNotification(
    val id: String,
    val text: String,
    val alertDate: LocalDateTime,
    val seen: Boolean
)