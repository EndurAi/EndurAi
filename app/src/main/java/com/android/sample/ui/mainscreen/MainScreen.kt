package com.android.sample.ui.mainscreen

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.sample.R
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.BodyWeightWorkout
import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.model.workout.YogaWorkout
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.composables.ImageComposable
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.AchievementButton
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.ContrailOne
import com.android.sample.ui.theme.DarkBlueTopBar1
import com.android.sample.ui.theme.DarkBlueTopBar2
import com.android.sample.ui.theme.DoubleArrow
import com.android.sample.ui.theme.Line
import com.android.sample.ui.theme.OpenSans

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

  val account = userAccountViewModel.userAccount.collectAsState().value
  val bodyWeightWorkouts = bodyWeightViewModel.workouts.collectAsState()
  val yogaWorkouts = yogaViewModel.workouts.collectAsState()

  val expanded = remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag("mainScreen"),
      topBar = {
        ProfileSection(
            account = account, navigationActions = navigationActions, expanded = expanded)
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween) {
              WorkoutSessionsSection(
                  bodyWeightViewModel = bodyWeightViewModel,
                  yogaViewModel = yogaViewModel,
                  profile = account?.profileImageUrl ?: "",
                  navigationActions = navigationActions,
                  expanded = expanded)

              if (!expanded.value) {
                Divider(
                    color = Line,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 1.dp).shadow(1.dp))
                QuickWorkoutSection(
                    navigationActions = navigationActions,
                    bodyWeightViewModel = bodyWeightViewModel,
                    yogaViewModel = yogaViewModel)
                Divider(
                    color = Line,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp).shadow(1.dp))
                AchievementsSection(navigationActions = navigationActions)
              }
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
fun ProfileSection(
    account: UserAccount?,
    navigationActions: NavigationActions,
    expanded: MutableState<Boolean>
) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .shadow(20.dp, clip = false)
              .background(
                  brush =
                      Brush.linearGradient(
                          colors = listOf(DarkBlueTopBar1, DarkBlueTopBar2), // brush of colors
                          start = Offset(0f, 0f),
                          end = Offset(Float.POSITIVE_INFINITY, 0f)))
              .padding(vertical = 25.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
              Image(
                  painter = rememberImagePainter(data = account?.profileImageUrl ?: ""),
                  contentDescription = "Profile",
                  contentScale = ContentScale.Crop,
                  modifier =
                      Modifier.size(54.dp).clip(CircleShape).testTag("ProfilePicture").shadow(4.dp))
              Spacer(modifier = Modifier.width(20.dp))
              Text(
                  text =
                      if (expanded.value) {
                        stringResource(id = R.string.WorkoutsTitle, account?.firstName ?: "")
                      } else {
                        stringResource(id = R.string.welcome_message, account?.firstName ?: "")
                      },
                  style =
                      MaterialTheme.typography.titleSmall.copy(
                          fontSize = 24.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.testTag("WelcomeText"))
              Spacer(modifier = Modifier.weight(1f))
              Image(
                  painter = painterResource(id = R.drawable.group),
                  contentDescription = "Group",
                  modifier =
                      Modifier.size(30.dp).testTag("FriendsButton").clickable {
                        navigationActions.navigateTo(Screen.FRIENDS)
                      })
            }
      }
}

/** Enum class that defines the three possible tab for workout display. */
enum class WorkoutTab {
  Bodyweight,
  Yoga,
  Running
}

/**
 * Composable function that displays a section with workout sessions.
 *
 * @param workout A list of workouts to display.
 * @param profile The resource ID for the profile picture.
 * @param navigationActions Actions for navigating between screens.
 */
