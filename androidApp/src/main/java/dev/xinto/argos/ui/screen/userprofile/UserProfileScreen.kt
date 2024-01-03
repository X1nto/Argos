package dev.xinto.argos.ui.screen.userprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.domain.user.DomainLecturerDegree
import dev.xinto.argos.domain.user.DomainLecturerProfile
import dev.xinto.argos.domain.user.DomainStudentDegree
import dev.xinto.argos.domain.user.DomainStudentProfile
import dev.xinto.argos.domain.user.DomainUserProfile
import dev.xinto.argos.ui.component.UserImage
import org.koin.androidx.compose.getStateViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: UserProfileViewModel = getStateViewModel(state = {
        bundleOf(UserProfileViewModel.KEY_USER_ID to userId)
    })
    val state by viewModel.state.collectAsStateWithLifecycle()
    UserProfileScreen(
        modifier = modifier,
        state = state,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: UserProfileState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.userprofile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state is UserProfileState.Success) {
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chat),
                        contentDescription = null
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is UserProfileState.Loading -> {
                    CircularProgressIndicator()
                }
                is UserProfileState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = 3.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserImage(
                                    modifier = Modifier.size(72.dp),
                                    url = state.profile.photoUrl
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = state.profile.fullName,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    if (state.profile.email != null) {
                                        Text(
                                            text = state.profile.email!!,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Text(
                                        text = state.profile.getLabelString(),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
                is UserProfileState.Error -> {
                    Text("Error")
                }
            }
        }

    }
}

@Composable
private fun DomainUserProfile.getLabelString(): String {
    val labelStringRes = when (this) {
        is DomainStudentProfile -> when (degree) {
            DomainStudentDegree.Bachelor -> R.string.userprofile_label_student_bachelor
            DomainStudentDegree.Master -> R.string.userprofile_label_student_master
            DomainStudentDegree.Doctor -> R.string.userprofile_label_student_doctor
        }
        is DomainLecturerProfile -> when (degree) {
            DomainLecturerDegree.Professor -> R.string.userprofile_label_lecturer_professor
            DomainLecturerDegree.AssociatedProfessor -> R.string.userprofile_label_lecturer_associatedprofessor
            DomainLecturerDegree.AssistantProfessor -> R.string.userprofile_label_lecturer_assistantprofessor
            DomainLecturerDegree.InvitedLecturer -> R.string.userprofile_label_lecturer_invitedlecturer
            DomainLecturerDegree.EmeritusProfessor -> R.string.userprofile_label_lecturer_emeritusprofessor
            DomainLecturerDegree.RespProfessor -> R.string.userprofile_label_lecturer_respprofessor
        }
    }
    return stringResource(labelStringRes)
}