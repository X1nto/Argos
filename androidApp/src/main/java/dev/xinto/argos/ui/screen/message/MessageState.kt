package dev.xinto.argos.ui.screen.message

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.messages.DomainMessage

sealed interface MessageState {

    data object Loading : MessageState

    @Immutable
    data class Success(val message: DomainMessage) : MessageState

    data object Error : MessageState

}