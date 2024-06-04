package com.example.cgo.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

interface GalleryLauncher {
    val imageUri: Uri
    fun selectImage()
}

@Composable
fun rememberGalleryLauncher(onImageSelected: (imageUri: Uri) -> Unit = {}): GalleryLauncher {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }

    val galleryActivityLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imgUri: Uri? ->
        val image = File.createTempFile("tmp_image", ".jpg", context.externalCacheDir)
        val inputStream = context.contentResolver.openInputStream(imgUri!!)
        val bitmap = BitmapFactory.decodeStream(inputStream).asImageBitmap().asAndroidBitmap()
        val outputStream = BufferedOutputStream(FileOutputStream(image))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        val finalUri = FileProvider.getUriForFile(context, context.packageName + ".provider", image)
        imageUri = finalUri
        onImageSelected(finalUri)
    }

    val galleryLauncher by remember {
        derivedStateOf {
            object : GalleryLauncher {
                override val imageUri = imageUri
                override fun selectImage() = galleryActivityLauncher.launch("image/*")
            }
        }
    }
    return galleryLauncher
}