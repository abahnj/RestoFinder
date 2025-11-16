package com.wolt.restofinder.presentation.venues.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.R
import com.wolt.restofinder.domain.model.Venue
import com.wolt.restofinder.presentation.theme.RestoFinderTheme
import kotlinx.coroutines.delay

/**
 * Lazy column displaying a list of venues with staggered entrance animations.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VenueList(
    venues: List<Venue>,
    listState: LazyListState,
    onFavouriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentDescription = stringResource(R.string.venue_list_content_description)

    LazyColumn(
        state = listState,
        modifier =
            modifier
                .fillMaxSize()
                .testTag("VenueList")
                .semantics {
                    this.contentDescription =
                        buildString {
                            append(contentDescription)
                            append(". ")
                            append(venues.size)
                            append(" ")
                            append(if (venues.size == 1) "restaurant" else "restaurants")
                        }
                },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(
            items = venues,
            key = { _, venue -> venue.id },
            contentType = { _, _ -> "VenueCard" },
        ) { index, venue ->
            // Animation state for scale and alpha (staggered entrance)
            val animatable = remember { Animatable(0f) }
            val staggerDelay = minOf(index * 35, 250)

            LaunchedEffect(Unit) {
                delay(staggerDelay.toLong())
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec =
                        tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing,
                        ),
                )
            }

            VenueCard(
                venue = venue,
                onFavouriteClick = onFavouriteClick,
                modifier =
                    Modifier
                        .graphicsLayer {
                            // Scale: 0.92f → 1.0f (popping in effect)
                            val scale = 0.92f + (animatable.value * 0.08f)
                            scaleX = scale
                            scaleY = scale
                            alpha = animatable.value
                        }
                        .animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = tween(durationMillis = 250),
                            placementSpec =
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium,
                                ),
                        ),
            )
        }
    }
}

// MARK: - Previews

@Preview(name = "Venue List", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun VenueListPreview() {
    RestoFinderTheme {
        VenueList(
            venues =
                listOf(
                    Venue("1", "McDonald's Helsinki", "I'm lovin' it.", "", "", false),
                    Venue("2", "Noodle Story Freda", "Fresh homemade noodles", "", "", true),
                    Venue("3", "Eat Poke Kamppi", null, "", "", false),
                ),
            listState = rememberLazyListState(),
            onFavouriteClick = {},
        )
    }
}
