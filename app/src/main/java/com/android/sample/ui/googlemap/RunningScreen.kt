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

@Composable
fun RunningScreen(onStartClick: () -> Unit, navigationActions: NavigationActions) {

  val currentLocation = LocationService.userLocation.collectAsState()
  val pathPoints = LocationService.pathPoints.collectAsState(initial = emptyList())
  val cameraPositionState = LocationService.camera.collectAsState()

  Scaffold(topBar = { TopBar(navigationActions, R.string.runningTitle) }) { innerPadding ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(
                    innerPadding) // Utilisez `innerPadding` pour éviter que la carte soit collée à
                                  // la TopBar
                .padding(horizontal = 16.dp)) {
          Spacer(Modifier.height(32.dp))

          Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState.value) {
                  // Ajouter Polyline pour le tracé
                  if (pathPoints.value.isNotEmpty()) {
                    Polyline(
                        points = pathPoints.value,
                        color = Color.Blue, // Couleur du tracé
                        width = 16f // Épaisseur du tracé
                        )
                  }
                }
          }

          Spacer(modifier = Modifier.weight(0.5f))

          Button(onClick = onStartClick, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(text = "Start")
          }

          Spacer(Modifier.height(96.dp))
        }
  }
}
