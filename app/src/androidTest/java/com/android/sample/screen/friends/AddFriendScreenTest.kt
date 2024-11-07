package com.android.sample.screen.friends


import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.friends.AddFriendScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.video.VideoScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class AddFriendScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    // Mock navigation actions
    private lateinit var navigationActions: NavigationActions

    @Before
    fun setUp() {
        // Initialize mock navigation actions
        navigationActions = Mockito.mock(NavigationActions::class.java)


        // Set the content of the test to the VideoScreen
        composeTestRule.setContent {
            AddFriendScreen(navigationActions = navigationActions)
        }
    }

    @Test
    fun addFriendScreenDisplaysCorrectly() {
        composeTestRule.onNodeWithTag("addFriendScreen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("Spacer1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tabButtons").assertIsDisplayed()

        composeTestRule.onNodeWithTag("newConnectionsTabButton").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithTag("Spacer2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("invitationsTabButton").assertIsDisplayed().assertHasClickAction()

        composeTestRule.onNodeWithTag("Spacer3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("newConnectionsContent").assertIsDisplayed()
    }
}
