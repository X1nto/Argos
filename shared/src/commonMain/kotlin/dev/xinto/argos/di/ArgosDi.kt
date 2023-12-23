package dev.xinto.argos.di

import dev.xinto.argos.domain.courses.CoursesRepository
import dev.xinto.argos.domain.lectures.LecturesRepository
import dev.xinto.argos.domain.messages.MessagesRepository
import dev.xinto.argos.domain.news.NewsRepository
import dev.xinto.argos.domain.notifications.NotificationsRepository
import dev.xinto.argos.domain.semester.SemesterRepository
import dev.xinto.argos.domain.user.UserRepository
import dev.xinto.argos.network.ArgosApi
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

internal expect object ArgosDiPlatform {

    val LocalModule: Module

}

internal object ArgosDi {

    val LocalModule = module {
        includes(ArgosDiPlatform.LocalModule)
    }

    val NetworkModule = module {
        singleOf(::ArgosApi)
    }

    val DomainModule = module {
        singleOf(::UserRepository)
        singleOf(::LecturesRepository)
        singleOf(::SemesterRepository)
        singleOf(::MessagesRepository)
        singleOf(::NewsRepository)
        singleOf(::NotificationsRepository)
        singleOf(::CoursesRepository)
    }

    operator fun invoke(): List<Module> {
        return listOf(LocalModule, DomainModule, NetworkModule)
    }
}

fun initArgosDi(platformInit: KoinAppDeclaration) {
    startKoin {
        platformInit()

        modules(ArgosDi())
    }
}