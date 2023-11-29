package dev.xinto.argos.network.response.attributes

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesMessage(
    val subject: String,
    val body: String,
    val seen: Boolean,
    val semId: Int,
    val messageType: Int,
    val senderProfileId: Int,
    val sentAt: Instant,
    val readAt: Instant,
)