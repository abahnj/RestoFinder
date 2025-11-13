package com.wolt.restofinder.presentation.venues.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysFilledIconWhenFavourited() {
        composeTestRule.setContent {
            MaterialTheme {
                FavouriteButton(
                    isFavourite = true,
                    onClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .assertIsDisplayed()
            .assertContentDescriptionEquals("Remove from favourites")
    }

    @Test
    fun displaysOutlinedIconWhenNotFavourited() {
        composeTestRule.setContent {
            MaterialTheme {
                FavouriteButton(
                    isFavourite = false,
                    onClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .assertIsDisplayed()
            .assertContentDescriptionEquals("Add to favourites")
    }

    @Test
    fun clickTriggersCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            MaterialTheme {
                FavouriteButton(
                    isFavourite = false,
                    onClick = { clickCount++ }
                )
            }
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun toggleChangesAccessibilityLabel() {
        composeTestRule.setContent {
            MaterialTheme {
                var isFavourite by remember { mutableStateOf(false) }

                FavouriteButton(
                    isFavourite = isFavourite,
                    onClick = { isFavourite = !isFavourite }
                )
            }
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .assertContentDescriptionEquals("Add to favourites")

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .assertContentDescriptionEquals("Remove from favourites")
    }

    @Test
    fun doesNotTriggerCallbackWhenDisabled() {
        var clickCount = 0

        composeTestRule.setContent {
            MaterialTheme {
                FavouriteButton(
                    isFavourite = false,
                    onClick = { clickCount++ },
                    enabled = false
                )
            }
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .performClick()

        assertEquals(0, clickCount)
    }
}
