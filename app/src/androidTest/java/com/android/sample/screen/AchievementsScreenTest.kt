package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.ui.achievements.AchievementsScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AchievementsScreenTest {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ACHIEVEMENTS)
    composeTestRule.setContent { AchievementsScreen(navigationActions) }
  }

  @Test
  fun hasMainScreen() {
    composeTestRule.onNodeWithTag("achievementsScreen").assertIsDisplayed()
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
