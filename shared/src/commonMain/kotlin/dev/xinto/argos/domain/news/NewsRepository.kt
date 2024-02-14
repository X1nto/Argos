package dev.xinto.argos.domain.news

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.util.asFormattedLocalDateTime

class NewsRepository(private val argosApi: ArgosApi) {

    fun getNews(): DomainPagedResponsePager<*, DomainNewsPreview> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getNews(page)
            },
            transform = {
                it.data!!.map { (id, attributes, relationships) ->
                    DomainNewsPreview(
                        id = id!!,
                        title = attributes.title,
                        publishDate = attributes.createdAt.asFormattedLocalDateTime(),
                        seen = relationships.userViewStatus.data.attributes.isViewed
                    )
                }
            }
        )
    }

}