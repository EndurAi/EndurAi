package com.android.sample.ui.googlemap

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.location.LocationService
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import android.location.Location
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.android.sample.ui.composables.chronoDisplay
import com.android.sample.ui.composables.CircularButton
import com.android.sample.ui.composables.distanceDisplay
import com.android.sample.ui.composables.paceDisplay
import com.google.android.gms.maps.model.LatLng
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunningScreen(navigationActions: NavigationActions) {

    val pathPoints = LocationService.pathPoints.collectAsState(initial = emptyList())
    val cameraPositionState = LocationService.camera.collectAsState()
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()

    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    val timer = remember { mutableStateOf<Timer?>(null) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (isRunning) {
                    chronoDisplay(elapsedTime)
                    distanceDisplay(calculateDistance(pathPoints.value))
                    paceDisplay(calculatePace(elapsedTime, calculateDistance(pathPoints.value)))

                    CircularButton(onClick = {
                        // Stop the timer and location service
                        timer.value?.cancel()
                        timer.value = null
                        isRunning = false
                        LocationServiceManager.stopLocationService(context)
                    })
                } else {
                    Button(onClick = {
                        // Start the location service and timer
                        LocationServiceManager.startLocationService(context)
                        isRunning = true

                        timer.value = Timer().apply {
                            scheduleAtFixedRate(object : TimerTask() {
                                override fun run() {
                                    elapsedTime += 1
                                }
                            }, 1000, 1000)
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Start")
                    }
                }
            }
        },
        sheetPeekHeight = 150.dp, // Controls how much of the sheet is visible when collapsed
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState.value
            ) {
                // Display the polyline for the running path
                if (pathPoints.value.isNotEmpty()) {
                    Polyline(
                        points = pathPoints.value,
                        color = Color.Blue,
                        width = 16f
                    )
                }
            }
        }
    }
}

fun calculateDistance(pathPoints: List<LatLng>): Double {
    // Implémentation simplifiée : calcul de la distance totale entre les points
    if (pathPoints.size < 2) return 0.0
    var totalDistance = 0.0
    for (i in 1 until pathPoints.size) {
        val start = pathPoints[i - 1]
        val end = pathPoints[i]
        totalDistance += FloatArray(1).apply {
            Location.distanceBetween(
                start.latitude, start.longitude,
                end.latitude, end.longitude,
                this
            )
        }[0]
    }
    return totalDistance // Convertir en kilomètres
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
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startService(this)
        }
    }

    fun stopLocationService(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            context.stopService(this)
        }
    }
}
