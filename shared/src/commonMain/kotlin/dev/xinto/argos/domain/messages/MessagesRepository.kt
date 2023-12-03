package dev.xinto.argos.domain.messages

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.network.ArgosApi
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MessagesRepository(
    private val argosApi: ArgosApi
) {

    fun getInboxMessages(semesterId: String): DomainPagedResponsePager<*, DomainMessageReceived> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getInboxMessages(semesterId, page)
            },
            transform = {
                it.data!!.map { (id, attributes, relationships) ->
                    DomainMessageReceived(
                        id = id!!,
                        sender = DomainMessageUser(
                            uid = relationships.sender.data.attributes.uid,
                            fullName = relationships.sender.data.attributes.fullName,
                        ),
                        subject = attributes.subject,
                        date = attributes.sentAt.toLocalDateTime(TimeZone.currentSystemDefault()),
                        seen = attributes.seen,
                        semId = attributes.semId.toString()
                    )
                }
            }
        )
    }

    fun getOutboxMessages(semesterId: String): DomainPagedResponsePager<*, DomainMessageSent> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getOutboxMessages(semesterId, page)
            },
            transform = {
                it.data!!.map { (id, attributes, relationships) ->
                    DomainMessageSent(
                        id = id!!,
                        receiver = DomainMessageUser(
                            uid = relationships.receiver.data.attributes.uid,
                            fullName = relationships.receiver.data.attributes.fullName,
                        ),
                        subject = attributes.subject,
                        date = attributes.sentAt.toLocalDateTime(TimeZone.currentSystemDefault()),
                        semId = attributes.semId.toString()
                    )
                }
            }
        )
    }

    fun getMessage(id: String, semId: String): DomainResponseSource<DomainMessage> {
        return DomainResponseSource(
            fetch = {
                argosApi.getMessage(id, semId)
            },
            transform = {
                it.data!!.let { (id, attributes, relationships) ->
                    DomainMessage(
                        id = id!!,
                        sender = DomainMessageUser(
                            uid = relationships.sender.data.attributes.uid,
                            fullName = relationships.sender.data.attributes.fullName,
                        ),
                        receiver = DomainMessageUser(
                            uid = relationships.receiver.data.attributes.uid,
                            fullName = relationships.receiver.data.attributes.fullName,
                        ),
                        subject = attributes.subject,
                        body = attributes.body,
                        date = attributes.sentAt.toLocalDateTime(TimeZone.currentSystemDefault()),
                        semId = attributes.semId.toString()
                    )
                }
            }
        )
    }
}