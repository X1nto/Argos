package dev.xinto.argos.di

import dev.xinto.argos.ui.screen.login.LoginViewModel
import dev.xinto.argos.ui.screen.main.MainViewModel
import dev.xinto.argos.ui.screen.main.dialog.user.UserViewModel
import dev.xinto.argos.ui.screen.main.page.home.HomeViewModel
import dev.xinto.argos.ui.screen.main.page.messages.MessagesViewModel
import dev.xinto.argos.ui.screen.main.page.news.NewsViewModel
import dev.xinto.argos.ui.screen.message.MessageViewModel
import dev.xinto.argos.ui.screen.notifications.NotificationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MessagesViewModel)
    viewModelOf(::NewsViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::UserViewModel)
    viewModel {
        MessageViewModel(it.get(), get())
    }
}