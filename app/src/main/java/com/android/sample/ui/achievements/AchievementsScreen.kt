package com.android.sample.ui.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.model.achievements.Statistics
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun AchievementsScreen(
    navigationActions: NavigationActions,
    statisticsViewModel: StatisticsViewModel,
) {
  val workouts = statisticsViewModel.workoutStatistics.collectAsState().value
  val statistics = Statistics(statisticsViewModel.workoutStatistics)
  Scaffold(
      modifier = Modifier.testTag("achievementsScreen"),
      content = { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
          if (workouts.isNotEmpty()) {
            Text(text = statistics.getTotalWorkouts().toString())
          }
        }
      },
      bottomBar = { BottomBar(navigationActions) })
}
