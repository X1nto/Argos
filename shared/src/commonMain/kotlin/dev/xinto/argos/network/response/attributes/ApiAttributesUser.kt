package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

private interface BaseUser {
    val uid: String
    val firstName: String
    val lastName: String
    val fullName: String
    val email: String?
    val avatar: String
    val photoUrl: String?
    val gender: String
    val mobileNumber: String?
    val mobileNumber2: String?
    val homeNumber: String?
}

@Serializable
data class ApiAttributesUser(
    override val uid: String,
    override val firstName: String,
    override val lastName: String,
    override val fullName: String,
    override val email: String?,
    override val avatar: String,
    override val photoUrl: String?,
    override val gender: String,
    override val mobileNumber: String?,
    override val mobileNumber2: String?,
    override val homeNumber: String?,
) : BaseUser

@Serializable
data class ApiAttributesAuthUser(
    override val uid: String,
    override val firstName: String,
    override val lastName: String,
    override val fullName: String,
    override val email: String,
    override val avatar: String,
    override val photoUrl: String?,
    override val gender: String,
    override val mobileNumber: String,
    override val mobileNumber2: String,
    override val homeNumber: String,
    val personalNumber: String,
    val birthDate: String,
    val juridicalAddress: String,
    val actualAddress: String
) : BaseUser