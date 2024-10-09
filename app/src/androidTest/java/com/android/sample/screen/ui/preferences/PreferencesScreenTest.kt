package com.android.sample.screen.ui.preferences


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.preferences.PreferencesRepository
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.ui.preferences.PreferencesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class PreferencesScreenTest {
    private lateinit var mockPreferencesRepository: PreferencesRepository
    private lateinit var preferencesViewModel: PreferencesViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        // Mock the ViewModel and Preferences
        mockPreferencesRepository = mock(PreferencesRepository::class.java)
        preferencesViewModel = PreferencesViewModel(mockPreferencesRepository)

    }

    @Test
    fun displayAllComponents() {
        composeTestRule.setContent { PreferencesScreen(preferencesViewModel) }

        composeTestRule.onNodeWithTag("preferencesTopBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("preferencesSaveButton").assertIsDisplayed()

        composeTestRule.onNodeWithTag("distanceSystemMenu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distanceSystemMenuText").assertTextEquals("Distance system")
        composeTestRule.onNodeWithTag("weightUnitMenu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("weightUnitMenuText").assertTextEquals("Weight unit")
        composeTestRule.onNodeWithTag("preferencesSaveButton").assertIsDisplayed()
    }

    @Test
    fun testDropdownMenuSelectionTextUpdate() {
        composeTestRule.setContent { PreferencesScreen(preferencesViewModel) }

        composeTestRule.onNodeWithTag("distanceSystemButton").assertTextEquals("METRIC")
        composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("KG")

        // Simulate user changing the distance system and weight unit
        composeTestRule.onNodeWithTag("distanceSystemButton").performClick()
        composeTestRule.onNodeWithTag("distanceSystemIMPERIAL").performClick()

        composeTestRule.onNodeWithTag("weightUnitButton").performClick()
        composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

        // Verify that the selections were updated
        composeTestRule.onNodeWithTag("distanceSystemButton").assertTextEquals("IMPERIAL")
        composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("LBS")
    }
}