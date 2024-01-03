package dev.xinto.argos.ui.screen.main.dialog.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.HorizontalSegmentedButton
import dev.xinto.argos.ui.component.SegmentedButtonColumn
import dev.xinto.argos.ui.component.SegmentedButtonRow
import dev.xinto.argos.ui.component.UserImage
import dev.xinto.argos.ui.component.VerticalSegmentedButton
import dev.xinto.argos.ui.theme.ArgosTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun UserDialog(
    onDismiss: () -> Unit,
    onBalanceNavigate: () -> Unit,
    onLibraryNavigate: () -> Unit,
    onUserNavigate: () -> Unit,
    onSettingsNavigate: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val viewModel: UserInfoViewModel = getViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    UserDialog(
        state = state,
        onDismiss = onDismiss,
        onBalanceNavigate = onBalanceNavigate,
        onLibraryNavigate = onLibraryNavigate,
        onUserNavigate = onUserNavigate,
        onSettingsNavigate = onSettingsNavigate,
        onLogoutClick = onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDialog(
    state: UserInfoState,
    onDismiss: () -> Unit,
    onBalanceNavigate: () -> Unit,
    onLibraryNavigate: () -> Unit,
    onUserNavigate: () -> Unit,
    onSettingsNavigate: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            tonalElevation = 3.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = onDismiss
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null
                    )
                }
                when (state) {
                    is UserInfoState.Loading -> {
                        Box(
                            modifier = Modifier.height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UserInfoState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            UserImage(
                                modifier = Modifier.size(72.dp),
                                url = state.userInfo.photoUrl
                            )
                            Text(
                                text = state.userInfo.fullName,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = state.userInfo.email,
                                style = MaterialTheme.typography.titleSmall
                            )
                            SegmentedButtonColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                SegmentedButtonRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RectangleShape
                                ) {
                                    VerticalSegmentedButton(onClick = onBalanceNavigate) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_balance),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = state.userState.billingBalance,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    VerticalSegmentedButton(onClick = onLibraryNavigate) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_book),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = state.userState.libraryBalance,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                                HorizontalSegmentedButton(onClick = onUserNavigate) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_account),
                                        contentDescription = null
                                    )
                                    Text(stringResource(R.string.meuserprofile_title))
                                }
                                HorizontalSegmentedButton(onClick = onSettingsNavigate) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_settings),
                                        contentDescription = null
                                    )
                                    Text(stringResource(R.string.settings_title))
                                }
                                HorizontalSegmentedButton(onClick = onLogoutClick) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_logout),
                                        contentDescription = null
                                    )
                                    Text(stringResource(R.string.meuserprofile_action_logout))
                                }
                            }
                        }
                    }

                    is UserInfoState.Error -> {

                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
fun UserDialog_Success_Preview() {
    ArgosTheme {
        UserDialog(
            state = UserInfoState.mockSuccess,
            onDismiss = {},
            onLogoutClick = {},
            onBalanceNavigate = {},
            onUserNavigate = {},
            onLibraryNavigate = {},
            onSettingsNavigate = {}
        )
    }
}

@Composable
@PreviewLightDark
fun UserDialog_Loading_Preview() {
    ArgosTheme {
        UserDialog(
            state = UserInfoState.Loading,
            onDismiss = {},
            onLogoutClick = {},
            onBalanceNavigate = {},
            onUserNavigate = {},
            onLibraryNavigate = {},
            onSettingsNavigate = {}
        )
    }
}