package com.android.sample.ui.mainscreen

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Blue

@Composable
fun ViewAllScreen(
    navigationActions: NavigationActions,
    bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    // runningViewModel: WorkoutViewModel<Workout>
) {
  val profile = R.drawable.homme
  var selectedTab by remember { mutableIntStateOf(0) } // (0 = Bodyweight, 1 = Yoga, 2 = Running)

  Scaffold(
      modifier = Modifier.testTag("ViewAllScreen"),
      topBar = { TopBar(navigationActions, R.string.ViewAllTitle) },
      content = { pd ->
        Column(modifier = Modifier.padding(pd)) {
          Tabs(selectedTab) { index -> selectedTab = index }
          Spacer(modifier = Modifier.height(10.dp))
          when (selectedTab) {
            0 ->
                WorkoutList(
                    viewModel = bodyWeightViewModel,
                    navigationActions = navigationActions,
                    profile = profile)
            1 ->
                WorkoutList(
                    viewModel = yogaViewModel,
                    navigationActions = navigationActions,
                    profile = profile)
          /*
          2 -> WorkoutList(
              viewModel = runningViewModel,
              navigationActions = navigationActions,
              profile = profile
          )
          */
          }
        }
      })
}

@Composable
fun Tabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
  Column {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().padding(8.dp)) {
          TabItem(
              title = R.string.TitleTabBody,
              isSelected = selectedTab == 0,
              modifier = Modifier.testTag("BodyTab"),
              onClick = { onTabSelected(0) })
          TabItem(
              title = R.string.TitleTabYoga,
              isSelected = selectedTab == 1,
              modifier = Modifier.testTag("YogaTab"),
              onClick = { onTabSelected(1) })
          TabItem(
              title = R.string.TitleTabRunning,
              isSelected = selectedTab == 2,
              modifier = Modifier.testTag("RunningTab"),
              onClick = { onTabSelected(2) })
        }

    Divider(
        color = Color.Gray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth())
  }
}

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

@Composable
fun <T : Workout> WorkoutList(
    viewModel: WorkoutViewModel<T>,
    navigationActions: NavigationActions,
    profile: Int
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
                viewModel = viewModel)
          }
        }
  } else {
    Spacer(modifier = Modifier.height(90.dp))
    Text(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        text = stringResource(id = R.string.noWorkouts),
        textAlign = TextAlign.Center)
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
      Image(
          painter = painterResource(id = com.android.sample.R.drawable.no_item),
          contentDescription = "App Logo",
          modifier = Modifier.size(200.dp).align(Alignment.Center) // Alignement centré
          )
    }
  }
}

@Composable
fun ViewAllCard(
    workout: Workout,
    profile: Int,
    navigationActions: NavigationActions,
    viewModel: WorkoutViewModel<Workout>
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
                viewModel.selectWorkout(workout)
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
                Image(
                    painter = painterResource(id = profile),
                    contentDescription = "Participant",
                    modifier = Modifier.size(15.dp))
              }

              // The type of workout
              Image(
                  painter = painterResource(id = workoutImage),
                  contentDescription = "Workout Icon",
                  modifier = Modifier.size(30.dp))
            }
      }
}
