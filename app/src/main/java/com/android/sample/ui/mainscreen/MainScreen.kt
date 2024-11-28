package com.android.sample.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberImagePainter
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.ContrailOne
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.DarkBlue2
import com.android.sample.ui.theme.DarkBlueTopBar1
import com.android.sample.ui.theme.DarkBlueTopBar2
import com.android.sample.ui.theme.DoubleArrow
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.Line
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.SoftGrey

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
    userAccountViewModel: UserAccountViewModel
) {
  // Configuration temporaire pour tester
  val account = userAccountViewModel.userAccount.collectAsState().value
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
      topBar = { ProfileSection(account = account, navigationActions = navigationActions) },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween) {
              WorkoutSessionsSection(
                  workout = workoutDisplayed,
                  profile = account?.profileImageUrl ?: "",
                  navigationActions = navigationActions)
            Divider(
                color = Line,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 25.dp, vertical = 3.dp).shadow(1.dp)
            )
              QuickWorkoutSection(
                  navigationActions = navigationActions,
                  bodyWeightViewModel = bodyWeightViewModel,
                  yogaViewModel = yogaViewModel)
            Divider(
                color = Line,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 25.dp, vertical = 3.dp).shadow(1.dp)
            )
              AchievementsSection(navigationActions = navigationActions)
            }
      },
      bottomBar = { BottomBar(navigationActions = navigationActions) })
}

/**
 * Composable function that displays the profile section at the top of the screen.
 *
 * @param account The user account containing the profile information.
 * @param profile The resource ID for the profile picture.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun ProfileSection(account: UserAccount?, navigationActions: NavigationActions) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, clip = false)
            .background(
                brush = Brush.linearGradient(
                colors = listOf(DarkBlueTopBar1, DarkBlueTopBar2), // Dégradé de DarkBlue à LightBlue
                start = Offset(0f, 0f), // Début du dégradé (à gauche)
                end = Offset(Float.POSITIVE_INFINITY, 0f))
            )
            .padding(vertical = 25.dp)
    ) {
        // Contenu de la Row (Image et Texte)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(data = account?.profileImageUrl ?: ""),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .testTag("ProfilePicture")
                    .clickable { navigationActions.navigateTo(Screen.SETTINGS) } // temporary until the bottom bar implemented
                    .shadow(4.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.welcome_message, account?.firstName ?: ""),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 24.sp, fontFamily = OpenSans,  fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.testTag("WelcomeText")
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.group),
                contentDescription = "Group",
                modifier = Modifier
                    .size(30.dp)
                    .testTag("Group")
                    .clickable { navigationActions.navigateTo(Screen.FRIENDS)}
            )
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
fun WorkoutSessionsSection(
    workout: Workout?,
    profile: String,
    navigationActions: NavigationActions
) {
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).testTag("WorkoutSection")) {
    Text(
        text = stringResource(id = R.string.WorkoutsTitle),
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 24.sp,
            fontFamily = OpenSans),
        modifier = Modifier.padding(vertical = 8.dp))
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .height(300.dp)
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

          Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = rememberImagePainter(data = R.drawable.double_arrow),
            contentDescription = "Double arrow",
            colorFilter = ColorFilter.tint(DoubleArrow),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .height(40.dp)
                .testTag("Double Arrow")
                .clickable { navigationActions.navigateTo(Screen.VIEW_ALL) }
        )
        }
  }
}

/**
 * Composable function that displays the quick workout section.
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun QuickWorkoutSection(
    navigationActions: NavigationActions,
    bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>
) {
    Column(modifier = Modifier.fillMaxWidth().testTag("QuickSection")) {
        Text(
            text = stringResource(id = R.string.QuickTitle),
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 24.sp,
                fontFamily = OpenSans
            ),
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
        )

        Row(
            modifier =
            Modifier.fillMaxWidth()
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickWorkoutButton(R.drawable.quick_bodyweight, onClick = {
                bodyWeightViewModel.selectWorkout(bodyWeightViewModel.copyOf(BodyWeightWorkout.QUICK_BODY_WEIGHT_WORKOUT))
                navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)})
            QuickWorkoutButton(R.drawable.quick_running, onClick = {
                bodyWeightViewModel.selectWorkout(bodyWeightViewModel.copyOf(BodyWeightWorkout.WARMUP_WORKOUT))
                navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
            })
            QuickWorkoutButton(R.drawable.quick_yoga, onClick = {
                yogaViewModel.selectWorkout(yogaViewModel.copyOf(YogaWorkout.QUICK_YOGA_WORKOUT))
                navigationActions.navigateTo(Screen.YOGA_OVERVIEW)
            })
        }
    }
}
/**
 * Composable function that displays a button for a quick workout session.
 *
 * @param iconId The resource ID for the quick workout icon.
 * @param onClick Functions called when the button is clicked.
 */
