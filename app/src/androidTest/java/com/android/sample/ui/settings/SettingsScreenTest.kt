package com.android.sample.ui.settings

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class SettingsScreenTest {
  private lateinit var userAccountViewModel: UserAccountViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockContext: Context
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userAccountViewModel = mock(UserAccountViewModel::class.java)
    mockContext = mock(Context::class.java)

    // Mock the workout view models
    // Get application context for testing
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Use a real WorkoutLocalCache with a real Context
    // This ensures no NullPointerException from null context.
    val workoutLocalCache = WorkoutLocalCache(context)

    // Mock the repos for workouts
    bodyWeightRepo = mock()
    yogaRepo = mock()

    `when`(bodyWeightRepo.getNewUid()).thenReturn("mocked-bodyweight-uid")
    `when`(yogaRepo.getNewUid()).thenReturn("mocked-yoga-uid")

    bodyWeightViewModel =
        WorkoutViewModel(bodyWeightRepo, workoutLocalCache, BodyWeightWorkout::class.java)
    yogaViewModel = WorkoutViewModel(yogaRepo, workoutLocalCache, YogaWorkout::class.java)
  }

  @Test
  fun displayAllComponents() {
    // Set up the SettingsScreen for testing
    composeTestRule.setContent {
      SettingsScreen(navigationActions, bodyWeightViewModel, yogaViewModel, userAccountViewModel)
    }

    // Verify all essential components are displayed
    composeTestRule.onNodeWithTag("settingsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userDataButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("preferencesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteAccountButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logoutButton").assertIsDisplayed()
  }

  @Test
  fun buttonLogoutNavigatesToAuthScreen() {
    reset(navigationActions)

    composeTestRule.setContent {
      SettingsScreen(navigationActions, bodyWeightViewModel, yogaViewModel)
    }

    // Perform click on the logout button
    composeTestRule.onNodeWithTag("logoutButton").performClick()

    // Verify navigation to the authentication screen
    verify(navigationActions).navigateTo("Auth Screen")
  }

  @Test
  fun deleteAccountButtonCallsDeleteFunction() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Set up the SettingsScreen for testing
    composeTestRule.setContent {
      SettingsScreen(navigationActions, bodyWeightViewModel, yogaViewModel, userAccountViewModel)
    }

    // Perform click on the delete account button
    composeTestRule.onNodeWithTag("deleteAccountButton").performClick()

    // TODO: Find a way to verify the deleteAccount function is called without having an unfinished
  }

  @After
  fun tearDown() {
    reset(navigationActions)
    reset(userAccountViewModel)
  }
}
