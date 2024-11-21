package com.android.sample.model.location

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

//@OptIn(ExperimentalCoroutinesApi::class)
//class LocationServiceTest {
//
//  private val testDispatcher = UnconfinedTestDispatcher()
//
//  private lateinit var context: Context
//  private lateinit var notificationManager: NotificationManager
//  private lateinit var notificationBuilder: NotificationCompat.Builder
//  private lateinit var locationClient: LocationClient
//  private lateinit var locationService: LocationService
//
//  @Before
//  fun setUp() {
//    Dispatchers.setMain(testDispatcher)
//
//    // Mock dependencies
//    context = mock(Context::class.java)
//    notificationManager = mock(NotificationManager::class.java)
//    notificationBuilder = mock(NotificationCompat.Builder::class.java)
//    locationClient = mock(LocationClient::class.java)
//
//    // Create instance of the service
//    locationService = LocationService(
//      intervalOfUpdate = 5000L,
//      serviceScope = TestScope(testDispatcher)
//    )
//
//    `when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)
//    `when`(notificationBuilder.setContentTitle(anyString())).thenReturn(notificationBuilder)
//    `when`(notificationBuilder.setContentText(anyString())).thenReturn(notificationBuilder)
//    `when`(notificationBuilder.setSmallIcon(anyInt())).thenReturn(notificationBuilder)
//    `when`(notificationBuilder.setOngoing(true)).thenReturn(notificationBuilder)
//  }
//
//  @After
//  fun tearDown() {
//    Dispatchers.resetMain()
//  }
//
//  @Test
//  fun `test service starts tracking on ACTION_START`() = runTest {
//
//    // Mock location updates
//    val mockLocation = mock(android.location.Location::class.java)
//    val latLng = LatLng(10.0, 20.0)
//    `when`(mockLocation.latitude).thenReturn(latLng.latitude)
//    `when`(mockLocation.longitude).thenReturn(latLng.longitude)
//
//    `when`(locationClient.getLocationUpdates(anyLong())).thenReturn(flowOf(mockLocation))
//
//    // Simulate sending ACTION_START intent
//    val intent = Intent(context, LocationService::class.java).apply {
//      action = LocationService.ACTION_START
//    }
//
//    locationService.onStartCommand(intent, 0, 0)
//
//    // Verify location updates and notifications
//    verify(locationClient).getLocationUpdates(5000L)
//    verify(notificationManager).notify(eq(1), any())
//  }
//
//  @Test
//  fun `test service stops tracking on ACTION_STOP`() = runTest {
//    // Simulate sending ACTION_STOP intent
//    val intent = Intent(context, LocationService::class.java).apply {
//      action = LocationService.ACTION_STOP
//    }
//
//    locationService.onStartCommand(intent, 0, 0)
//
//    // Verify service stops
//    verify(locationClient, never()).getLocationUpdates(anyLong())
//    verify(locationService).stopForeground(1)
//    verify(locationService).stopSelf()
//  }
//}
