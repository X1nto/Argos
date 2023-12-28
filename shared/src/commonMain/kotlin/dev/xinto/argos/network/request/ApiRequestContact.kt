package dev.xinto.argos.network.request

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequestContact(
    val mobileNumber: String,
    val mobileNumber2: String,
    val homeNumber: String,
    val actualAddress: String
)