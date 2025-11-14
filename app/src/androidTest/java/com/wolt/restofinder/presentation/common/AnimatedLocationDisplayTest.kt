package com.wolt.restofinder.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimatedLocationDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysComponentWithAllElements() {
        val testAddress = "Kanavaranta 7 F, 00160 Helsinki"

        composeTestRule.setContent {
            MaterialTheme {
                AnimatedLocationDisplay(
                    address = testAddress,
                    coordinates = 60.169418 to 24.931618,
                    isAnimating = true
                )
            }
        }

        composeTestRule
            .onNodeWithTag("AnimatedLocationDisplay")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("LocationPin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("LocationAddress")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("LocationChevron")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(testAddress)
            .assertIsDisplayed()
    }

    @Test
    fun displaysCorrectAddressText() {
        val testAddress = "Discovering restaurants nearby..."

        composeTestRule.setContent {
            MaterialTheme {
                AnimatedLocationDisplay(
                    address = testAddress,
                    coordinates = 0.0 to 0.0,
                    isAnimating = false
                )
            }
        }

        composeTestRule
            .onNodeWithText(testAddress)
            .assertIsDisplayed()
    }

    @Test
    fun rendersWithAnimationDisabled() {
        composeTestRule.setContent {
            MaterialTheme {
                AnimatedLocationDisplay(
                    address = "Test Location",
                    coordinates = 60.169818 to 24.932906,
                    isAnimating = false
                )
            }
        }

        composeTestRule
            .onNodeWithTag("AnimatedLocationDisplay")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("LocationPin")
            .assertIsDisplayed()
    }

    @Test
    fun handlesLongAddressText() {
        val longAddress = "This is a very long address that should still be displayed properly within the component bounds without causing layout issues"

        composeTestRule.setContent {
            MaterialTheme {
                AnimatedLocationDisplay(
                    address = longAddress,
                    coordinates = 60.170005 to 24.935105,
                    isAnimating = true
                )
            }
        }

        composeTestRule
            .onNodeWithTag("LocationAddress")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(longAddress)
            .assertIsDisplayed()
    }
}
