package dev.xinto.argos.ui.screen.course.page.classmates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.SegmentedListItem
import dev.xinto.argos.ui.component.UserImage
import dev.xinto.argos.ui.component.itemsSegmented
import org.koin.androidx.compose.getStateViewModel

@Composable
fun ClassmatesPage(
    modifier: Modifier = Modifier,
    courseId: String
) {
    val viewModel: ClassmatesViewModel = getStateViewModel(state = {
        bundleOf(ClassmatesViewModel.KEY_COURSE_ID to courseId)
    })
    val state by viewModel.state.collectAsStateWithLifecycle()
    ClassmatesPage(
        state = state,
        modifier = modifier
    )
}

@Composable
fun ClassmatesPage(
    modifier: Modifier = Modifier,
    state: ClassmatesState
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ClassmatesState.Loading -> {
                CircularProgressIndicator()
            }

            is ClassmatesState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsSegmented(
                        items = state.classmates,
                        key = { it.uuid }
                    ) { type, item ->
                        SegmentedListItem(
                            type = type,
                            headlineContent = {
                                Text(item.fullName)
                            },
                            leadingContent = {
                                UserImage(
                                    modifier = Modifier.size(40.dp),
                                    url = item.photoUrl
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_navigate_next),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }
            }

            is ClassmatesState.Error -> {
                Text("Error")
            }
        }
    }
}