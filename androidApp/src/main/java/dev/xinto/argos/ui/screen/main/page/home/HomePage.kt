package dev.xinto.argos.ui.screen.main.page.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.SecondaryScrollableTabPager
import dev.xinto.argos.ui.component.SegmentedListItem
import dev.xinto.argos.ui.component.itemsSegmented
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onCourseClick: (String) -> Unit,
) {
    val viewModel: HomeViewModel = getViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomePage(
        modifier = modifier,
        state = state,
        onDaySelect = viewModel::selectDay,
        onCourseClick = onCourseClick
    )
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onDaySelect: (Int) -> Unit,
    onCourseClick: (String) -> Unit,
    state: HomeState,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is HomeState.Loading -> {
                CircularProgressIndicator()
            }

            is HomeState.Success -> {
                SecondaryScrollableTabPager(
                    modifier = Modifier.fillMaxSize(),
                    selectedIndex = state.selectedDay,
                    onIndexSelect = onDaySelect
                ) {
                    tabPages(
                        items = state.lectures,
                        tabContent = { Text(it) }
                    ) { lectures ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            itemsSegmented(lectures) { type, lecture ->
                                SegmentedListItem(
                                    onClick = {
                                        onCourseClick(lecture.id)
                                    },
                                    type = type,
                                    overlineContent = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(text = lecture.time)
                                            Text("•")
                                            Text(text = lecture.room)
                                        }
                                    },
                                    headlineContent = {
                                        Text(lecture.name)
                                    },
                                    supportingContent = {
                                        Text(lecture.lecturer)
                                    },
                                    trailingContent = {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_navigate_next),
                                            contentDescription = null
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }

            is HomeState.Error -> {
                Text("Error")
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
private fun HomeScreen_Success_Preview() {
    ArgosTheme {
        var state by remember {
            mutableStateOf(HomeState.Success.mockData)
        }
        Surface(color = MaterialTheme.colorScheme.background) {
            HomePage(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onDaySelect = {
                    state = state.copy(selectedDay = it)
                },
                onCourseClick = {}
            )
        }
    }
}