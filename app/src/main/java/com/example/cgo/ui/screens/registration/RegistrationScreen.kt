package com.example.cgo.ui.screens.registration

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cgo.R
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.Size
import com.example.cgo.utils.rememberCameraLauncher
import com.example.cgo.utils.rememberPermission

@Composable
fun RegistrationScreen(
    state: RegistrationState,
    actions: RegistrationActions,
    onSubmit: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current

    // Camera
    val cameraLauncher = rememberCameraLauncher { imageUri -> actions.setProfilePicture(imageUri) }
    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted)
            cameraLauncher.captureImage()
        else
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
    }
    fun takePicture() {
        if (cameraPermission.status.isGranted)
            cameraLauncher.captureImage()
        else
            cameraPermission.launchPermissionRequest()
    }

    // UI
    Scaffold { contentPadding ->
        Column (
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = state.username,
                onValueChange = actions::setUsername,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
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
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = actions::setConfirmPassword,
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =  PasswordVisualTransformation()
            )
            Spacer(Modifier.size(24.dp))
            Button(
                onClick = ::takePicture,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    painterResource(id = R.drawable.camera),
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Take a picture")
            }
            Spacer(Modifier.size(8.dp))
            ImageWithPlaceholder(uri = state.profilePicture, size = Size.Large)
            Spacer(Modifier.size(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = {
                    if (!state.canSubmit) return@Button
                    onSubmit()
                    navController.navigateUp()
                }
            ) {
                Text(text = "Submit")
            }
        }
    }
}