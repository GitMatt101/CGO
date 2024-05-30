package com.example.cgo.ui.screens.registration

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.example.cgo.utils.rememberGalleryLauncher
import com.example.cgo.utils.rememberPermission

val PADDING = 10.dp

@Composable
fun RegistrationScreen(
    state: RegistrationState,
    actions: RegistrationActions,
    onSubmit: () -> Unit
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
    val galleryLauncher = rememberGalleryLauncher { imageUri -> actions.setProfilePicture(imageUri) }

    // UI
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
        Spacer(Modifier.size(PADDING))
        Row {
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
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Button(
                onClick = { galleryLauncher.selectImage() },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    painterResource(id = R.drawable.gallery),
                    contentDescription = "Gallery icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Select Image")
            }
        }
        if (state.profilePicture != Uri.EMPTY) {
            Spacer(Modifier.size(PADDING))
            ImageWithPlaceholder(uri = state.profilePicture, size = Size.Large)
        }
        Spacer(modifier = Modifier.weight(1.0f))
        HorizontalDivider()
        Button(
            modifier = Modifier.padding(bottom = PADDING),
            onClick = {
                if (!state.canSubmit)
                    return@Button
                onSubmit()
            }
        ) {
            Text(text = "Submit")
        }
    }
}