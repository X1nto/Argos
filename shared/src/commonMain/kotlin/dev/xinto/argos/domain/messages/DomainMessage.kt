package dev.xinto.argos.domain.messages

import dev.xinto.argos.util.FormattedLocalDateTime

sealed interface DomainMessageSource {

    data class Inbox(val sender: DomainMessageUser): DomainMessageSource

    data class Outbox(val receiver: DomainMessageUser): DomainMessageSource

    data class General(
        val sender: DomainMessageUser,
        val receiver: DomainMessageUser
    ): DomainMessageSource

}

data class DomainMessage(
    val id: String,
    val subject: String,
    val body: String,
    val semId: String,
    val source: DomainMessageSource,
    val sentAt: FormattedLocalDateTime,
    val seenAt: FormattedLocalDateTime?
)