package dev.xinto.argos.ui.screen.course.page.syllabus

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.MaterialHtmlText2
import org.koin.androidx.compose.getStateViewModel

@Composable
fun SyllabusPage(
    courseId: String,
    modifier: Modifier = Modifier
) {
    val viewModel: SyllabusViewModel = getStateViewModel(state = {
        bundleOf(SyllabusViewModel.KEY_COURSE_ID to courseId)
    })
    val state by viewModel.state.collectAsStateWithLifecycle()
    SyllabusPage(
        state = state,
        modifier = modifier
    )
}

@Composable
fun SyllabusPage(
    state: SyllabusState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is SyllabusState.Loading -> {
                CircularProgressIndicator()
            }

            is SyllabusState.Success -> {
                val context = LocalContext.current
                val text = remember(context) {
                    buildString {
                        fun append(@StringRes title: Int, body: String?) {
                            if (body != null) {
                                append("<div style=\"display:flex; flex-direction:column; gap:8px;\">")
                                append("<h3>")
                                append(context.getString(title))
                                append("</h3>")
//                                    appendLine()
                                append(body)
                                append("</div>")
                            }
                        }
                        append("<div style=\"display:flex; flex-direction:column; gap:12px;\">")
                        append(R.string.syllabus_section_duration, state.syllabus.duration)
                        append(R.string.syllabus_section_hours, state.syllabus.hours)
                        append(R.string.syllabus_section_lecturers, state.syllabus.lecturers)
                        append(
                            R.string.syllabus_section_prerequisites,
                            state.syllabus.prerequisites
                        )
                        append(R.string.syllabus_section_methods, state.syllabus.methods)
                        append(R.string.syllabus_section_mission, state.syllabus.mission)
                        append(R.string.syllabus_section_topics, state.syllabus.topics)
                        append(R.string.syllabus_section_outcomes, state.syllabus.outcomes)
                        append(R.string.syllabus_section_evaluation, state.syllabus.evaluation)
                        append(R.string.syllabus_section_resources, state.syllabus.resources)
                        append(
                            R.string.syllabus_section_resources_other,
                            state.syllabus.otherResources
                        )
                        append(R.string.syllabus_section_schedule, state.syllabus.schedule)
                        append("</div>")
                    }
                }
                MaterialHtmlText2(
                    text = text,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                )
            }

            is SyllabusState.Error -> {
                Text(text = "Error")
            }
        }
    }
}