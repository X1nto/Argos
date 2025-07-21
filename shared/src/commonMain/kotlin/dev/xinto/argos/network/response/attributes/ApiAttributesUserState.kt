package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesUserState(
    val billingBalance: Int?,
    val libraryBalance: Int?,
    val newsUnread: Int?,
    val messagesUnread: Int?,
    val notificationsUnread: Int?,
    val surveyExists: Boolean,
    val rechoose: Boolean
)