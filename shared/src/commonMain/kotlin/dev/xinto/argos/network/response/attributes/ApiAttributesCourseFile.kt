package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesCourseFile(
    val url: String?,
    val name: String,
    val isViewableInWeb: Boolean
)
