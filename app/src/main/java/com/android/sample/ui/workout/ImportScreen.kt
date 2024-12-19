package com.android.sample.ui.workout

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.ContrailOne
import com.android.sample.ui.theme.Dimensions
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.LightBackground
import com.android.sample.ui.theme.Line
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Shape
import com.android.sample.ui.theme.TitleBlue

/**
 * Composable function for the import screen.
 *
 * @param navigationActions Actions for navigating between screens.
 * @param workoutViewModel The viewmodel of the workouts
 */
@Composable
fun ImportScreen(
    navigationActions: NavigationActions,
    workoutViewModel: WorkoutViewModel<Workout>,
    workoutType: WorkoutType
) {

  val title =
      when (workoutType) {
        WorkoutType.BODY_WEIGHT -> R.string.TitleTabBody
        else -> R.string.TitleTabYoga
      }
  Scaffold(
      topBar = { TopBar(navigationActions, R.string.Import) },
      containerColor = LightBackground,
      modifier = Modifier.testTag("ImportScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimensions.LargePadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
              Text(
                  text = stringResource(id = title),
                  style =
                      MaterialTheme.typography.labelLarge.copy(
                          fontSize = FontSizes.MediumBigTitleFontSize),
                  fontFamily = OpenSans,
                  color = TitleBlue,
                  modifier = Modifier.height(50.dp).testTag("ExerciseTypeTitle"))
              Divider(
                  color = Line,
                  thickness = 0.5.dp,
                  modifier =
                      Modifier.padding(horizontal = 25.dp, vertical = 1.dp)
                          .padding(bottom = 10.dp)
                          .shadow(1.dp))
              WorkoutList(navigationActions, workoutViewModel)
            }
      }
}

/**
 * Composable function that the list of done workouts.
 *
 * @param navigationActions Actions for navigating between screens.
 * @param workoutViewModel The viewmodel of the workouts
 */
@Composable
fun WorkoutList(navigationActions: NavigationActions, workoutViewModel: WorkoutViewModel<Workout>) {
  val doneWorkouts = workoutViewModel.doneWorkouts.collectAsState().value

  if (doneWorkouts.isEmpty()) {
    Text(
        text = stringResource(id = R.string.NoDoneWorkout),
        fontSize = SubtitleFontSize,
        fontFamily = OpenSans)
  } else {}
  LazyColumn(
      modifier = Modifier.padding(vertical = 20.dp).testTag("DoneWorkoutList").fillMaxHeight()) {
        items(doneWorkouts) { workoutItem ->
          DoneWorkoutCard(workoutItem, workoutViewModel, navigationActions)
          Spacer(modifier = Modifier.height(15.dp))
        }
      }
}

/**
 * Composable function that displays a done workout card with its details.
 *
 * @param workout The workout data to display..
 * @param workoutViewModel The viewmodel of the workouts
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun DoneWorkoutCard(
    workout: Workout,
    workoutViewModel: WorkoutViewModel<Workout>,
    navigationActions: NavigationActions
) {
  val shape = Shape.buttonShape
  val context = LocalContext.current
  Card(
      shape = shape,
      modifier =
          Modifier.padding(horizontal = 30.dp, vertical = 3.dp)
              .shadow(elevation = 8.dp, shape = shape)
              .fillMaxWidth()
              .clickable {
                workoutViewModel.importWorkoutFromDone(workout.workoutId)
                Toast.makeText(context, R.string.ImportSuccessful, Toast.LENGTH_SHORT).show()
                navigationActions.navigateTo(Screen.MAIN)
              }
              .testTag("DoneWorkoutCard"),
      colors = CardDefaults.cardColors(containerColor = BlueWorkoutCard)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text(
                    text = workout.name,
                    style =
                        MaterialTheme.typography.titleSmall.copy(
                            fontSize = 19.sp, fontFamily = ContrailOne),
                    modifier = Modifier.padding(horizontal = 10.dp))
              }
              Image(
                  painter =
                      painterResource(
                          id =
                              when (workout) {
                                is BodyWeightWorkout -> R.drawable.dumbell_inner_shadow
                                else -> R.drawable.yoga_innershadow
                              }),
                  contentDescription = "Workout Icon",
                  modifier = Modifier.size(70.dp).padding(horizontal = 15.dp))
            }
      }
}
