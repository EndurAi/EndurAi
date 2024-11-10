package com.android.sample.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.calendar.CalendarViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.BodyWeightTag
import com.android.sample.ui.theme.CalendarBackground
import com.android.sample.ui.theme.RunningTag
import com.android.sample.ui.theme.YogaTag
import java.time.format.DateTimeFormatter
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/**
 * Displays the Day Calendar screen with workouts for the selected date.
 *
 * @param navigationActions Navigation actions for navigating between screens.
 * @param bodyworkoutViewModel ViewModel for managing bodyweight workouts.
 * @param yogaworkoutViewModel ViewModel for managing yoga workouts.
 * @param calendarViewModel ViewModel for managing the selected date.
 */
@Composable
fun DayCalendarScreen(
    navigationActions: NavigationActions,
    bodyworkoutViewModel: WorkoutViewModel<Workout>,
    yogaworkoutViewModel: WorkoutViewModel<Workout>,
    calendarViewModel: CalendarViewModel
) {
  val selectedDate by calendarViewModel.selectedDate.collectAsState()
  val workoutsBody by bodyworkoutViewModel.workouts.collectAsState(emptyList())
  val workoutsYoga by yogaworkoutViewModel.workouts.collectAsState(emptyList())

  val dailyWorkouts =
      (workoutsYoga + workoutsBody).filter {
        it.date.toLocalDate().toKotlinLocalDate() == selectedDate
      }

  val coloredWorkouts =
      dailyWorkouts
          .map {
            when (it) {
              is BodyWeightWorkout -> ColoredWorkout(it, BodyWeightTag, WorkoutType.BODY_WEIGHT)
              is YogaWorkout -> ColoredWorkout(it, YogaTag, WorkoutType.YOGA)
              else -> ColoredWorkout(it, RunningTag, WorkoutType.RUNNING)
            }
          }
          .sortedBy { it.workout.date.minute }

  Scaffold(
      topBar = { TopBar(navigationActions, R.string.calendar_title) },
      bottomBar = { BottomBar(navigationActions) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
          Legend()

          Divider(
              color = Color.LightGray,
              thickness = 1.dp,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

          Text(
              text =
                  selectedDate?.toJavaLocalDate()?.format(DateTimeFormatter.ofPattern("d MMMM"))
                      ?: "",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              modifier =
                  Modifier.padding(top = 8.dp, start = 16.dp, bottom = 16.dp).testTag("Date"))

          Box(
              modifier =
                  Modifier.testTag("Hours")
                      .fillMaxWidth()
                      .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                      .background(CalendarBackground, shape = MaterialTheme.shapes.medium)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                  items(24) { hour ->
                    HourBlock(hour, coloredWorkouts.filter { it.workout.date.hour == hour })
                  }
                }
              }
        }
      }
}

/** Displays a row of workout type legends (e.g., Bodyweight, Yoga, Running). */
@Composable
fun Legend() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("Categories"),
      horizontalArrangement = Arrangement.SpaceEvenly) {
        LegendItem(BodyWeightTag, stringResource(R.string.TitleTabBody))
        LegendItem(YogaTag, stringResource(R.string.TitleTabYoga))
        LegendItem(RunningTag, stringResource(R.string.TitleTabRunning))
      }
}

/**
 * Displays an individual legend item with a colored background and text.
 *
 * @param color The color for the legend item.
 * @param text The text description for the workout type.
 */
@Composable
fun LegendItem(color: Color, text: String) {
  Box(
      modifier =
          Modifier.background(color, shape = MaterialTheme.shapes.medium)
              .padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold)
      }
}

/**
 * Displays a block representing a specific hour with a list of workouts scheduled for that hour.
 *
 * @param hour The hour to display (e.g., 9 for 9:00 AM).
 * @param workouts A list of colored workouts scheduled for that hour.
 */
@Composable
fun HourBlock(hour: Int, workouts: List<ColoredWorkout>) {
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
    Text(
        text = "${hour}:00",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp))
    workouts.forEach { workout -> WorkoutItem(workout) }
  }
}

/**
 * Displays an individual workout item in a card with its name and scheduled time.
 *
 * @param coloredWorkout The colored workout to display, which includes workout name and color.
 */
@Composable
fun WorkoutItem(coloredWorkout: ColoredWorkout) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 4.dp, horizontal = 30.dp)
              .testTag("WorkoutCard")
              .clickable { /*navigate to edit or start workout screen*/},
      colors = CardDefaults.cardColors(containerColor = coloredWorkout.backgroundColor),
      shape = MaterialTheme.shapes.medium) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = coloredWorkout.workout.name, fontWeight = FontWeight.Bold)
              Text(text = formatTime(coloredWorkout.workout.date))
            }
      }
}
