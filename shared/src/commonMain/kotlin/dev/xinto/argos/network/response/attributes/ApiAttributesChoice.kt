package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesChoice(
    val credits: Int,
    val courseName: String,
    val courseCode: String,
    val courseCredits: Int,
    val score: Float,
    val isLatChoice: Boolean,
    val latScore: String,
    val status: Int,
    val isGeneral: Boolean,
    val creditType: String
)