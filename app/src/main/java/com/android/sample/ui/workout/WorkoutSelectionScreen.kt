package com.android.sample.ui.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.mainscreen.WorkoutList
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

/**
 * Screen that displays a list of workouts to choose from.
 *
 * @param viewModel the [WorkoutViewModel] to use to get the list of workouts.
 * @param navigationActions the navigation actions to use to navigate to other screens.
 */
@Composable
fun <T : Workout> WorkoutSelectionScreen(
    viewModel: WorkoutViewModel<T>,
    navigationActions: NavigationActions
) {
  val profile = R.drawable.homme

  Scaffold(
      modifier = Modifier.testTag("WorkoutSelectionScreen"),
      topBar = { TopBar(navigationActions, R.string.WorkoutSelectionTitle) },
      content = { pd ->
        Column(modifier = Modifier.padding(pd)) {
          WorkoutList(
              viewModel = viewModel,
              navigationActions = navigationActions,
              profile = profile,
              onClick = ::navigateToWorkoutCreationScreen)
        }
      })
}

fun navigateToWorkoutCreationScreen(
    workout: Workout,
    viewModel: WorkoutViewModel<Workout>,
    navigationActions: NavigationActions
) {
  when (workout) {
    is BodyWeightWorkout -> navigationActions.navigateTo(Screen.BODY_WEIGHT_IMPORT)
    is YogaWorkout -> navigationActions.navigateTo(Screen.YOGA_IMPORT)
  }
}
