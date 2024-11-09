package com.android.sample.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.navigation.NavigationActions
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.R
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.mainscreen.BottomNavigationBar
import com.android.sample.ui.mainscreen.NewWorkoutSection
import com.android.sample.ui.mainscreen.ProfileSection
import com.android.sample.ui.mainscreen.QuickWorkoutSection
import com.android.sample.ui.mainscreen.WorkoutSessionsSection
import com.android.sample.ui.navigation.TopLevelDestinations

@Composable
fun DayCalendarScreen(
    navigationActions: NavigationActions,
    bodyworkoutViewModel: WorkoutViewModel<Workout>,
    yogaworkoutViewModel: WorkoutViewModel<Workout>
) {
    Scaffold(
        modifier = Modifier.testTag("dayScreen"),
        topBar = {
            TopBar(navigationActions, R.string.calendar_title)
        },
        content = { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.SpaceBetween) {
               Text("day screen")
            }
        },
        bottomBar = { BottomBar(navigationActions)}
    )
}