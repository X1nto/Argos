package dev.xinto.argos.ui.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.UserImage
import dev.xinto.argos.ui.screen.main.dialog.user.UserDialog
import dev.xinto.argos.ui.screen.main.page.home.HomePage
import dev.xinto.argos.ui.screen.main.page.home.HomeState
import dev.xinto.argos.ui.screen.main.page.messages.MessagesPage
import dev.xinto.argos.ui.screen.main.page.news.NewsPage
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    onNotificationsClick: () -> Unit,
    onMessageClick: (messageId: String, semesterId: String) -> Unit,
    onCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = getViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    MainScreen(
        modifier = modifier,
        state = state,
        onNotificationsClick = onNotificationsClick,
        onLogoutClick = viewModel::logout,
        onMessageClick = onMessageClick,
        onCourseClick = onCourseClick
    )
}

@Composable
fun MainScreen(
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMessageClick: (messageId: String, semesterId: String) -> Unit,
    onCourseClick: (String) -> Unit,
    state: MainState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController<MainNavigation>(MainNavigation.Home)
    val page by remember {
        derivedStateOf {
            navController.backstack.entries.last().destination
        }
    }
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    var userDialogShown by rememberSaveable { mutableStateOf(false) }
    MainScreenScaffold(
        modifier = modifier,
        state = state,
        page = page,
        onNavigate = navController::replaceAll,
        onNotificationsClick = onNotificationsClick,
        onUserClick = { userDialogShown = true }
    ) { padding ->
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            controller = navController
        ) {
            CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner!!) {
                when (it) {
                    is MainNavigation.Home -> {
                        HomePage(
                            modifier = Modifier.fillMaxSize(),
                            onCourseClick = onCourseClick
                        )
                    }

                    is MainNavigation.Messages -> {
                        MessagesPage(
                            modifier = Modifier.fillMaxSize(),
                            onMessageClick = onMessageClick
                        )
                    }

                    is MainNavigation.News -> {
                        NewsPage(modifier = Modifier.fillMaxSize())
                    }
                }

                if (userDialogShown) {
                    UserDialog(
                        onDismiss = { userDialogShown = false },
                        onBalanceNavigate = { /*TODO*/ },
                        onLibraryNavigate = { /*TODO*/ },
                        onSettingsNavigate = { /*TODO*/ },
                        onLogoutClick = {
                            userDialogShown = false
                            onLogoutClick()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScaffold(
    state: MainState,
    page: MainNavigation,
    onNavigate: (MainNavigation) -> Unit,
    onNotificationsClick: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_notifications),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = onUserClick,
                        enabled = state is MainState.Success
                    ) {
                        when (state) {
                            is MainState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }

                            is MainState.Success -> {
                                UserImage(url = state.userInfo.photoUrl)
                            }

                            is MainState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                MainNavigation.bottomBarItems.forEach {
                    val selected = it == page
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            onNavigate(it)
                        },
                        icon = {
                            Icon(
                                painter = when (selected) {
                                    true -> painterResource(it.iconSelected)
                                    false -> painterResource(it.icon)
                                },
                                contentDescription = stringResource(it.title)
                            )
                        },
                        label = {
                            Text(stringResource(it.title))
                        },
                    )
                }
            }
        },
        content = content
    )
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MainScreen_Loading_Preview() {
    ArgosTheme {
        MainScreenScaffold(
            state = MainState.Loading,
            page = MainNavigation.Home,
            onNavigate = {},
            onNotificationsClick = {},
            onUserClick = {}
        ) {
            HomePage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = HomeState.Success.mockData,
                onDaySelect = {},
                onCourseClick = {}
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MainScreen_Error_Preview() {
    ArgosTheme {
        MainScreenScaffold(
            state = MainState.Error,
            page = MainNavigation.Home,
            onNavigate = {},
            onNotificationsClick = {},
            onUserClick = {}
        ) {
            HomePage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = HomeState.Success.mockData,
                onDaySelect = {},
                onCourseClick = {}
            )
        }
    }
}