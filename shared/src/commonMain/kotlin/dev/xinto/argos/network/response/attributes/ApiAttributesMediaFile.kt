package dev.xinto.argos.network.response.attributes

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesMediaFile(
    val name: String,
    val title: String,
    val originalName: String,
    val downloadEndpoint: String,
    val mimeType: String,
    val size: Long,
    val extension: String,
    val downloads: Int,
    val iconClass: String,
    val isPublic: Boolean,
    val createdAt: Instant
)