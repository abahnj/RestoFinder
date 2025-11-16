package com.wolt.restofinder.presentation.common

import android.graphics.PathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.theme.PathBackgroundDark
import com.wolt.restofinder.presentation.theme.PathBackgroundLight
import com.wolt.restofinder.presentation.theme.Primary
import com.wolt.restofinder.presentation.theme.RestoFinderTheme
import timber.log.Timber

/**
 * Animated location display with a border line that traces around the edges.
 */
@Composable
fun AnimatedLocationDisplay(
    address: String,
    coordinates: Pair<Double, Double>,
    modifier: Modifier = Modifier,
    isAnimating: Boolean = true,
    locationKey: Any = Unit,
) {
    val progress = remember { Animatable(0f) }
    val primaryColor = Primary
    val density = LocalDensity.current

    // Dark mode detection for color inversion
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    // Adjust colors for dark mode visibility
    val pathBackgroundColor =
        if (isDarkMode) {
            PathBackgroundDark
        } else {
            PathBackgroundLight
        }

    // 10-second animation synced with ViewModel location emissions
    LaunchedEffect(locationKey, isAnimating) {
        if (isAnimating) {
            progress.snapTo(0f)
            Timber.d("Animation starting for location: $locationKey")
            progress.animateTo(
                targetValue = 1f,
                animationSpec =
                    tween(
                        durationMillis = 10_000,
                        easing = LinearEasing,
                    ),
            )
            Timber.d("Animation completed for location: $locationKey")
        } else {
            progress.snapTo(0f)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(96.dp)
                .testTag("AnimatedLocationDisplay"),
        contentAlignment = Alignment.Center,
    ) {
        // Map background pattern
        Icon(
            painter = painterResource(R.drawable.map_background),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (isDarkMode) {
                            Modifier.graphicsLayer {
                                // Invert colors for dark mode
                                colorFilter =
                                    androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                                        androidx.compose.ui.graphics.ColorMatrix().apply {
                                            setToScale(-1f, -1f, -1f, 1f) // Invert RGB, keep Alpha
                                            this[0, 4] = 255f // Add offset to R
                                            this[1, 4] = 255f // Add offset to G
                                            this[2, 4] = 255f // Add offset to B
                                        },
                                    )
                            }
                        } else {
                            Modifier
                        },
                    ),
        )

        // Content layer (centered address text with location pin)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Address and coordinates
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = address,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("LocationAddress"),
                )

                Text(
                    text = String.format(java.util.Locale.US, "%.6f, %.6f", coordinates.first, coordinates.second),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("LocationCoordinates"),
                )
            }
        }

        // Chevron down icon at bottom center
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Expand location",
            tint = primaryColor,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .size(28.dp)
                    .testTag("LocationChevron"),
        )

        // Animated border overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val cornerRadius = with(density) { 20.dp.toPx() }
            val strokeWidth = with(density) { 2.dp.toPx() }
            val inset = with(density) { 4.dp.toPx() }
            val cutoutWidth = with(density) { 100.dp.toPx() }
            val cutoutDepth = with(density) { 20.dp.toPx() }

            // Create the border path
            val path =
                createMapBackgroundPath(
                    width = width,
                    height = height,
                    cornerRadius = cornerRadius,
                    inset = inset,
                    cutoutWidth = cutoutWidth,
                    cutoutDepth = cutoutDepth,
                )

            // Measure the path
            val pathMeasure =
                PathMeasure().apply {
                    setPath(path.asAndroidPath(), false)
                }
            val pathLength = pathMeasure.length
            val animatedLength = pathLength * progress.value

            // Extract the animated segment
            val animatedPath = android.graphics.Path()
            if (animatedLength > 0 && pathLength > 0) {
                try {
                    pathMeasure.getSegment(0f, animatedLength, animatedPath, true)
                } catch (e: Exception) {
                    Timber.e(e, "Error getting animated path segment")
                }
            }

            // 1. Draw the full background path
            drawPath(
                path = path,
                color = pathBackgroundColor,
                style =
                    Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
            )

            // 2. Draw the animated line segment (primary blue)
            if (animatedLength > 0) {
                drawPath(
                    path = animatedPath.asComposePath(),
                    color = primaryColor,
                    style =
                        Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                )
            }

            // 3. Draw the static dot at start position
            drawCircle(
                color = primaryColor,
                radius = strokeWidth * 1.25f,
                center = Offset(inset, height / 2f),
            )
        }
    }
}

