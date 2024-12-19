package com.android.sample.ui.googlemap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.achievements.StatisticsViewModel
import com.android.sample.model.location.LocationService
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.model.workout.RunningWorkout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.ChronoDisplay
import com.android.sample.ui.composables.DistanceDisplay
import com.android.sample.ui.composables.PaceDisplay
import com.android.sample.ui.composables.PathDisplay
import com.android.sample.ui.composables.RunningBottomBarControl
import com.android.sample.ui.composables.RunningDesignButton
import com.android.sample.ui.composables.RunningStatsScreen
import com.android.sample.ui.composables.ToggleButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.White
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import java.time.LocalDateTime
import java.util.Timer
import java.util.TimerTask

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunningScreen(
    navigationActions: NavigationActions,
    runningWorkoutViewModel: WorkoutViewModel<RunningWorkout>,
    statisticsViewModel: StatisticsViewModel,
    userAccountViewModel: UserAccountViewModel
) {

  var finalPathPoints by remember { mutableStateOf(mutableListOf<LatLng>()) }

  var isPaused by remember { mutableStateOf(false) }

  var isSavingRunning by remember { mutableStateOf(false) }

  var isSharingWithFriends by remember { mutableStateOf(false) }

  var isFirstTime by remember { mutableStateOf(true) }

  var isFinished by remember { mutableStateOf(false) }

  var isStatsDisplayed by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("") }

  var description by remember { mutableStateOf("") }

  val pathPoints = LocationService.pathPoints.collectAsState(initial = emptyList())
  val cameraPositionState = LocationService.camera.collectAsState()
  val context = LocalContext.current
  val scaffoldState = rememberBottomSheetScaffoldState()

  var isRunning by remember { mutableStateOf(false) }
  var elapsedTime by remember { mutableStateOf(0L) }
  val timer = remember { mutableStateOf<Timer?>(null) }

  val offsetX by
      animateDpAsState(
          targetValue = if (isStatsDisplayed) 0.dp else (-500).dp, // Start off-screen to the left
          animationSpec = tween(durationMillis = 600) // Animation duration
          )

  when {
    isFirstTime -> {
      Scaffold(
          topBar = { TopBar(navigationActions = navigationActions, R.string.RunningScreenTopBar) },
      ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          GoogleMap(
              modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState.value)

          Column(
              modifier = Modifier.fillMaxSize().padding(32.dp),
              verticalArrangement = Arrangement.Bottom,
              horizontalAlignment = Alignment.CenterHorizontally) {
                RunningDesignButton(
                    onClick = {
                      LocationServiceManager.startLocationService(context)
                      isRunning = true
                      isFirstTime = false

                      timer.value =
                          Timer().apply {
                            scheduleAtFixedRate(
                                object : TimerTask() {
                                  override fun run() {
                                    elapsedTime += 1
                                  }
                                },
                                1000,
                                1000)
                          }
                    },
                    title = "Start",
                    showIcon = true,
                    testTag = "StartButton")
              }
        }
      }
    }
    isRunning -> {
      Scaffold(modifier = Modifier.fillMaxSize().testTag("MainRunningScreen")) {
        Box(modifier = Modifier.fillMaxSize()) {
          GoogleMap(
              modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState.value) {
                // Display the polyline for the running path
                if (pathPoints.value.isNotEmpty()) {
                  Polyline(points = pathPoints.value, color = Color.Blue, width = 16f)
                }
              }
          Column(
              modifier = Modifier.fillMaxSize().padding(16.dp),
              verticalArrangement = Arrangement.Bottom,
              horizontalAlignment = Alignment.CenterHorizontally) {
                RunningBottomBarControl(
                    pauseOnClick = {
                      isPaused = true
                      // Stop the timer and location service
                      timer.value?.cancel()
                      timer.value = null
                      LocationServiceManager.stopLocationService(context)
                    },
                    resumeOnClick = {
                      // Start the location service and timer
                      isPaused = false
                      LocationServiceManager.startLocationService(context)

                      timer.value =
                          Timer().apply {
                            scheduleAtFixedRate(
                                object : TimerTask() {
                                  override fun run() {
                                    elapsedTime += 1
                                  }
                                },
                                1000,
                                1000)
                          }
                    },
                    finishOnClick = {
                      // Stop and Reset the location service

                      finalPathPoints = pathPoints.value.toMutableList()

                      LocationServiceManager.stopAndResetLocationService(context)
                      isRunning = false
                      isFirstTime = false
                      isPaused = false
                      isFinished = true
                    },
                    locationOnClick = { isStatsDisplayed = true },
                    isSplit = isPaused,
                    isSelected = true,
                    PauseTestTag = "PauseButton",
                    ResumeTestTag = "ResumeButton",
                    FinishTestTag = "FinishButton",
                    LocationTestTag = "LocationButton")
              }
          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .offset(x = offsetX)
                      .background(color = White) // Animate the horizontal offset
              ) {
                RunningStatsScreen(
                    elapsedTime = elapsedTime,
                    paceString = calculatePace(elapsedTime, calculateDistance(pathPoints.value)),
                    distance = calculateDistance(pathPoints.value),
                    locationOnClick = { isStatsDisplayed = false },
                    isSplit = isPaused,
                    resumeOnClick = {
                      // Start the location service and timer
                      isPaused = false
                      LocationServiceManager.startLocationService(context)

                      timer.value =
                          Timer().apply {
                            scheduleAtFixedRate(
                                object : TimerTask() {
                                  override fun run() {
                                    elapsedTime += 1
                                  }
                                },
                                1000,
                                1000)
                          }
                    },
                    pauseOnClick = {
                      isPaused = true
                      // Stop the timer and location service
                      timer.value?.cancel()
                      timer.value = null
                      LocationServiceManager.stopLocationService(context)
                    },
                    finishOnClick = {
                      // Stop and Reset the location service

                      finalPathPoints = pathPoints.value.toMutableList()

                      LocationServiceManager.stopAndResetLocationService(context)
                      isRunning = false
                      isFirstTime = false
                      isPaused = false
                      isFinished = true
                    },
                    PauseTestTag = "PauseButtonStats",
                    ResumeTestTag = "ResumeButtonStats",
                    FinishTestTag = "FinishButtonStats",
                    LocationTestTag = "LocationButtonStats")
              }
        }
      }
    }
    isFinished -> {

      if (isSavingRunning) {

        Scaffold(
            topBar = {
              TopBar(navigationActions = navigationActions, R.string.RunningScreenTopBar)
            },
        ) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(2.dp),
              modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(60.dp))
                ChronoDisplay(elapsedTime)
                Divider(
                    color = LightGrey,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally))
                DistanceDisplay(calculateDistance(pathPoints.value))
                Divider(
                    color = LightGrey,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally))
                PaceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

                Spacer(Modifier.height(10.dp))

                Divider(
                    color = LightGrey,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(5.dp))

                PathDisplay(modifier = Modifier, finalPathPoints)

                Divider(
                    color = LightGrey,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(5.dp))

                ToggleButton(onClick = { isSavingRunning = it }, isSavingRunning, "Save Running")

                ToggleButton(
                    onClick = { isSharingWithFriends = it },
                    isSharingWithFriends,
                    "Share with Friends")

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("Enter a name") },
                    modifier = Modifier.testTag("nameTextField"))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe your running session") },
                    modifier = Modifier.testTag("descriptionTextField"))

                Spacer(Modifier.height(41.dp))

                Divider(
                    color = LightGrey,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))
                RunningDesignButton(
                    onClick = {
                      // Stop and Reset the location service
                      isRunning = false
                      isFirstTime = true
                      isPaused = false
                      isFinished = false

                      val runningWorkout =
                          RunningWorkout(
                              runningWorkoutViewModel.getNewUid(),
                              name,
                              description,
                              date = LocalDateTime.now(),
                              path =
                                  pathPoints.value.map { loc ->
                                    com.google.type.LatLng.newBuilder()
                                        .setLongitude(loc.longitude)
                                        .setLatitude(loc.latitude)
                                        .build()
                                  },
                              timeMs = elapsedTime)

                      val stats =
                          statisticsViewModel.computeWorkoutStatistics(
                              workout = runningWorkout,
                              exerciseList = emptyList(),
                              userAccountViewModel = userAccountViewModel)
                      statisticsViewModel.addWorkoutStatistics(stats)

                      runningWorkoutViewModel.addWorkout(runningWorkout)
                      LocationServiceManager.stopAndResetLocationService(context)

                      navigationActions.navigateTo(Screen.MAIN)
                    },
                    title = "Save",
                    showIcon = false,
                    testTag = "SaveButton")
                Spacer(Modifier.height(20.dp))
              }
        }
      } else {

        Scaffold(
            topBar = {
              TopBar(navigationActions = navigationActions, R.string.RunningScreenTopBar)
            },
        ) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(2.dp),
              modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(60.dp))
                ChronoDisplay(elapsedTime)
                Divider(
                    color = LightGrey,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally))
                DistanceDisplay(calculateDistance(pathPoints.value))
                Divider(
                    color = LightGrey,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally))
                PaceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

                Spacer(Modifier.height(10.dp))

                Divider(
                    color = LightGrey,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(5.dp))

                PathDisplay(modifier = Modifier, finalPathPoints)

                Divider(
                    color = LightGrey,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(5.dp))

                ToggleButton(onClick = { isSavingRunning = it }, isSavingRunning, "Save Running")

                Spacer(Modifier.height(254.dp))

                Divider(
                    color = LightGrey,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))
                RunningDesignButton(
                    onClick = {
                      // Stop and Reset the location service
                      LocationServiceManager.stopAndResetLocationService(context)
                      isRunning = false
                      isFirstTime = true
                      isPaused = false
                      isFinished = false

                      val runningWorkout =
                          RunningWorkout(
                              runningWorkoutViewModel.getNewUid(),
                              name,
                              description,
                              date = LocalDateTime.now(),
                              path =
                                  pathPoints.value.map { loc ->
                                    com.google.type.LatLng.newBuilder()
                                        .setLongitude(loc.longitude)
                                        .setLatitude(loc.latitude)
                                        .build()
                                  },
                              timeMs = elapsedTime)

                      val stats =
                          statisticsViewModel.computeWorkoutStatistics(
                              workout = runningWorkout,
                              exerciseList = emptyList(),
                              userAccountViewModel = userAccountViewModel)
                      statisticsViewModel.addWorkoutStatistics(stats)

                      navigationActions.navigateTo(Screen.MAIN)
                    },
                    title = "Finish",
                    showIcon = false,
                    testTag = "FinishButton")

                Spacer(Modifier.height(20.dp))
              }
        }
      }
    }
  }
}