@Composable
fun WorkoutSessionsSection(
    bodyWeightViewModel: WorkoutViewModel<BodyWeightWorkout>,
    yogaViewModel: WorkoutViewModel<YogaWorkout>,
    profile: String,
    navigationActions: NavigationActions,
    expanded: MutableState<Boolean>
) {
  val selectedTab = remember { mutableStateOf(WorkoutTab.Bodyweight) }

  val bodyweightWorkouts = bodyWeightViewModel.workouts.collectAsState()
  val yogaWorkouts = yogaViewModel.workouts.collectAsState()
  val workout: List<Workout> =
      if (expanded.value) {
        when (selectedTab.value) {
          WorkoutTab.Bodyweight -> {
            bodyweightWorkouts.value
          }
          WorkoutTab.Yoga -> {
            yogaWorkouts.value
          }
          WorkoutTab.Running -> {
            emptyList()
          }
        }
      } else {
        bodyweightWorkouts.value.take(2) +
            yogaWorkouts.value.take(maxOf(2 - bodyweightWorkouts.value.size, 0))
      }

  val animatedHeight by
      animateDpAsState(
          targetValue = if (expanded.value) 500.dp else 200.dp,
          animationSpec = tween(durationMillis = 300))

  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).testTag("WorkoutSection")) {
    if (expanded.value) {
      TabsMainScreen(selectedTab.value, onTabSelected = { tab -> selectedTab.value = tab })
    } else {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
          text = stringResource(id = R.string.WorkoutsTitle),
          style =
              MaterialTheme.typography.titleSmall.copy(
                  fontSize = 24.sp, fontFamily = OpenSans, fontWeight = FontWeight.SemiBold),
          modifier = Modifier.padding(vertical = 8.dp))
    }
    Column(modifier = Modifier.fillMaxWidth().height(animatedHeight).padding(8.dp)) {
      if (workout.isNotEmpty()) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
          items(workout) { workoutItem ->
            val workoutViewModel =
                when (workoutItem) {
                  is BodyWeightWorkout -> bodyWeightViewModel
                  else -> yogaViewModel
                }
            WorkoutCard(workoutItem, workoutViewModel, profile, navigationActions)
            Spacer(modifier = Modifier.height(15.dp))
          }
        }
      } else {
        Text(
            modifier = Modifier.fillMaxWidth().padding(12.dp).testTag("NoWorkoutMessage"),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.noWorkouts),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))

        if (expanded.value) {
          ImageComposable(
              R.drawable.no_item,
              "No workout logo",
              Modifier.size(200.dp).align(Alignment.CenterHorizontally).testTag("NoWorkoutImage"))
        }
      }

      Spacer(modifier = Modifier.weight(1f))
    }
    Image(
        painter = rememberImagePainter(data = R.drawable.double_arrow),
        contentDescription = "Double arrow",
        colorFilter = ColorFilter.tint(DoubleArrow),
        modifier =
            Modifier.rotate(if (expanded.value) 180f else 0f)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .height(40.dp)
                .testTag("DoubleArrow")
                .clickable { expanded.value = !expanded.value })
  }
}

/**
 * Displays the tabs for selecting different workout types.
 *
 * @param selectedTab The currently selected tab index.
 * @param onTabSelected Callback function to handle tab selection.
 */
