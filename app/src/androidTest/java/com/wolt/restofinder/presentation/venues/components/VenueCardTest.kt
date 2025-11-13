package com.wolt.restofinder.presentation.venues.components

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wolt.restofinder.domain.model.Venue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VenueCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val baseVenue = Venue(
        id = "venue123",
        name = "Test Restaurant",
        description = "Great food here",
        blurHash = "LEHV6nWB2yk8",
        imageUrl = "https://example.com/image.jpg",
        isFavourite = false
    )

    // MARK: - Display Tests

    @Test
    fun displaysVenueNameAndDescription() {
        composeTestRule.setContent {
            VenueCard(
                venue = baseVenue,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("VenueName")
            .assertIsDisplayed()
            .assertTextEquals("Test Restaurant")

        composeTestRule
            .onNodeWithTag("VenueDescription")
            .assertIsDisplayed()
            .assertTextEquals("Great food here")
    }

    @Test
    fun handlesNullDescription() {
        val venueWithoutDescription = baseVenue.copy(description = null)

        composeTestRule.setContent {
            VenueCard(
                venue = venueWithoutDescription,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("VenueName")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("VenueDescription")
            .assertDoesNotExist()
    }

    @Test
    fun handlesEmptyDescription() {
        val venueWithEmptyDescription = baseVenue.copy(description = "")

        composeTestRule.setContent {
            VenueCard(
                venue = venueWithEmptyDescription,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("VenueDescription")
            .assertDoesNotExist()
    }

    @Test
    fun handlesLongVenueName() {
        val longName = "A".repeat(100)
        val venueWithLongName = baseVenue.copy(name = longName)

        composeTestRule.setContent {
            VenueCard(
                venue = venueWithLongName,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("VenueName")
            .assertIsDisplayed()
            .assertTextContains(longName, substring = true)
    }

    @Test
    fun handlesSpecialCharactersInText() {
        val specialVenue = baseVenue.copy(
            name = "Café & Restaurant \"Special\"",
            description = "Food with <html> & symbols: €£¥"
        )

        composeTestRule.setContent {
            VenueCard(
                venue = specialVenue,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("VenueName")
            .assertTextEquals("Café & Restaurant \"Special\"")

        composeTestRule
            .onNodeWithTag("VenueDescription")
            .assertTextEquals("Food with <html> & symbols: €£¥")
    }

    // MARK: - Favourite Button Tests

    @Test
    fun favouriteButtonClickTriggersCallbackWithVenueId() {
        var clickedVenueId: String? = null

        composeTestRule.setContent {
            VenueCard(
                venue = baseVenue,
                onFavouriteClick = { venueId ->
                    clickedVenueId = venueId
                }
            )
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .performClick()

        composeTestRule.waitForIdle()

        assertEquals("Expected venueId 'venue123' but got '$clickedVenueId'", "venue123", clickedVenueId)
    }

    @Test
    fun favouriteButtonReflectsVenueState() {
        val favouritedVenue = baseVenue.copy(isFavourite = true)

        composeTestRule.setContent {
            VenueCard(
                venue = favouritedVenue,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("FavouriteButton")
            .assertContentDescriptionEquals("Remove from favourites")
    }

    @Test
    fun favouriteButtonMultipleClicksTriggersCallbackMultipleTimes() {
        var clickCount = 0

        composeTestRule.setContent {
            VenueCard(
                venue = baseVenue,
                onFavouriteClick = { clickCount++ }
            )
        }

        with(composeTestRule.onNodeWithTag("FavouriteButton")) {
            performClick()
            performClick()
            performClick()
        }

        composeTestRule.waitForIdle()
        assertEquals("Expected 3 clicks but got $clickCount", 3, clickCount)
    }

    // MARK: - Accessibility Tests

    @Test
    fun allInteractiveElementsHaveContentDescriptions() {
        composeTestRule.setContent {
            VenueCard(
                venue = baseVenue,
                onFavouriteClick = {}
            )
        }

        composeTestRule
            .onAllNodes(hasClickAction())
            .fetchSemanticsNodes()
            .forEach { node ->
                assert(node.config.any { it.key.name == "ContentDescription" }) {
                    "Clickable element missing content description"
                }
            }
    }
}
