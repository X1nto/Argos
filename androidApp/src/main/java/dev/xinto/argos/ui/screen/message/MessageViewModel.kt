package dev.xinto.argos.ui.screen.message

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.messages.MessagesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MessageViewModel(
    savedStateHandle: SavedStateHandle,
    messagesRepository: MessagesRepository
): ViewModel() {

    private val message = messagesRepository.getMessage(
        id = savedStateHandle[KEY_MESSAGE_ID]!!,
        semId = savedStateHandle[KEY_MESSAGE_SEMESTER]!!
    )

    val state = message
        .asFlow()
        .map {
            when (it) {
                is DomainResponse.Loading -> MessageState.Loading
                is DomainResponse.Error -> MessageState.Error
                is DomainResponse.Success -> MessageState.Success(it.value)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MessageState.Loading
        )

    fun refresh() {
        message.refresh()
    }

    companion object {
        const val KEY_MESSAGE_ID = "id"
        const val KEY_MESSAGE_SEMESTER = "sem"
    }
}