@Composable
fun TabsMainScreen(selectedTab: WorkoutTab, onTabSelected: (WorkoutTab) -> Unit) {
  val tabTitles = listOf(R.string.TitleTabBody, R.string.TitleTabYoga, R.string.TitleTabRunning)
  val tabTags = listOf("BodyTab", "YogaTab", "RunningTab")

  Column(modifier = Modifier.fillMaxWidth().testTag("TabSection")) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
          WorkoutTab.entries.forEachIndexed { index, tab ->
            TabItemMainScreen(
                title = tabTitles[index],
                isSelected = selectedTab == tab,
                modifier = Modifier.testTag(tabTags[index]),
                onClick = { onTabSelected(tab) })
          }
        }
    Divider(
        color = Line, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 5.dp).shadow(1.dp))
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
fun TabItemMainScreen(
    @StringRes title: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
  val shape =
      RoundedCornerShape(topStart = 25.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 25.dp)
  Box(
      modifier
          .shadow(if (isSelected) 4.dp else 0.dp, shape = shape)
          .border(
              width = 1.dp,
              color = if (isSelected) Color.Transparent else Color.LightGray,
              shape = shape)
          .background(color = if (isSelected) BlueWorkoutCard else Color.Transparent, shape = shape)
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clickable(onClick = onClick)) {
        Text(
            text = stringResource(id = title),
            color = if (isSelected) Color.Black else Color.Gray,
            fontSize = 18.sp,
            fontFamily = OpenSans)
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
        style =
            MaterialTheme.typography.titleSmall.copy(
                fontSize = 24.sp, fontFamily = OpenSans, fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp))

    Row(
        modifier = Modifier.fillMaxWidth().background(Color.Transparent),
        horizontalArrangement = Arrangement.SpaceEvenly) {
          QuickWorkoutButton(
              R.drawable.quick_bodyweight,
              onClick = {
                bodyWeightViewModel.selectWorkout(
                    bodyWeightViewModel.copyOf(BodyWeightWorkout.QUICK_BODY_WEIGHT_WORKOUT))
                navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
              })
          QuickWorkoutButton(
              R.drawable.quick_running,
              onClick = {
                bodyWeightViewModel.selectWorkout(
                    bodyWeightViewModel.copyOf(BodyWeightWorkout.WARMUP_WORKOUT))
                navigationActions.navigateTo(Screen.BODY_WEIGHT_OVERVIEW)
              })
          QuickWorkoutButton(
              R.drawable.quick_yoga,
              onClick = {
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
  val shape =
      RoundedCornerShape(topStart = 25.dp, topEnd = 11.dp, bottomEnd = 25.dp, bottomStart = 11.dp)
  Box(
      modifier =
          Modifier.height(76.dp)
              .width(117.dp)
              .background(Color.Transparent, shape = shape)
              .padding(5.dp)) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Quick Workout Icon",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier.fillMaxSize()
                    .shadow(4.dp, shape = shape)
                    .clip(shape)
                    .testTag("QuickWorkoutButton")
                    .clickable { onClick() })
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
fun WorkoutCard(
    workout: Workout,
    viewModel: WorkoutViewModel<Workout>,
    profile: String,
    navigationActions: NavigationActions
) {
  val shape =
      RoundedCornerShape(topStart = 40.dp, topEnd = 15.dp, bottomStart = 15.dp, bottomEnd = 40.dp)

  Card(
      shape = shape,
      modifier =
          Modifier.padding(horizontal = 30.dp, vertical = 3.dp)
              .shadow(elevation = 8.dp, shape = shape)
              .fillMaxWidth()
              .clickable { navigateToWorkoutScreen(workout, viewModel, navigationActions) }
              .testTag("WorkoutCard"),
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
  val shape =
      RoundedCornerShape(topStart = 25.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 25.dp)
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        stringResource(id = R.string.AchievementsTitle),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp).testTag("AchievementText"),
        style =
            MaterialTheme.typography.titleSmall.copy(
                fontSize = 24.sp, fontFamily = OpenSans, fontWeight = FontWeight.SemiBold))

    Row() {
      Text(
          stringResource(
              id = R.string.TotalTrainings,
              10), // Hardcoded value until the achievements epic is implemented
          modifier = Modifier.padding(horizontal = 12.dp).align(Alignment.CenterVertically),
          style = MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp),
          fontFamily = OpenSans)
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(vertical = 16.dp, horizontal = 20.dp)
                  .shadow(4.dp, shape = shape)
                  .height(60.dp)
                  .background(AchievementButton, shape = shape)
                  .testTag("AchievementButton")
                  .clickable { navigationActions.navigateTo(Screen.ACHIEVEMENTS) },
          contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                  stringResource(id = R.string.ViewAllTitle),
                  modifier = Modifier.padding(horizontal = 12.dp),
                  style =
                      MaterialTheme.typography.titleSmall.copy(
                          fontSize = 22.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold))
              Image(
                  painter = painterResource(id = R.drawable.trophy),
                  contentDescription = "Trophy",
                  modifier = Modifier.size(38.dp))
            }
          }
    }
  }
}
