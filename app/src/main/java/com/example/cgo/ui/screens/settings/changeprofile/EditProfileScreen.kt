package com.example.cgo.ui.screens.settings.changeprofile

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.cgo.R
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.Size
import com.example.cgo.utils.rememberCameraLauncher
import com.example.cgo.utils.rememberPermission

@Composable
fun EditProfileScreen(
    username: String,
    profilePicture: String?,
    state: EditProfileState,
    actions: EditProfileActions,
    onSubmit: (String, Uri) -> Unit
) {
    val context = LocalContext.current
    var isUsernameSet by remember { mutableStateOf(false) }
    var isProfilePictureSet by remember { mutableStateOf(false) }
    if (!isUsernameSet)
        actions.setUsername(username)
    isUsernameSet = true
    if (!isProfilePictureSet)
        profilePicture?.let { actions.setProfilePicture(it.toUri()) }
    isProfilePictureSet = true

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
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = state.username,
            onValueChange = actions::setUsername,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(10.dp))
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
        if (state.profilePicture != Uri.EMPTY) {
            Spacer(Modifier.size(10.dp))
            ImageWithPlaceholder(uri = state.profilePicture, size = Size.Large)
        }
        Spacer(modifier = Modifier.weight(1.0f))
        HorizontalDivider()
        Button(
            onClick = {
                if (!state.canSubmit)
                    return@Button
                onSubmit(state.username, state.profilePicture)
            }
        ) {
            Text(text = "Update")
        }
    }
}