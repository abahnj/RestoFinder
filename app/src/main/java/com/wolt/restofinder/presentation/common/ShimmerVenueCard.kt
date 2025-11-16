package com.wolt.restofinder.presentation.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.theme.RestoFinderTheme

@Composable
fun ShimmerVenueCard(
    modifier: Modifier = Modifier,
    imageHeight: Dp = 160.dp,
    cornerRadius: Dp = 20.dp,
) {
    val shimmerDescription = stringResource(R.string.shimmer_card_description)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shimmer()
                .semantics {
                    contentDescription = shimmerDescription
                },
    ) {
        // Image placeholder
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("ShimmerImage"),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Content placeholders
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Name placeholder
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.7f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("ShimmerName"),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description placeholder
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.9f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("ShimmerDescription1"),
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description placeholder
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("ShimmerDescription2"),
                )
            }

            // Favourite button placeholder
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("ShimmerFavouriteButton"),
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ShimmerVenueCardPreview() {
    RestoFinderTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ShimmerVenueCard()
            }
        }
    }
}

@Preview(name = "Different Sizes", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun ShimmerVenueCardSizesPreview() {
    RestoFinderTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            ) {
                ShimmerVenueCard(imageHeight = 150.dp)
                ShimmerVenueCard(imageHeight = 250.dp)
            }
        }
    }
}
