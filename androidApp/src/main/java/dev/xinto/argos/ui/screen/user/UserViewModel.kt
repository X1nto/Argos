package dev.xinto.argos.ui.screen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.user.DomainUserInfo
import dev.xinto.argos.domain.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val tempInfo = MutableStateFlow<DomainUserInfo?>(null)

    private val userInfo = userRepository.getUserInfo()

    val state = combine(userInfo.asFlow(), tempInfo) { userInfo, tempUserInfo ->
        when (userInfo) {
            is DomainResponse.Loading -> UserState.Loading
            is DomainResponse.Success -> {
                val info = tempUserInfo ?: userInfo.value
                UserState.Success(info)
            }
            is DomainResponse.Error -> UserState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserState.Loading
    )

    private val _saving = MutableStateFlow(false)
    val saving = _saving.asStateFlow()

    val canSave = combine(userInfo.asFlow(), tempInfo) { userInfo, tempInfo ->
        userInfo is DomainResponse.Success && tempInfo != null && userInfo.value != tempInfo
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun updateMobile1(newNumber: String) {
        updateInfo {
            it.copy(mobileNumber1 = newNumber)
        }
    }

    fun updateMobile2(newNumber: String) {
        updateInfo {
            it.copy(mobileNumber2 = newNumber)
        }
    }

    fun updateHomeNumber(newNumber: String) {
        updateInfo {
            it.copy(homeNumber = newNumber)
        }
    }

    fun updateCurrentAddress(newAddress: String) {
        updateInfo {
            it.copy(currentAddress = newAddress)
        }
    }

    fun save() {
        if (saving.value) return
        if (tempInfo.value == null) return

        viewModelScope.launch {
            _saving.value = true
            if (userRepository.updateUserInfo(tempInfo.value!!)) {
                userInfo.refresh()
                tempInfo.value = null
            }
            _saving.value = false
        }
    }

    private inline fun updateInfo(update: (DomainUserInfo) -> DomainUserInfo) {
        tempInfo.update {
            if (it == null) {
                val user = (state.value as? UserState.Success)?.user
                user?.let(update)
            } else {
                it.let(update)
            }
        }
    }
}