package dev.xinto.argos.domain.news

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.util.asFormattedLocalDateTime

class NewsRepository(private val argosApi: ArgosApi) {

    fun getAllNews(): DomainPagedResponsePager<*, DomainNewsPreview> {
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

    fun getNews(id: String): DomainResponseSource<*, DomainNews> {
        return DomainResponseSource(
            fetch = { language ->
                argosApi.getNews(id)
            },
            transform = {
                it.data!!.let { (_, attributes, relationships) ->
                    DomainNews(
                        title = attributes.title,
                        text = attributes.text,
                        publishDate = attributes.createdAt.asFormattedLocalDateTime(),
                        files = relationships.files.data.map { (_, _, relationships) ->
                            DomainNewsFile(
                                name = relationships.mediaFile.data.attributes.name,
                                url = relationships.mediaFile.data.attributes.downloadEndpoint,
                                size = relationships.mediaFile.data.attributes.size
                            )
                        }
                    )
                }
            }
        )
    }

}