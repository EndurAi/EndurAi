package com.android.sample.screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.preferences.PreferencesRepository
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.ui.achievements.AchievementsScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Test class for `AchievementsScreen`. This class contains tests to verify that all components in
 * the achievements screen are displayed correctly and that the toggle button between stats and
 * history screens works as expected.
 */
class AchievementsScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockStatisticsViewModel: StatisticsViewModel
  private lateinit var mockPreferencesViewModel: PreferencesViewModel
  private lateinit var mockPreferencesRepository: PreferencesRepository
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ACHIEVEMENTS)

    mockStatisticsViewModel = mock()
    mockPreferencesViewModel = mock()
  }

  /** Test to verify that all components in the stats screen are displayed correctly. */
  @Test
  fun displayAllComponentsInStatsScreen() {
    composeTestRule.setContent {
      AchievementsScreen(navigationActions, mockStatisticsViewModel, mockPreferencesViewModel)
    }

    // Verify that the screen and toggleButton is displayed
    composeTestRule.onNodeWithTag("AchievementsScreen").assertExists()
    composeTestRule.onNodeWithTag("BottomBar").assertExists()
    composeTestRule.onNodeWithText(text = "Stats").assertExists()
    composeTestRule.onNodeWithText(text = "History").assertExists()

    // Verify we are in StatsScreen
    composeTestRule.onNodeWithTag("StatsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithText(text = "Calories of the week").assertIsDisplayed()
    composeTestRule.onNodeWithTag("KcalCard").assertIsDisplayed()
    composeTestRule.onNodeWithText(text = "Kcal").assertIsDisplayed()

    // Verify we have 2 charts and are displayed
    val charts = composeTestRule.onAllNodesWithTag("Chart").assertCountEquals(2)
    charts[0].assertIsDisplayed()
    charts[1].assertIsDisplayed()

    // Verify the pie chart
    composeTestRule.onNodeWithText(text = "Type exercise repartition").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PieChart").assertIsDisplayed()
  }
}
