package dev.xinto.argos.domain.news

import dev.xinto.argos.util.FormattedLocalDateTime

data class DomainNews(
    val title: String,
    val text: String,
    val files: List<DomainNewsFile>,
    val publishDate: FormattedLocalDateTime,
)