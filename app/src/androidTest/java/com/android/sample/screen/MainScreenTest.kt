package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.ui.mainscreen.MainScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class MainScreenTest {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)
    composeTestRule.setContent { MainScreen(navigationActions) }
  }

  @Test
  fun hasMainScreen() {
    composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
  }

  @Test
  fun hasNavigationBar() {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun hasNavigationDestinations() {
    composeTestRule.onNodeWithTag("Main").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Video").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Achievements").assertIsDisplayed()
  }
}
