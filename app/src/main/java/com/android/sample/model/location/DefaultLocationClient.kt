package com.android.sample.model.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * Implementation of the [LocationClient] interface that provides location updates using Google's
 * [FusedLocationProviderClient].
 *
 * @property context The [Context] used to check location permissions and access system services.
 * @property client The [FusedLocationProviderClient] used to request location updates.
 */
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

  @SuppressLint("MissingPermission")
  override fun getLocationUpdates(interval: Long): Flow<Location> {
    return callbackFlow {
      if (!context.hasLocationPermission()) {
        throw LocationClient.LocationException("Missing location permission")
      }

      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
      val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

      if (!isGPSEnabled && !isNetworkEnabled) {
        throw LocationClient.LocationException("GPS is disabled")
      }

      val request =
          LocationRequest.Builder(interval).apply { setMinUpdateIntervalMillis(interval) }.build()

      val locationCallback =
          object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
              super.onLocationResult(result)
              result.locations.lastOrNull()?.let { location -> launch { send(location) } }
            }
          }

      client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

      awaitClose { client.removeLocationUpdates(locationCallback) }
    }
  }
}
