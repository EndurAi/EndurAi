package com.android.sample.endToend

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
    composeTestRule.onNodeWithTag("AchievementButton").performScrollTo().performClick()
    achievementScreenIsWellDisplayed()
    // go back to the main screen
    composeTestRule.onNodeWithTag("Main").performClick()
    // composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
    // go to the video screen
    //    composeTestRule.onNodeWithTag("Video").performClick()
    // composeTestRule.onNodeWithTag("videoScreen").assertIsDisplayed()
    // go back to the main screen
    //    composeTestRule.onNodeWithTag("backButton").performClick()
    // composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
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
    // settingScreenIsWellDisplayed()
    // go back to the main screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    // mainScreenIsWellDisplayed()
  }

  private fun settingScreenIsWellDisplayed() {
    if (composeTestRule.onNodeWithTag("settingsScreen").isNotDisplayed()) {
      throw Exception("settingsScreen not displayed in settingScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("preferencesButton").isNotDisplayed()) {
      throw Exception("preferencesButton not displayed in settingScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("deleteAccountButton").isNotDisplayed()) {
      throw Exception("deleteAccountButton not displayed in settingScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("logoutButton").isNotDisplayed()) {
      throw Exception("logoutButton not displayed in settingScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("userDataButton").isNotDisplayed()) {
      throw Exception("userDataButton not displayed in settingScreenIsWellDisplayed")
    }
    // navigation bar should not be displayed
    if (composeTestRule.onNodeWithTag("bottomNavigationMenu").isDisplayed()) {
      throw Exception(
          "bottomNavigationMenu should not be displayed in settingScreenIsWellDisplayed")
    }
  }

  private fun mainScreenIsWellDisplayed() {
    if (composeTestRule.onNodeWithTag("mainScreen").isNotDisplayed()) {
      throw Exception("mainScreen not displayed in mainScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("Main").isNotDisplayed()) {
      throw Exception("Main not displayed in mainScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("Video").isNotDisplayed()) {
      throw Exception("Video not displayed in mainScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("SettingsButton").isNotDisplayed()) {
      throw Exception("SettingsButton not displayed in mainScreenIsWellDisplayed")
    }
    composeTestRule.onNodeWithTag("NewWorkoutButton").assertExists("NewWorkoutButton doesent exist")
  }

  private fun achievementScreenIsWellDisplayed() {
    if (composeTestRule.onNodeWithTag("achievementsScreen").isNotDisplayed()) {
      throw Exception("achievementsScreen not displayed in achievementScreenIsWellDisplayed")
    }
  }

  private fun preferencesScreenIsWellDisplayed() {
    if (composeTestRule.onNodeWithTag("preferencesTopBar").isNotDisplayed()) {
      throw Exception("preferencesTopBar not displayed in preferencesScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("ArrowBackButton").isNotDisplayed()) {
      throw Exception("ArrowBackButton not displayed in preferencesScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("preferencesSaveButton").isNotDisplayed()) {
      throw Exception("preferencesSaveButton not displayed in preferencesScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("unitsSystemMenu").isNotDisplayed()) {
      throw Exception("unitsSystemMenu not displayed in preferencesScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("unitsSystemMenuText").isNotDisplayed()) {
      throw Exception("unitsSystemMenuText not displayed in preferencesScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("weightUnitMenu").isNotDisplayed()) {
      throw Exception("weightUnitMenu not displayed in preferencesScreenIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("weightUnitMenuText").isNotDisplayed()) {
      throw Exception("weightUnitMenuText not displayed in preferencesScreenIsWellDisplayed")
    }
  }

  private fun preferencesUpdateOnClick() {
    // default values
    if (composeTestRule.onNodeWithTag("unitsSystemButton").isNotDisplayed()) {
      throw Exception("unitsSystemButton not displayed in preferencesUpdateOnClick")
    }
    if (composeTestRule.onNodeWithTag("weightUnitButton").isNotDisplayed()) {
      throw Exception("weightUnitButton not displayed in preferencesUpdateOnClick")
    }

    // Simulate user changing the system of units and weight unit
    // (metric,kg) -> (imperial,lbs)
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()
    if (composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").isNotDisplayed()) {
      throw Exception("unitsSystemIMPERIAL not displayed in preferencesUpdateOnClick")
    }
    if (composeTestRule.onNodeWithTag("unitsSystemMETRIC").isNotDisplayed()) {
      throw Exception("unitsSystemMETRIC not displayed in preferencesUpdateOnClick")
    }
    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").performClick()

    composeTestRule.onNodeWithTag("weightUnitButton").performClick()
    if (composeTestRule.onNodeWithTag("weightUnitLBS").isNotDisplayed()) {
      throw Exception("weightUnitLBS not displayed in preferencesUpdateOnClick")
    }
    if (composeTestRule.onNodeWithTag("weightUnitKG").isNotDisplayed()) {
      throw Exception("weightUnitKG not displayed in preferencesUpdateOnClick")
    }
    composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

    // Verify that the selections were updated
    composeTestRule.onNodeWithTag("unitsSystemButton").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("LBS")
  }

  private fun selectionWorkoutIsWellDisplayed() {
    if (composeTestRule.onNodeWithTag("sessionSelectionScreen").isNotDisplayed()) {
      throw Exception("sessionSelectionScreen not displayed in selectionWorkoutIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("sessionCard_Body weight").isNotDisplayed()) {
      throw Exception("sessionCard_Body weight not displayed in selectionWorkoutIsWellDisplayed")
    }
    if (composeTestRule.onNodeWithTag("sessionCard_Running").isNotDisplayed())
        throw Exception("sessionCard_Running not displayed in selectionWorkoutIsWellDisplayed")
  }
}