/**
 * Creates the border path with rounded corners and bottom cutout.
 */
private fun createMapBackgroundPath(
    width: Float,
    height: Float,
    cornerRadius: Float,
    inset: Float,
    cutoutWidth: Float,
    cutoutDepth: Float,
): Path {
    return Path().apply {
        // Calculate bounds with inset
        val w = width - (inset * 2)
        val h = height - (inset * 2)
        val centerX = w / 2f + inset
        val cutoutStart = centerX - cutoutWidth / 2f
        val cutoutEnd = centerX + cutoutWidth / 2f

        // Ensure corner radius is valid (not larger than half the width/height)
        val maxCornerRadius = minOf(w / 2f, h / 2f)
        val cr =
            (cornerRadius - inset)
                .coerceAtMost(maxCornerRadius)
                .coerceAtLeast(0f)

        // Start at left center (where the static dot is)
        moveTo(inset, h / 2f + inset)

        // Go up to top-left corner
        lineTo(inset, cr + inset)

        // Top-left corner arc
        arcTo(
            rect =
                Rect(
                    left = inset,
                    top = inset,
                    right = cr * 2 + inset,
                    bottom = cr * 2 + inset,
                ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Top edge to top-right corner
        lineTo(w - cr + inset, inset)

        // Top-right corner arc
        arcTo(
            rect =
                Rect(
                    left = w - cr * 2 + inset,
                    top = inset,
                    right = w + inset,
                    bottom = cr * 2 + inset,
                ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Right edge down to bottom-right corner
        lineTo(w + inset, h - cr + inset)

        // Bottom-right corner arc
        arcTo(
            rect =
                Rect(
                    left = w - cr * 2 + inset,
                    top = h - cr * 2 + inset,
                    right = w + inset,
                    bottom = h + inset,
                ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Bottom edge to cutout start (moving RIGHT TO LEFT)
        lineTo(cutoutEnd, h + inset)

        // Right side curve: from cutoutEnd to centerX
        cubicTo(
            x1 = cutoutEnd - cutoutWidth * 0.169f,
            // stays at bottom
            y1 = h + inset,
            x2 = centerX + cutoutWidth * 0.255f,
            // at cutout top
            y2 = h - cutoutDepth + inset,
            x3 = centerX,
            y3 = h - cutoutDepth + inset,
        )

        // Left side curve: from centerX to cutoutStart (smooth/reflected)
        cubicTo(
            // reflected from right CP2
            x1 = centerX - cutoutWidth * 0.255f,
            // at cutout top
            y1 = h - cutoutDepth + inset,
            x2 = cutoutStart + cutoutWidth * 0.183f,
            // back to bottom
            y2 = h + inset,
            x3 = cutoutStart,
            y3 = h + inset,
        )

        // Continue bottom edge to bottom-left corner
        lineTo(cr + inset, h + inset)

        // Bottom-left corner arc
        arcTo(
            rect =
                Rect(
                    left = inset,
                    top = h - cr * 2 + inset,
                    right = cr * 2 + inset,
                    bottom = h + inset,
                ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Left edge back up to start (center-left)
        lineTo(inset, h / 2f + inset)

        // Close the path to complete the loop
        close()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun AnimatedLocationDisplayPreview() {
    RestoFinderTheme {
        AnimatedLocationDisplay(
            address = "Kanavaranta 7 F, 00160 Helsinki",
            coordinates = 60.169418 to 24.931618,
            isAnimating = true,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun AnimatedLocationDisplayStaticPreview() {
    RestoFinderTheme {
        AnimatedLocationDisplay(
            address = "Discovering restaurants nearby...",
            coordinates = 0.0 to 0.0,
            isAnimating = false,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun AnimatedLocationDisplayLongAddressPreview() {
    RestoFinderTheme {
        AnimatedLocationDisplay(
            address = "Very Long Street Name 123 A, 12345 City With Long Name",
            coordinates = 60.170005 to 24.935105,
            isAnimating = true,
        )
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun AnimatedLocationDisplayDarkPreview() {
    RestoFinderTheme(darkTheme = true) {
        AnimatedLocationDisplay(
            address = "Kanavaranta 7 F, 00160 Helsinki",
            coordinates = 60.169418 to 24.931618,
            isAnimating = true,
        )
    }
}
