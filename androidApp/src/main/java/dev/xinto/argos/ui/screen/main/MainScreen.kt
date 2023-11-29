package dev.xinto.argos.ui.screen.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import dev.xinto.argos.R
import dev.xinto.argos.ui.screen.main.page.home.HomePage
import dev.xinto.argos.ui.screen.main.page.home.HomeState
import dev.xinto.argos.ui.screen.main.page.messages.MessagesPage
import dev.xinto.argos.ui.screen.main.page.news.NewsPage
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = getViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    MainScreen(
        modifier = modifier,
        state = state,
        onNotificationsClick = onNotificationsClick
    )
}

@Composable
fun MainScreen(
    onNotificationsClick: () -> Unit,
    state: MainState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController<MainNavigation>(MainNavigation.Home)
    val page by remember {
        derivedStateOf {
            navController.backstack.entries.last().destination
        }
    }
    MainScreenScaffold(
        modifier = modifier,
        state = state,
        page = page,
        onNavigate = navController::replaceAll,
        onNotificationsClick = onNotificationsClick,
    ) { padding ->
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            controller = navController
        ) {
            when (it) {
                is MainNavigation.Home -> {
                    HomePage(modifier = Modifier.fillMaxSize())
                }
                is MainNavigation.Messages -> {
                    MessagesPage(modifier = Modifier.fillMaxSize())
                }
                is MainNavigation.News -> {
                    NewsPage(modifier = Modifier.fillMaxSize())
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
                        onClick = { /*TODO*/ },
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
                                if (state.userInfo.photoUrl != null) {
                                    val context = LocalContext.current
                                    val imageRequest = remember(context) {
                                        ImageRequest.Builder(context)
                                            .decoderFactory(SvgDecoder.Factory())
                                            .data(state.userInfo.photoUrl)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .build()
                                    }
                                    AsyncImage(
                                        model = imageRequest,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_account),
                                            contentDescription = null
                                        )
                                    }
                                }
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
            onNotificationsClick = {}
        ) {
            HomePage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = HomeState.Success.mockData,
                onDaySelect = {}
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
            onNotificationsClick = {}
        ) {
            HomePage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = HomeState.Success.mockData,
                onDaySelect = {}
            )
        }
    }
}