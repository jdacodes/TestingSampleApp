package com.jdacodes.turbinemockkunittest.authlogin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jdacodes.turbinemockkunittest.NavRouteName

@Composable
fun AuthLoginScreen(navController: NavController) {

    val viewModel = hiltViewModel<AuthVM>()

    val onTriggerEvents = viewModel::onTriggerEvent
    val viewState = viewModel.viewState.value

    LaunchedEffect(Unit) {
        viewModel.navigationEventFlow.collect {
            when (it) {
                is AuthNavigationEvent.NavigateToProfile -> {
                    navController.navigate(NavRouteName.auth_profile)
                }
            }
        }
    }
    AuthLogin(viewModel, viewState, onTriggerEvents)
}

@Composable
fun AuthLogin(
    viewModel: AuthVM,
    viewState: AuthState,
    onTriggerEvents: (AuthEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(modifier = Modifier.semantics {
                contentDescription = "enter email"
            },
                value = viewState.username,
                placeholder = {
                    Text("Username")
                },
                onValueChange = {
                    onTriggerEvents(AuthEvent.OnUsernameChange(it))
                }
            )

            OutlinedTextField(
                modifier = Modifier.semantics {
                    contentDescription = "enter password"
                },
                value = viewState.password,
                placeholder = {
                    Text("Password")
                },
                onValueChange = {
                    onTriggerEvents(AuthEvent.OnPasswordChange(it))
                }
            )
            Button(
                modifier = Modifier.semantics {
                    contentDescription = "login click"
                },
                onClick = {
                    onTriggerEvents(AuthEvent.OnLoginClick)
                }
            ) {
                Text("Login")
            }

        }
    }
}

//@Composable
//fun AuthProfileScreen(navController: NavController) {
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier.align(Alignment.Center),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Profile Screen", Modifier.size(32.dp))
//        }
//    }
//
//
//}