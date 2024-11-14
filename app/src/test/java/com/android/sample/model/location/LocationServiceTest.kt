package com.android.sample.model.location

import android.app.NotificationManager
import android.content.Intent
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class LocationServiceTest {

  private lateinit var service: LocationService
  private lateinit var locationClient1: LocationClient
  private lateinit var notificationManager: NotificationManager
  private val dispatcher = TestCoroutineDispatcher()

  @Before
  fun setUp() {
    // Mock du LocationClient et du NotificationManager
    locationClient1 = mock(LocationClient::class.java)
    notificationManager = mock(NotificationManager::class.java)

    // Création du service avec des dépendances injectées pour les tests
    service = LocationService().apply { locationClient = locationClient1 }
  }

  @Test
  fun start_should_begin_location_updates_and_update_notification() =
      dispatcher.runBlockingTest {
        // Mock des données de localisation
        val mockLocation = mock(Location::class.java)
        val loc = LatLng(46.27432, 6.34173)

        `when`(mockLocation.latitude).thenReturn(loc.latitude)
        `when`(mockLocation.longitude).thenReturn(loc.longitude)

        // Simulation des updates de localisation
        `when`(locationClient1.getLocationUpdates(anyLong())).thenReturn(flowOf(mockLocation))

        // Initialisation du service avec ACTION_START
        val intent = Intent().apply { action = LocationService.ACTION_START }
        service.onStartCommand(intent, 0, 0)

        // Vérification des appels
        verify(locationClient1).getLocationUpdates(anyLong())
        // verify(notificationManager).notify(eq(1), any(NotificationCompat.Builder::class.java))

        // Assure-toi que le flux de `userLocation` et `pathPoints` est mis à jour
        assertEquals(mockLocation.latitude, LocationService.userLocation.value?.latitude ?: 0)
        assertEquals(mockLocation.longitude, LocationService.userLocation.value?.longitude ?: 0)
        // assertTrue(LocationService.pathPoints.value.contains(mockLocation))
      }

  @Test
  fun `stop should cancel foreground service`() {
    // Lancer l'appel à stop
    // service.stop()

    // Vérification que le service se termine correctement
    //  verify(service).stopForeground(LocationService.STOP_FOREGROUND_REMOVE)
  }
}
