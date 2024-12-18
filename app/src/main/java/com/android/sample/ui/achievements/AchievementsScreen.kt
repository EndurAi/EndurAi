package com.android.sample.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.Point
import com.android.sample.R
import com.android.sample.model.achievements.Statistics
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.ui.composables.CaloriesDisplay
import com.android.sample.ui.composables.Charts
import com.android.sample.ui.composables.ToggleButtonAchievements
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.OpenSans

@Composable
fun AchievementsScreen(
    navigationActions: NavigationActions,
    statisticsViewModel: StatisticsViewModel
) {
  val statistics = Statistics(statisticsViewModel.workoutStatistics)

  var isStatsSelected by remember { mutableStateOf(true) }

  @Composable
  fun StatisticsScreen(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(Modifier.weight(0.12f))

          Text(
              text = "Calories of the week",
              fontSize = 28.sp,
              fontFamily = OpenSans,
              fontWeight = FontWeight.SemiBold,
              color = Black)

          Spacer(Modifier.weight(0.03f))

          CaloriesDisplay(calories = statistics.getCaloriesOfTheWeek())

          val pointsData: List<Point> =
              listOf(
                  Point(0f, 562f),
                  Point(1f, 1540f),
                  Point(2f, 850f),
                  Point(3f, 200f),
                  Point(4f, 690f))
          Charts()

          Spacer(Modifier.weight(0.85f))
        }
  }

  Scaffold(
      modifier = Modifier.testTag("achievementsScreen"),
      topBar = { TopBar(navigationActions, R.string.achievements) }) { padding ->
        Column(
            modifier = Modifier.fillMaxHeight(fraction = 0.2f).fillMaxWidth().padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(Modifier.weight(0.5f))

              ToggleButtonAchievements(
                  onClick = {
                    isStatsSelected = !isStatsSelected
                    println("isStateSelected : " + isStatsSelected)
                  })

              Spacer(Modifier.weight(0.5f))
            }

        when (isStatsSelected) {
          true -> StatisticsScreen(padding = padding)
          false -> InfiniteCalendar(statistics, padding)
        }
      }
}
