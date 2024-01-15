package dev.xinto.argos.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavTransitionScope
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import dev.xinto.argos.domain.user.UserRepository
import dev.xinto.argos.ui.screen.ArgosNavigation
import dev.xinto.argos.ui.screen.course.CourseScreen
import dev.xinto.argos.ui.screen.login.LoginScreen
import dev.xinto.argos.ui.screen.main.MainScreen
import dev.xinto.argos.ui.screen.message.MessageScreen
import dev.xinto.argos.ui.screen.notifications.NotificationsScreen
import dev.xinto.argos.ui.screen.meuserprofile.MeUserProfileScreen
import dev.xinto.argos.ui.screen.userprofile.UserProfileScreen
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val userRepository: UserRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ArgosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootNavController = rememberNavController<ArgosNavigation>(listOf())
                    val isLoggedIn by userRepository.observeLoggedIn()
                        .collectAsStateWithLifecycle(initialValue = false)
                    LaunchedEffect(isLoggedIn) {
                        if (isLoggedIn) {
                            rootNavController.replaceAll(ArgosNavigation.Main)
                        } else {
                            rootNavController.replaceAll(ArgosNavigation.Login)
                        }
                    }
                    AnimatedNavHost(
                        modifier = Modifier.fillMaxSize(),
                        controller = rootNavController,
                        transitionSpec = ArgosTransitionSpec
                    ) { destination ->
                        when (destination) {
                            is ArgosNavigation.Login -> {
                                LoginScreen(modifier = Modifier.fillMaxSize())
                            }

                            is ArgosNavigation.Main -> {
                                MainScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onNotificationsClick = {
                                        rootNavController.navigate(ArgosNavigation.Notifications)
                                    },
                                    onUserClick = {
                                        rootNavController.navigate(ArgosNavigation.MeUserProfile)
                                    },
                                    onMessageClick = { messageId, semesterId ->
                                        rootNavController.navigate(
                                            ArgosNavigation.Message(
                                                id = messageId,
                                                semesterId = semesterId
                                            )
                                        )
                                    },
                                    onCourseClick = {
                                        rootNavController.navigate(ArgosNavigation.Course(it))
                                    }
                                )
                            }

                            is ArgosNavigation.Notifications -> {
                                NotificationsScreen(
                                    onBackClick = rootNavController::pop
                                )
                            }

                            is ArgosNavigation.MeUserProfile -> {
                                MeUserProfileScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onBackNavigate = rootNavController::pop
                                )
                            }

                            is ArgosNavigation.Message -> {
                                MessageScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    messageId = destination.id,
                                    semesterId = destination.semesterId,
                                    onBackClick = rootNavController::pop
                                )
                            }

                            is ArgosNavigation.Course -> {
                                CourseScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    courseId = destination.id,
                                    onBackClick = rootNavController::pop,
                                    onUserClick = {
                                        rootNavController.navigate(ArgosNavigation.UserProfile(it))
                                    }
                                )
                            }

                            is ArgosNavigation.UserProfile -> {
                                UserProfileScreen(
                                    userId = destination.id,
                                    modifier = Modifier.fillMaxSize(),
                                    onBackNavigate = rootNavController::pop
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private val ArgosTransitionSpec = object : NavTransitionSpec<ArgosNavigation> {

        override fun NavTransitionScope.getContentTransform(
            action: NavAction,
            from: ArgosNavigation,
            to: ArgosNavigation
        ): ContentTransform {
            return when (action) {
                NavAction.Navigate -> {
                    fadeIn() + scaleIn(
                        initialScale = 0.9f
                    ) togetherWith fadeOut() + scaleOut(
                        targetScale = 1.1f
                    )
                }
                NavAction.Pop -> {
                    fadeIn() + scaleIn(
                        initialScale = 1.1f
                    ) togetherWith fadeOut() + scaleOut(
                        targetScale = 0.9f
                    )
                }
                else -> fadeIn(tween()) togetherWith fadeOut(tween())
            }
        }

        override fun NavTransitionScope.fromEmptyBackstack(
            action: NavAction,
            to: ArgosNavigation
        ): ContentTransform {
            return fadeIn(tween()) togetherWith fadeOut(tween())
        }

        override fun NavTransitionScope.toEmptyBackstack(
            action: NavAction,
            from: ArgosNavigation
        ): ContentTransform {
            return fadeIn(tween()) togetherWith fadeOut(tween())
        }
    }
}