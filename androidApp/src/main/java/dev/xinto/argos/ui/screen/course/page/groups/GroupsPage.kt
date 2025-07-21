package dev.xinto.argos.ui.screen.course.page.groups

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.domain.DomainResponse
import dev.xinto.argos.domain.courses.DomainCourseGroupSchedule
import dev.xinto.argos.ui.component.ExpandableListItem
import dev.xinto.argos.ui.component.Table
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getStateViewModel

@Composable
fun GroupsPage(
    courseId: String,
    modifier: Modifier = Modifier,
) {
    val viewModel: GroupsViewModel = getStateViewModel(state = {
        bundleOf(GroupsViewModel.KEY_COURSE_ID to courseId)
    })
    val state by viewModel.state.collectAsStateWithLifecycle()
    val expanded by viewModel.expanded.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    GroupsPage(
        modifier = modifier,
        state = state,
        expanded = expanded,
        onExpandChange = viewModel::setExpanded,
        schedules = schedules
    )
}

@Composable
fun GroupsPage(
    state: GroupsState,
    expanded: Set<String>,
    onExpandChange: (String, Boolean) -> Unit,
    schedules: Map<String, DomainResponse<List<DomainCourseGroupSchedule>>>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is GroupsState.Loading -> {
                CircularProgressIndicator()
            }

            is GroupsState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        items = state.groups,
                        key = { it.id }
                    ) { group ->
                        ExpandableListItem(
                            modifier = Modifier.fillParentMaxWidth(),
                            expanded = expanded.contains(group.id),
                            onExpandedChange = {
                                onExpandChange(group.id, it)
                            },
                            expandedContent = {
                                val schedule = schedules[group.id]
                                if (schedule != null) {
                                    when (schedule) {
                                        is DomainResponse.Success -> {
                                            val date = remember(schedule) {
                                                schedule.value.map { it.day to it.fullTime }
                                            }
                                            val room = remember(schedule) {
                                                schedule.value.map { it.room }
                                            }
                                            val info = remember(schedule) {
                                                schedule.value.mapNotNull { it.lecturer }
                                            }
                                            Table(
                                                shape = RectangleShape,
                                                border = null
                                            ) {
                                                column(
                                                    items = date,
                                                    header = {
                                                        Text(stringResource(R.string.group_table_time))
                                                    }
                                                ) { (day, time) ->
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(day)
                                                        Text(time)
                                                    }
                                                }
                                                column(
                                                    items = room,
                                                    header = {
                                                        Text(stringResource(R.string.group_table_room))
                                                    }
                                                ) {
                                                    Text(it)
                                                }
                                                if (info.isNotEmpty()) {
                                                    column(
                                                        items = info,
                                                        header = {
                                                            Text(stringResource(R.string.group_table_info))
                                                        }
                                                    ) {
                                                        Text(it, textAlign = TextAlign.Center)
                                                    }
                                                }
                                            }
                                        }

                                        is DomainResponse.Error -> {

                                        }

                                        else -> {}
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            },
                            headlineContent = {
                                Text(group.name)
                            },
                            supportingContent = {
                                CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
                                    Row(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .height(32.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (group.isChosen) {
                                            AssistChip(
                                                onClick = {

                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_change),
                                                        contentDescription = null
                                                    )
                                                },
                                                label = {
                                                    Text("Rechoose")
                                                },
                                                colors = AssistChipDefaults.elevatedAssistChipColors(),
                                                enabled = group.rechooseError == null
                                            )
                                            AssistChip(
                                                onClick = { /*TODO*/ },
                                                label = {
                                                    Text("Remove")
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_delete),
                                                        contentDescription = null
                                                    )
                                                },
                                                colors = AssistChipDefaults.elevatedAssistChipColors(),
                                                enabled = group.removeError == null
                                            )
                                        } else {
                                            AssistChip(
                                                onClick = { /*TODO*/ },
                                                label = {
                                                    Text("Choose")
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_add_circle),
                                                        contentDescription = null
                                                    )
                                                },
                                                colors = AssistChipDefaults.elevatedAssistChipColors(),
                                                enabled = group.chooseError == null
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            is GroupsState.Error -> {
                Text("error")
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun GroupsPage_Success_Preview() {
    ArgosTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GroupsPage(
                modifier = Modifier.fillMaxSize(),
                state = GroupsState.mockSuccess,
                expanded = setOf(),
                onExpandChange = { _, _ -> },
                schedules = emptyMap()
            )
        }
    }
}