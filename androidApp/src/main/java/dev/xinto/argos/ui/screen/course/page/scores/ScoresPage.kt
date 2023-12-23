package dev.xinto.argos.ui.screen.course.page.scores

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.ColumnSize
import dev.xinto.argos.ui.component.Table
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getStateViewModel

@Composable
fun ScoresPage(
    courseId: String,
    modifier: Modifier = Modifier
) {
    val viewModel: ScoresViewModel = getStateViewModel(state = {
        bundleOf(ScoresViewModel.KEY_COURSE_ID to courseId)
    })
    val state by viewModel.state.collectAsStateWithLifecycle()
    ScoresPage(
        modifier = modifier,
        state = state
    )
}

@Composable
fun ScoresPage(
    state: ScoresState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ScoresState.Loading -> {
                CircularProgressIndicator()
            }

            is ScoresState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val criteria = remember(state.scores) {
                        state.scores.scores.map {
                            it.first
                        }
                    }
                    val scores = remember(state.scores) {
                        state.scores.scores.map {
                            it.second
                        }
                    }
                    Table(modifier = Modifier.fillMaxWidth()) {
                        column(
                            items = criteria,
                            size = ColumnSize.Variable(1f),
                            header = { Text(stringResource(R.string.scores_table_criterion)) }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it
                            )
                        }
                        column(
                            items = scores,
                            size = ColumnSize.Fixed(85.dp),
                            header = { Text(stringResource(R.string.scores_table_score)) }
                        ) {
                            Text(it.toString())
                        }
                    }


                    Surface(
                        tonalElevation = 5.dp,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.scores_credits))

                            CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
                                SegmentedContainer(
                                    segment1 = {
                                        Text(state.scores.acquiredCredits.toString())
                                    },
                                    segment2 = {
                                        Text(state.scores.requiredCredits.toString())
                                    }
                                )
                            }
                        }
                    }
                }
            }

            is ScoresState.Error -> {
                Text("Error")
            }
        }
    }
}

@Composable
private fun SegmentedContainer(
    segment1: @Composable () -> Unit,
    segment2: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Transparent,
                    start = Offset(size.width / 2 + 10f, 0f),
                    end = Offset(size.width / 2 - 10f, size.height + 10f),
                    blendMode = BlendMode.Clear,
                    strokeWidth = 8f
                )
            },
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                segment1()
            }
            Box(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                segment2()
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ScoresPage_Success_Preview() {
    ArgosTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScoresPage(
                modifier = Modifier.fillMaxSize(),
                state = ScoresState.mockSuccess
            )
        }
    }
}