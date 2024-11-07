package com.android.sample.ui.workout

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.WorkoutRepository
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import java.time.LocalDateTime

class ImportWorkoutTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>
    private lateinit var yogaViewModel: WorkoutViewModel<YogaWorkout>
    private lateinit var navigationActions: NavigationActions
    private lateinit var bodyWeightRepo: WorkoutRepository<BodyWeightWorkout>
    private lateinit var yogaRepo: WorkoutRepository<YogaWorkout>

    @Before
    fun setUp()
    {
        bodyWeightRepo = mock()
        yogaRepo = mock()

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

        `when`(bodyWeightRepo.getDocuments(any(), any())).then {
            it.getArgument<(List<BodyWeightWorkout>) -> Unit>(0)(bodyWeightWorkouts)
        }

        `when`(yogaRepo.getDocuments(any(), any())).then {
            it.getArgument<(List<YogaWorkout>) -> Unit>(0)(yogaWorkouts)
        }
        bodyWeightViewModel = WorkoutViewModel(bodyWeightRepo)
        yogaViewModel = WorkoutViewModel(yogaRepo)

        navigationActions = mock(NavigationActions::class.java)

    }

    @Test
    fun testWorkoutSelectionScreen() {
        // Set the content of the screen
        composeTestRule.setContent {
            WorkoutSelectionScreen(
                navigationActions = navigationActions,
                viewModel = bodyWeightViewModel,
            )
        }
        bodyWeightViewModel.getWorkouts()
        // Check that the screen is displayed
        composeTestRule.onNodeWithTag("WorkoutSelectionScreen").assertIsDisplayed()
        // Check that the body weight workout is displayed
        composeTestRule.onAllNodesWithTag("WorkoutCard").assertCountEquals(2)
        //Check that the body weight workout is clickable
        composeTestRule.onAllNodesWithTag("WorkoutCard").assertAll(hasClickAction())

    }
}
