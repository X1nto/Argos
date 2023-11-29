package dev.xinto.argos.domain

import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.xinto.argos.local.settings.ArgosLanguage
import dev.xinto.argos.local.settings.ArgosSettings
import dev.xinto.argos.network.response.ApiResponsePaged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DomainPagedResponsePager<T, R : Any>(
    private val fetch: suspend (page: Int, ArgosLanguage) -> ApiResponsePaged<T>,
    private val transform: suspend (ApiResponsePaged<T>) -> List<R>
): KoinComponent {

    private val settings: ArgosSettings = get()

    private val invalidatingFactory = InvalidatingPagingSourceFactory {
        DomainPagedResponseSource(
            fetch = {
                fetch(it, settings.getLanguage())
            },
            transform = transform
        )
    }
    private val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5
        ),
        pagingSourceFactory = invalidatingFactory
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val flow = settings.observeLanguage()
        .onEach {
            invalidate()
        }.flatMapLatest {
            pager.flow
        }

    fun invalidate() {
        invalidatingFactory.invalidate()
    }
}

class DomainPagedResponseSource<T, R : Any>(
    private val fetch: suspend (page: Int) -> ApiResponsePaged<T>,
    private val transform: suspend (ApiResponsePaged<T>) -> List<R>
) : PagingSource<Int, R>() {

    override fun getRefreshKey(state: PagingState<Int, R>): Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, R> {
        val page = params.key ?: 1
        return try {
            val fetched = fetch(page)
            if (fetched.message == "ok") {
                val transformed = transform(fetched)
                LoadResult.Page(
                    data = transformed,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page == fetched.meta?.pagination?.totalPages) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(fetched.errors?.general?.get(0)))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}