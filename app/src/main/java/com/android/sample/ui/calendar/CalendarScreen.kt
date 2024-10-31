package com.android.sample.ui.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.model.workout.Workout
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import androidx.compose.runtime.*
import java.time.LocalDateTime


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CalendarScreen(
    navigationActions: NavigationActions,
    bodyworkoutViewModel: WorkoutViewModel<Workout>,
    yogaworkoutViewModel: WorkoutViewModel<Workout>
) {
    val workoutsBody = bodyworkoutViewModel.workouts.value
    val workoutsYoga = yogaworkoutViewModel.workouts.value
    val workouts = workoutsYoga + workoutsBody

    // Infinite scrolling logic
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var daysToShow by remember { mutableStateOf(7) } // Start with 7 days

    val startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dates = generateDateRange(startDate, daysToShow)


    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size >= lazyListState.layoutInfo.totalItemsCount }
            .collect { isAtEnd ->
                if (isAtEnd) {
                    println("Reached the end of the list")
                    daysToShow += 7
                }
            }
    }




    // Group workouts by date
    val workoutsByDate = workouts.groupBy {
        it.date.toLocalDate().toKotlinLocalDate()
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(dates) { date ->
            DaySection(
                date = date,
                workouts = workoutsByDate[date] ?: emptyList()
            )
        }
    }
}

private fun generateDateRange(startDate: kotlinx.datetime.LocalDate, count: Int): List<kotlinx.datetime.LocalDate> {
    return List(count) { daysToAdd -> startDate.plus(daysToAdd.toLong(), DateTimeUnit.DAY) }
}

@Composable
fun LazyListState.isScrolledToEnd(): Boolean {
    val layoutInfo = remember(this) { this.layoutInfo }
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}

@Composable
fun DaySection(
    date: kotlinx.datetime.LocalDate,
    workouts: List<Workout>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = Color.LightGray.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getMonthName(date.monthNumber),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                color = Color.DarkGray
            )
        }

        if (workouts.isEmpty()) {
            Text(
                text = "Pas d'entraînement",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.End
            ) {
                workouts.sortedBy { it.date }.forEach { workout ->
                    WorkoutItem(workout)
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(
    workout: Workout
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = formatTime(workout.date),
                style = MaterialTheme.typography.bodyLarge
            )
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