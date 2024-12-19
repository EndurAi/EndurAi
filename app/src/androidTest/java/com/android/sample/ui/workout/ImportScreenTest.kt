package com.android.sample.ui.workout

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutLocalCache
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.navigation.NavigationActions
import java.time.LocalDateTime
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class ImportScreenTest {
  private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
    private lateinit var workoutLocalCache: WorkoutLocalCache
  private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displaysScreenCorrectly() {
    bodyWeightRepo = mock()

    // Mock dependencies
    val mockNavigationActions = mock(NavigationActions::class.java)

    val mockDoneWorkouts =
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
        )
    `when`(bodyWeightRepo.getDoneDocuments(any(), any())).then {
      it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(mockDoneWorkouts)
    }

      val context = ApplicationProvider.getApplicationContext<Context>()
      // Use a real WorkoutLocalCache with a real Context
      // This ensures no NullPointerException from null context.
      workoutLocalCache = WorkoutLocalCache(context)
      bodyWeightViewModel =
          WorkoutViewModel(bodyWeightRepo, workoutLocalCache, BodyWeightWorkout::class.java)

    // Launch the composable
    composeTestRule.setContent {
      ImportScreen(
          navigationActions = mockNavigationActions,
          workoutViewModel = bodyWeightViewModel,
          workoutType = WorkoutType.BODY_WEIGHT)
    }

    bodyWeightViewModel.getDoneWorkouts()
    // Assertions: Verify all key UI elements are displayed
    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ImportScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ExerciseTypeTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoneWorkoutList").assertIsDisplayed()

    composeTestRule.onAllNodesWithTag("DoneWorkoutCard").assertCountEquals(2)

    // Verify that clicking on a card call import from the repo
    composeTestRule.onAllNodesWithTag("DoneWorkoutCard")[0].performClick()
    verify(bodyWeightRepo).importDocumentFromDone(any(), any(), any())
  }
}
