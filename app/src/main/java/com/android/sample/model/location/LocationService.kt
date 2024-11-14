package com.android.sample.model.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.android.sample.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    companion object {

        private val camera_ = MutableStateFlow<CameraPositionState>(CameraPositionState(CameraPosition.fromLatLngZoom(LatLng(46.27432, 6.34173), 10f)))
        val camera: StateFlow<CameraPositionState> = camera_.asStateFlow()

        private val userLocation_ = MutableStateFlow<LatLng?>(null)
        val userLocation: StateFlow<LatLng?> = userLocation_.asStateFlow()

        private val pathPoints_ = MutableStateFlow<MutableList<LatLng>>(mutableListOf())
        val pathPoints: StateFlow<MutableList<LatLng>> = pathPoints_.asStateFlow()

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private val intervalOfUpdate = 5000L // 5 seconds

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var locationClient: LocationClient


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        if(!hasNotificationPermission()) {
            throw Exception("Missing Notification Permission")
        }


        val notification = NotificationCompat.Builder(this,"location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(intervalOfUpdate)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val loc = LatLng(location.latitude,location.longitude)

                userLocation_.value = loc

                pathPoints_.value = pathPoints_.value.toMutableList().apply { add(loc) }

                camera_.value = CameraPositionState(position = CameraPosition.fromLatLngZoom(loc,18f))

                val updatedNotification = notification.setContentText("Location: ($lat, $long)")
                notificationManager.notify(1,updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1,notification.build())

    }

    private fun stop(){
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}