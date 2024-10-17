package com.android.sample.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.userAccount.Gender
import com.android.sample.model.userAccount.HeightUnit
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.WeightUnit
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.YogaWorkout
import androidx.compose.ui.unit.dp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.DarkBlue2
import com.android.sample.ui.theme.Grey
import com.android.sample.ui.theme.GreyLight
import java.util.Date

/**
 * Main composable function that sets up the main screen layout.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun MainScreen(navigationActions: NavigationActions) {
  // Temp until got real account repository
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
          Date(),
          "")
  val profile = R.drawable.homme

  // Temp until got real workouts repo
  val workouts =
      listOf(
          BodyWeightWorkout(
              workoutId = "1",
              name = "Run in Lavaux",
              description = "Enjoying Lavaux with user2 and user3",
              warmup = true,
              userIdSet = mutableSetOf("user1", "user2", "user3")),
          YogaWorkout(
              workoutId = "3",
              name = "After Comparch relax",
              description = "Chilling time",
              warmup = true,
              userIdSet = mutableSetOf("user1")),
          BodyWeightWorkout(
              workoutId = "2",
              name = "Summer body",
              description = "Be ready for the summer",
              warmup = false,
              userIdSet = mutableSetOf("user1", "user4")),
      )

  Scaffold(
      modifier = Modifier.testTag("MainScreen"),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.SpaceBetween) {
              // Display the profile section
              ProfileSection(
                  account = account, profile = profile, navigationActions = navigationActions)

              // Display the workout sessions
              WorkoutSessionsSection(
                  workouts = workouts, profile = profile, navigationActions = navigationActions)

              // Display the quick workout section
              QuickWorkoutSection(navigationActions = navigationActions)

              // Display the new workout plan button
              NewWorkoutSection(navigationActions = navigationActions)
            }
      },
      bottomBar = { BottomNavigationBar(navigationActions = navigationActions) })
}

/**
 * Composable function that displays the profile section, including the profile picture and
 * settings.
 *
 * @param account The user account information.
 * @param profile The resource ID for the profile image.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun ProfileSection(account: UserAccount, profile: Int, navigationActions: NavigationActions) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .background(DarkBlue)
              .padding(vertical = 16.dp)
              .testTag("ProfileSection")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)) {
              // Profile image
              Image(
                  painter = painterResource(id = profile),
                  contentDescription = "Profile",
                  modifier = Modifier.size(48.dp).clip(CircleShape).testTag("ProfilePicture"))
              Spacer(modifier = Modifier.width(8.dp))
              // Display user name dynamically
              Text(
                  text = stringResource(id = R.string.welcome_message, account.firstName),
                  style = MaterialTheme.typography.titleSmall.copy(fontSize = 30.sp),
                  color = Color.White,
                  modifier = Modifier.testTag("WelcomeText"))
            }

        Spacer(Modifier.weight(1f))

        // Settings Icon
        IconButton(
            onClick = { navigationActions.navigateTo(Screen.SETTINGS) },
            modifier = Modifier.padding(end = 16.dp).testTag("SettingsButton")) {
              Icon(
                  imageVector = Icons.Outlined.Settings,
                  contentDescription = "Settings",
                  tint = Color.White,
                  modifier = Modifier.size(30.dp))
            }
      }
}

/**
 * Composable function that displays a list of workout sessions.
 *
 * @param workouts The list of workout sessions.
 * @param profile The resource ID for the profile image.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun WorkoutSessionsSection(
    workouts: List<Workout>,
    profile: Int,
    navigationActions: NavigationActions
) {
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("WorkoutSection")) {
    Text(
        text = "My workout sessions",
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp),
        modifier = Modifier.padding(vertical = 8.dp))

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GreyLight)
                .padding(10.dp)) {
          // Display the first two workouts
          for (workout in workouts.take(2)) {
            WorkoutCard(workout, profile, navigationActions = navigationActions)
          }

          // "View All" button than navigate to a screen with all workouts
          Button(
              onClick = { /* Navigate to screen displaying all workouts */},
              modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("ViewAllButton"),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = DarkBlue2, contentColor = Color.White)) {
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                )
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
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
    Text(
        text = "Quick workout",
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp),
        modifier = Modifier.padding(vertical = 8.dp).testTag("QuickSection"))

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GreyLight)
                .padding(10.dp)) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceAround) {
                // One button for each Quickworkout session
                QuickWorkoutButton(R.drawable.running_man, navigationActions = navigationActions)
                QuickWorkoutButton(R.drawable.pushups, navigationActions = navigationActions)
                QuickWorkoutButton(R.drawable.yoga, navigationActions = navigationActions)
                QuickWorkoutButton(R.drawable.dumbbell, navigationActions = navigationActions)
              }
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
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp))
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { /*Navigate to screen to choose type of workout*/}
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .height(48.dp)
                .background(Grey, RoundedCornerShape(24.dp))
                .testTag("NewWorkoutButton"),
        contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Outlined.Add,
              contentDescription = "New Workout",
              tint = Color.Black,
              modifier = Modifier.size(30.dp))
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

/**
 * Composable function that displays a workout card for a given workout.
 *
 * @param workout The exercise.
 * @param profile The resource ID for the profile image.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun WorkoutCard(workout: Workout, profile: Int, navigationActions: NavigationActions) {
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
              .clickable { /*Navigate to the screen to edit or start the workout*/}
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

/**
 * Composable function that displays a button for a quick workout session.
 *
 * @param iconId The resource ID for the quick workout icon.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun QuickWorkoutButton(iconId: Int, navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.size(75.dp)
              .background(Blue, CircleShape)
              .clickable { /*Navigate to the screen of associated Quickworkout*/}
              .testTag("QuickWorkoutButton"),
      contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Quick Workout Icon",
            modifier = Modifier.size(35.dp))
      }
}
