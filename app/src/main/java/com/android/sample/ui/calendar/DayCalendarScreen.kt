package com.android.sample.ui.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.calendar.CalendarViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.Legend
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.mainscreen.navigateToWorkoutScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.CalendarBackground
import com.android.sample.ui.theme.LegendBodyweight
import com.android.sample.ui.theme.LegendRunning
import com.android.sample.ui.theme.LegendYoga
import com.android.sample.ui.theme.OpenSans
import java.time.format.DateTimeFormatter
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/**
 * Class that store the workout and its associated informations.
 *
 * @param workout The workout.
 * @param backgroundColor The color of the card.
 * @param viewModel The associated viewModel.
 */
data class DayColoredWorkout(
    val workout: Workout,
    val backgroundColor: Color,
    val viewModel: WorkoutViewModel<Workout>
)

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
              is BodyWeightWorkout -> DayColoredWorkout(it, LegendBodyweight, bodyworkoutViewModel)
              is YogaWorkout -> DayColoredWorkout(it, LegendYoga, yogaworkoutViewModel)
              else ->
                  DayColoredWorkout(
                      it,
                      LegendRunning,
                      bodyworkoutViewModel) // Temps until we got some saved running workouts
            }
          }
          .sortedBy { it.workout.date.minute }

  Scaffold(topBar = { TopBar(navigationActions, R.string.calendar_title) }) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      Legend()

      Divider(
          color = Color.LightGray,
          thickness = 1.dp,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

      Text(
          text =
              selectedDate?.toJavaLocalDate()?.format(DateTimeFormatter.ofPattern("dd MMMM")) ?: "",
          fontFamily = OpenSans,
          fontWeight = FontWeight.SemiBold,
          fontSize = 45.sp,
          modifier = Modifier.padding(top = 8.dp, start = 16.dp, bottom = 16.dp).testTag("Date"))

      Box(
          modifier =
              Modifier.testTag("Hours")
                  .fillMaxWidth()
                  .padding(start = 25.dp, end = 25.dp, bottom = 25.dp)
                  .shadow(4.dp, shape = RoundedCornerShape(25.dp))
                  .background(CalendarBackground, shape = RoundedCornerShape(25.dp))) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
              items(25) { hourInput ->
                val hour = if (hourInput == 24) 0 else hourInput
                HourBlock(
                    hour,
                    coloredWorkouts.filter { it.workout.date.hour == hour },
                    navigationActions)
              }
            }
          }
    }
  }
}

/**
 * Displays a block representing a specific hour with a list of workouts scheduled for that hour.
 *
 * @param hour The hour to display (e.g., 9 for 9:00 AM).
 * @param workouts A list of colored workouts scheduled for that hour.
 */
@SuppressLint("DefaultLocale")
@Composable
fun HourBlock(hour: Int, workouts: List<DayColoredWorkout>, navigationActions: NavigationActions) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        fontFamily = OpenSans,
        fontWeight = FontWeight.SemiBold,
        text = String.format("%02d:00", hour),
        modifier = Modifier.padding(start = 25.dp).testTag("hour"))

    if (workouts.isEmpty()) {
      Spacer(modifier = Modifier.height(25.dp))
    } else {
      workouts.forEach { workout -> WorkoutItem(workout, navigationActions) }
    }
  }
}

/**
 * Displays an individual workout item in a card with its name and scheduled time.
 *
 * @param coloredWorkout The colored workout to display, which includes workout name and color.
 */
@Composable
fun WorkoutItem(coloredWorkout: DayColoredWorkout, navigationActions: NavigationActions) {
  val shape =
      RoundedCornerShape(topStart = 15.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 15.dp)
  Row() {
    Spacer(modifier = Modifier.width(30.dp))
    Card(
        modifier =
            Modifier.padding(horizontal = 50.dp)
                .testTag("WorkoutCard")
                .clickable {
                  navigateToWorkoutScreen(
                      coloredWorkout.workout, coloredWorkout.viewModel, navigationActions)
                }
                .shadow(4.dp, shape = shape),
        colors = CardDefaults.cardColors(containerColor = coloredWorkout.backgroundColor),
        shape = shape) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 16.dp),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = coloredWorkout.workout.name,
                    fontFamily = OpenSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold)
                Text(
                    text = formatTime(coloredWorkout.workout.date),
                    fontFamily = OpenSans,
                    fontSize = 18.sp)
              }
        }
  }
}
