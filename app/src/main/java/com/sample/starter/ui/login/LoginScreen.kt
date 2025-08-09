package com.sample.starter.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen() {
    val viewModel: LoginViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect {
            when (it) {
                is SideEffects.ShowToast -> snackbarHostState.showSnackbar(message = it.message)
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) })
    { paddingVal ->
        Column(
            modifier = Modifier
                .padding(paddingVal)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.value.isLoading) {
                CircularProgressIndicator()
            } else {
                Content(
                    content = state.value, onFirstNameChanged = {
                        scope.launch {
                            viewModel.events.send(Events.FirstNameUpdate(it))
                        }
                    }, onLastNameChanged = {
                        scope.launch {
                            viewModel.events.send(Events.LastNameUpdated(it))
                        }
                    }, onLogin = {
                        scope.launch {
                            viewModel.events.send(Events.Submit)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Content(
    content: UiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onLogin: () -> Unit,
) {
    OutlinedTextField(
        value = content.firstName, onValueChange = onFirstNameChanged
    )
    Spacer(modifier = Modifier.padding(8.dp))
    OutlinedTextField(
        value = content.lastName, onValueChange = onLastNameChanged
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Button(
        modifier = Modifier,
        onClick = onLogin
    ) {
        Text("Login")
    }
}
