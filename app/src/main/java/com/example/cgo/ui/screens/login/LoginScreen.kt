package com.example.cgo.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.CGORoute
import com.example.cgo.ui.screens.registration.PADDING

@Composable
fun LoginScreen(
    state: LoginState,
    actions: LoginActions,
    onLogin: (String, String, () -> Unit) -> Unit,
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(
                    top = 0.dp,
                    bottom = 0.dp,
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Rtl)
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(top = PADDING)
                    .size(150.dp)
                    .clip(shape = CircleShape)
            )
            Text(
                text = "Login", fontWeight = FontWeight.Bold, fontSize = 24.sp
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = actions::setEmail,
                label = { Text("Email") },
                modifier = Modifier,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = actions::setPassword,
                label = { Text("Password") },
                modifier = Modifier,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            TextButton(onClick = {
                navController.popBackStack()
                navController.navigate(CGORoute.Registration.route)
            }) {
                Text("Don't have an account? Register")
            }
            HorizontalDivider()
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(bottom = 10.dp), onClick = {
                    if (!state.canSubmit) {
                        when {
                            state.email.isBlank() -> {
                                snackbarMessage = "Email cannot be empty"
                                showSnackbar = true
                            }

                            state.email.contains("@").not() -> {
                                snackbarMessage = "Invalid email"
                                showSnackbar = true
                            }

                            state.password.isBlank() -> {
                                snackbarMessage = "Password cannot be empty"
                                showSnackbar = true
                            }
                        }
                        return@Button
                    }
                    onLogin(state.email, state.password) {
                        snackbarMessage = "Login unsuccessful"
                        showSnackbar = true
                    }
                }
            ) {
                Text(text = "Login")
            }
        }
    }
    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }
}