package com.android.sample.ui.workout

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
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
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class QuickWorkoutTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
  private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>
  private lateinit var accountViewModel: UserAccountViewModel
  private lateinit var accountRepo: UserAccountRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    // Mock the repos for workouts
    bodyWeightRepo = mock()
    yogaRepo = mock()
    accountRepo = mock()

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
                date = LocalDateTime.of(2024, 11, 1, 0, 43)))
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
    accountViewModel = UserAccountViewModel(accountRepo)
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
  // Test that each button calls navigateTo
  @Test
  fun testQuickWorkoutButtonsNavigateCorrectly() {
    val nodes = composeTestRule.onAllNodesWithTag("QuickWorkoutButton")
    for (i in 0 until nodes.fetchSemanticsNodes().size) {
      nodes[i].performClick()
    }
    verify(navigationActions, times(nodes.fetchSemanticsNodes().size)).navigateTo(screen = any())
  }

  // Test that each button selects the correct workout
  @Test
  fun testQuickWorkoutButtonsSelectCorrectWorkout() {
    val nodes = composeTestRule.onAllNodesWithTag("QuickWorkoutButton")
    val expectedWorkouts =
        listOf(
            BodyWeightWorkout.WARMUP_WORKOUT,
            BodyWeightWorkout.WORKOUT_PUSH_UPS,
            YogaWorkout.QUICK_YOGA_WORKOUT,
            BodyWeightWorkout.QUICK_BODY_WEIGHT_WORKOUT)

    for (i in 0 until nodes.fetchSemanticsNodes().size) {
      nodes[i].performClick()
      when (i) {
        0 -> assert(bodyWeightViewModel.selectedWorkout.value == expectedWorkouts[i])
        1 -> assert(bodyWeightViewModel.selectedWorkout.value == expectedWorkouts[i])
        2 -> assert(yogaViewModel.selectedWorkout.value == expectedWorkouts[i])
        3 -> assert(bodyWeightViewModel.selectedWorkout.value == expectedWorkouts[i])
      }
    }
  }
}
