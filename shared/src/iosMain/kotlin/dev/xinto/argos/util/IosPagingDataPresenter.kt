package dev.xinto.argos.util

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// FIXME https://github.com/touchlab/SKIE/issues/97
@Throws(Exception::class)
@FlowInterop.Disabled
fun <T: Any> CreateIosPagingItems(flow: Flow<PagingData<T>>): IosPagingItems<T> {
    return IosPagingItems(flow)
}

class IosPagingItems<T: Any>(
    @FlowInterop.Disabled
    private val flow: Flow<PagingData<T>>
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val pagingDataPresenter = object : PagingDataPresenter<T>(
        mainContext = Dispatchers.Main,
        cachedPagingData = (flow as? SharedFlow<PagingData<T>>)?.replayCache?.firstOrNull()
    ) {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) {
            updateSnapshotList()
        }
    }

    private val _snapshotList = MutableStateFlow(pagingDataPresenter.snapshot())
    val snapshotList = _snapshotList.asStateFlow() as StateFlow<List<T>>

    val loadState = pagingDataPresenter.loadStateFlow
        .filterNotNull()
        .stateIn(
            scope = coroutineScope,
            initialValue = CombinedLoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.Loading,
                    prepend = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                )
            ),
            started = SharingStarted.Eagerly
        )

    fun updateSnapshotList() {
        _snapshotList.value = pagingDataPresenter.snapshot()
    }

    operator fun get(index: Int): T {
        pagingDataPresenter[index]
        return snapshotList.value[index]
    }

    fun refresh() {
        pagingDataPresenter.refresh()
    }

    fun retry() {
        pagingDataPresenter.retry()
    }

    fun dispose() {
        coroutineScope.cancel()
    }

    init {
        coroutineScope.launch {
            flow.collectLatest {
                pagingDataPresenter.collectFrom(it)
            }
        }
    }
}