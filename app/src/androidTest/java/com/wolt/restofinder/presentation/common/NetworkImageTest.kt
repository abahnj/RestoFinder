package com.wolt.restofinder.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkImageTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingIndicatorWhileLoading() {
        composeTestRule.setContent {
            MaterialTheme {
                NetworkImage(
                    imageUrl = "https://example.com/image.jpg",
                    contentDescription = "Test image",
                )
            }
        }

        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }

    @Test
    fun showsBlurHashPlaceholderWhenProvided() {
        composeTestRule.setContent {
            MaterialTheme {
                NetworkImage(
                    imageUrl = "https://example.com/image.jpg",
                    contentDescription = "Test image",
                    blurHash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj",
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("BlurHashPlaceholder")
            .assertExists()
    }

    @Test
    fun showsErrorPlaceholderOnLoadFailure() {
        composeTestRule.setContent {
            MaterialTheme {
                NetworkImage(
                    imageUrl = "invalid-url",
                    contentDescription = "Test image",
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("ErrorPlaceholder")
            .assertIsDisplayed()
    }

    @Test
    fun hidesLoadingIndicatorWhenDisabled() {
        composeTestRule.setContent {
            MaterialTheme {
                NetworkImage(
                    imageUrl = "https://example.com/image.jpg",
                    contentDescription = "Test image",
                    showLoadingIndicator = false,
                )
            }
        }

        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertDoesNotExist()
    }
}
