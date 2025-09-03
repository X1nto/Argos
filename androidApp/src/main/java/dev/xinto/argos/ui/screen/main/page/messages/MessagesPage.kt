package dev.xinto.argos.ui.screen.main.page.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.xinto.argos.R
import dev.xinto.argos.domain.messages.DomainMessage
import dev.xinto.argos.domain.messages.DomainMessageSource
import dev.xinto.argos.domain.semester.DomainSemester
import org.koin.androidx.compose.getViewModel
import kotlin.math.absoluteValue

@Composable
fun MessagesPage(
    modifier: Modifier = Modifier,
    onMessageClick: (messageId: String, semesterId: String) -> Unit
) {
    val viewModel: MessagesViewModel = getViewModel()
    val inbox = viewModel.inboxMessages.collectAsLazyPagingItems()
    val outbox = viewModel.outboxMessages.collectAsLazyPagingItems()
    val tab by viewModel.tab.collectAsStateWithLifecycle()

    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val selectedSemester by viewModel.selectedSemester.collectAsStateWithLifecycle()

    MessagesPage(
        modifier = modifier,
        tab = tab,
        onTabChange = viewModel::switchTab,
        inbox = inbox,
        outbox = outbox,
        semesters = semesters,
        selectedSemester = selectedSemester,
        onSemesterSelect = viewModel::selectSemester,
        onMessageClick = onMessageClick
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MessagesPage(
    modifier: Modifier = Modifier,
    tab: MessagesTab,
    onTabChange: (MessagesTab) -> Unit,
    inbox: LazyPagingItems<DomainMessage>,
    outbox: LazyPagingItems<DomainMessage>,
    onMessageClick: (messageId: String, semesterId: String) -> Unit,
    semesters: List<DomainSemester>,
    selectedSemester: DomainSemester?,
    onSemesterSelect: (DomainSemester) -> Unit,
) {
    var showSemesterSheet by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        val context = LocalContext.current
        ButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            overflowIndicator = {}
        ) {
            toggleableItem(
                checked = tab == MessagesTab.Inbox,
                onCheckedChange = { onTabChange(MessagesTab.Inbox) },
                label = context.getString(R.string.messages_tab_inbox),
                weight = 1f
            )
            toggleableItem(
                checked = tab == MessagesTab.Outbox,
                onCheckedChange = { onTabChange(MessagesTab.Outbox) },
                label = context.getString(R.string.messages_tab_outbox),
                weight = 1f
            )
            customItem(
                buttonGroupContent = {
                    FilledIconButton(
                        onClick = { showSemesterSheet = true },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        enabled = selectedSemester != null
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar_month),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )
        }

        when (tab) {
            MessagesTab.Inbox -> {
                MessagesList(
                    modifier = Modifier.fillMaxSize(),
                    messages = inbox,
                    onMessageClick = onMessageClick
                )
            }
            MessagesTab.Outbox -> {
                MessagesList(
                    modifier = Modifier.fillMaxSize(),
                    messages = outbox,
                    onMessageClick = onMessageClick
                )
            }
        }

    }

    if (showSemesterSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showSemesterSheet = false }
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                semesters.forEach { semester ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSemesterSheet = false
                                onSemesterSelect(semester)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = semester.name,
                        )
                        RadioButton(
                            selected = selectedSemester == semester,
                            onClick = null
                        )
                    }
                }
            }
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
                if (messages.itemCount == 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(72.dp),
                            painter = painterResource(R.drawable.ic_mail_off),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.messages_empty),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
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
                            Message(
                                modifier = Modifier.clickable { onMessageClick(message.id, message.semId) },
                                message = message
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

            }

            is LoadState.Error -> {
                Text(state.error.stackTraceToString())
            }
        }
    }
}

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: DomainMessage
) {
    val user = remember(message.source) {
        when (val source = message.source) {
            is DomainMessageSource.General -> source.sender.fullName
            is DomainMessageSource.Inbox -> source.sender.fullName
            is DomainMessageSource.Outbox -> source.receiver.fullName
        }
    }
    val cleanBody = remember(message.body) {
        message.body
            .replace("<br />", "")
            .replace('\n', ' ')
    }
    Row(
        modifier = modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrettyPlaceholderImage(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.Top),
            fullName = user
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(user)
                Text(
                    text = message.sentAt.relativeDateTime,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                text = message.subject,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = cleanBody,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PrettyPlaceholderImage(
    modifier: Modifier = Modifier,
    fullName: String
) {
    val avatarColors = remember {
        listOf(
            0xFFE57373.toInt(), // Red
            0xFFF06292.toInt(), // Pink
            0xFFBA68C8.toInt(), // Purple
            0xFF64B5F6.toInt(), // Blue
            0xFF4DB6AC.toInt(), // Teal
            0xFF81C784.toInt(), // Green
            0xFFFFB74D.toInt(), // Orange
            0xFFA1887F.toInt(), // Brown
            0xFF90A4AE.toInt()  // Blue Grey
        )
    }
    val avatarName = fullName[0].uppercase()
    val avatarColor = remember(fullName) {
        val hash = fullName.hashCode().absoluteValue
        val colorHex = avatarColors[hash % avatarColors.size]
        Color(colorHex)
    }
    Box(
       modifier = modifier.background(color = avatarColor, shape = CircleShape),
       contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatarName,
            color = MaterialTheme.colorScheme.surface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}