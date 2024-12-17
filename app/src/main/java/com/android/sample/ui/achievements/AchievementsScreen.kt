package com.android.sample.ui.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.model.achievements.Statistics
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun AchievementsScreen(
    navigationActions: NavigationActions,
    statisticsViewModel: StatisticsViewModel,
    bodyWeightViewModel: WorkoutViewModel<Workout>,
    yogaViewModel: WorkoutViewModel<Workout>
) {
  val workouts = statisticsViewModel.workoutStatistics.collectAsState().value
  val statistics = Statistics(statisticsViewModel.workoutStatistics)
    val doneYogaWorkouts = yogaViewModel.doneWorkouts.collectAsState().value
    val doneBodyWeightWorkouts = bodyWeightViewModel.doneWorkouts.collectAsState().value
  Scaffold(
      modifier = Modifier.testTag("achievementsScreen"),
      content = { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
          if (workouts.isNotEmpty()) {
            Text(text = statistics.getTotalWorkouts().toString())
              Text(text = "Done yoga : " + doneYogaWorkouts.size.toString())
              Text(text = "Done bodyweight : " + doneBodyWeightWorkouts.size.toString())
          }
            Button(onClick = {bodyWeightViewModel.importWorkoutFromDone(doneBodyWeightWorkouts[0].workoutId)}) { }
            Button(onClick = {yogaViewModel.importWorkoutFromDone(doneYogaWorkouts[0].workoutId)}) { }
        }
      },
      bottomBar = { BottomBar(navigationActions) })
}
