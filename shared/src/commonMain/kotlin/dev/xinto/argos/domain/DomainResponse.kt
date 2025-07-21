package dev.xinto.argos.domain

import dev.xinto.argos.local.settings.ArgosLanguage
import dev.xinto.argos.local.settings.ArgosSettings
import dev.xinto.argos.network.response.ApiResponseBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class DomainResponse<out T> {
    data class Success<T>(val value: T) : DomainResponse<T>()

    data class Error(val error: String) : DomainResponse<Nothing>()

    data object Loading : DomainResponse<Nothing>()
}

inline fun <T1, T2, R> combine(
    flow1: Flow<DomainResponse<T1>>,
    flow2: Flow<DomainResponse<T2>>,
    crossinline transform: (T1, T2) -> R,
): Flow<DomainResponse<R>> {
    return combine(flow1, flow2) { f1, f2 ->
        when {
            f1 is DomainResponse.Loading || f2 is DomainResponse.Loading -> DomainResponse.Loading
            f1 is DomainResponse.Error  -> f1
            f2 is DomainResponse.Error -> f2
            else -> {
                val f1value = (f1 as DomainResponse.Success).value
                val f2value = (f2 as DomainResponse.Success).value
                DomainResponse.Success(transform(f1value, f2value))
            }
        }
    }
}

class DomainResponseSource<T : ApiResponseBase, R>(
    private val fetch: suspend (ArgosLanguage) -> T,
    private val transform: (T) -> R,
): KoinComponent {

    private val settings: ArgosSettings = get()

    private val state = MutableStateFlow<DomainResponse<R>>(DomainResponse.Loading)

    val flow = combine(state, settings.observeLanguage()) { state, language ->
        state.takeUnlessOr(predicate = { it is DomainResponse.Loading }) {
            try {
                val result = fetch(language)
                if (result.message == "ok") {
                    DomainResponse.Success(transform(result))
                } else {
                    DomainResponse.Error(result.errors!!.general[0])
                }
            } catch (e: Exception) {
                DomainResponse.Error(e.message ?: e.stackTraceToString())
            }.also {
                this.state.value = it
            }
        }
    }.flowOn(Dispatchers.IO)

    fun refresh() {
        state.value = DomainResponse.Loading
    }

    @OptIn(ExperimentalContracts::class)
    private inline fun <T> T.takeUnlessOr(
        predicate: (T) -> Boolean,
        unless: () -> T
    ): T {
        contract {
            callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
            callsInPlace(unless, InvocationKind.AT_MOST_ONCE)
        }
        return if (!predicate(this)) this else unless()
    }

    suspend operator fun invoke() = flow.first()
}