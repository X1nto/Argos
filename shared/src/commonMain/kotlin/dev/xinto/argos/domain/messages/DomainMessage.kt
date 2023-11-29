package dev.xinto.argos.domain.messages

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

sealed interface DomainMessage {
    val id: String
    val subject: String
    val date: LocalDateTime
    val user: DomainMessageUser
}

data class DomainMessageSent(
    override val id: String,
    override val user: DomainMessageUser,
    override val subject: String,
    override val date: LocalDateTime
) : DomainMessage

data class DomainMessageReceived(
    override val id: String,
    override val user: DomainMessageUser,
    override val subject: String,
    override val date: LocalDateTime,
    val seen: Boolean,
): DomainMessage