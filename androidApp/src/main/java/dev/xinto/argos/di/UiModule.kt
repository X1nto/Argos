package dev.xinto.argos.di

import androidx.lifecycle.SavedStateHandle
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
import dev.xinto.argos.ui.screen.meuserprofile.MeUserProfileViewModel
import dev.xinto.argos.ui.screen.userprofile.UserProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MessagesViewModel)
    viewModelOf(::NewsViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::UserInfoViewModel)

    // FIXME Koin is dumb
    viewModel { (messageId: String, semesterId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[MessageViewModel.KEY_MESSAGE_ID] = messageId
        savedStateHandle[MessageViewModel.KEY_MESSAGE_SEMESTER] = semesterId
        MessageViewModel(savedStateHandle, get())
    }
    viewModel { (courseId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[SyllabusViewModel.KEY_COURSE_ID] = courseId
        SyllabusViewModel(savedStateHandle, get())
    }
    viewModel { (courseId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[GroupsViewModel.KEY_COURSE_ID] = courseId
        GroupsViewModel(savedStateHandle, get())
    }
    viewModel { (courseId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[ScoresViewModel.KEY_COURSE_ID] = courseId
        ScoresViewModel(savedStateHandle, get())
    }
    viewModel { (courseId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[MaterialsViewModel.KEY_COURSE_ID] = courseId
        MaterialsViewModel(savedStateHandle, get())
    }
    viewModel { (courseId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[ClassmatesViewModel.KEY_COURSE_ID] = courseId
        ClassmatesViewModel(savedStateHandle, get())
    }
    viewModelOf(::MeUserProfileViewModel)
    viewModel { (userId: String, savedStateHandle: SavedStateHandle) ->
        savedStateHandle[UserProfileViewModel.KEY_USER_ID] = userId
        UserProfileViewModel(savedStateHandle, get())
    }
}