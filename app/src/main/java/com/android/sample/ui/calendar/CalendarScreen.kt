package com.android.sample.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.DarkGrey
import com.android.sample.ui.theme.MediumGrey
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.PastelBlue
import com.android.sample.ui.theme.PastelRed
import com.android.sample.ui.theme.Red
import com.android.sample.ui.theme.Yellow
import java.time.LocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime

data class ColoredWorkout(val workout: Workout, val backgroundColor: Color, val type: WorkoutType)

@Composable
fun CalendarScreen(
    navigationActions: NavigationActions,
    bodyworkoutViewModel: WorkoutViewModel<Workout>,
    yogaworkoutViewModel: WorkoutViewModel<Workout>
) {
  val workoutsBody by bodyworkoutViewModel.workouts.collectAsState(emptyList())
  val workoutsYoga by yogaworkoutViewModel.workouts.collectAsState(emptyList())

  val coloredWorkoutsBody =
      workoutsBody.map { ColoredWorkout(it, PastelBlue, WorkoutType.BODY_WEIGHT) }
  val coloredWorkoutsYoga = workoutsYoga.map { ColoredWorkout(it, PastelRed, WorkoutType.YOGA) }
  val workouts = coloredWorkoutsYoga + coloredWorkoutsBody

  // Infinite scrolling logic
  val lazyListState = rememberLazyListState()
  var daysToShow by remember { mutableStateOf(7) } // Start with 7 days

  var selectedWorkout by remember { mutableStateOf<ColoredWorkout?>(null) }
  var showDialog by remember { mutableStateOf(false) }

  val startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
  val dates = generateDateRange(startDate, daysToShow)

  LaunchedEffect(lazyListState) {
    snapshotFlow {
          lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size >=
              lazyListState.layoutInfo.totalItemsCount
        }
        .collect { isAtEnd ->
          if (isAtEnd) {
            daysToShow += 7
          }
        }
  }

  if (showDialog && selectedWorkout != null) {
    AlertDialog(
        modifier = Modifier.testTag("alertDialog"),
        onDismissRequest = { showDialog = false },
        title = {
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically) {
                Text("Workout Actions")
              }
        },
        confirmButton = {
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(),
                    modifier = Modifier.testTag("editButton"),
                    border = BorderStroke(2.dp, Yellow)) {
                      Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Yellow)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Edit", color = Black)
                    }

                OutlinedButton(
                    onClick = {
                      when (selectedWorkout!!.type) {
                        WorkoutType.BODY_WEIGHT ->
                            bodyworkoutViewModel.deleteWorkoutById(
                                selectedWorkout!!.workout.workoutId)
                        WorkoutType.YOGA ->
                            yogaworkoutViewModel.deleteWorkoutById(
                                selectedWorkout!!.workout.workoutId)
                        WorkoutType.RUNNING -> {}
                      }
                      showDialog = false
                    },
                    colors = ButtonDefaults.outlinedButtonColors(),
                    modifier = Modifier.testTag("deleteButton"),
                    border = BorderStroke(2.dp, Red)) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Red)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Delete", color = Black)
                    }
              }
        })
  }

  val workoutsByDate = workouts.groupBy { it.workout.date.toLocalDate().toKotlinLocalDate() }

  Scaffold(topBar = { TopBar(navigationActions, R.string.calendar_title) }) { innerPadding ->
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      Spacer(modifier = Modifier.height(8.dp))

      Legend(modifier = Modifier.testTag("legend"))

      LazyColumn(
          state = lazyListState,
          modifier = Modifier.fillMaxSize().padding(16.dp).testTag("lazyColumn")) {
            items(dates) { date ->
              DaySection(
                  date = date,
                  workouts = workoutsByDate[date] ?: emptyList(),
                  onWorkoutClick = { workout ->
                    selectedWorkout = workout
                    showDialog = true
                  })
            }
          }
    }
  }
}

private fun generateDateRange(
    startDate: kotlinx.datetime.LocalDate,
    count: Int
): List<kotlinx.datetime.LocalDate> {
  return List(count) { daysToAdd -> startDate.plus(daysToAdd.toLong(), DateTimeUnit.DAY) }
}

@Composable
fun Legend(modifier: Modifier = Modifier) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        LegendItem(color = PastelBlue, label = "Yoga", modifier = Modifier.testTag("legendYoga"))

        LegendItem(
            color = PastelRed,
            label = "Bodyweight",
            modifier = Modifier.testTag("legendBodyweight"))
      }
}

@Composable
fun LegendItem(color: Color, label: String, modifier: Modifier = Modifier) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(16.dp).background(color, shape = MaterialTheme.shapes.small))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
      }
}

@Composable
fun DaySection(
    date: kotlinx.datetime.LocalDate,
    workouts: List<ColoredWorkout>,
    onWorkoutClick: (ColoredWorkout) -> Unit
) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("daySection")
              .padding(vertical = 8.dp)
              .background(
                  color = MediumGrey.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
              .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
          Text(
              text = date.dayOfMonth.toString(),
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
              text = getMonthName(date.monthNumber),
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Light,
              color = DarkGrey)
        }

        if (workouts.isEmpty()) {
          Text(
              text = "No workout", style = MaterialTheme.typography.bodyMedium, color = NeutralGrey)
        } else {
          Column(modifier = Modifier, horizontalAlignment = Alignment.End) {
            workouts
                .sortedBy { it.workout.date }
                .forEach { workout -> WorkoutItem(workout, onWorkoutClick) }
          }
        }
      }
}

@Composable
fun WorkoutItem(coloredWorkout: ColoredWorkout, onClick: (ColoredWorkout) -> Unit) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { onClick(coloredWorkout) }
              .padding(vertical = 4.dp)
              .testTag("workoutItem"),
      colors = CardDefaults.cardColors(containerColor = coloredWorkout.backgroundColor),
      shape = MaterialTheme.shapes.medium) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(text = coloredWorkout.workout.name, style = MaterialTheme.typography.bodyLarge)
              Text(
                  text = formatTime(coloredWorkout.workout.date),
                  style = MaterialTheme.typography.bodyLarge)
            }
      }
}

private fun getMonthName(monthNumber: Int): String {
  return when (monthNumber) {
    1 -> "January"
    2 -> "February"
    3 -> "March"
    4 -> "April"
    5 -> "May"
    6 -> "June"
    7 -> "July"
    8 -> "August"
    9 -> "September"
    10 -> "October"
    11 -> "November"
    12 -> "December"
    else -> ""
  }
}

private fun formatTime(dateTime: LocalDateTime): String {
  val hour = dateTime.hour.toString().padStart(2, '0')
  val minute = dateTime.minute.toString().padStart(2, '0')
  return "$hour:$minute"
}
