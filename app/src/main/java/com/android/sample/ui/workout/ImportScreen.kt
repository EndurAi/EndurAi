package com.android.sample.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.LightBackground

@Composable
fun ImportScreen(navigationActions: NavigationActions,
                 viewModel: WorkoutViewModel<Workout>) {
    Scaffold(
        topBar = { TopBar(navigationActions, R.string.Import) },
        containerColor = LightBackground,
        modifier = Modifier.testTag("ImportScreen")
    ) { paddingValues ->
        Column(
            modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimensions.LargePadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center the content vertically
        ) {
            Text("Import")
        }
    }
}
