package com.android.sample.ui.googlemap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.location.LocationService
import com.android.sample.model.workout.RunningWorkout
import com.android.sample.model.workout.WorkoutViewModel
import com.android.sample.ui.composables.CircularButton
import com.android.sample.ui.composables.RunningDesignButton
import com.android.sample.ui.composables.ToggleButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.composables.chronoDisplay
import com.android.sample.ui.composables.distanceDisplay
import com.android.sample.ui.composables.paceDisplay
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.LightGrey
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
    runningWorkoutViewModel: WorkoutViewModel<RunningWorkout>
) {

  var isPaused by remember { mutableStateOf(false) }

  var isSavingRunning by remember { mutableStateOf(false) }

  var isSharingWithFriends by remember { mutableStateOf(false) }

  var isFirstTime by remember { mutableStateOf(true) }

  var isFinished by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("") }

  var description by remember { mutableStateOf("") }

  val pathPoints = LocationService.pathPoints.collectAsState(initial = emptyList())
  val cameraPositionState = LocationService.camera.collectAsState()
  val context = LocalContext.current
  val scaffoldState = rememberBottomSheetScaffoldState()

  var isRunning by remember { mutableStateOf(false) }
  var elapsedTime by remember { mutableStateOf(0L) }
  val timer = remember { mutableStateOf<Timer?>(null) }

  when {
    isFirstTime -> {
      Scaffold(
          topBar = { TopBar(navigationActions = navigationActions, R.string.RunningScreenTopBar) },
      ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          GoogleMap(
              modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState.value)

          Column(
              modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    showIcon = true)
              }
        }
      }
    }
    isRunning -> {
      BottomSheetScaffold(
          topBar = { TopBar(navigationActions, R.string.RunningScreenTopBar) },
          scaffoldState = scaffoldState,
          sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                  chronoDisplay(elapsedTime)
                  distanceDisplay(calculateDistance(pathPoints.value))
                  paceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

                  CircularButton(
                      onClick = {
                        // Stop the timer and location service
                        timer.value?.cancel()
                        timer.value = null
                        isRunning = false
                        isPaused = true
                        LocationServiceManager.stopLocationService(context)
                      })
                }
          },
          sheetPeekHeight = 150.dp,
          modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
              GoogleMap(
                  modifier = Modifier.fillMaxSize(),
                  cameraPositionState = cameraPositionState.value) {
                    // Display the polyline for the running path
                    if (pathPoints.value.isNotEmpty()) {
                      Polyline(points = pathPoints.value, color = Color.Blue, width = 16f)
                    }
                  }
            }
          }
    }
    isPaused -> {
      Scaffold(
          topBar = { TopBar(navigationActions = navigationActions, R.string.RunningScreenTopBar) },
      ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
              Spacer(Modifier.height(60.dp))
              chronoDisplay(elapsedTime)
              distanceDisplay(calculateDistance(pathPoints.value))
              paceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

              Spacer(Modifier.height(10.dp))

              Divider(
                  color = LightGrey,
                  thickness = 2.dp,
                  modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

              Spacer(Modifier.height(80.dp))

              Text(text = "Paused", color = Black, fontSize = 50.sp)

              Row(
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    RunningDesignButton(
                        onClick = {
                          // Start the location service and timer
                          LocationServiceManager.startLocationService(context)
                          isRunning = true
                          isPaused = false

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
                        title = "Resume",
                        showIcon = false)
                  }

              Spacer(Modifier.height(100.dp))

              Divider(
                  color = LightGrey,
                  thickness = 2.dp,
                  modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))
              RunningDesignButton(
                  onClick = {
                    // Stop and Reset the location service
                    LocationServiceManager.stopAndResetLocationService(context)
                    isRunning = false
                    isFirstTime = false
                    isPaused = false
                    isFinished = true
                  },
                  title = "Finish",
                  showIcon = false)
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
                chronoDisplay(elapsedTime)
                distanceDisplay(calculateDistance(pathPoints.value))
                paceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

                Spacer(Modifier.height(10.dp))

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

                      runningWorkoutViewModel.addWorkout(runningWorkout)

                      navigationActions.navigateTo(Screen.MAIN)
                    },
                    title = "Save",
                    showIcon = false)
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
                chronoDisplay(elapsedTime)
                distanceDisplay(calculateDistance(pathPoints.value))
                paceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

                Spacer(Modifier.height(10.dp))

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

                      navigationActions.navigateTo(Screen.MAIN)
                    },
                    title = "Finish",
                    showIcon = false)
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
  if (distanceInKm == 0.0 || elapsedTime == 0L) return "0:00/km"
  val paceInSeconds = elapsedTime / distanceInKm
  val minutes = (paceInSeconds / 60).toInt()
  val seconds = (paceInSeconds % 60).toInt()
  return String.format("%d:%02d/km", minutes, seconds)
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
          context.stopService(this)
        }
  }

  fun stopAndResetLocationService(context: Context) {
    val intent =
        Intent(context, LocationService::class.java).apply {
          action = LocationService.ACTION_RESET
          context.stopService(this)
        }
  }
}