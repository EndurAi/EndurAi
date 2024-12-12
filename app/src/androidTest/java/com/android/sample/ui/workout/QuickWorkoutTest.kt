package com.android.sample.ui.workout

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountLocalCache
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.mainscreen.MainScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times

class QuickWorkoutTest {
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
      MockitoAnnotations.openMocks(this)

      // Get application context for testing
      val context = ApplicationProvider.getApplicationContext<Context>()

      // Initialize localCache with the context
      localCache = UserAccountLocalCache(context)

      // Use a real WorkoutLocalCache with a real Context
      // This ensures no NullPointerException from null context.
      val workoutLocalCache = WorkoutLocalCache(context)

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

      `when`(bodyWeightRepo.getNewUid()).thenReturn("mocked-bodyweight-uid")
      `when`(yogaRepo.getNewUid()).thenReturn("mocked-yoga-uid")

      `when`(yogaRepo.getDocuments(any(), any())).then {
        it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
      }

      accountViewModel = UserAccountViewModel(accountRepo, localCache)
      bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo, workoutLocalCache)
      yogaViewModel = WorkoutViewModel(yogaRepo, workoutLocalCache)
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

  // Test that each button selects the correct workout
  @Test
  fun testQuickWorkoutButtonsSelectCorrectWorkout() {
    val nodes = composeTestRule.onAllNodesWithTag("QuickWorkoutButton")
    val expectedWorkouts: List<Workout> =
        listOf(BodyWeightWorkout.QUICK_BODY_WEIGHT_WORKOUT, YogaWorkout.QUICK_YOGA_WORKOUT).map {
          when (it) {
            is BodyWeightWorkout ->
                bodyWeightViewModel.copyOf(it) // In this test, the new UID is harcoded
            is YogaWorkout -> yogaViewModel.copyOf(it)
            else -> {
              null as Workout
            }
          }
        }

    for (i in 0 until nodes.fetchSemanticsNodes().size) {
      nodes[i].performClick()
      when (i) {
        0 -> assert(equals(bodyWeightViewModel.selectedWorkout.value!!, expectedWorkouts[0]))
        2 -> assert(equals(yogaViewModel.selectedWorkout.value!!, expectedWorkouts[1]))
      }
    }
  }

  private fun equals(workout1: Workout, workout2: Workout): Boolean {
    return workout1.workoutId == workout2.workoutId &&
        workout1.name == workout2.name &&
        workout1.description == workout2.description &&
        workout1.warmup == workout2.warmup &&
        workout1.date == workout2.date &&
        workout1.userIdSet == workout2.userIdSet &&
        workout1.exercises == workout2.exercises
  }
}