@Composable
fun QuickWorkoutButton(
    iconId: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(76.dp)
            .width(117.dp)
            .clickable { onClick() }
            .background(Color.Transparent)
            .padding(5.dp)
    ) {
        val shape = RoundedCornerShape(
            topStart = 25.dp,
            topEnd = 11.dp,
            bottomEnd = 25.dp,
            bottomStart = 11.dp
        )
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Quick Workout Icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
                .shadow(4.dp, shape = shape)
                .clip(shape)
        )
    }
}

/**
 * Composable function that displays a button for a quick workout session.
 *
 * @param iconId The resource ID for the quick workout icon.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun WorkoutButton(
    iconId: Int,
    navigationActions: NavigationActions,
    bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    buttonSize: Int
) {
  Box(
      modifier =
          Modifier.size(buttonSize.dp)
              .aspectRatio(1f)
              .background(Blue)
              .clickable {
                when (iconId) {
                  R.drawable.running_man -> {

                  }
                  R.drawable.pushups -> {
                    bodyWeightViewModel.selectWorkout(
                        bodyWeightViewModel.copyOf(BodyWeightWorkout.WORKOUT_PUSH_UPS))
                    navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
                  }
                  R.drawable.yoga -> {

                  }
                  R.drawable.dumbbell -> {

                  }
                }
              }
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
fun WorkoutCard(workout: Workout, profile: String, navigationActions: NavigationActions) {
    val shape = RoundedCornerShape(topStart = 40.dp, topEnd = 15.dp, bottomStart = 15.dp, bottomEnd = 40.dp)

    Card(
        shape = shape,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .shadow(
                elevation = 8.dp, shape = shape
            )
            .fillMaxWidth()
            .clickable { /* Navigate to workout details or start workout */}
            .testTag("WorkoutCard"),
        colors = CardDefaults.cardColors(containerColor = BlueWorkoutCard)) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 19.sp, fontFamily = ContrailOne),
                    modifier = Modifier.padding(horizontal = 10.dp))
                  Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberImagePainter(data = profile),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(28.dp).clip(CircleShape))
              }
              Image(
                  painter =
                      painterResource(
                          id =
                              when (workout) {
                                is BodyWeightWorkout -> R.drawable.dumbell_inner_shadow
                                is YogaWorkout -> R.drawable.yoga_innershadow
                                else -> R.drawable.running_innershadow
                              }),
                  contentDescription = "Workout Icon",
                  modifier = Modifier.size(70.dp).padding(horizontal = 15.dp))
            }
      }
}



/**
 * Composable function that the button to navigate to the achievements
 *
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun AchievementsSection(navigationActions: NavigationActions) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        stringResource(id = R.string.AchievementsTitle),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 24.sp, fontFamily = OpenSans))
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { navigationActions.navigateTo(Screen.ACHIEVEMENTS) }
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .height(80.dp)
                .background(LightGrey, RoundedCornerShape(20.dp))
                .testTag("AchievementButton"),
        contentAlignment = Alignment.Center) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.trophy),
                contentDescription = "Trophy",
                modifier = Modifier.size(40.dp))
            Text(
                stringResource(id = R.string.View),
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 23.sp))
          }
        }
  }
}
