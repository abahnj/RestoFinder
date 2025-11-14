package com.wolt.restofinder.presentation.common

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.wolt.restofinder.util.BlurHashDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NetworkImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    blurHash: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    showLoadingIndicator: Boolean = true
) {
    var blurHashBitmap by remember(blurHash) { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(blurHash) {
        blurHash?.let { hash ->
            scope.launch {
                blurHashBitmap = withContext(Dispatchers.Default) {
                    BlurHashDecoder.decode(blurHash = hash, width = 32, height = 32)
                }
            }
        }
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .testTag("NetworkImage")
            .semantics {
                this.contentDescription = contentDescription ?: "Image"
            },
        loading = {
            LoadingPlaceholder(
                blurHashBitmap = blurHashBitmap,
                showLoadingIndicator = showLoadingIndicator,
                hasBlurHash = blurHash != null
            )
        },
        error = {
            ErrorPlaceholder()
        }
    )
}

@Composable
private fun LoadingPlaceholder(
    blurHashBitmap: Bitmap?,
    showLoadingIndicator: Boolean,
    hasBlurHash: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("LoadingPlaceholder"),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = blurHashBitmap,
            animationSpec = tween(durationMillis = 300),
            label = "BlurHashCrossfade"
        ) { bitmap ->
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("BlurHashPlaceholder")
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        // Only show loading indicator if no blurhash is provided
        if (showLoadingIndicator && !hasBlurHash) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .testTag("LoadingIndicator"),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ErrorPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .testTag("ErrorPlaceholder"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = "Failed to load image",
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(48.dp)
        )
    }
}
