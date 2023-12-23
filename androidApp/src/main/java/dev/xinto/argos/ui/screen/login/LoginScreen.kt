package dev.xinto.argos.ui.screen.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.ui.component.AppAuthRequest
import dev.xinto.argos.ui.component.AppAuthServiceConfiguration
import dev.xinto.argos.ui.component.rememberAppAuthRequest
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = getViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loginRequestHandler = rememberAppAuthRequest(
        request = AppAuthRequest(
            config = AppAuthServiceConfiguration(
                authEndpoint = "https://accounts.google.com/o/oauth2/v2/auth",
                tokenEndpoint = "https://oauth2.googleapis.com/token"
            ),
            responseType = "code",
            clientId = "590553979193-1jilroobo7m2p55apfk1icuo0pktc9ru.apps.googleusercontent.com",
            redirectUri = "com.googleusercontent.apps.590553979193-1jilroobo7m2p55apfk1icuo0pktc9ru:/oauth2callback",
            scope = "email profile"
        ),
        viewModel::login
    )
    LoginScreen(
        onLoginClick = loginRequestHandler::performRequest,
        state = state,
        modifier = modifier,
    )
}

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    state: LoginState,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is LoginState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is LoginState.Error -> {
                        Text("Error")
                    }

                    else -> {}
                }
            }
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                onClick = onLoginClick,
                shape = MaterialTheme.shapes.medium,
                enabled = state !is LoginState.Loading,
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(text = "Login")
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun LoginScreen_Stale_Preview() {
    ArgosTheme {
        LoginScreen(
            onLoginClick = { /*TODO*/ },
            state = LoginState.Stale
        )
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun LoginScreen_Loading_Preview() {
    ArgosTheme {
        LoginScreen(
            onLoginClick = { /*TODO*/ },
            state = LoginState.Loading
        )
    }
}

@Composable
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun LoginScreen_Error_Preview() {
    ArgosTheme {
        LoginScreen(
            onLoginClick = { /*TODO*/ },
            state = LoginState.Error
        )
    }
}