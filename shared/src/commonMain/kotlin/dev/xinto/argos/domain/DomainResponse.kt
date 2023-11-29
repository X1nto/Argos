package dev.xinto.argos.domain

import dev.xinto.argos.local.settings.ArgosLanguage
import dev.xinto.argos.local.settings.ArgosSettings
import dev.xinto.argos.network.response.ApiResponseBase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.jvm.JvmInline

sealed interface DomainResponse<out T> {
    @JvmInline
    value class Success<T>(val value: T) : DomainResponse<T>

    @JvmInline
    value class Error(val error: String) : DomainResponse<Nothing>

    data object Loading : DomainResponse<Nothing>
}

inline fun <T : ApiResponseBase, R> DomainResponseSource(
    crossinline fetch: suspend (ArgosLanguage) -> T,
    crossinline transform: (T) -> R,
): DomainResponseSource<R> {
    return DomainResponseSource {
        try {
            val result = fetch(it)
            if (result.message == "ok") {
                DomainResponse.Success(transform(result))
            } else {
                DomainResponse.Error(result.errors!!.general[0])
            }
        } catch (e: Exception) {
            DomainResponse.Error(e.message ?: e.stackTraceToString())
        }
    }
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

class DomainResponseSource<T>(
    private inline val compute: suspend (ArgosLanguage) -> DomainResponse<T>
): KoinComponent {

    private val settings: ArgosSettings = get()

    private val state = MutableStateFlow<DomainResponse<T>>(DomainResponse.Loading)

    fun asFlow(): Flow<DomainResponse<T>> {
        return combine(state, settings.observeLanguage()) { state, language ->
            return@combine if (state is DomainResponse.Loading) {
                compute(language).also {
                    this.state.value = it
                }
            } else {
                state
            }
        }
    }

    fun refresh() {
        state.value = DomainResponse.Loading
    }

    suspend operator fun invoke() = asFlow().first()
}