package dev.xinto.argos.domain.courses

import dev.xinto.argos.domain.DomainPagedResponsePager
import dev.xinto.argos.domain.DomainResponseSource
import dev.xinto.argos.network.ArgosApi
import dev.xinto.argos.network.response.ApiResponseData
import dev.xinto.argos.network.response.attributes.ApiAttributesUser
import dev.xinto.argos.util.asFormattedLocalDateTime

class CoursesRepository(
    private val argosApi: ArgosApi
) {

    fun getCourse(courseId: String): DomainResponseSource<*, DomainCourse> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourse(courseId)
            },
            transform = {
                it.data!!.let { (id, attributes, _) ->
                    DomainCourse(
                        id = id!!,
                        name = attributes.name,
                        code = attributes.code,
                        programCode = attributes.programCode,
                        credits = attributes.credits,
                        degree = attributes.degree,
                        isEnabledForChoose = attributes.isEnabledForChoose,
                        isGeneral = attributes.isGeneral
                    )
                }
            }
        )
    }

    fun getMyCourseChoices(): DomainResponseSource<*, DomainCourseChoices> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourseChoices()
            },
            transform = {
                DomainCourseChoices(
                    allCredits = it.data!!.attributes.creditsAll,
                    currentCredits = it.data.attributes.creditsCurrent,
                    generalCredits = it.data.attributes.creditsGeneral,
                    course = it.data.relationships.choices.data.map { (_, attributes, relationships) ->
                        DomainMyCourse(
                            id = relationships.course.data.id!!,
                            name = attributes.courseName,
                            code = attributes.courseCode,
                            courseCredits = attributes.courseCredits,
                            receivedCredits = attributes.credits,
                            score = attributes.score,
                            type = if (attributes.isGeneral) DomainMyCourse.Type.General else DomainMyCourse.Type.Program,
                            status = when (attributes.status) {
                                0 -> DomainMyCourse.Status.Failed
                                1 -> DomainMyCourse.Status.Completed
                                else -> DomainMyCourse.Status.Ongoing
                            },
                            creditType = attributes.creditType
                        )
                    }
                )
            }
        )
    }

    fun getMyCurrentCourseChoices(): DomainResponseSource<*, List<DomainMyCourse>> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCurrentCourseChoices()
            },
            transform = {
                it.data!!.map { (_, attributes, relationships) ->
                    DomainMyCourse(
                        id = relationships.course.data.id!!,
                        name = attributes.courseName,
                        code = attributes.courseCode,
                        courseCredits = attributes.courseCredits,
                        receivedCredits = attributes.credits,
                        score = attributes.score,
                        type = if (attributes.isGeneral) DomainMyCourse.Type.General else DomainMyCourse.Type.Program,
                        status = when (attributes.status) {
                            0 -> DomainMyCourse.Status.Failed
                            1 -> DomainMyCourse.Status.Completed
                            else -> DomainMyCourse.Status.Ongoing
                        },
                        creditType = attributes.creditType
                    )
                }
            }
        )
    }

    fun getCourseCatalog(search: String): DomainPagedResponsePager<*, DomainCourse> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getCourseCatalog(search, page)
            },
            transform = {
                it.data!!.map {
                    DomainCourse(
                        id = it.id!!,
                        name = it.attributes.name,
                        code = it.attributes.code,
                        programCode = it.attributes.programCode,
                        credits = it.attributes.credits,
                        degree = it.attributes.degree,
                        isEnabledForChoose = it.attributes.isEnabledForChoose,
                        isGeneral = it.attributes.isGeneral,
                    )
                }
            }
        )
    }

    fun getCourseSyllabus(courseId: String): DomainResponseSource<*, DomainCourseSyllabus> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourseSyllabus(courseId)
            },  
            transform = {
                it.data!!.let { (_, attributes, _) ->
                    fun String.nullIfSyllabusSectionBlank(): String? {
                        if (removePrefix("[GEO]").isBlank()) {
                            return null
                        }
                        return this
                    }

                    DomainCourseSyllabus(
                        duration = attributes.duration.nullIfSyllabusSectionBlank(),
                        hours = attributes.hours.nullIfSyllabusSectionBlank(),
                        lecturers = attributes.lecturers.nullIfSyllabusSectionBlank(),
                        prerequisites = attributes.lecturers.nullIfSyllabusSectionBlank(),
                        methods = attributes.methods.nullIfSyllabusSectionBlank(),
                        mission = attributes.mission.nullIfSyllabusSectionBlank(),
                        topics = attributes.topics.nullIfSyllabusSectionBlank(),
                        outcomes = attributes.outcomes.nullIfSyllabusSectionBlank(),
                        evaluation = attributes.evaluation.nullIfSyllabusSectionBlank(),
                        resources = attributes.resources.nullIfSyllabusSectionBlank(),
                        otherResources = attributes.otherResources.nullIfSyllabusSectionBlank(),
                        schedule = attributes.schedule.nullIfSyllabusSectionBlank()
                    )
                }
            }
        )
    }

    fun getCourseScores(courseId: String): DomainResponseSource<*, DomainCourseScores> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourseScores(courseId)
            },
            transform = { response ->
                response.data!!.let { (_, attributes, relationships) ->
                    DomainCourseScores(
                        requiredCredits = attributes.courseCredits,
                        acquiredCredits = attributes.credits,
                        criteria = relationships.scores.data.map {
                            DomainCourseCriterion(
                                name = it.attributes.criteria,
                                score = it.attributes.score ?: 0f
                            )
                        }
                    )
                }
            }
        )
    }

    fun getCourseClassmates(courseId: String): DomainResponseSource<*, List<DomainCourseClassmate>> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourseClassmates(courseId)
            },
            transform = { response ->
                response.data!!.map {
                    DomainCourseClassmate(
                        uuid = it.attributes.uid,
                        fullName = it.attributes.fullName,
                        photoUrl = it.attributes.photoUrl
                    )
                }
            }
        )
    }

    fun getCourseGroups(courseId: String): DomainResponseSource<*, List<DomainCourseGroup>> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourseGroups(courseId)
            },
            transform = { response ->
                response.data!!.map { (id, attributes, relationships) ->
                    DomainCourseGroup(
                        id = id!!,
                        name = attributes.name,
                        isChosen = relationships.choiceStatus.data.attributes.isChosen,
                        isConflicting = relationships.choiceStatus.data.attributes.isScheduleInConflict,
                        chooseError = relationships.choiceStatus.data.attributes.chooseError,
                        rechooseError = relationships.choiceStatus.data.attributes.rechooseError,
                        removeError = relationships.choiceStatus.data.attributes.removeChoiceError,
                        lecturers = relationships.lecturers.data.map { it.toDomainLecturer() }
                    )
                }
            }
        )
    }

    fun getCourseChosenGroup(courseId: String): DomainResponseSource<*, DomainCourseChosenGroup> {
        return DomainResponseSource(
            fetch = {
                argosApi.getCourseChosenGroup(courseId)
            },
            transform = {
                it.data!!.let { (_, attributes, relationships) ->
                    DomainCourseChosenGroup(
                        lecturers = relationships.lecturers.data.map { it.toDomainLecturer() },
                        groupName = attributes.name,
                        courseName = relationships.course.data.attributes.fullName
                    )
                }
            }
        )
    }

    fun getCourseGroupWeekSchedule(
        courseId: String,
        groupId: String
    ): DomainResponseSource<*, List<DomainCourseGroupSchedule>> {
        return DomainResponseSource(
            fetch = {
                argosApi.getGroupWeekSchedule(courseId, groupId)
            },
            transform = {
                it.data!!.map { (_, attributes, relationships) ->
                    DomainCourseGroupSchedule(
                        id = buildString {
                            append(attributes.date)
                            append(attributes.startTime)
                            append(attributes.endTime)
                        },
                        date = attributes.date,
                        day = attributes.day,
                        startTime = attributes.startTime,
                        fullTime = "${attributes.startTime} - ${attributes.endTime}",
                        room = attributes.locationName,
                        lecturer = relationships.lecturers.data.first().attributes.fullName
                    )
                }
            }
        )
    }

    fun getCourseGroupSchedule(
        courseId: String,
        groupId: String
    ): DomainPagedResponsePager<*, DomainCourseGroupSchedule> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getGroupSchedule(courseId, groupId, page)
            },
            transform = {
                it.data!!.map { (_, attributes, relationships) ->
                    DomainCourseGroupSchedule(
                        id = buildString {
                            append(attributes.date)
                            append(attributes.startTime)
                            append(attributes.endTime)
                        },
                        date = attributes.date,
                        day = attributes.day,
                        startTime = attributes.startTime,
                        fullTime = "${attributes.startTime} - ${attributes.endTime}",
                        room = attributes.locationName,
                        lecturer = relationships.lecturers.data.first().attributes.fullName
                    )
                }
            }
        )
    }

    fun getCourseGroupMaterials(courseId: String): DomainPagedResponsePager<*, DomainCourseMaterial> {
        return DomainPagedResponsePager(
            fetch = { page, language ->
                argosApi.getCourseMaterials(courseId, page)
            },
            transform = {
                it.data!!.map { (_, _, relationships) ->
                    DomainCourseMaterial(
                        id = relationships.mediaFile.data.id!!,
                        name = relationships.mediaFile.data.attributes.title,
                        date = relationships.mediaFile.data.attributes.createdAt.asFormattedLocalDateTime(),
                        lecturer = relationships.user.data.toDomainLecturer(),
                        size = relationships.mediaFile.data.attributes.size,
                        downloadUrl = relationships.mediaFile.data.attributes.downloadEndpoint,
                        type = when (relationships.mediaFile.data.attributes.extension) {
                            "ppt", "pptx" -> DomainCourseMaterialType.Powerpoint
                            "doc", "docx" -> DomainCourseMaterialType.Word
                            "xls", "xlsx" -> DomainCourseMaterialType.Excel
                            "pdf" -> DomainCourseMaterialType.Pdf
                            "mp4", "mov" -> DomainCourseMaterialType.Video
                            else -> DomainCourseMaterialType.Text
                        }
                    )
                }
            }
        )
    }

    private fun ApiResponseData<ApiAttributesUser>.toDomainLecturer(): DomainCourseLecturer {
        return DomainCourseLecturer(
            uuid = attributes.uid,
            fullName = attributes.fullName,
            photoUrl = attributes.photoUrl
        )
    }
}