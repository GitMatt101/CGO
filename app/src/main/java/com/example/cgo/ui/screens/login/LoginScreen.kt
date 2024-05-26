package com.example.cgo.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.ui.OCGRoute

@Composable
fun LoginScreen(
    state: LoginState,
    actions: LoginActions,
    onLogin: (String, String) -> Unit,
    navController: NavHostController
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = actions::setEmail,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = actions::setPassword,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation =  PasswordVisualTransformation()
        )
        HorizontalDivider()
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(OCGRoute.Registration.route)
            }
        ) {
            Text(text = "Don't have an account?")
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.weight(1.0f))
        Button(
            modifier = Modifier.fillMaxWidth(0.5f).padding(bottom = 10.dp),
            onClick = {
                if (!state.canSubmit)
                    return@Button
                onLogin(state.email, state.password)
            }
        ) {
            Text(text = "Login")
        }
    }
}