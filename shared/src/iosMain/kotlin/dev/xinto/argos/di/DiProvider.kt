package dev.xinto.argos.di

import dev.xinto.argos.domain.courses.CoursesRepository
import dev.xinto.argos.domain.lectures.LecturesRepository
import dev.xinto.argos.domain.messages.MessagesRepository
import dev.xinto.argos.domain.news.NewsRepository
import dev.xinto.argos.domain.notifications.NotificationsRepository
import dev.xinto.argos.domain.semester.SemesterRepository
import dev.xinto.argos.domain.user.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DiProvider : KoinComponent {

    val userRepository by inject<UserRepository>()
    val newsRepository by inject<NewsRepository>()
    val messagesRepository by inject<MessagesRepository>()
    val notificationsRepository by inject<NotificationsRepository>()
    val coursesRepository by inject<CoursesRepository>()
    val lecturesRepository by inject<LecturesRepository>()
    val semesterRepository by inject<SemesterRepository>()

    fun initKoin() {
        initArgosDi {  }
    }

}