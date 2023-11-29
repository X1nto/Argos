package dev.xinto.argos.ui.screen.main.page.home

import androidx.compose.runtime.Immutable
import dev.xinto.argos.domain.lectures.DomainLectureInfo

@Immutable
sealed interface HomeState {

    @Immutable
    data object Loading : HomeState

    @Immutable
    data class Success(
        val selectedDay: Int,
        val lectures: Map<String, List<DomainLectureInfo>>,
    ) : HomeState {

        companion object {
            val mockData = Success(
                selectedDay = 0,
                lectures = buildMap {
                    val days =
                        listOf("ორშაბათი", "სამშაბათი", "ოთხშაბათი", "ხუთშაბათი", "პარასკევი")
                    days.forEachIndexed { index, day ->
                        val lectures = List(index + 1) {
                            DomainLectureInfo(
                                time = "09:00 - 09:50",
                                room = "T304",
                                name = "კალკულუს I",
                                lecturer = "ნათია საზანდრიშვილი"
                            )
                        }
                        put(day, lectures)
                    }
                }
            )
        }
    }

    data object Error : HomeState

}

