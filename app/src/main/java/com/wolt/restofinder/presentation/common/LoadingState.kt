package com.wolt.restofinder.presentation.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.theme.RestoFinderTheme

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    itemCount: Int = 15,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    itemSpacing: Dp = 16.dp,
) {
    val loadingDescription = stringResource(R.string.loading_state_description)

    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .testTag("LoadingState")
                .semantics {
                    contentDescription = loadingDescription
                },
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        items(itemCount) { index ->
            ShimmerVenueCard(
                modifier = Modifier.testTag("ShimmerCard_$index"),
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingStatePreview() {
    RestoFinderTheme {
        Surface {
            LoadingState()
        }
    }
}
