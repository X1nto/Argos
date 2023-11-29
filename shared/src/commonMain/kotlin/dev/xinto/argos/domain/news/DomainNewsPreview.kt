package dev.xinto.argos.domain.news

import kotlinx.datetime.LocalDateTime

data class DomainNewsPreview(
    val id: String,
    val title: String,
    val publishDate: LocalDateTime,
    val seen: Boolean
)