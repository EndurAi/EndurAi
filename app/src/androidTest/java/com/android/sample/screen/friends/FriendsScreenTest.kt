package com.android.sample.screen.friends

import com.android.sample.ui.friends.FriendsScreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class FriendsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock navigation actions
    private lateinit var navigationActions: NavigationActions

    @Before
    fun setUp() {
        // Initialize mock navigation actions
        navigationActions = Mockito.mock(NavigationActions::class.java)

        // Set the content of the test to the FriendsScreen
        composeTestRule.setContent {
            FriendsScreen(navigationActions = navigationActions)
        }
    }

    @Test
    fun friendsScreenDisplaysCorrectly() {
        composeTestRule.onNodeWithTag("friendsScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("searchBarRow").assertIsDisplayed()
        composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("addFriendButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("friendsList").assertIsDisplayed()
        composeTestRule.onNodeWithTag("friendsList").performClick()
        composeTestRule.onNodeWithTag("inviteButton").assertIsDisplayed()
    }
}