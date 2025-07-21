package dev.xinto.argos.domain.user

import dev.xinto.argos.util.FormattedLocalDateTime

data class DomainUserAuthorization(
    val id: String,
    val ip: String,
    val client: String,
    val date: FormattedLocalDateTime
)