fun calculateDistance(pathPoints: List<LatLng>): Double {
  if (pathPoints.size < 2) return 0.0
  var totalDistance = 0.0
  for (i in 1 until pathPoints.size) {
    val start = pathPoints[i - 1]
    val end = pathPoints[i]
    totalDistance +=
        FloatArray(1)
            .apply {
              Location.distanceBetween(
                  start.latitude, start.longitude, end.latitude, end.longitude, this)
            }[0]
  }
  return totalDistance / 1000.0
}

fun calculatePace(elapsedTime: Long, distance: Double): String {
  val distanceInKm = distance
  if (distanceInKm == 0.0 || elapsedTime == 0L) return "0:00"
  val paceInSeconds = elapsedTime / distanceInKm
  val minutes = (paceInSeconds / 60).toInt()
  val seconds = (paceInSeconds % 60).toInt()
  return String.format("%d:%02d", minutes, seconds)
}

object LocationServiceManager {
  fun startLocationService(context: Context) {
    val intent =
        Intent(context, LocationService::class.java).apply {
          action = LocationService.ACTION_START
          context.startService(this)
        }
  }

  fun stopLocationService(context: Context) {
    val intent =
        Intent(context, LocationService::class.java).apply {
          action = LocationService.ACTION_STOP
          context.startService(this)
        }
  }

  fun stopAndResetLocationService(context: Context) {
    val intent =
        Intent(context, LocationService::class.java).apply {
          action = LocationService.ACTION_RESET
          context.startService(this)
        }
  }
}
