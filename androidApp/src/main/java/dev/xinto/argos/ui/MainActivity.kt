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
import dev.xinto.argos.domain.auth.AuthRepository
import dev.xinto.argos.ui.screen.ArgosNavigation
import dev.xinto.argos.ui.screen.login.LoginScreen
import dev.xinto.argos.ui.screen.main.MainScreen
import dev.xinto.argos.ui.screen.notifications.NotificationsScreen
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val authRepository: AuthRepository by inject()

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
                    val isLoggedIn by authRepository.observeLoggedIn().collectAsStateWithLifecycle(initialValue = false)
                    LaunchedEffect(isLoggedIn) {
                        if (isLoggedIn) {
                            rootNavController.replaceAll(ArgosNavigation.Main)
                        } else {
                            rootNavController.replaceAll(ArgosNavigation.Login)
                        }
                    }
                    AnimatedNavHost(controller = rootNavController) {
                        when (it) {
                            is ArgosNavigation.Login -> {
                                LoginScreen(modifier = Modifier.fillMaxSize())
                            }
                            is ArgosNavigation.Main -> {
                                MainScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onNotificationsClick = {
                                        rootNavController.navigate(ArgosNavigation.Notifications)
                                    }
                                )
                            }
                            is ArgosNavigation.Notifications -> {
                                NotificationsScreen(
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