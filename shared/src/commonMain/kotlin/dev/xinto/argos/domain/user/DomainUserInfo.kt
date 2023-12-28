package dev.xinto.argos.domain.user

data class DomainUserInfo(
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val birthDate: String,
    val idNumber: String,
    val email: String,
    val mobileNumber1: String,
    val mobileNumber2: String,
    val homeNumber: String,
    val juridicalAddress: String,
    val currentAddress: String,
    val photoUrl: String?,
    val degree: Int,
)