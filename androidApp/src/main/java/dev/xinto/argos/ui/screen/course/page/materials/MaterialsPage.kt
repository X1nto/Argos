package dev.xinto.argos.ui.screen.course.page.materials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.xinto.argos.R
import dev.xinto.argos.domain.courses.DomainCourseMaterial
import dev.xinto.argos.domain.courses.DomainCourseMaterialType
import dev.xinto.argos.ui.component.SegmentedListItem
import dev.xinto.argos.ui.component.itemsSegmented
import org.koin.androidx.compose.getStateViewModel

@Composable
fun MaterialsPage(
    courseId: String,
    modifier: Modifier = Modifier
) {
    val viewModel: MaterialsViewModel = getStateViewModel(state = {
        bundleOf(MaterialsViewModel.KEY_COURSE_ID to courseId)
    })
    val items = viewModel.state.collectAsLazyPagingItems()
    MaterialsPage(
        items = items,
        modifier = modifier
    )
}

@Composable
fun MaterialsPage(
    items: LazyPagingItems<DomainCourseMaterial>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val refreshState = items.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator()
            }

            is LoadState.NotLoading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    itemsSegmented(
                        count = items.itemCount,
                        key = { items[it]!!.id }
                    ) { type, i ->
                        val material = items[i]!!
                        SegmentedListItem(
                            type = type,
                            overlineContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {

                                    Text(text = material.date.toString())
                                    Text("•")
                                    Text(text = material.size.toString())
                                }
                            },
                            headlineContent = {
                                Text(material.name)
                            },
                            supportingContent = {
                                Text(material.lecturer.fullName)
                            },
                            leadingContent = {
                                val icon = remember(material.type) {
                                    when (material.type) {
                                        DomainCourseMaterialType.Word -> R.drawable.ic_word
                                        DomainCourseMaterialType.Excel -> R.drawable.ic_excel
                                        DomainCourseMaterialType.Powerpoint -> R.drawable.ic_powerpoint
                                        DomainCourseMaterialType.Pdf -> R.drawable.ic_pdf
                                        else -> R.drawable.ic_file
                                    }
                                }
                                Icon(
                                    modifier = Modifier.size(28.dp),
                                    painter = painterResource(icon),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            is LoadState.Error -> {
                Text(refreshState.error.stackTraceToString())
            }
        }
    }
}