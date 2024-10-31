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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.DarkGray
import com.android.sample.ui.theme.Gray
import com.android.sample.ui.theme.LightGray
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
      workoutsBody.map { ColoredWorkout(it, PastelBlue, WorkoutType.BODY_WEIGHT) } // Rouge pastel
  val coloredWorkoutsYoga =
      workoutsYoga.map { ColoredWorkout(it, PastelRed, WorkoutType.YOGA) } // Bleu pastel
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

      Legend()

      LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
fun Legend() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        LegendItem(color = PastelBlue, label = "Yoga")

        LegendItem(color = PastelRed, label = "Bodyweight")
      }
}

@Composable
fun LegendItem(color: Color, label: String) {
  Row(
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
              .padding(vertical = 8.dp)
              .background(color = LightGray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
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
              color = DarkGray)
        }

        if (workouts.isEmpty()) {
          Text(
              text = "Pas d'entraînement",
              style = MaterialTheme.typography.bodyMedium,
              color = Gray)
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
          Modifier.fillMaxWidth().clickable { onClick(coloredWorkout) }.padding(vertical = 4.dp),
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
    1 -> "janvier"
    2 -> "février"
    3 -> "mars"
    4 -> "avril"
    5 -> "mai"
    6 -> "juin"
    7 -> "juillet"
    8 -> "août"
    9 -> "septembre"
    10 -> "octobre"
    11 -> "novembre"
    12 -> "décembre"
    else -> ""
  }
}

private fun formatTime(dateTime: LocalDateTime): String {
  val hour = dateTime.hour.toString().padStart(2, '0')
  val minute = dateTime.minute.toString().padStart(2, '0')
  return "$hour:$minute"
}
