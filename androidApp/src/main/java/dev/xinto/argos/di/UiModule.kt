package dev.xinto.argos.di

import dev.xinto.argos.ui.screen.course.page.classmates.ClassmatesViewModel
import dev.xinto.argos.ui.screen.course.page.groups.GroupsViewModel
import dev.xinto.argos.ui.screen.course.page.materials.MaterialsViewModel
import dev.xinto.argos.ui.screen.course.page.scores.ScoresViewModel
import dev.xinto.argos.ui.screen.course.page.syllabus.SyllabusViewModel
import dev.xinto.argos.ui.screen.login.LoginViewModel
import dev.xinto.argos.ui.screen.main.MainViewModel
import dev.xinto.argos.ui.screen.main.dialog.user.UserInfoViewModel
import dev.xinto.argos.ui.screen.main.page.home.HomeViewModel
import dev.xinto.argos.ui.screen.main.page.messages.MessagesViewModel
import dev.xinto.argos.ui.screen.main.page.news.NewsViewModel
import dev.xinto.argos.ui.screen.message.MessageViewModel
import dev.xinto.argos.ui.screen.notifications.NotificationsViewModel
import dev.xinto.argos.ui.screen.user.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MessagesViewModel)
    viewModelOf(::NewsViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::UserInfoViewModel)
    viewModelOf(::MessageViewModel)
    viewModelOf(::SyllabusViewModel)
    viewModelOf(::GroupsViewModel)
    viewModelOf(::ScoresViewModel)
    viewModelOf(::MaterialsViewModel)
    viewModelOf(::ClassmatesViewModel)
    viewModelOf(::UserViewModel)
}