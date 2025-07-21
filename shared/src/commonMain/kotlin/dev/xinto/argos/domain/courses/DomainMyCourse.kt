package dev.xinto.argos.domain.courses

data class DomainMyCourse(
    val id: String,
    val name: String,
    val code: String,
    val receivedCredits: Int,
    val courseCredits: Int,
    val score: Float,
    val status: Status,
    val type: Type,
    val creditType: String
) {
    enum class Status {
        Ongoing,
        Completed,
        Failed
    }

    enum class Type {
        General,
        Program
    }
}
