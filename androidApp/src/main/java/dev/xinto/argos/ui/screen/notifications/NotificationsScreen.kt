package dev.xinto.argos.ui.screen.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.xinto.argos.R
import dev.xinto.argos.domain.notifications.DomainNotification
import dev.xinto.argos.ui.component.SegmentedListItem
import dev.xinto.argos.ui.component.itemsSegmented
import org.koin.androidx.compose.getViewModel

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    val viewModel: NotificationsViewModel = getViewModel()
    val notifications = viewModel.notifications.collectAsLazyPagingItems()
    BackHandler(onBack = onBackClick)
    NotificationsScreen(
        modifier = modifier,
        notifications = notifications,
        onNotificationClick = {

        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    notifications: LazyPagingItems<DomainNotification>,
    onNotificationClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val refreshState = notifications.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator()
                }

                is LoadState.NotLoading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsSegmented(notifications.itemCount) { type, i ->
                            val notification = notifications[i]!!
                            SegmentedListItem(
                                type = type,
                                onClick = {
                                    onNotificationClick(notification.id)
                                },
                                overlineContent = {
                                    Text(notification.alertDate.toString())
                                },
                                headlineContent = {
                                    Text(notification.text)
                                },
                            )
                        }
                        item {
                            when (val appendState = notifications.loadState.append) {
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
}