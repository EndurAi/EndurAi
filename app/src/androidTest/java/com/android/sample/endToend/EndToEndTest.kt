package com.android.sample.endToend

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.android.sample.MainActivity
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class EndToEndTest {
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

  private fun nodeControl(testTag : String, testName : String) {
    if (composeTestRule.onNodeWithTag(testTag).isNotDisplayed()) {
      throw Exception("$testTag not displayed in $testName")
    }
  }

  private fun nodeControlWithScroll(testTag : String, testName : String) {
    if (composeTestRule.onNodeWithTag(testTag).performScrollTo().isNotDisplayed()) {
      throw Exception("$testTag not displayed in $testName")
    }
  }

  @Test
  fun testEndToEnd1() {
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

  @Test
  fun testEndToEnd2() {
    // test begins at the main screen
    // check that everything is displayed


    mainScreenIsWellDisplayed()

    // bo to the View all Screen

    // simulate clicking on "View all"
    composeTestRule.onNodeWithTag("ViewAllButton").performClick()

    viewAllScreenIsWellDisplayed()

    // go back to the main screen
    composeTestRule.onNodeWithTag("Main").performClick()

    // go to the achivement Screen
    composeTestRule.onNodeWithTag("AchievementButton").performScrollTo().performClick()

    achievementScreenIsWellDisplayed()

    // go back to the main screen
    composeTestRule.onNodeWithTag("Main").performClick()

    // go to the setting Screen
    composeTestRule.onNodeWithTag("SettingsButton").performClick()

    settingScreenIsWellDisplayed()

    // go to the preferences screen
    composeTestRule.onNodeWithTag("preferencesButton").performClick()

    preferencesScreenIsWellDisplayed()

    // Perform some actions
    preferencesUpdateOnClick()

    // go back to the settings screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // go to the edit account screen
    //composeTestRule.onNodeWithTag("userDataButton").performClick()

    //editAccountScreenIsWellDisplayed()

    // go back to main
    //composeTestRule.onNodeWithText("Save Changes").performScrollTo().performClick()

    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // go to videos library screen
    composeTestRule.onNodeWithTag("Video").performClick()

    videoLibraryScreenIsWellDisplayed()

    // go back to main
    composeTestRule.onNodeWithTag("backButton").performClick()

    // go to calendar screen
    composeTestRule.onNodeWithTag("Calendar").performClick()

    calendarScreenIsWellDisplayed()

    // we click on the first displayed day
    composeTestRule.onAllNodesWithTag("daySection").get(0).performClick()

    dayCalendarScreenIsWellDisplayed()

    // go back to main
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // go to workout creation screen
    composeTestRule.onNodeWithTag("NewWorkoutButton").performClick()

    sessionSelectionScreenIsWellDisplayed()

    // we decide to go to body weight
    composeTestRule.onNodeWithTag("sessionCard_Body weight").performClick()

    importOrCreateScreenIsWellDisplayed()

    // we go to the import screen
    composeTestRule.onNodeWithText("Import").performClick()

    workoutSelectionScreenIsWellDisplayed()

    // go back to workout creation screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    // we decide to create from scratch
    composeTestRule.onNodeWithText("Create from scratch").performClick()

    workoutCreationScreenIsWellDisplayed()

    // this confirms that we are on main screen
    mainScreenIsWellDisplayed()

  }

  private fun workoutCreationScreenIsWellDisplayed() {
    val testName = "workoutCreationScreenIsWellDisplayed"

    nodeControl("workoutTopBar", testName)
    nodeControl("nameTextField", testName)
    nodeControl("descriptionTextField", testName)
    nodeControl("nextButton", testName)

    composeTestRule.onNodeWithTag("nextButton").performClick()

    nodeControl("addExerciseButton", testName)

    composeTestRule.onNodeWithTag("addExerciseButton").performClick()

    nodeControl("selectExerciseTypeButton", testName)

    composeTestRule.onNodeWithTag("selectExerciseTypeButton").performClick()

    for (exerciseType in
    ExerciseType.entries.filter { it.workoutType == WorkoutType.BODY_WEIGHT }) {
      nodeControl("exerciseType${exerciseType.name}", testName)
    }

    composeTestRule.onNodeWithTag("exerciseTypePUSH_UPS").performClick()

    // we check the text
    composeTestRule
      .onNodeWithTag("selectedExerciseType")
      .assertTextEquals("Selected Exercise: ${ExerciseType.PUSH_UPS}")

    nodeControl("repetitionsTextField", testName)
    nodeControl("addExerciseConfirmButton", testName)

    composeTestRule.onNodeWithTag("addExerciseConfirmButton").performClick()

    nodeControl("exerciseCard", testName)
    nodeControl("saveButton", testName)

    composeTestRule.onNodeWithTag("saveButton").assertHasClickAction()

    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()






  }

  private fun importOrCreateScreenIsWellDisplayed() {
    val testName = "importOrCreateScreenIsWellDisplayed"

    composeTestRule.onNodeWithText("Import").isDisplayed()

    composeTestRule.onNodeWithText("Create from scratch").isDisplayed()

    nodeControl("TopBar", testName)

  }

  private fun workoutSelectionScreenIsWellDisplayed() {
    val testName = "workoutSelectionScreenIsWellDisplayed"

    nodeControl("WorkoutSelectionScreen", testName)

    nodeControl("emptyWorkoutPrompt", testName)

    nodeControl("TopBar", testName)

  }

  private fun sessionSelectionScreenIsWellDisplayed() {
    val testName = "sessionSelectionScreenIsWellDisplayed"

    nodeControl("sessionSelectionScreen", testName)

    nodeControl("sessionCard_Body weight", testName)

    nodeControl("sessionCard_Running", testName)

    nodeControl("sessionCard_Yoga", testName)

    // check that the right text is displayed
    composeTestRule.onNodeWithText("Body weight").assertIsDisplayed()
    composeTestRule.onNodeWithText("Running").assertIsDisplayed()
    composeTestRule.onNodeWithText("Yoga").assertIsDisplayed()
  }



  private fun dayCalendarScreenIsWellDisplayed() {
    val testName = "dayCalendarScreenIsWellDisplayed"

    nodeControl("TopBar", testName)

    nodeControl("Categories", testName)

    nodeControl("Date", testName)

    nodeControl("Hours", testName)

    nodeControl("BottomBar", testName)


  }

  private fun calendarScreenIsWellDisplayed(){
    val testName = "calendarScreenIsWellDisplayed"

    nodeControl("TopBar", testName)

    nodeControl("Categories", testName)

    nodeControl("lazyColumn", testName)

    nodeControl("BottomBar", testName)

  }

  private fun videoScreenIsWellDisplayed(){
    val testName = "videoScreenIsWellDisplayed"

    nodeControl("videoScreen", testName)

    nodeControl("backButton", testName)

    composeTestRule.onNodeWithTag("backButton").assertHasClickAction()

    nodeControl("videosLibraryTitle", testName)

    composeTestRule.onNodeWithTag("videosLibraryTitle").assertTextEquals("Videos Library")

    nodeControl("videoContentBox", testName)

    nodeControl("playerView", testName)

  }

  private fun videoLibraryScreenIsWellDisplayed(){
    val testName = "videoLibraryScreenIsWellDisplayed"

    nodeControl("libraryTitle", testName)

    nodeControl("searchBar", testName)

    nodeControl("tagDropdown", testName)

  }


  private fun editAccountScreenIsWellDisplayed(){
    val testName = "editAccountScreenIsWellDisplayed"

    nodeControl("editScreen", testName)

    nodeControlWithScroll("profileImage", testName)

    nodeControlWithScroll("firstName", testName)

    nodeControlWithScroll("lastName", testName)

    nodeControlWithScroll("height", testName)

    nodeControlWithScroll("heightUnit", testName)

    nodeControlWithScroll("weight", testName)

    nodeControlWithScroll("weightUnit", testName)

    nodeControlWithScroll("gender", testName)

    nodeControlWithScroll("birthday", testName)

    nodeControlWithScroll("submit", testName)

  }



  private fun viewAllScreenIsWellDisplayed(){
    val testName = "viewAllScreenIsWellDisplayed"

    nodeControl("ViewAllScreen", testName)

    nodeControl("ScreenTitle", testName)

    nodeControl("ArrowBackButton", testName)

    nodeControl("BodyTab", testName)

    nodeControl("YogaTab", testName)

    nodeControl("RunningTab", testName)

    // we check that the prompt is there
    composeTestRule.onNodeWithTag("emptyWorkoutPrompt").assertIsDisplayed()

    // we click on the yoga tab
    composeTestRule.onNodeWithTag("YogaTab").performClick()

    // we check that the prompt is there
    composeTestRule.onNodeWithTag("emptyWorkoutPrompt").assertIsDisplayed()

  }

  private fun settingScreenIsWellDisplayed() {
    val testName = "settingScreenIsWellDisplayed"

    nodeControl("settingsScreen", testName)

    nodeControl("preferencesButton", testName)

    nodeControl("deleteAccountButton", testName)

    nodeControl("logoutButton", testName)

    nodeControl("userDataButton", testName)

    nodeControl("ArrowBackButton", testName)

  }

  private fun mainScreenIsWellDisplayed() {
    val testName = "mainScreenIsWellDisplayed"

    nodeControl("mainScreen", testName)

    nodeControl("Main", testName)

    nodeControl("Video", testName)

    nodeControl("SettingsButton", testName)

    nodeControl("WelcomeText", testName)

    nodeControl("ProfilePicture", testName)

    nodeControl("WorkoutSection", testName)

    nodeControl("ViewAllButton", testName)

    nodeControl("QuickSection", testName)

    nodeControl("BottomBar", testName)

    nodeControlWithScroll("AchievementButton", testName)

    // Check that four quick workout buttons are displayed
    composeTestRule.onAllNodesWithTag("QuickWorkoutButton").assertCountEquals(4)

    composeTestRule.onNodeWithTag("NewWorkoutButton").assertExists("NewWorkoutButton doesn't exist")
  }

  private fun achievementScreenIsWellDisplayed() {
    val testName = "achievementScreenIsWellDisplayed"

    nodeControl("achievementsScreen", testName)
  }

  private fun preferencesScreenIsWellDisplayed() {
    val testName = "preferencesScreenIsWellDisplayed"

    nodeControl("preferencesTopBar", testName)

    nodeControl("ArrowBackButton", testName)

    nodeControl("preferencesSaveButton", testName)

    nodeControl("unitsSystemMenu", testName)

    nodeControl("unitsSystemMenuText", testName)

    nodeControl("weightUnitMenu", testName)

    nodeControl("weightUnitMenuText", testName)

  }

  private fun preferencesUpdateOnClick() {
    val testName = "preferencesUpdateOnClick"

    // default values

    nodeControl("unitsSystemButton", testName)

    nodeControl("weightUnitButton", testName)


    // Simulate user changing the system of units and weight unit
    // (metric,kg) -> (imperial,lbs)
    composeTestRule.onNodeWithTag("unitsSystemButton").performClick()

    nodeControl("unitsSystemIMPERIAL", testName)
    nodeControl("unitsSystemMETRIC", testName)

    composeTestRule.onNodeWithTag("unitsSystemIMPERIAL").performClick()
    composeTestRule.onNodeWithTag("weightUnitButton").performClick()

    nodeControl("weightUnitLBS", testName)
    nodeControl("weightUnitKG", testName)

    composeTestRule.onNodeWithTag("weightUnitLBS").performClick()

    // Verify that the selections were updated
    composeTestRule.onNodeWithTag("unitsSystemButton").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitButton").assertTextEquals("LBS")
  }

  private fun selectionWorkoutIsWellDisplayed() {
    val testName = "selectionWorkoutIsWellDisplayed"

    nodeControl("sessionSelectionScreen", testName)

    nodeControl("sessionCard_Body weight", testName)

    nodeControl("sessionCard_Running", testName)

  }
}
