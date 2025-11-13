package com.wolt.restofinder.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
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
            NetworkImage(
                imageUrl = "https://example.com/image.jpg",
                contentDescription = "Test image"
            )
        }

        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }

    @Test
    fun showsBlurHashPlaceholderWhenProvided() {
        composeTestRule.setContent {
            NetworkImage(
                imageUrl = "https://example.com/image.jpg",
                contentDescription = "Test image",
                blurHash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj"
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("BlurHashPlaceholder")
            .assertExists()
    }

    @Test
    fun showsErrorPlaceholderOnLoadFailure() {
        composeTestRule.setContent {
            NetworkImage(
                imageUrl = "invalid-url",
                contentDescription = "Test image"
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("ErrorPlaceholder")
            .assertIsDisplayed()
    }

    @Test
    fun hidesLoadingIndicatorWhenDisabled() {
        composeTestRule.setContent {
            NetworkImage(
                imageUrl = "https://example.com/image.jpg",
                contentDescription = "Test image",
                showLoadingIndicator = false
            )
        }

        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertDoesNotExist()
    }

}
