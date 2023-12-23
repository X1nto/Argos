package dev.xinto.argos.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.AnimatedNavHost
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
                        controller = rootNavController
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
                                    onBackClick = rootNavController::pop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}