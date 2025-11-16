package com.wolt.restofinder.presentation.venues.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.domain.model.Venue
import com.wolt.restofinder.presentation.common.NetworkImage
import com.wolt.restofinder.presentation.theme.RestoFinderTheme

@Composable
fun VenueCard(
    venue: Venue,
    onFavouriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("VenueCard")
    ) {
        NetworkImage(
            imageUrl = venue.imageUrl,
            contentDescription = venue.name,
            blurHash = venue.blurHash,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = venue.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("VenueName")
                )

                venue.description?.takeIf { it.isNotBlank() }?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("VenueDescription")
                    )
                }
            }

            val onClick = remember(venue.id, onFavouriteClick) {
                { onFavouriteClick(venue.id) }
            }
            FavouriteButton(
                isFavourite = venue.isFavourite,
                onClick = onClick
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun VenueCardPreview() {
    RestoFinderTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            VenueCard(
                venue = Venue(
                    id = "1",
                    name = "McDonald's Helsinki Kamppi",
                    description = "I'm lovin' it.",
                    blurHash = "",
                    imageUrl = "",
                    isFavourite = false
                ),
                onFavouriteClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun VenueCardFavouritedPreview() {
    RestoFinderTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            VenueCard(
                venue = Venue(
                    id = "1",
                    name = "Noodle Story Freda",
                    description = "Fresh homemade noodles",
                    blurHash = "",
                    imageUrl = "",
                    isFavourite = true
                ),
                onFavouriteClick = {}
            )
        }
    }
}

@Preview(name = "Long Text", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun VenueCardLongTextPreview() {
    RestoFinderTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            VenueCard(
                venue = Venue(
                    id = "1",
                    name = "Restaurant with Very Long Name That Should Be Truncated",
                    description = "This is a very long description that should wrap to two lines and then be truncated with ellipsis to show proper text handling",
                    blurHash = "",
                    imageUrl = "",
                    isFavourite = false
                ),
                onFavouriteClick = {}
            )
        }
    }
}
