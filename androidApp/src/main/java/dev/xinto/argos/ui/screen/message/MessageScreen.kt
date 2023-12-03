package dev.xinto.argos.ui.screen.message

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xinto.argos.R
import dev.xinto.argos.ui.component.HtmlText
import org.koin.androidx.compose.getStateViewModel

@Composable
fun MessageScreen(
    messageId: String,
    semesterId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MessageViewModel = getStateViewModel(
        state = { Bundle().apply {
            putString(MessageViewModel.KEY_MESSAGE_ID, messageId)
            putString(MessageViewModel.KEY_MESSAGE_SEMESTER, semesterId)
        }
    })
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(onBack = onBackClick)
    MessageScreen(
        modifier = modifier,
        state = state,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    state: MessageState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(stringResource(R.string.message_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is MessageState.Loading -> {
                    CircularProgressIndicator()
                }
                is MessageState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = state.message.subject,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        HtmlText(text = state.message.body)
                    }
                }
                is MessageState.Error -> {
                    Text("Error")
                }
            }
        }
    }
}