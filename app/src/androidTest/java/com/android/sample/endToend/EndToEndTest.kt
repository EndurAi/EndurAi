package com.android.sample.endToend

import android.Manifest
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
import androidx.test.rule.GrantPermissionRule
import com.android.sample.MainActivity
import com.android.sample.model.workout.ExerciseType
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EndToEndTest {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.POST_NOTIFICATIONS)

  @Before
  fun setUp() {
    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
    intent.putExtra("START_DESTINATION", Route.MAIN)
    val context = ApplicationProvider.getApplicationContext<Context>()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  }

  private fun nodeControl(testTag: String, testName: String) {
    if (composeTestRule.onNodeWithTag(testTag).isNotDisplayed()) {
      throw Exception("$testTag not displayed in $testName")
    }
  }

  private fun nodeControlWithText(text: String, testName: String) {
    if (composeTestRule.onNodeWithText(text).isNotDisplayed()) {
      throw Exception("Node with text :{$text} not displayed in $testName")
    }
  }

  private fun nodeControlWithScroll(testTag: String, testName: String) {
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
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    // composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
    // go to the video screen
    //    composeTestRule.onNodeWithTag("Video").performClick()
    // composeTestRule.onNodeWithTag("videoScreen").assertIsDisplayed()
    // go back to the main screen
    //    composeTestRule.onNodeWithTag("backButton").performClick()
    // composeTestRule.onNodeWithTag("mainScreen").assertIsDisplayed()
    // go to the settings screen
    composeTestRule.onNodeWithTag("profile").performClick()
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
    composeTestRule.onNodeWithTag("Main").performClick()
    // mainScreenIsWellDisplayed()
  }

  @Test
  fun testEndToEnd2() {
    // test begins at the main screen
    // check that everything is displayed

    mainScreenIsWellDisplayed()

    // go to the achivement Screen
    composeTestRule.onNodeWithTag("AchievementButton").performScrollTo().performClick()

    achievementScreenIsWellDisplayed()

    // go back to the main screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // go to the setting Screen
    composeTestRule.onNodeWithTag("profile").performClick()

    settingScreenIsWellDisplayed()

    // go to the preferences screen
    composeTestRule.onNodeWithTag("preferencesButton").performClick()

    preferencesScreenIsWellDisplayed()

    // Perform some actions
    preferencesUpdateOnClick()

    // go back to the settings screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // go to the edit account screen
    // composeTestRule.onNodeWithTag("userDataButton").performClick()

    // editAccountScreenIsWellDisplayed()

    // go back to main
    // composeTestRule.onNodeWithText("Save Changes").performScrollTo().performClick()

    composeTestRule.onNodeWithTag("Main").performClick()

    // go to videos library screen
    composeTestRule.onNodeWithTag("Video").performClick()

    composeTestRule
        .onNodeWithTag("loadingIndicator")
        .assertIsDisplayed() // Ensure it starts loading
    composeTestRule.waitUntil(5_000) {
      composeTestRule.onAllNodesWithTag("loadingIndicator").fetchSemanticsNodes().isEmpty()
    }

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
    composeTestRule.onNodeWithTag("Main").performClick()

    // go to workout creation screen
    composeTestRule.onNodeWithTag("Add").performClick()

    // we decide to go to body weight
    composeTestRule.onNodeWithTag("BottomBarBodyweight").performClick()

    importOrCreateScreenIsWellDisplayed()

    // we go to the import screen
    composeTestRule.onNodeWithText("Import from done").performClick()

    workoutSelectionScreenIsWellDisplayed()

    // go back to workout creation screen
    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
    // we decide to create from scratch
    composeTestRule.onNodeWithText("Create from scratch").performClick()

    workoutCreationScreenIsWellDisplayed()

    // this confirms that we are on main screen
    mainScreenIsWellDisplayed()
  }

  @Test
  fun testEndToEnd3() {
    // test begins at the main screen
    // check that everything is displayed

    mainScreenIsWellDisplayed()

    // go to the social screen
    composeTestRule.onNodeWithTag("FriendsButton").performClick()

    // verify that the friend screen is correctly displayed
    friendsScreenIsWellDisplayed()

    // go to the add friend screen
    composeTestRule.onNodeWithTag("addFriendButton").performClick()

    // verify that the add friend screen is correctly displayed
    addFriendScreenIsWellDisplayed()

    composeTestRule.onNodeWithTag("CustomSearchBar").assertIsDisplayed()

    // go to the invitation friend section
    composeTestRule.onNodeWithTag("invitationsTabButton").performClick()

    // we check that this string is correctly displayed
    composeTestRule.onNodeWithText("Oh no! You have no invitations.").assertIsDisplayed()

    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    composeTestRule.onNodeWithTag("ArrowBackButton").performClick()

    // this confirms that we are on main screen
    mainScreenIsWellDisplayed()
  }

  private fun workoutCreationScreenIsWellDisplayed() {
    val testName = "workoutCreationScreenIsWellDisplayed"

    nodeControl("TopBar", testName)
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
    // composeTestRule.onNodeWithTag("ArrowBackButton").performClick()
  }

  private fun importOrCreateScreenIsWellDisplayed() {
    val testName = "importOrCreateScreenIsWellDisplayed"

    composeTestRule.onNodeWithText("Choose from existing").isDisplayed()

    composeTestRule.onNodeWithText("Create from scratch").isDisplayed()

    nodeControl("TopBar", testName)
  }

  private fun workoutSelectionScreenIsWellDisplayed() {
    val testName = "workoutSelectionScreenIsWellDisplayed"

    nodeControl("ImportScreen", testName)

    nodeControl("TopBar", testName)
  }

  private fun dayCalendarScreenIsWellDisplayed() {
    val testName = "dayCalendarScreenIsWellDisplayed"

    nodeControl("TopBar", testName)

    nodeControl("Categories", testName)

    nodeControl("Date", testName)

    nodeControl("Hours", testName)
  }

  private fun calendarScreenIsWellDisplayed() {
    val testName = "calendarScreenIsWellDisplayed"

    nodeControl("TopBar", testName)

    nodeControl("Categories", testName)

    nodeControl("lazyColumn", testName)

    nodeControl("BottomBar", testName)
  }

  private fun videoScreenIsWellDisplayed() {
    val testName = "videoScreenIsWellDisplayed"

    nodeControl("videoScreen", testName)

    nodeControl("backButton", testName)

    composeTestRule.onNodeWithTag("backButton").assertHasClickAction()

    nodeControl("videosLibraryTitle", testName)

    composeTestRule.onNodeWithTag("videosLibraryTitle").assertTextEquals("Videos Library")

    nodeControl("videoContentBox", testName)

    nodeControl("playerView", testName)
  }

  private fun videoLibraryScreenIsWellDisplayed() {
    val testName = "videoLibraryScreenIsWellDisplayed"

    //    nodeControl("libraryTitle", testName) the title was removed in the ui

    nodeControl("searchField", testName)

    nodeControl("tagDropdown", testName)
  }

  private fun editAccountScreenIsWellDisplayed() {
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

  private fun viewAllScreenIsWellDisplayed() {
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

    nodeControl("TopBar", testName)
  }

  private fun mainScreenIsWellDisplayed() {
    val testName = "mainScreenIsWellDisplayed"

    nodeControl("mainScreen", testName)

    nodeControl("Main", testName)

    nodeControl("Video", testName)

    nodeControl("WelcomeText", testName)

    nodeControl("ProfilePicture", testName)

    nodeControl("WorkoutSection", testName)

    nodeControl("QuickSection", testName)

    nodeControl("BottomBar", testName)

    nodeControlWithScroll("AchievementButton", testName)

    // Check that four quick workout buttons are displayed
    composeTestRule.onAllNodesWithTag("QuickWorkoutButton").assertCountEquals(3)

    composeTestRule.onNodeWithTag("DoubleArrow").assertExists("Double Arrow doesn't exist")
  }

  private fun achievementScreenIsWellDisplayed() {
    val testName = "achievementScreenIsWellDisplayed"

    // Verify we are in StatsScreen
    nodeControl("StatsScreen", testName)
    nodeControlWithText("Calories of the week", testName)
    nodeControl("KcalCard", testName)
    nodeControlWithText("Kcal", testName)

    // Verify we have 2 charts and are displayed
    val charts = composeTestRule.onAllNodesWithTag("Chart").assertCountEquals(2)
    charts[0].assertIsDisplayed()
    charts[1].assertIsDisplayed()

    // Verify the pie chart
    nodeControlWithText("Type exercise repartition", testName)
    nodeControl("PieChart", testName)
  }

  private fun preferencesScreenIsWellDisplayed() {
    val testName = "preferencesScreenIsWellDisplayed"

    nodeControl("TopBar", testName)

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
    composeTestRule.onNodeWithTag("unitsSystemText").assertTextEquals("IMPERIAL")
    composeTestRule.onNodeWithTag("weightUnitText").assertTextEquals("LBS")
  }

  private fun selectionWorkoutIsWellDisplayed() {
    val testName = "selectionWorkoutIsWellDisplayed"

    nodeControl("sessionSelectionScreen", testName)

    nodeControl("sessionCard_Body weight", testName)

    nodeControl("sessionCard_Running", testName)
  }

  private fun friendsScreenIsWellDisplayed() {
    val testName = "friendsScreenIsWellDisplayed"

    nodeControl("friendsScreen", testName)
    nodeControl("searchBarRow", testName)
    nodeControl("searchBar", testName)
    nodeControl("addFriendButton", testName)
    composeTestRule.onNodeWithTag("addFriendButton").assertHasClickAction()
  }

  private fun addFriendScreenIsWellDisplayed() {
    val testName = "addFriendScreenIsWellDisplayed"

    nodeControl("addFriendScreen", testName)
    nodeControl("tabButtons", testName)
    nodeControl("newConnectionsTabButton", testName)
    composeTestRule.onNodeWithTag("newConnectionsTabButton").assertHasClickAction()
    nodeControl("invitationsTabButton", testName)
    composeTestRule.onNodeWithTag("invitationsTabButton").assertIsDisplayed().assertHasClickAction()
    nodeControl("newConnectionsContent", testName)
  }
}
