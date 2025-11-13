package com.wolt.restofinder.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // MARK: - LoadingState Tests

    @Test
    fun loadingState_displaysShimmerCards() {
        composeTestRule.setContent {
            MaterialTheme {
                LoadingState()
            }
        }

        composeTestRule.onNodeWithTag("LoadingState").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ShimmerCard_0").assertExists()
    }

    @Test
    fun loadingState_rendersWithCustomItemCount() {
        composeTestRule.setContent {
            MaterialTheme {
                LoadingState(itemCount = 5)
            }
        }

        composeTestRule.onNodeWithTag("LoadingState").assertIsDisplayed()
        // LazyColumn only renders visible items, can't test all
        composeTestRule.onNodeWithTag("ShimmerCard_0").assertExists()
    }

    @Test
    fun loadingState_isScrollable() {
        composeTestRule.setContent {
            MaterialTheme {
                LoadingState(itemCount = 20)
            }
        }

        composeTestRule.onNodeWithTag("LoadingState")
            .assertIsDisplayed()
            .performScrollToIndex(10)
    }

    // MARK: - ShimmerVenueCard Tests

    @Test
    fun shimmerVenueCard_displaysAllPlaceholderElements() {
        composeTestRule.setContent {
            MaterialTheme {
                ShimmerVenueCard()
            }
        }

        with(composeTestRule) {
            onNodeWithTag("ShimmerImage").assertExists()
            onNodeWithTag("ShimmerName").assertExists()
            onNodeWithTag("ShimmerDescription1").assertExists()
            onNodeWithTag("ShimmerDescription2").assertExists()
            onNodeWithTag("ShimmerFavouriteButton").assertExists()
        }
    }

    // MARK: - ErrorState Tests

    @Test
    fun errorState_displaysAllRequiredElements() {
        composeTestRule.setContent {
            MaterialTheme {
                ErrorState(
                    message = "Network error occurred",
                    onRetry = {}
                )
            }
        }

        with(composeTestRule) {
            onNodeWithTag("ErrorState").assertIsDisplayed()
            onNodeWithTag("ErrorStateIcon").assertIsDisplayed()
            onNodeWithTag("ErrorMessage")
                .assertIsDisplayed()
                .assertTextEquals("Network error occurred")
            onNodeWithTag("RetryButton")
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    @Test
    fun errorState_withTitle_displaysTitle() {
        composeTestRule.setContent {
            MaterialTheme {
                ErrorState(
                    title = "Connection Failed",
                    message = "Please check your internet",
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("ErrorTitle")
            .assertIsDisplayed()
            .assertTextEquals("Connection Failed")
    }

    @Test
    fun errorState_retryButtonTriggersCallback() {
        var retryCount = 0

        composeTestRule.setContent {
            MaterialTheme {
                ErrorState(
                    message = "Error",
                    onRetry = { retryCount++ }
                )
            }
        }

        composeTestRule.onNodeWithTag("RetryButton").performClick()
        composeTestRule.waitForIdle()

        assertEquals("Expected retry count to be 1 but was $retryCount", 1, retryCount)
    }

    @Test
    fun errorState_multipleRetryClicks_triggersCallbackMultipleTimes() {
        var retryCount = 0

        composeTestRule.setContent {
            MaterialTheme {
                ErrorState(
                    message = "Error",
                    onRetry = { retryCount++ }
                )
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithTag("RetryButton").performClick()
        }

        composeTestRule.waitForIdle()
        assertEquals("Expected retry count to be 3 but was $retryCount", 3, retryCount)
    }

    @Test
    fun errorState_withoutRetryButton_hidesButton() {
        composeTestRule.setContent {
            MaterialTheme {
                ErrorState(
                    message = "Feature not available",
                    onRetry = {},
                    showRetryButton = false
                )
            }
        }

        composeTestRule.onNodeWithTag("RetryButton").assertDoesNotExist()
        composeTestRule.onNodeWithTag("ErrorMessage").assertIsDisplayed()
    }

    // MARK: - EmptyState Tests

    @Test
    fun emptyState_displaysAllElements() {
        composeTestRule.setContent {
            MaterialTheme {
                EmptyState()
            }
        }

        with(composeTestRule) {
            onNodeWithTag("EmptyState").assertIsDisplayed()
            onNodeWithTag("EmptyStateIcon").assertIsDisplayed()
            onNodeWithTag("EmptyStateTitle").assertIsDisplayed()
            onNodeWithTag("EmptyStateSubtitle").assertIsDisplayed()
        }
    }

    @Test
    fun emptyState_isNotInteractive() {
        composeTestRule.setContent {
            MaterialTheme {
                EmptyState()
            }
        }

        composeTestRule.onNodeWithTag("EmptyState")
            .assertHasNoClickAction()
    }
}
