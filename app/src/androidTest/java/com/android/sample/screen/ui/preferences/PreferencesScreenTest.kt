package com.android.sample.screen.ui.preferences

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.preferences.PreferencesRepository
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.preferences.PreferencesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class PreferencesScreenTest {
  private lateinit var mockPreferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var mockNavHostController: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    // Mock the ViewModel and Preferences
    mockPreferencesRepository = mock(PreferencesRepository::class.java)
    mockNavHostController = mock(NavigationActions::class.java)
    preferencesViewModel = PreferencesViewModel(mockPreferencesRepository)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { PreferencesScreen(mockNavHostController, preferencesViewModel) }

    composeTestRule.onNodeWithTag("preferencesTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesSaveButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("unitsSystemMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemMenuText").assertTextEquals("System of units")
    composeTestRule.onNodeWithTag("weightUnitMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitMenuText").assertTextEquals("Weight unit")
    composeTestRule.onNodeWithTag("preferencesSaveButton").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuSelectionTextUpdate() {
    composeTestRule.setContent { PreferencesScreen(mockNavHostController, preferencesViewModel) }

    composeTestRule.onNodeWithTag("unitsSystemButton").assertTextEquals("METRIC")
    composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("KG")

    // Simulate user changing the distance system and weight unit
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").performClick()

    composeTestRule.onNodeWithTag("weightUnitButton").performClick()
    composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

    // Verify that the selections were updated
    composeTestRule.onNodeWithTag("unitsSystemButton").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("LBS")
  }
}
