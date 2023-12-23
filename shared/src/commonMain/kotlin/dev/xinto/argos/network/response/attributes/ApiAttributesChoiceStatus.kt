package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesChoiceStatus(
    val canChoose: Boolean,
    val isChosen: Boolean,
    val canRemoveChoice: Boolean,
    val canRechoose: Boolean,
    val chooseError: String?,
    val removeChoiceError: String?,
    val rechooseError: String?,
    val isScheduleInConflict: Boolean,
    val scheduleConflictMessage: String?
)
