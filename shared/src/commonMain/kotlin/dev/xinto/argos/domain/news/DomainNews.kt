package dev.xinto.argos.domain.news

import kotlinx.datetime.LocalDateTime

data class DomainNews(
    val title: String,
    val text: String,
    val files: List<DomainNewsFile>,
    val publishDate: LocalDateTime,
)