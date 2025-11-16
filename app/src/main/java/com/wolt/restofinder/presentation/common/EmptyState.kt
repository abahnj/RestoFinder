package com.wolt.restofinder.presentation.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.theme.RestoFinderTheme

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.empty_state_no_restaurants),
    subtitle: String = stringResource(R.string.empty_state_try_different_location),
    icon: ImageVector = Icons.Default.SearchOff,
    iconContentDescription: String? = stringResource(R.string.empty_state_icon_description),
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("EmptyState")
                .semantics {
                    contentDescription = "$title. $subtitle"
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            modifier =
                Modifier
                    .size(64.dp)
                    .testTag("EmptyStateIcon"),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("EmptyStateTitle"),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("EmptyStateSubtitle"),
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyStatePreview() {
    RestoFinderTheme {
        Surface {
            EmptyState()
        }
    }
}

@Preview(name = "Custom Content", showBackground = true)
@Composable
private fun EmptyStateCustomPreview() {
    RestoFinderTheme {
        Surface {
            EmptyState(
                title = "No Favorites Yet",
                subtitle = "Tap the heart icon to save your favorite restaurants",
                icon = Icons.Default.FavoriteBorder,
            )
        }
    }
}
