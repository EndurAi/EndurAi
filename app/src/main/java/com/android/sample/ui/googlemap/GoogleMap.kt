package com.android.sample.ui.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleMap() {
    val context = LocalContext.current

    val locationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> /* handle result */ }
    )


    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


    val pathPoints = remember { mutableStateListOf<LatLng>() }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 15f) // San Francisco par défaut
    }


    LaunchedEffect(Unit) {
        locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val newPoint = LatLng(location.latitude, location.longitude)
                    pathPoints.add(newPoint)


                    cameraPositionState.position = CameraPosition(newPoint, 15f, 0f, 0f)
                }
            }
        }
    }


    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suivi GPS et Tracé") },
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = cameraPositionState
                ) {

                    Polyline(
                        points = pathPoints,
                        color = Color.Red,
                        width = 5f
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { startLocationUpdates() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("Démarrer le suivi", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { stopLocationUpdates() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Arrêter le suivi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}
