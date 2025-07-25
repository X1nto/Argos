package dev.xinto.argos.domain.lectures

import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.network.ArgosApi
import kotlinx.coroutines.flow.Flow

class LecturesRepository(
    private val argosApi: ArgosApi
) {

    private val lectures = DomainResponseSource({
        argosApi.getCurrentSchedule()
    }) {
        it.data!!.associate { (_, scheduleAttributes, scheduleRelationships ) ->
            scheduleAttributes.name to scheduleRelationships.schedules.data.map { (_, scheduleAttributes, scheduleRelationships) ->
                DomainLectureInfo(
                    id = scheduleRelationships.course.data.id!!,
                    time = scheduleRelationships.hour.data.attributes.times,
                    room = scheduleAttributes.locationName,
                    name = scheduleRelationships.course.data.attributes.name,
                    lecturer = scheduleAttributes.info.let { lecturer ->
                        if (!lecturer.isNullOrBlank()) {
                            return@let lecturer
                        }

                        val mainLecturer = scheduleRelationships.lecturers.data.getOrNull(0)?.attributes?.fullName
                        if (mainLecturer != null) {
                            return@let mainLecturer
                        }

                        val groupLecturer = scheduleRelationships.group.data.relationships?.lecturers?.data?.get(0)?.attributes?.fullName
                        if (groupLecturer != null) {
                            return@let groupLecturer
                        }

                        return@let ""
                    }
                )
            }
        }
    }

    /**
     * @return A [Flow] of map of day to a list of [DomainLectureInfo]
     */
    fun observeLectures() = lectures.flow

}