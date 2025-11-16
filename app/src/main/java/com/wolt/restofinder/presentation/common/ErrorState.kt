package com.wolt.restofinder.presentation.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.theme.RestoFinderTheme

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector = Icons.Default.ErrorOutline,
    retryButtonText: String = stringResource(R.string.error_state_retry),
    showRetryButton: Boolean = true,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("ErrorState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.error_state_icon_description),
            modifier =
                Modifier
                    .size(64.dp)
                    .testTag("ErrorStateIcon"),
            tint = MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("ErrorTitle"),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .testTag("ErrorMessage")
                    .padding(horizontal = 24.dp),
        )

        if (showRetryButton) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry,
                modifier = Modifier.testTag("RetryButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(retryButtonText)
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorStatePreview() {
    RestoFinderTheme {
        Surface {
            ErrorState(
                message = "Failed to load venues. Please check your connection.",
                onRetry = {},
            )
        }
    }
}

@Preview(name = "With Title", showBackground = true)
@Composable
private fun ErrorStateWithTitlePreview() {
    RestoFinderTheme {
        Surface {
            ErrorState(
                title = "Something Went Wrong",
                message = "We couldn't load the venues. Please try again.",
                onRetry = {},
            )
        }
    }
}

@Preview(name = "Without Retry", showBackground = true)
@Composable
private fun ErrorStateNoRetryPreview() {
    RestoFinderTheme {
        Surface {
            ErrorState(
                message = "This feature is not available in your region.",
                onRetry = {},
                showRetryButton = false,
            )
        }
    }
}

@Preview(name = "Long Message", showBackground = true)
@Composable
private fun ErrorStateLongMessagePreview() {
    RestoFinderTheme {
        Surface {
            ErrorState(
                message =
                    "We're experiencing technical difficulties with our servers. " +
                        "Our team has been notified and is working to resolve the issue. " +
                        "Please try again in a few moments.",
                onRetry = {},
            )
        }
    }
}
