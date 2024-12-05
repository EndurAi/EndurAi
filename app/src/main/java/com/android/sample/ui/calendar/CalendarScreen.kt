package com.android.sample.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.android.sample.R
import com.android.sample.model.calendar.CalendarViewModel
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.composables.Legend
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.LegendBodyweight
import com.android.sample.ui.theme.LegendYoga
import com.android.sample.ui.theme.Line
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.OpenSans
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
    yogaworkoutViewModel: WorkoutViewModel<Workout>,
    calendarViewModel: CalendarViewModel
) {
  val workoutsBody by bodyworkoutViewModel.workouts.collectAsState(emptyList())
  val workoutsYoga by yogaworkoutViewModel.workouts.collectAsState(emptyList())

  val coloredWorkoutsBody =
      workoutsBody.map { ColoredWorkout(it, LegendBodyweight, WorkoutType.BODY_WEIGHT) }
  val coloredWorkoutsYoga = workoutsYoga.map { ColoredWorkout(it, LegendYoga, WorkoutType.YOGA) }
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
                    onClick = {
                      showDialog = false
                      when (selectedWorkout!!.type) {
                        WorkoutType.BODY_WEIGHT ->
                            navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
                        WorkoutType.YOGA -> navigationActions.navigateTo(Screen.YOGA_OVERVIEW)
                        WorkoutType.WARMUP -> TODO()
                        WorkoutType.RUNNING -> TODO()
                      }
                    },
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
                        WorkoutType.WARMUP -> TODO()
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

  Scaffold(
      topBar = { TopBar(navigationActions, R.string.calendar_title, false) },
      bottomBar = { BottomBar(navigationActions) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          Legend()

          LazyColumn(
              state = lazyListState,
              modifier = Modifier.fillMaxSize().padding(16.dp).testTag("lazyColumn")) {
                items(dates) { date ->
                  DaySection(
                      date = date,
                      workouts = (workoutsByDate[date] ?: emptyList()).take(3),
                      onWorkoutClick = { workout ->
                        selectedWorkout = workout
                        showDialog = true
                      },
                      navigationActions,
                      calendarViewModel)
                  Divider(
                      color = Line,
                      thickness = 0.5.dp,
                      modifier = Modifier.padding(vertical = 15.dp).shadow(1.dp).testTag("Divider"))
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

@SuppressLint("DefaultLocale")
@Composable
fun DaySection(
    date: kotlinx.datetime.LocalDate,
    workouts: List<ColoredWorkout>,
    onWorkoutClick: (ColoredWorkout) -> Unit,
    navigationActions: NavigationActions,
    calendarViewModel: CalendarViewModel
) {
  val context = LocalContext.current
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("daySection")
              .padding(vertical = 8.dp)
              .background(color = Color.Transparent, shape = MaterialTheme.shapes.medium)
              .padding(16.dp)
              .clickable {
                calendarViewModel.updateSelectedDate(date)
                navigationActions.navigateTo(Screen.DAY_CALENDAR)
              },
      verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center) {
              Spacer(modifier = Modifier.height(40.dp))
              Box(
                  modifier =
                      Modifier.drawBehind {
                        drawIntoCanvas { canvas ->
                          shadowText(
                              canvas, context, String.format("%02d", date.dayOfMonth), 50.sp.toPx())
                        }
                      })
              Spacer(modifier = Modifier.height(20.dp))
              Box(
                  modifier =
                      Modifier.drawBehind {
                        drawIntoCanvas { canvas ->
                          shadowText(canvas, context, getMonthName(date.monthNumber), 16.sp.toPx())
                        }
                      })
            }
        Spacer(modifier = Modifier.width(150.dp))
        if (workouts.isEmpty()) {
          Text(
              text = stringResource(id = R.string.NoWorkout),
              style = MaterialTheme.typography.bodyMedium,
              color = NeutralGrey,
              fontFamily = OpenSans)
        } else {
          Column(modifier = Modifier) {
            workouts
                .sortedBy { it.workout.date }
                .forEach { workout -> WorkoutItem(workout, onWorkoutClick, context) }
          }
        }
      }
}

fun shadowText(
    canvas: Canvas,
    context: android.content.Context,
    text: String,
    textSizeSp: Float,
    y: Float = 0f
) {
  val openSansTypeface = ResourcesCompat.getFont(context, R.font.open_sans_regular)

  val textPaint =
      android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = textSizeSp
        setShadowLayer(8f, 4f, 4f, android.graphics.Color.GRAY)
        typeface = openSansTypeface
      }

  canvas.nativeCanvas.drawText(text, 0f, y, textPaint)
}

@Composable
fun WorkoutItem(
    coloredWorkout: ColoredWorkout,
    onClick: (ColoredWorkout) -> Unit,
    context: Context
) {
  Card(
      modifier =
          Modifier.clickable { onClick(coloredWorkout) }
              .padding(vertical = 4.dp)
              .testTag("workoutItem"),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Row(modifier = Modifier.width(200.dp).height(30.dp)) {
          Spacer(modifier = Modifier.width(20.dp))
          CircleDot(coloredWorkout.backgroundColor)
          Spacer(modifier = Modifier.width(20.dp))
          Box(
              modifier =
                  Modifier.drawBehind {
                    drawIntoCanvas { canvas ->
                      shadowText(canvas, context, coloredWorkout.workout.name, 18.sp.toPx(), 35f)
                    }
                  })
        }
      }
}

@Composable
fun CircleDot(color: Color) {
  Column(
      modifier =
          Modifier.size(15.dp)
              .shadow(4.dp, shape = CircleShape)
              .background(color, shape = CircleShape)) {}
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

fun formatTime(dateTime: LocalDateTime): String {
  val hour = dateTime.hour.toString().padStart(2, '0')
  val minute = dateTime.minute.toString().padStart(2, '0')
  return "$hour:$minute"
}
