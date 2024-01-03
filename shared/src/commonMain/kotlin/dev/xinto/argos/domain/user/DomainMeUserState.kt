package dev.xinto.argos.domain.user

data class DomainMeUserState(
    val billingBalance: String,
    val libraryBalance: String,
    val newsUnread: Int,
    val messagesUnread: Int,
    val notificationsUnread: Int,
)