package dev.xinto.argos.ui.screen.main.page.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.messages.MessagesRepository
import dev.xinto.argos.domain.semester.DomainSemester
import dev.xinto.argos.domain.semester.SemesterRepository
import dev.xinto.argos.local.settings.ArgosSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MessagesViewModel(
    private val semesterRepository: SemesterRepository,
    private val messagesRepository: MessagesRepository,
    private val settings: ArgosSettings
) : ViewModel() {

    private val _tab = MutableStateFlow(MessagesTab.Inbox)
    val tab = _tab.asStateFlow()

    val semesters = semesterRepository.semesters.flow.map {
        if (it is DomainResponse.Success) {
            return@map it.value.reversed()
        }

        return@map emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedSemester = MutableStateFlow<DomainSemester?>(null)

    val selectedSemester = combine(_selectedSemester, semesterRepository.semesters.flow) { selected, all ->
        if (selected != null) return@combine selected

        when (all) {
            is DomainResponse.Error -> null
            is DomainResponse.Loading -> null
            is DomainResponse.Success<*> -> {
                val semesters = all.value as List<DomainSemester>
                semesters.firstOrNull { it.active }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val inboxMessages = mapActiveSemesterToPagingData {
        messagesRepository.getInboxMessages(it.id).flow
    }.cachedIn(viewModelScope)

    val outboxMessages = mapActiveSemesterToPagingData {
        messagesRepository.getOutboxMessages(it.id).flow
    }.cachedIn(viewModelScope)

    fun switchTab(tab: MessagesTab) {
        _tab.value = tab
    }

    fun selectSemester(semester: DomainSemester) {
        _selectedSemester.value = semester
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private inline fun <T : Any> mapActiveSemesterToPagingData(
        crossinline onSuccess: (DomainSemester) -> Flow<PagingData<T>>
    ): Flow<PagingData<T>> {
        return combine(_selectedSemester, semesterRepository.semesters.flow) { selected, all ->
            if (selected != null) return@combine DomainResponse.Success(selected)

            when (all) {
                is DomainResponse.Error -> DomainResponse.Error(all.error)
                DomainResponse.Loading -> DomainResponse.Loading
                is DomainResponse.Success<*> -> {
                    val semesters = all.value as List<DomainSemester>
                    val activeSemester = semesters.firstOrNull { it.active }
                    if (activeSemester != null) {
                        DomainResponse.Success(activeSemester)
                    } else {
                        DomainResponse.Error("Active semester not found")
                    }
                }
            }
        }.flatMapLatest {
            when (it) {
                is DomainResponse.Error -> {
                    flowOf(
                        PagingData.empty(
                            LoadStates(
                                refresh = LoadState.Error(Exception(it.error)),
                                append = LoadState.NotLoading(false),
                                prepend = LoadState.NotLoading(false)
                            )
                        )
                    )
                }

                is DomainResponse.Loading -> {
                    flowOf(
                        PagingData.empty(
                            LoadStates(
                                refresh = LoadState.Loading,
                                append = LoadState.NotLoading(false),
                                prepend = LoadState.NotLoading(false)
                            )
                        )
                    )
                }

                is DomainResponse.Success -> onSuccess(it.value)
            }
        }
    }

}