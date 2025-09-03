package dev.xinto.argos.network.response.attributes

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesNews(
    val title: String,
    val titleColor: String,
    val text: String,
    val createdAt: Instant
)