package com.android.sample.screen.mainscreen

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.mainscreen.MainScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class MainScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var accountViewModel: UserAccountViewModel
  private lateinit var accountRepo: UserAccountRepository
  private lateinit var localCache: UserAccountLocalCache

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    runTest {
      // Mock the repos for workouts
      bodyWeightRepo = mock()
      yogaRepo = mock()
      accountRepo = mock()

      // Get application context for testing
      val context = ApplicationProvider.getApplicationContext<Context>()

      // Initialize localCache with the context
      localCache = UserAccountLocalCache(context)

      val account =
          UserAccount(
              "1111",
              "Micheal",
              "Phelps",
              1.8f,
              HeightUnit.METER,
              70f,
              WeightUnit.KG,
              Gender.MALE,
              Timestamp(Date()),
              "")

      val bodyWeightWorkouts =
          listOf(
              BodyWeightWorkout(
                  "1",
                  "NopainNogain",
                  "Do 20 push-ups",
                  false,
                  date = LocalDateTime.of(2024, 11, 1, 0, 42)),
              BodyWeightWorkout(
                  "2",
                  "NightSes",
                  "Hold for 60 seconds",
                  false,
                  date = LocalDateTime.of(2024, 11, 1, 0, 43)),
              BodyWeightWorkout(
                  "3",
                  "Hello",
                  "Do 20 push-ups",
                  true,
                  date = LocalDateTime.of(2024, 11, 1, 0, 42)))
      val yogaWorkouts: List<YogaWorkout> = listOf()

      `when`(accountRepo.getUserAccount(any(), any(), any())).thenAnswer {
        val onSuccess = it.getArgument<(UserAccount) -> Unit>(1)
        onSuccess(account)
      }

      `when`(bodyWeightRepo.getDocuments(any(), any())).then {
        it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
      }

      `when`(yogaRepo.getDocuments(any(), any())).then {
        it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
      }
      `when`(bodyWeightRepo.getNewUid()).thenReturn("mocked_uid_123")
      `when`(yogaRepo.getNewUid()).thenReturn("mocked_uid_456")

      accountViewModel = UserAccountViewModel(accountRepo, localCache)
      bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo)
      yogaViewModel = WorkoutViewModel(yogaRepo)
      // Mock the NavigationActions
      navigationActions = mock(NavigationActions::class.java)

      // Mock the current route
      `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)

      // Set the content of the screen for testing
      composeTestRule.setContent {
        MainScreen(navigationActions, bodyWeightViewModel, yogaViewModel, accountViewModel)
      }
    }
  }

  @Test
  fun testMainScreenDisplaysProfileSection() {
    // Check that the profile picture is displayed
    composeTestRule.onNodeWithTag("ProfilePicture").assertIsDisplayed()

    // Check that the welcome text is displayed
    composeTestRule.onNodeWithTag("WelcomeText").assertIsDisplayed()

    // Check that the settings button is displayed
    composeTestRule.onNodeWithTag("FriendsButton").assertExists()

    // Simulate a click on the settings button and verify the navigation
    composeTestRule.onNodeWithTag("FriendsButton").performClick()

    // Verify that navigateTo for SETTINGS was called
    verify(navigationActions).navigateTo(Screen.FRIENDS)
  }

  @Test
  fun testMainScreenDisplaysWorkoutSessionsSection() {
    bodyWeightViewModel.getWorkouts()
    // Check that the workout section is displayed
    composeTestRule.onNodeWithTag("WorkoutSection").assertIsDisplayed()

    // Check that two workout cards are displayed
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(2)

    // Check that the "View all" button is displayed
    composeTestRule.onNodeWithTag("DoubleArrow").assertIsDisplayed()

    // Simulate clicking on "View all"
    composeTestRule.onNodeWithTag("DoubleArrow").performClick()

    // Check tabs are displayed
    composeTestRule.onNodeWithTag("TabSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BodyTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("YogaTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RunningTab").assertIsDisplayed()

    // Check empty card list
    composeTestRule.onNodeWithTag("YogaTab").performClick()
    composeTestRule.onNodeWithTag("NoWorkoutMessage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("NoWorkoutImage").assertIsDisplayed()

    // Check filled card list
    composeTestRule.onNodeWithTag("BodyTab").performClick()
    composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(3)

    // Check navigation when a card is clicked
    composeTestRule.onAllNodesWithTag("WorkoutCard")[0].performClick()
    verify(navigationActions).navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
  }

  @Test
  fun testMainScreenDisplaysQuickWorkoutSection() {
    // Check that the Quick Workout section is displayed
    composeTestRule.onNodeWithTag("QuickSection").assertIsDisplayed()

    // Check that four quick workout buttons are displayed
    composeTestRule.onAllNodesWithTag("QuickWorkoutButton").assertCountEquals(3)

    composeTestRule.onAllNodesWithTag("QuickWorkoutButton")[0].performClick()
    verify(navigationActions).navigateTo(Screen.BODY_WEIGHT_OVERVIEW)

    composeTestRule.onNodeWithTag("Main").performClick()

    composeTestRule.onAllNodesWithTag("QuickWorkoutButton")[2].performClick()
    verify(navigationActions).navigateTo(Screen.YOGA_OVERVIEW)
  }

  @Test
  fun testAchievemetsSectionIsDisplayed() {
    composeTestRule.onNodeWithTag("AchievementText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("AchievementButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("AchievementButton").performClick()
    verify(navigationActions).navigateTo(Screen.ACHIEVEMENTS)
  }

  @Test
  fun testBottomNavigationBarIsDisplayed() {
    // Check that the BottomNavigationBar is displayed
    composeTestRule.onNodeWithTag("BottomBar").assertIsDisplayed()
  }
}
