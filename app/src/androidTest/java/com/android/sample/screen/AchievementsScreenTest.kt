package com.android.sample.screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
    mockPreferencesRepository = mock()
    mockPreferencesViewModel = PreferencesViewModel(mockPreferencesRepository)
  }

  /** Test to verify that all components in the stats screen are displayed correctly. */
  @Test
  fun displayAllComponentsInStatsScreen() {
    composeTestRule.setContent {
      AchievementsScreen(navigationActions, mockStatisticsViewModel, mockPreferencesViewModel)
    }

    // Verify that the screen and toggleButton is displayed
    composeTestRule.onNodeWithTag("AchievementsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BottomBar").assertIsDisplayed()
    composeTestRule.onNodeWithText(text = "Stats").assertIsDisplayed()
    composeTestRule.onNodeWithText(text = "History").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StatsButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("HistoryButton").assertIsDisplayed().assertHasClickAction()

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

  /**
   * Test to verify that all components in the history screen are displayed correctly after
   * navigating from the stats screen.
   */
  @Test
  fun displayAllComponentsInHistoryScreen() {
    composeTestRule.setContent {
      AchievementsScreen(navigationActions, mockStatisticsViewModel, mockPreferencesViewModel)
    }

    // Verify that the screen and toggleButton is displayed
    composeTestRule.onNodeWithTag("AchievementsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BottomBar").assertIsDisplayed()
    composeTestRule.onNodeWithText(text = "Stats").assertIsDisplayed()
    composeTestRule.onNodeWithText(text = "History").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StatsButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("HistoryButton").assertIsDisplayed().assertHasClickAction()

    // We go to the history screen
    composeTestRule.onNodeWithTag("HistoryButton").performClick()

    // Verify that history screen is correctly displayed
    composeTestRule.onNodeWithTag("HistoryScreen").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("MonthView").assertCountEquals(2)

    // Verify that at least one month is displayed
    val nbrOfDay = composeTestRule.onAllNodesWithTag("DayView").fetchSemanticsNodes().size
    assert(nbrOfDay > 30)
  }

  /**
   * Test to verify that the toggle button transition between stats and history screens works
   * correctly. This test checks that clicking the buttons transitions to the appropriate screen.
   */
  @Test
  fun verifyToggleButtonTransitionWorks() {
    composeTestRule.setContent {
      AchievementsScreen(navigationActions, mockStatisticsViewModel, mockPreferencesViewModel)
    }

    // Verify that we are in the StatsScreen screen
    composeTestRule.onNodeWithTag("StatsScreen").assertIsDisplayed()

    // Verify that the buttons work
    composeTestRule.onNodeWithTag("StatsButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("HistoryButton").assertIsDisplayed().assertHasClickAction()

    // We press the stats button
    composeTestRule.onNodeWithTag("StatsButton").performClick()

    // Verify that we are STILL in the StatsScreen screen
    composeTestRule.onNodeWithTag("StatsScreen").assertIsDisplayed()

    // We press the history button
    composeTestRule.onNodeWithTag("HistoryButton").performClick()

    // Verify that we are in the history screen
    composeTestRule.onNodeWithTag("HistoryScreen").assertIsDisplayed()

    // We press the history button
    composeTestRule.onNodeWithTag("HistoryButton").performClick()

    // Verify that we are STILL in the history screen
    composeTestRule.onNodeWithTag("HistoryScreen").assertIsDisplayed()

    // We press the stats button
    composeTestRule.onNodeWithTag("StatsButton").performClick()

    // Verify that we are finally in the StatsScreen screen
    composeTestRule.onNodeWithTag("StatsScreen").assertIsDisplayed()
  }
}
