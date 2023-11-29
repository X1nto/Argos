package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesCourse(
    val name: String,
    val fullName: String,
    val groupName: String,
    val code: String,
    val programCode: String,
    val credits: Int,
    val degree: Int,
    val isEnabledForChoose: Boolean,
    val isGeneral: Boolean
)