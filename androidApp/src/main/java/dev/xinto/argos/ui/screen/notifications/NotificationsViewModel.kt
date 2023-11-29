package dev.xinto.argos.ui.screen.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.xinto.argos.domain.notifications.NotificationsRepository

class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    val notifications = notificationsRepository
        .getNotifications()
        .flow
        .cachedIn(viewModelScope)

}