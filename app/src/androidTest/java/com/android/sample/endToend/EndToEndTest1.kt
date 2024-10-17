package com.android.sample.endToend

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.content.ContextCompat.startActivity
import androidx.test.core.app.ApplicationProvider
import com.android.sample.MainActivity
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EndToEndTest1 {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
    intent.putExtra("START_DESTINATION", Route.MAIN)
    val context = ApplicationProvider.getApplicationContext<Context>()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  }

  @Test
  fun testEndToEnd() {
    // Test begins at the main screen
    // check that everything is displayed
    mainScreenIsWellDisplayed()
    // Go to the achievement screen
    composeTestRule.onNodeWithTag("Achievements").performClick()
    achievementScreenIsWellDisplayed()
    // go back to the main screen
    composeTestRule.onNodeWithTag("Main").performClick()
    composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
    // go to the video screen
    composeTestRule.onNodeWithTag("Video").performClick()
    composeTestRule.onNodeWithTag("videoScreen").assertIsDisplayed()
    // go back to the main screen
    composeTestRule.onNodeWithTag("Main").performClick()
    composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
    // go to the settings screen
    composeTestRule.onNodeWithTag("SettingsButton").performClick()
    settingScreenIsWellDisplayed()
    // go to the preferences screen
    composeTestRule.onNodeWithTag("preferencesButton").performClick()
    preferencesScreenIsWellDisplayed()
    // Perform some actions
    preferencesUpdateOnClick()
    // go back to the settings screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    settingScreenIsWellDisplayed()
    // go back to the main screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    mainScreenIsWellDisplayed()
    // go to workout selection screen
    composeTestRule.onNodeWithTag("NewWorkoutButton").performClick()
    selectionWorkoutIsWellDisplayed()
    // go back to the main screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    mainScreenIsWellDisplayed()
  }

  private fun settingScreenIsWellDisplayed() {
    composeTestRule.onNodeWithTag("settingsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteAccountButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logoutButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userDataButton").assertIsDisplayed()
    // navigation bar should not be displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertDoesNotExist()
  }

  private fun mainScreenIsWellDisplayed() {
    composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Main").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Video").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Achievements").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SettingsButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("NewWorkoutButton").assertIsDisplayed()
  }

  private fun achievementScreenIsWellDisplayed() {
    composeTestRule.onNodeWithTag("achievementsScreen").assertIsDisplayed()
  }

  private fun preferencesScreenIsWellDisplayed() {
    composeTestRule.onNodeWithTag("preferencesTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesSaveButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("preferencesSaveButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemMenuText").assertTextEquals("System of units")
    composeTestRule.onNodeWithTag("weightUnitMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitMenuText").assertTextEquals("Weight unit")
  }

  private fun preferencesUpdateOnClick() {
    // default values
    composeTestRule.onNodeWithTag("unitsSystemButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemButton").assertTextEquals("METRIC")
    composeTestRule.onNodeWithTag("weightUnitButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("KG")

    // Simulate user changing the system of units and weight unit
    // (metric,kg) -> (imperial,lbs)
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemMETRIC").assertIsDisplayed()
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").performClick()

    composeTestRule.onNodeWithTag("weightUnitButton").performClick()
    composeTestRule.onNodeWithTag("weightUnitLBS").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitKG").assertIsDisplayed()
    composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

    // Verify that the selections were updated
    composeTestRule.onNodeWithTag("unitsSystemButton").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("LBS")
  }

  private fun selectionWorkoutIsWellDisplayed() {

    composeTestRule.onNodeWithTag("sessionSelectionScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Body weight").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Running").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sessionCard_Yoga").assertIsDisplayed()

    composeTestRule.onNodeWithText("Body weight").assertIsDisplayed()
    composeTestRule.onNodeWithText("Running").assertIsDisplayed()
    composeTestRule.onNodeWithText("Yoga").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertDoesNotExist()
    composeTestRule.onNodeWithTag("ArrowBackButton").assertIsDisplayed()
  }
}
