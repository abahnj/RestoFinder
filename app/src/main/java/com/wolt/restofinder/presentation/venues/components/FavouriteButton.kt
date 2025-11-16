package com.wolt.restofinder.presentation.venues.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.presentation.theme.FavoriteRed

@Composable
fun FavouriteButton(
    isFavourite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val scale = remember { Animatable(1f) }
    var previousFavourite by remember { mutableStateOf(isFavourite) }

    LaunchedEffect(isFavourite) {
        if (isFavourite && !previousFavourite) {
            // Pulse sequence: scale up then back to normal
            scale.animateTo(
                targetValue = 1.4f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        previousFavourite = isFavourite
    }

    val iconColor by animateColorAsState(
        targetValue = if (isFavourite) {
            FavoriteRed
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        label = "FavouriteButtonColor"
    )

    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        enabled = enabled,
        modifier = modifier
            .testTag("FavouriteButton")
            .semantics {
                contentDescription = if (isFavourite) {
                    "Remove from favourites"
                } else {
                    "Add to favourites"
                }
            },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor
        )
    ) {
        Icon(
            imageVector = if (isFavourite) {
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            },
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .scale(scale.value)
        )
    }
}

@Preview(name = "Unfavourited", showBackground = true)
@Composable
private fun FavouriteButtonUnfavouritedPreview() {
    MaterialTheme {
        FavouriteButton(isFavourite = false, onClick = {})
    }
}

@Preview(name = "Favourited", showBackground = true)
@Composable
private fun FavouriteButtonFavouritedPreview() {
    MaterialTheme {
        FavouriteButton(isFavourite = true, onClick = {})
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun FavouriteButtonDisabledPreview() {
    MaterialTheme {
        FavouriteButton(isFavourite = false, onClick = {}, enabled = false)
    }
}
