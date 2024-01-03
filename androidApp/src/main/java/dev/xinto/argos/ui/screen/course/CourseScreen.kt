package dev.xinto.argos.ui.screen.course

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.PrimaryScrollableTabPager
import dev.xinto.argos.ui.screen.course.page.classmates.ClassmatesPage
import dev.xinto.argos.ui.screen.course.page.groups.GroupsPage
import dev.xinto.argos.ui.screen.course.page.materials.MaterialsPage
import dev.xinto.argos.ui.screen.course.page.scores.ScoresPage
import dev.xinto.argos.ui.screen.course.page.syllabus.SyllabusPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    courseId: String,
) {
    BackHandler(onBack = onBackClick)

    //Groups selected by default
    val (selectedTab, setSelectedTab) = rememberSaveable { mutableIntStateOf(1) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.course_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { padding ->
        PrimaryScrollableTabPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            selectedIndex = selectedTab,
            onIndexSelect = setSelectedTab
        ) {
            tabPages(
                items = CourseNavigation.tabs,
                tabContent = { tab ->
                    val stringRes = remember(tab) {
                        when (tab) {
                            is CourseNavigation.Syllabus -> R.string.course_tab_syllabus
                            is CourseNavigation.Groups -> R.string.course_tab_groups
                            is CourseNavigation.Scores -> R.string.course_tab_scores
                            is CourseNavigation.Materials -> R.string.course_tab_materials
                            is CourseNavigation.Classmates -> R.string.course_tab_classmates
                        }
                    }
                    Text(stringResource(stringRes))
                }
            ) { tab ->
                when (tab) {
                    is CourseNavigation.Syllabus -> {
                        SyllabusPage(
                            courseId = courseId,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is CourseNavigation.Groups -> {
                        GroupsPage(
                            courseId = courseId,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is CourseNavigation.Scores -> {
                        ScoresPage(
                            courseId = courseId,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is CourseNavigation.Materials -> {
                        MaterialsPage(
                            courseId = courseId,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is CourseNavigation.Classmates -> {
                        ClassmatesPage(
                            courseId = courseId,
                            modifier = Modifier.fillMaxSize(),
                            onClassmateClick = onUserClick
                        )
                    }
                }
            }
        }
    }
}