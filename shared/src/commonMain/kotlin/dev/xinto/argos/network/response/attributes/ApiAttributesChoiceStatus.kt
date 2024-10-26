package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesChoiceStatus(
    val isChosen: Boolean,
    val canChoose: Boolean = false,
    val canRemoveChoice: Boolean = false,
    val canRechoose: Boolean = false,
    val chooseError: String? = null,
    val removeChoiceError: String? = null,
    val rechooseError: String? = null,
    val isScheduleInConflict: Boolean = false,
    val scheduleConflictMessage: String? = null
)
