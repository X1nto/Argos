package dev.xinto.argos.domain.user

sealed interface DomainUserProfile {
    val id: String
    val fullName: String
    val photoUrl: String?
    val email: String?
}

data class DomainStudentProfile(
    override val id: String,
    override val fullName: String,
    override val photoUrl: String?,
    override val email: String?,
    val degree: DomainStudentDegree
) : DomainUserProfile

data class DomainLecturerProfile(
    override val id: String,
    override val fullName: String,
    override val photoUrl: String?,
    override val email: String?,
    val degree: DomainLecturerDegree
) : DomainUserProfile

enum class DomainStudentDegree(internal val value: Int) {
    Bachelor(1),
    Master(2),
    Doctor(3)
}

enum class DomainLecturerDegree(internal val value: Int) {
    Professor(1),
    AssociatedProfessor(2),
    AssistantProfessor(3),
    InvitedLecturer(4),
    EmeritusProfessor(5),
    RespProfessor(6)
}