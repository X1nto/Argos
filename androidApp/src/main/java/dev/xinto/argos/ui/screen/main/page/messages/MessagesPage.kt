package dev.xinto.argos.ui.screen.main.page.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.xinto.argos.R
import dev.xinto.argos.domain.messages.DomainMessage
import dev.xinto.argos.domain.messages.DomainMessageSource
import dev.xinto.argos.ui.component.SecondaryTabPager
import org.koin.androidx.compose.getViewModel

@Composable
fun MessagesPage(
    modifier: Modifier = Modifier,
    onMessageClick: (messageId: String, semesterId: String) -> Unit
) {
    val viewModel: MessagesViewModel = getViewModel()
    val inbox = viewModel.inboxMessages.collectAsLazyPagingItems()
    val outbox = viewModel.outboxMessages.collectAsLazyPagingItems()
    val tab by viewModel.tab.collectAsStateWithLifecycle()
    MessagesPage(
        modifier = modifier,
        tab = tab,
        onTabChange = viewModel::switchTab,
        inbox = inbox,
        outbox = outbox,
        onMessageClick = onMessageClick
    )
}

@Composable
fun MessagesPage(
    tab: MessagesTab,
    onTabChange: (MessagesTab) -> Unit,
    inbox: LazyPagingItems<DomainMessage>,
    outbox: LazyPagingItems<DomainMessage>,
    onMessageClick: (messageId: String, semesterId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SecondaryTabPager(
        modifier = modifier,
        selectedIndex = MessagesTab.entries.indexOf(tab),
        onIndexSelect = {
            onTabChange(MessagesTab.entries[it])
        }
    ) {
        tabPage(tabContent = { Text(stringResource(R.string.messages_tab_inbox)) }) {
            MessagesList(
                modifier = Modifier.fillMaxSize(),
                messages = inbox,
                onMessageClick = onMessageClick
            )
        }
        tabPage(tabContent = { Text(stringResource(R.string.messages_tab_outbox)) }) {
            MessagesList(
                modifier = Modifier.fillMaxSize(),
                messages = outbox,
                onMessageClick = onMessageClick
            )
        }
    }
}

@Composable
private fun MessagesList(
    messages: LazyPagingItems<DomainMessage>,
    onMessageClick: (messageId: String, semesterId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = messages.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator()
            }

            is LoadState.NotLoading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        if (messages.loadState.prepend is LoadState.Loading) {
                            CircularProgressIndicator()
                        }
                    }
                    items(
                        count = messages.itemCount,
                        key = messages.itemKey { it.id }
                    ) {
                        val message = messages[it]!!
                        if (it != 0) {
                            HorizontalDivider()
                        }
                        ListItem(
                            modifier = Modifier
                                .clickable {
                                    onMessageClick(message.id, message.semId)
                                },
                            overlineContent = {
                                Text(message.sentAt.relativeDateTime)
                            },
                            headlineContent = {
                                Text(message.subject)
                            },
                            supportingContent = {
                                when (val source = message.source) {
                                    is DomainMessageSource.Inbox -> {
                                        Text(source.sender.fullName)
                                    }
                                    is DomainMessageSource.Outbox -> {
                                        Text(source.receiver.fullName)
                                    }
                                    is DomainMessageSource.General -> {}
                                }
                            }
                        )
                    }
                    item {
                        when (val appendState = messages.loadState.append) {
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
                Text(state.error.stackTraceToString())
            }
        }
    }
}