package dev.xinto.argos.domain.news

import dev.xinto.argos.util.FormattedLocalDateTime

data class DomainNewsPreview(
    val id: String,
    val title: String,
    val publishDate: FormattedLocalDateTime,
    val seen: Boolean
)