package com.android.sample.ui.mainscreen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.composables.ImageComposable
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue

/**
 * Displays the screen for viewing all workouts.
 *
 * @param navigationActions The navigation actions to handle navigation events.
 * @param bodyWeightViewModel The ViewModel for managing bodyweight workouts.
 * @param yogaViewModel The ViewModel for managing yoga workouts.
 */
@Composable
fun ViewAllScreen(
    navigationActions: NavigationActions,
    bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    // runningViewModel: WorkoutViewModel<Workout>
) {
  val profile = R.drawable.homme
  var selectedTab by remember { mutableIntStateOf(0) } // (0 = Bodyweight, 1 = Yoga, 2 = Running)

  val workoutViewModels = listOf(bodyWeightViewModel, yogaViewModel /*, runningViewModel */)

  Scaffold(
      modifier = Modifier.testTag("ViewAllScreen"),
      topBar = { TopBar(navigationActions, R.string.ViewAllTitle) },
      bottomBar = { BottomBar(navigationActions) },
      content = { pd ->
        Column(modifier = Modifier.padding(pd)) {
          Tabs(selectedTab) { index -> selectedTab = index }
          Spacer(modifier = Modifier.height(10.dp))
          if (selectedTab in 0..1) {
            WorkoutList(
                viewModel = workoutViewModels[selectedTab],
                navigationActions = navigationActions,
                profile = profile,
                onClick = ::navigateToWorkoutScreen)
          }
        }
      })
}

/**
 * Displays the tabs for selecting different workout types.
 *
 * @param selectedTab The currently selected tab index.
 * @param onTabSelected Callback function to handle tab selection.
 */
@Composable
fun Tabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
  val tabTitles = listOf(R.string.TitleTabBody, R.string.TitleTabYoga, R.string.TitleTabRunning)
  val tabTags = listOf("BodyTab", "YogaTab", "RunningTab")

  Column {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().padding(8.dp)) {
          tabTitles.forEachIndexed { index, titleRes ->
            TabItem(
                title = titleRes,
                isSelected = selectedTab == index,
                modifier = Modifier.testTag(tabTags[index]),
                onClick = { onTabSelected(index) })
          }
        }
    Divider(
        color = Color.Gray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth())
  }
}

/**
 * Display a single tab item in the tabs.
 *
 * @param title The resource ID for the tab title.
 * @param isSelected Indicates if this tab is currently selected.
 * @param onClick Callback function invoked when the tab is clicked.
 * @param modifier A modifier for styling.
 */
@Composable
fun TabItem(@StringRes title: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
  Box(
      modifier =
          modifier
              .clickable(onClick = onClick)
              .padding(4.dp)
              .border(
                  width = 1.dp,
                  color = if (isSelected) Blue else Color.LightGray,
                  shape = RoundedCornerShape(16.dp))
              .background(
                  color = if (isSelected) Blue.copy(alpha = 0.1f) else Color.Transparent,
                  shape = RoundedCornerShape(16.dp))
              .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(id = title),
            color = if (isSelected) Blue else Color.Gray,
            fontSize = 14.sp)
      }
}

/**
 * Displays a list of workouts based on the specified ViewModel.
 *
 * @param viewModel The ViewModel providing the workouts.
 * @param navigationActions The navigation actions for handling navigation events.
 * @param profile The resource ID for the profile image.
 * @param T The type of workout being displayed, must extend [Workout].
 * @param onClick Not necessary, you probably shouldn't use it. Callback function invoked when a
 *   workout is clicked.
 */
@Composable
fun <T : Workout> WorkoutList(
    viewModel: WorkoutViewModel<T>,
    navigationActions: NavigationActions,
    profile: Int,
    onClick: (Workout, WorkoutViewModel<Workout>, NavigationActions) -> Unit = { _, _, _ -> }
) {
  val workouts = viewModel.workouts.collectAsState()

  if (workouts.value.isNotEmpty()) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
          items(workouts.value.size) { index ->
            ViewAllCard(
                workout = workouts.value[index],
                profile = profile,
                navigationActions = navigationActions,
                viewModel = viewModel,
                onClick = onClick)
          }
        }
  } else {
    Spacer(modifier = Modifier.height(90.dp))
    Text(
        modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("emptyWorkoutPrompt"),
        text = stringResource(id = R.string.noWorkouts),
        textAlign = TextAlign.Center)
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
      ImageComposable(
          R.drawable.no_item, "No workout logo", Modifier.size(200.dp).align(Alignment.Center))
    }
  }
}

/**
 * Displays a card for a single workout, showing its details and an icon.
 *
 * @param workout The workout to display.
 * @param profile The resource ID for the profile image.
 * @param navigationActions The navigation actions for handling navigation events.
 * @param viewModel The ViewModel for the selected workout.
 */
@Composable
fun ViewAllCard(
    workout: Workout,
    profile: Int,
    navigationActions: NavigationActions,
    viewModel: WorkoutViewModel<Workout>,
    onClick: (Workout, WorkoutViewModel<Workout>, NavigationActions) -> Unit = { _, _, _ -> }
) {
  // Choose icon dynamically with the workout type
  val workoutImage =
      when (workout) {
        is BodyWeightWorkout -> R.drawable.pushups
        is YogaWorkout -> R.drawable.yoga
        else -> R.drawable.dumbbell
      }

  Card(
      shape = RoundedCornerShape(30.dp),
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable {
                onClick(workout, viewModel, navigationActions)

              /*Navigate to the screen to edit or start the workout*/ }
              .testTag("WorkoutCard"),
      colors = CardDefaults.cardColors(containerColor = Blue)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                // Name of the workout
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 17.sp))

                // Descrition of the workout
                Text(text = workout.description, style = MaterialTheme.typography.bodyMedium)

                // Temp should display profile pictures of all participants
                ImageComposable(profile, "Participant", Modifier.size(15.dp))
              }

              // The type of workout
              ImageComposable(workoutImage, "Workout Icon", Modifier.size(30.dp))
            }
      }
}

/**
 * Navigate to the screen to actually do a workout. This function was created to avoid duplicating
 * code, thus allowing the composable to be used also in the WorkoutSelectionScreen.
 *
 * @param workout The selected workout.
 * @param viewModel The ViewModel for the selected workout.
 * @param navigationActions The navigation actions for handling navigation events.
 */
private fun navigateToWorkoutScreen(
    workout: Workout,
    viewModel: WorkoutViewModel<Workout>,
    navigationActions: NavigationActions
) {
  when (workout) {
    is BodyWeightWorkout -> {
      viewModel.selectWorkout(workout)
      navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
    }
    is YogaWorkout -> {
      viewModel.selectWorkout(workout)
      navigationActions.navigateTo(Screen.YOGA_OVERVIEW)
    }
  }
}
