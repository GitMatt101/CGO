package com.example.cgo.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

interface CameraLauncher {
    val imageUri: Uri
    fun captureImage()
}

@Composable
fun rememberCameraLauncher(onPictureTaken: (imageUri: Uri) -> Unit = {}): CameraLauncher {
    val context = LocalContext.current
    val uri = remember {
        val image = File.createTempFile("tmp_image", ".jpg", context.externalCacheDir)
        FileProvider.getUriForFile(context, context.packageName + ".provider", image)
    }
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    val cameraActivityLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { pictureTaken ->
        if (pictureTaken) {
            imageUri = uri
            onPictureTaken(imageUri)
        }
    }

    val cameraLauncher by remember {
        derivedStateOf {
            object : CameraLauncher {
                override val imageUri = imageUri
                override fun captureImage() = cameraActivityLauncher.launch((uri))
            }
        }
    }
    return cameraLauncher
}