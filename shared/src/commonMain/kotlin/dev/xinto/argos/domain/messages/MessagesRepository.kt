package dev.xinto.argos.domain.messages

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.util.asFormattedLocalDateTime

class MessagesRepository(
    private val argosApi: ArgosApi
) {

    fun getInboxMessages(semesterId: String): DomainPagedResponsePager<*, DomainMessage> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getInboxMessages(semesterId, page)
            },
            transform = {
                it.data!!.map { (id, attributes, relationships) ->
                    DomainMessage(
                        id = id!!,
                        semId = attributes.semId.toString(),
                        source = DomainMessageSource.Inbox(
                            sender = DomainMessageUser(
                                uid = relationships.sender.data.attributes.uid,
                                fullName = relationships.sender.data.attributes.fullName,
                            )
                        ),
                        subject = attributes.subject,
                        body = attributes.body,
                        sentAt = attributes.sentAt.asFormattedLocalDateTime(),
                        seenAt = attributes.readAt?.asFormattedLocalDateTime()
                    )
                }
            }
        )
    }

    fun getOutboxMessages(semesterId: String): DomainPagedResponsePager<*, DomainMessage> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getOutboxMessages(semesterId, page)
            },
            transform = {
                it.data!!.map { (id, attributes, relationships) ->
                    DomainMessage(
                        id = id!!,
                        semId = attributes.semId.toString(),
                        source = DomainMessageSource.Outbox(
                            receiver = DomainMessageUser(
                                uid = relationships.receiver.data.attributes.uid,
                                fullName = relationships.receiver.data.attributes.fullName,
                            )
                        ),
                        subject = attributes.subject,
                        body = attributes.body,
                        sentAt = attributes.sentAt.asFormattedLocalDateTime(),
                        seenAt = attributes.readAt?.asFormattedLocalDateTime()
                    )
                }
            }
        )
    }

    fun getMessage(id: String, semId: String): DomainResponseSource<*, DomainMessage> {
        return DomainResponseSource(
            fetch = {
                argosApi.getMessage(id, semId)
            },
            transform = {
                it.data!!.let { (id, attributes, relationships) ->
                    DomainMessage(
                        id = id!!,
                        semId = attributes.semId.toString(),
                        source = DomainMessageSource.General(
                            sender = DomainMessageUser(
                                uid = relationships.sender.data.attributes.uid,
                                fullName = relationships.sender.data.attributes.fullName,
                            ),
                            receiver = DomainMessageUser(
                                uid = relationships.receiver.data.attributes.uid,
                                fullName = relationships.receiver.data.attributes.fullName,
                            )
                        ),
                        subject = attributes.subject,
                        body = attributes.body,
                        sentAt = attributes.sentAt.asFormattedLocalDateTime(),
                        seenAt = attributes.readAt?.asFormattedLocalDateTime()
                    )
                }
            }
        )
    }
}