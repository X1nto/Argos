package dev.xinto.argos.ui.screen.main.page.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.xinto.argos.R
import dev.xinto.argos.domain.news.DomainNewsPreview
import dev.xinto.argos.ui.component.SegmentedListItem
import dev.xinto.argos.ui.component.itemsSegmented
import org.koin.androidx.compose.getViewModel

@Composable
fun NewsPage(modifier: Modifier = Modifier) {
    val viewModel: NewsViewModel = getViewModel()
    val news = viewModel.news.collectAsLazyPagingItems()
    NewsPage(
        modifier = modifier,
        news = news,
        onNewsClick = {

        }
    )
}

@Composable
fun NewsPage(
    modifier: Modifier = Modifier,
    news: LazyPagingItems<DomainNewsPreview>,
    onNewsClick: (String) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val refreshState = news.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator()
            }

            is LoadState.NotLoading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsSegmented(news.itemCount) { type, i ->
                        val newsPreview = news[i]!!
//                        if (i != 0) {
//                            HorizontalDivider()
//                        }
                        SegmentedListItem(
                            type = type,
                            onClick = {
                                onNewsClick(newsPreview.id)
                            },
                            overlineContent = {
                                Text(newsPreview.publishDate.fullDateTime)
                            },
                            headlineContent = {
                                Text(newsPreview.title)
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_navigate_next),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    item {
                        when (val appendState = news.loadState.append) {
                            is LoadState.Loading -> {
                                CircularProgressIndicator()
                            }

                            is LoadState.Error -> {
                                Text(appendState.error.stackTraceToString())
                            }

                            else -> {}
                        }
                    }
                }
            }

            is LoadState.Error -> {
                Text(refreshState.error.stackTraceToString())
            }
        }
    }
}