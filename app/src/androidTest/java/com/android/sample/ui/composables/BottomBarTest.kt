package com.android.sample.ui.composables

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

/**
 * Test class for verifying the functionality and appearance of the `BottomBar` composable.
 *
 * This class tests the following aspects:
 * - Ensures the BottomBar is displayed on the screen.
 * - Verifies that all navigation destinations in the bottom bar are visible.
 * - Validates navigation actions when each destination is clicked.
 * - Checks that the selected screen is highlighted (increased size of the circular indicator).
 * - Ensures that unselected screens maintain their default appearance.
 */
class BottomBarTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock the NavigationActions
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAIN)
  }

  /** Tests if the BottomBar is displayed on the screen. */
  @Test
  fun hasNavigationBar() {
    composeTestRule.setContent { BottomBar(navigationActions) }
    composeTestRule.onNodeWithTag("BottomBar").assertIsDisplayed()
  }

  /** Tests if all navigation destinations in the BottomBar are displayed. */
  @Test
  fun testAllScreensAreDisplayed() {
    composeTestRule.setContent { BottomBar(navigationActions) }
    LIST_OF_TOP_LEVEL_DESTINATIONS.forEach { destination ->
      composeTestRule.onNodeWithTag(destination.textId).assertIsDisplayed()
    }
  }

  /** Tests if clicking on each destination triggers the appropriate navigation action. */
  @Test
  fun testNavigationToEachScreen() {
    composeTestRule.setContent { BottomBar(navigationActions) }
    LIST_OF_TOP_LEVEL_DESTINATIONS.forEach { destination ->
      composeTestRule.onNodeWithTag(destination.textId).performClick()

      verify(navigationActions).navigateTo(destination.route)
    }
  }

  /** Tests if the `MAIN Button` is highlighted correctly when selected. */
  @Test
  fun testSelectedScreenMainIsHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAIN)
    composeTestRule.setContent { BottomBar(navigationActions) }
    composeTestRule.onNodeWithTag(TopLevelDestinations.MAIN.textId).assertHeightIsEqualTo(55.dp)
  }

  /** Tests if the `VIDEO Button` is highlighted correctly when selected. */
  @Test
  fun testSelectedScreenVideoIsHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.VIDEO_LIBRARY)
    composeTestRule.setContent { BottomBar(navigationActions) }
    composeTestRule.onNodeWithTag(TopLevelDestinations.VIDEO.textId).assertHeightIsAtLeast(55.dp)
  }

  /** Tests if the `CALENDAR Button` is highlighted correctly when selected. */
  @Test
  fun testSelectedScreenCalendarIsHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CALENDAR)
    composeTestRule.setContent { BottomBar(navigationActions) }
    composeTestRule.onNodeWithTag(TopLevelDestinations.CALENDAR.textId).assertHeightIsAtLeast(55.dp)
  }

  /** Tests if the `ADD Button` is highlighted correctly when selected. */
  @Test
  fun testSelectedScreenAddIsHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SESSIONSELECTION)
    composeTestRule.setContent { BottomBar(navigationActions) }
    composeTestRule.onNodeWithTag(TopLevelDestinations.ADD.textId).assertHeightIsAtLeast(55.dp)
  }

  /** Tests if the `PROFLIE Button` is highlighted correctly when selected. */
  @Test
  fun testSelectedScreenProfileIsHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SETTINGS)
    composeTestRule.setContent { BottomBar(navigationActions) }

    composeTestRule.onNodeWithTag(TopLevelDestinations.PROFILE.textId).assertHeightIsAtLeast(55.dp)
  }

  /** Tests if unselected screens (other than `Screen.SETTINGS`) maintain their default size. */
  @Test
  fun testUnselectedScreensOfSettingsAreNotHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SETTINGS)
    composeTestRule.setContent { BottomBar(navigationActions) }
    LIST_OF_TOP_LEVEL_DESTINATIONS.filter { it != TopLevelDestinations.PROFILE }
        .forEach { destination ->
          composeTestRule.onNodeWithTag(destination.textId).assertHeightIsEqualTo(30.dp)
        }
  }

  /** Tests if unselected screens (other than `Screen.MAIN`) maintain their default size. */
  @Test
  fun testUnselectedScreensOfMainAreNotHighlighted() {
    reset(navigationActions)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAIN)
    composeTestRule.setContent { BottomBar(navigationActions) }
    LIST_OF_TOP_LEVEL_DESTINATIONS.filter { it != TopLevelDestinations.MAIN }
        .forEach { destination ->
          composeTestRule.onNodeWithTag(destination.textId).assertHeightIsEqualTo(30.dp)
        }
  }
}
