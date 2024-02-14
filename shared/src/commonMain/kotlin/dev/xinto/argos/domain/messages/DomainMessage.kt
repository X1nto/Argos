package dev.xinto.argos.domain.messages

import dev.xinto.argos.util.FormattedLocalDateTime

sealed interface DomainMessagePreview {
    val id: String
    val subject: String
    val semId: String
    val date: FormattedLocalDateTime
    val user: DomainMessageUser
}

data class DomainMessage(
    val id: String,
    val subject: String,
    val body: String,
    val semId: String,
    val date: FormattedLocalDateTime,
    val sender: DomainMessageUser,
    val receiver: DomainMessageUser,
)

data class DomainMessageSent(
    override val id: String,
    override val subject: String,
    override val semId: String,
    override val date: FormattedLocalDateTime,
    val receiver: DomainMessageUser,
) : DomainMessagePreview {
    override val user: DomainMessageUser = receiver
}

data class DomainMessageReceived(
    override val id: String,
    override val subject: String,
    override val semId: String,
    override val date: FormattedLocalDateTime,
    val sender: DomainMessageUser,
    val seen: Boolean,
): DomainMessagePreview {
    override val user: DomainMessageUser = sender
}