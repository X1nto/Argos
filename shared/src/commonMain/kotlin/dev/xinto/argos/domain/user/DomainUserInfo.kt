package dev.xinto.argos.domain.user

import kotlin.jvm.JvmInline

data class DomainUserInfo(
    val fullName: String,
    val email: String,
    val photoUrl: String?,
    val degree: Int,
)