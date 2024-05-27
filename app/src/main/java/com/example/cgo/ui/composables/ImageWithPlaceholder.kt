package com.example.cgo.ui.composables

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cgo.R

enum class Size {
    VerySmall,
    Small,
    Large
}

@Composable
fun ImageWithPlaceholder(uri: Uri?, size: Size) {
    if (uri?.path?.isNotEmpty()!!) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            "Profile Picture",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(when(size) {
                    Size.VerySmall -> 36.dp
                    Size.Small -> 72.dp
                    Size.Large -> 128.dp
                })
                .clip(CircleShape)
        )
    } else {
        Image(
            painterResource(id = R.drawable.image_placeholder),
            "Profile Picture",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
            modifier = Modifier
                .size(when(size) {
                    Size.VerySmall -> 36.dp
                    Size.Small -> 72.dp
                    Size.Large -> 128.dp
                })
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
        )
    }
}