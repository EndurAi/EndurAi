package com.android.sample.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.achievements.Statistics
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.achievements.WorkoutStatistics
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.preferences.UnitsSystem
import com.android.sample.ui.composables.CaloriesDisplay
import com.android.sample.ui.composables.Charts
import com.android.sample.ui.composables.PieChartWorkoutType
import com.android.sample.ui.composables.ToggleButtonAchievements
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.OpenSans
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AchievementsScreen(
    navigationActions: NavigationActions,
    statisticsViewModel: StatisticsViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  val workoutStatistics = statisticsViewModel.workoutStatistics
  val emptyListFlow: StateFlow<List<WorkoutStatistics>> = MutableStateFlow(emptyList())
  val statistics = Statistics(if (workoutStatistics != null) workoutStatistics else emptyListFlow)
  val preferences = preferencesViewModel.preferences.collectAsState().value

  var isStatsSelected by remember { mutableStateOf(true) }

  @Composable
  fun StatisticsScreen(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().testTag("StatsScreen"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(Modifier.weight(0.50f))

          Text(
              text = stringResource(R.string.weeklyCalories),
              fontSize = 28.sp,
              fontFamily = OpenSans,
              fontWeight = FontWeight.SemiBold,
              color = Black)

          Spacer(Modifier.weight(0.03f))

          CaloriesDisplay(calories = statistics.getCaloriesOfTheWeek())

          Spacer(Modifier.weight(0.07f))

          Row(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Charts(
                  data = statistics.getCaloriesOfTheWeekToList(),
                  labelTitle = stringResource(R.string.caloriesLabel))

              Spacer(Modifier.height(4.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Charts(
                  data =
                      statistics.getDistanceOfTheWeekPerDay(
                          isInMile =
                              (preferences?.unitsSystem ?: UnitsSystem.METRIC) ==
                                  UnitsSystem.IMPERIAL),
                  labelTitle =
                      stringResource(R.string.distanceWithoutUnit) +
                          if ((preferences?.unitsSystem ?: UnitsSystem.METRIC) ==
                              UnitsSystem.IMPERIAL) {
                            stringResource(R.string.mileWithParentheses)
                          } else stringResource(R.string.kmWithParentheses))

              Spacer(Modifier.height(2.dp))
            }
          }

          Spacer(Modifier.weight(0.02f))

          Text(
              text = stringResource(R.string.typeRepartition),
              fontSize = 28.sp,
              fontFamily = OpenSans,
              fontWeight = FontWeight.SemiBold,
              color = Black)

          Spacer(Modifier.weight(0.03f))

          PieChartWorkoutType(frequency = statistics.getWorkoutTypeFrequency())

          Spacer(Modifier.weight(0.1f))
        }
  }

  Scaffold(
      modifier = Modifier.testTag("AchievementsScreen"),
      topBar = { TopBar(navigationActions, R.string.achievements) }) { padding ->
        Column(
            modifier = Modifier.fillMaxHeight(fraction = 0.2f).fillMaxWidth().padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(Modifier.weight(0.5f))

              ToggleButtonAchievements(onClick = { isStatsSelected = !isStatsSelected })

              Spacer(Modifier.weight(0.5f))
            }

        when (isStatsSelected) {
          true -> StatisticsScreen(padding = padding)
          false -> InfiniteCalendar(statistics, padding)
        }
      }
}
