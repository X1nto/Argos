package dev.xinto.argos.domain.notifications

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.network.ArgosApi
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class NotificationsRepository(private val argosApi: ArgosApi) {

    fun getNotifications(): DomainPagedResponsePager<*, DomainNotification> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getNotifications(page)
            },
            transform = {
                it.data!!.map { (id, attributes) ->
                    DomainNotification(
                        id = id!!,
                        text = attributes.title,
                        alertDate = attributes.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
                        seen = attributes.readAt != null
                    )
                }
            }
        )
    }

}