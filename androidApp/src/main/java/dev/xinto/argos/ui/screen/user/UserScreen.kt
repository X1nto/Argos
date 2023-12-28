package dev.xinto.argos.ui.screen.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import org.koin.androidx.compose.getViewModel

@Composable
fun UserScreen(
    onBackNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: UserViewModel = getViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    val saving by viewModel.saving.collectAsStateWithLifecycle()
    UserScreen(
        modifier = modifier,
        onBackNavigate = onBackNavigate,
        state = state,
        canSave = canSave,
        saving = saving,
        onSaveClick = viewModel::save,
        onMobile1Change = viewModel::updateMobile1,
        onMobile2Change = viewModel::updateMobile2,
        onHomeNumberChange = viewModel::updateHomeNumber,
        onCurrentAddressChange = viewModel::updateCurrentAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    state: UserState,
    onBackNavigate: () -> Unit,
    canSave: Boolean,
    saving: Boolean,
    onSaveClick: () -> Unit,
    onMobile1Change: (String) -> Unit,
    onMobile2Change: (String) -> Unit,
    onHomeNumberChange: (String) -> Unit,
    onCurrentAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.user_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackNavigate) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = canSave,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = onSaveClick) {
                    if (saving) {
                        CircularProgressIndicator(color = LocalContentColor.current)
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is UserState.Loading -> {
                    CircularProgressIndicator()
                }
                is UserState.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item(key = "firstname") {
                            ReadOnlyTextField(
                                value = state.user.firstName,
                                label = { Text(stringResource(R.string.user_field_firstname)) },
                            )
                        }
                        item(key = "lastname") {
                            ReadOnlyTextField(
                                value = state.user.lastName,
                                label = { Text(stringResource(R.string.user_field_lastname)) },
                            )
                        }
                        item(key = "idnumber") {
                            ReadOnlyTextField(
                                value = state.user.idNumber,
                                label = { Text(stringResource(R.string.user_field_idnumber)) },
                            )
                        }
                        item(key = "birthdate") {
                            ReadOnlyTextField(
                                value = state.user.birthDate,
                                label = { Text(stringResource(R.string.user_field_birthdate)) },
                            )
                        }
                        item(
                            key = "email",
                            span = { GridItemSpan(maxCurrentLineSpan) }
                        ) {
                            ReadOnlyTextField(
                                value = state.user.email,
                                label = { Text(stringResource(R.string.user_field_email)) },
                            )
                        }
                        item(key = "mobile1") {
                            TextField(
                                value = state.user.mobileNumber1,
                                onValueChange = onMobile1Change,
                                label = { Text(stringResource(R.string.user_field_mobilenum1)) },
                            )
                        }
                        item(key = "mobile2") {
                            TextField(
                                value = state.user.mobileNumber2,
                                onValueChange = onMobile2Change,
                                label = { Text(stringResource(R.string.user_field_mobilenum2)) },
                            )
                        }
                        item(
                            key = "home",
                            span = { GridItemSpan(maxCurrentLineSpan) }
                        ) {
                            TextField(
                                value = state.user.homeNumber,
                                onValueChange = onHomeNumberChange,
                                label = { Text(stringResource(R.string.user_field_homenum)) },
                            )
                        }
                        item(
                            key = "juridicaladdr",
                            span = { GridItemSpan(maxCurrentLineSpan) }
                        ) {
                            ReadOnlyTextField(
                                value = state.user.juridicalAddress,
                                label = { Text(stringResource(R.string.user_field_address_juridical)) },
                                singleLine = false
                            )
                        }
                        item(
                            key = "currentaddr",
                            span = { GridItemSpan(maxCurrentLineSpan) }
                        ) {
                            TextField(
                                value = state.user.currentAddress,
                                onValueChange = onCurrentAddressChange,
                                label = { Text(stringResource(R.string.user_field_address_current)) },
                                singleLine = false
                            )
                        }
                    }
                }
                is UserState.Error -> {
                    Text("Error")
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyTextField(
    value: String,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {},
        label = label,
        singleLine = singleLine,
        enabled = false,
        readOnly = true
    )
}

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = singleLine,
    )
}