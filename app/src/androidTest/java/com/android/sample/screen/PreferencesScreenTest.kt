package com.android.sample.screen

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.preferences.Preferences
import com.android.sample.model.preferences.PreferencesRepository
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.preferences.UnitsSystem
import com.android.sample.model.preferences.WeightUnit
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.preferences.PreferencesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

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

    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesSaveButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesSaveButton").assertHasClickAction()

    composeTestRule.onNodeWithTag("unitsSystemMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemMenuText").assertTextEquals("System of units")
    composeTestRule.onNodeWithTag("weightUnitMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitMenuText").assertTextEquals("Weight unit")
    composeTestRule.onNodeWithTag("wavyBackground").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuSelectionTextUpdate() {
    composeTestRule.setContent { PreferencesScreen(mockNavHostController, preferencesViewModel) }

    composeTestRule.onNodeWithTag("unitsSystemButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("METRIC")
    composeTestRule.onNodeWithTag("weightUnitButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("KG")

    // Simulate user changing the system of units and weight unit
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemMETRIC").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").performClick()

    composeTestRule.onNodeWithTag("weightUnitButton").performClick()
    composeTestRule.onNodeWithTag("weightUnitLBS").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitKG").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

    // Verify that the selections were updated
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("LBS")
  }

  @Test
  fun savingPreferencesCallsUpdatePreferences() {
    composeTestRule.setContent { PreferencesScreen(mockNavHostController, preferencesViewModel) }
    val secondPreferences = Preferences(unitsSystem = UnitsSystem.IMPERIAL, weight = WeightUnit.LBS)
    // check that users has default value of preferences
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("METRIC")
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("KG")

    // Simulate user changing the system of units and weight unit
    // from (METRIC, KG) to (IMPERIAL, LBS)
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").performClick()
    composeTestRule.onNodeWithTag("weightUnitButton").performClick()
    composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

    // Save the changes
    composeTestRule.onNodeWithTag("preferencesSaveButton").performClick()

    // check that users has default (IMPERIAL, LBS) of preferences
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("LBS")

    // check if the preferences were updated in the repository
    verify(mockPreferencesRepository).updatePreferences(eq(secondPreferences), any(), any())
  }

  @Test
  fun savingPreferencesCallsGoBack() {
    composeTestRule.setContent { PreferencesScreen(mockNavHostController, preferencesViewModel) }
    composeTestRule.onNodeWithTag("preferencesSaveButton").performClick()
    // verify that the user goes back
    verify(mockNavHostController).goBack()
  }

  @Test
  fun savingPreferencesNeverCallsUpdatePreferences_OnUnchangedValues() {
    composeTestRule.setContent { PreferencesScreen(mockNavHostController, preferencesViewModel) }
    val secondPreferences = Preferences(unitsSystem = UnitsSystem.METRIC, weight = WeightUnit.KG)
    // check that users has default value of preferences
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("METRIC")
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("KG")

    // Simulate user changing the system of units and weight unit
    // from (METRIC, KG) to (METRIC, KG)
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()
    composeTestRule.onNodeWithTag("unitsSystemMETRIC").performClick()
    composeTestRule.onNodeWithTag("weightUnitButton").performClick()
    composeTestRule.onNodeWithTag("weightUnitKG").performClick()

    // Save the changes
    composeTestRule.onNodeWithTag("preferencesSaveButton").performClick()

    // check that users has default (IMPERIAL, LBS) of preferences
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("METRIC")
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("KG")

    // check if the the user doesn't make unnecessary update in teh firestore repository
    verify(mockPreferencesRepository, never())
        .updatePreferences(eq(secondPreferences), any(), any())
  }
}
