package dev.xinto.argos.ui.component

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration

@Immutable
data class AppAuthRequest(
    val config: AppAuthServiceConfiguration,
    val clientId: String,
    val responseType: String,
    val redirectUri: String,
    val scope: String? = null,
)

@Immutable
data class AppAuthServiceConfiguration(
    val authEndpoint: String,
    val tokenEndpoint: String
)

@Immutable
sealed interface AuthResult {

    @Immutable
    data class Error(val error: String) : AuthResult

    @Immutable
    data class Success(
        val idToken: String?,
        val accessToken: String?,
        val refreshToken: String?
    ) : AuthResult
}


@Immutable
data class AppAuthRequestHandler(
    private val authorizationService: AuthorizationService,
    private val authorizationRequest: AuthorizationRequest,
    private val activityResult: ManagedActivityResultLauncher<Intent, ActivityResult>
) {

    fun performRequest() {
        activityResult.launch(
            authorizationService.getAuthorizationRequestIntent(
                authorizationRequest
            )
        )
    }
}

@Composable
fun rememberAppAuthRequest(
    request: AppAuthRequest,
    onResult: (AuthResult) -> Unit,
): AppAuthRequestHandler {
    val context = LocalContext.current
    val authorizationService = remember(context) {
        AuthorizationService(context)
    }
    DisposableEffect(authorizationService) {
        onDispose {
            authorizationService.dispose()
        }
    }
    val authorizationRequest = remember(request) {
        AuthorizationRequest.Builder(
            AuthorizationServiceConfiguration(
                Uri.parse(request.config.authEndpoint),
                Uri.parse(request.config.tokenEndpoint),
            ),
            request.clientId,
            request.responseType,
            Uri.parse(request.redirectUri)
        ).setScope(request.scope).build()
    }
    val loginResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data == null) {
                onResult(AuthResult.Error("No intent"))
                return@rememberLauncherForActivityResult
            }

            val authResponse = AuthorizationResponse.fromIntent(it.data!!)
            val authException = AuthorizationException.fromIntent(it.data!!)

            if (authResponse != null) {
                authorizationService.performTokenRequest(authResponse.createTokenExchangeRequest()) { response, e ->
                    if (response != null) {
                        onResult(
                            AuthResult.Success(
                                idToken = response.idToken,
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken
                            )
                        )
                    } else {
                        onResult(e!!.toAuthResult())
                    }
                }
            } else {
                onResult(authException!!.toAuthResult())
            }
        }
    return remember(authorizationService, authorizationRequest, loginResult) {
        AppAuthRequestHandler(authorizationService, authorizationRequest, loginResult)
    }
}

private fun AuthorizationException.toAuthResult() =
    AuthResult.Error(error ?: errorDescription ?: code.toString())