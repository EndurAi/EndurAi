package com.android.sample.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.DarkBlue2
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.SoftGrey
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Main composable function that sets up the main screen layout.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun MainScreen(
    navigationActions: NavigationActions,
    bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
) {
  // Configuration temporaire pour tester
  val account =
      UserAccount(
          "",
          "Micheal",
          "Phelps",
          1.8f,
          HeightUnit.METER,
          70f,
          WeightUnit.KG,
          Gender.MALE,
          Timestamp(Date()),
          "")
  val profile = R.drawable.homme
  val bodyWeightWorkouts = bodyWeightViewModel.workouts.collectAsState()
  val yogaWorkouts = yogaViewModel.workouts.collectAsState()

  val workoutDisplayed =
      when {
        bodyWeightWorkouts.value.isNotEmpty() -> bodyWeightWorkouts.value[0]
        yogaWorkouts.value.isNotEmpty() -> yogaWorkouts.value[0]
        else -> null
      }

  Scaffold(
      modifier = Modifier.testTag("mainScreen"),
      topBar = {
        ProfileSection(account = account, profile = profile, navigationActions = navigationActions)
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.SpaceBetween) {
              WorkoutSessionsSection(
                  workout = workoutDisplayed,
                  profile = profile,
                  navigationActions = navigationActions)
              QuickWorkoutSection(navigationActions = navigationActions)
              NewWorkoutSection(navigationActions = navigationActions)
            }
      },
      bottomBar = { BottomNavigationBar(navigationActions = navigationActions) })
}

/**
 * Composable function that displays the profile section at the top of the screen.
 *
 * @param account The user account containing the profile information.
 * @param profile The resource ID for the profile picture.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun ProfileSection(account: UserAccount, profile: Int, navigationActions: NavigationActions) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .background(DarkBlue)
              .padding(vertical = 12.dp)
              .testTag("ProfileSection")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)) {
              Image(
                  painter = painterResource(id = profile),
                  contentDescription = "Profile",
                  modifier =
                      Modifier.size(40.dp)
                          .clip(CircleShape)
                          .clickable { navigationActions.navigateTo(Screen.FRIENDS) }
                          .testTag("ProfilePicture"))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                  text = stringResource(id = R.string.welcome_message, account.firstName),
                  style = MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp),
                  color = Color.White,
                  modifier = Modifier.testTag("WelcomeText"))
            }
        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = { navigationActions.navigateTo(Screen.SETTINGS) },
            modifier = Modifier.padding(end = 12.dp).testTag("SettingsButton")) {
              Icon(
                  imageVector = Icons.Outlined.Settings,
                  contentDescription = "Settings",
                  tint = Color.White,
                  modifier = Modifier.size(24.dp))
            }
      }
}

/**
 * Composable function that displays a section with workout sessions.
 *
 * @param workouts A list of workouts to display.
 * @param profile The resource ID for the profile picture.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun WorkoutSessionsSection(workout: Workout?, profile: Int, navigationActions: NavigationActions) {
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).testTag("WorkoutSection")) {
    Text(
        text = "My workout sessions",
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 22.sp),
        modifier = Modifier.padding(vertical = 8.dp))
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SoftGrey)
                .padding(8.dp)) {
          if (workout != null) {
            Box(modifier = Modifier.padding(vertical = 4.dp)) {
              WorkoutCard(workout, profile, navigationActions)
            }
          } else {
            Text(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                textAlign = TextAlign.Center,
                text = "No workouts yet",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
          }

          Button(
              onClick = { navigationActions.navigateTo(Screen.VIEW_ALL) },
              modifier =
                  Modifier.fillMaxWidth().padding(horizontal = 12.dp).testTag("ViewAllButton"),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = DarkBlue2, contentColor = Color.White)) {
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp))
              }
        }
  }
}

/**
 * Composable function that displays the quick workout section.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun QuickWorkoutSection(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val metrics = context.resources.displayMetrics
  val screenWidth = metrics.widthPixels
  val screenWidthDp = screenWidth / metrics.density
  val buttonSizeDp = (screenWidthDp * 0.15).toInt()

  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).testTag("QuickSection")) {
    Text(
        text = "Quick workout",
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 22.sp),
        modifier = Modifier.padding(vertical = 8.dp))
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SoftGrey)
                .padding(10.dp)) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
              horizontalArrangement = Arrangement.SpaceAround) {
                QuickWorkoutButton(R.drawable.running_man, navigationActions, buttonSizeDp)
                QuickWorkoutButton(R.drawable.pushups, navigationActions, buttonSizeDp)
                QuickWorkoutButton(R.drawable.yoga, navigationActions, buttonSizeDp)
                QuickWorkoutButton(R.drawable.dumbbell, navigationActions, buttonSizeDp)
              }
        }
  }
}

/**
 * Composable function that displays a button for a quick workout session.
 *
 * @param iconId The resource ID for the quick workout icon.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun QuickWorkoutButton(iconId: Int, navigationActions: NavigationActions, buttonSize: Int) {
  Box(
      modifier =
          Modifier.size(buttonSize.dp)
              .aspectRatio(1f)
              .background(Blue, CircleShape)
              .clickable { /* Navigate to the screen associated with QuickWorkout */}
              .testTag("QuickWorkoutButton"),
      contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Quick Workout Icon",
            modifier = Modifier.fillMaxSize(0.5f))
      }
}
/**
 * Composable function that displays a workout card with its details.
 *
 * @param workout The workout data to display.
 * @param profile The resource ID for the participant icon.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun WorkoutCard(workout: Workout, profile: Int, navigationActions: NavigationActions) {
  Card(
      shape = RoundedCornerShape(12.dp),
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 3.dp)
              .clickable { /* Navigate to workout details or start workout */}
              .testTag("WorkoutCard"),
      colors = CardDefaults.cardColors(containerColor = Blue)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 17.sp))
                Text(text = workout.description, style = MaterialTheme.typography.bodyMedium)
                Image(
                    painter = painterResource(id = profile),
                    contentDescription = "Participant",
                    modifier = Modifier.size(20.dp))
              }
              Image(
                  painter =
                      painterResource(
                          id =
                              when (workout) {
                                is BodyWeightWorkout -> R.drawable.pushups
                                is YogaWorkout -> R.drawable.yoga
                                else -> R.drawable.dumbbell
                              }),
                  contentDescription = "Workout Icon",
                  modifier = Modifier.size(40.dp))
            }
      }
}

/**
 * Composable function that displays the button to create a new workout plan.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun NewWorkoutSection(navigationActions: NavigationActions) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        "New workout plan",
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 22.sp))
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { navigationActions.navigateTo(Screen.SESSIONSELECTION) }
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .height(48.dp)
                .background(LightGrey, RoundedCornerShape(20.dp))
                .testTag("NewWorkoutButton"),
        contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Outlined.Add,
              contentDescription = "New Workout",
              tint = Color.Black,
              modifier = Modifier.size(28.dp))
        }
  }
}

/**
 * Composable function that displays the bottom navigation bar.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun BottomNavigationBar(navigationActions: NavigationActions) {
  Column(
      modifier = Modifier.background(Blue).testTag("BottomNavigationBar"),
  ) {
    BottomNavigationMenu(
        onTabSelect = { route -> navigationActions.navigateTo(route) },
        tabList = LIST_OF_TOP_LEVEL_DESTINATIONS,
        selectedItem = navigationActions.currentRoute(),
    )
  }
}
