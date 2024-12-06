package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.theme.Black
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.BlueLocation
import com.android.sample.ui.theme.ContrailOne
import com.android.sample.ui.theme.NeutralGrey
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.White
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline

@Composable
fun DistanceDisplay(distance: Double) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally, // Align "Distance" to the start
      modifier = Modifier.padding(16.dp)) {
        Text(
            "DISTANCE",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = ContrailOne,
            color = NeutralGrey,
            fontSize = 18.sp,
            modifier = Modifier.padding(4.dp))
        Box(
            modifier =
                Modifier.clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 5.dp, horizontal = 16.dp)) {
              Text(
                  text = String.format("%.2f km", distance),
                  style = MaterialTheme.typography.displayMedium,
                  fontFamily = ContrailOne,
                  fontSize = 60.sp,
                  color = Black,
                  modifier = Modifier.align(Alignment.Center))
            }
      }
}

@Composable
fun ChronoDisplay(elapsedTime: Long) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally, // Align "Time" to the start
      modifier = Modifier.padding(16.dp)) {
        Text(
            "TIME",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = ContrailOne,
            color = NeutralGrey,
            fontSize = 18.sp,
            modifier = Modifier.padding(4.dp))
        Box(
            modifier =
                Modifier.clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 5.dp, horizontal = 16.dp)) {
              Text(
                  text = formatElapsedTime(elapsedTime),
                  style = MaterialTheme.typography.displayMedium,
                  fontFamily = ContrailOne,
                  fontSize = 60.sp,
                  color = Black,
                  modifier = Modifier.align(Alignment.Center))
            }
      }
}

@Composable
fun PaceDisplay(paceString: String) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally, // Align "Pace" to the start
      modifier = Modifier.padding(16.dp)) {
        Text(
            "PACE",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = ContrailOne,
            color = NeutralGrey,
            fontSize = 18.sp,
            modifier = Modifier.padding(4.dp))
        Box(
            modifier =
                Modifier.clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 5.dp, horizontal = 16.dp)) {
              Text(
                  text = paceString,
                  style = MaterialTheme.typography.displayMedium,
                  fontFamily = ContrailOne,
                  fontSize = 60.sp,
                  color = Black,
                  modifier = Modifier.align(Alignment.Center))
            }
      }
}

@Composable
fun PathDisplay(modifier: Modifier = Modifier, pathPoints: List<LatLng>) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
    Text(
        "PATH",
        style = MaterialTheme.typography.bodySmall,
        fontFamily = ContrailOne,
        color = NeutralGrey,
        fontSize = 24.sp,
        modifier = Modifier.padding(10.dp))

    Box(modifier = Modifier.height(250.dp).width(600.dp)) {
      GoogleMap(
          modifier = Modifier.fillMaxSize(),
          cameraPositionState = cameraStateCalculator(pathPoints)) {
            // Display the polyline for the running path
            if (pathPoints.isNotEmpty()) {
              Polyline(points = pathPoints, color = Color.Blue, width = 16f)
            }
          }
    }
  }
}

fun cameraStateCalculator(pathPoints: List<LatLng>): CameraPositionState {
  if (pathPoints.isNotEmpty()) {

    val boundsBuilder = LatLngBounds.Builder()

    for (point in pathPoints) {
      boundsBuilder.include(point)
    }

    val bounds = boundsBuilder.build()

    val padding = 100
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
    val newCam = CameraPositionState()
    newCam.move(cameraUpdate)

    return newCam
  }
  return CameraPositionState()
}

fun formatElapsedTime(elapsedTime: Long): String {
  val hours = elapsedTime / 3600
  val minutes = (elapsedTime % 3600) / 60
  val seconds = elapsedTime % 60
  return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun RunningStatsScreen(
    elapsedTime: Long,
    paceString: String,
    distance: Double,
    locationOnClick: () -> Unit = {},
    finishOnClick: () -> Unit = {},
    resumeOnClick: () -> Unit = {},
    pauseOnClick: () -> Unit = {},
    isSplit: Boolean,
    PauseTestTag: String,
    FinishTestTag: String,
    ResumeTestTag: String,
    LocationTestTag: String
) {

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(16.dp)
              .verticalScroll(rememberScrollState())
              .testTag("StatsScreen"),
      verticalArrangement = Arrangement.spacedBy(25.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Section "Time"
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
          "TIME",
          style = MaterialTheme.typography.bodySmall,
          fontFamily = ContrailOne,
          color = NeutralGrey,
          fontSize = 18.sp)
      Spacer(modifier = Modifier.height(8.dp))
      Text(
          text = formatElapsedTime(elapsedTime),
          style = MaterialTheme.typography.displayMedium,
          fontFamily = ContrailOne,
          fontSize = 90.sp,
          modifier = Modifier.testTag("TimeValueText"))
      Divider()
    }

    // Section "Avg Pace"
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
          "AVG PACE",
          style = MaterialTheme.typography.bodySmall,
          fontFamily = ContrailOne,
          color = NeutralGrey,
          fontSize = 18.sp)
      Spacer(modifier = Modifier.height(8.dp))
      Text(
          text = paceString,
          style = MaterialTheme.typography.displayLarge,
          fontFamily = ContrailOne,
          fontSize = if (paceString.length == 4) 180.sp else 135.sp,
          modifier = Modifier.testTag("PaceValueText"))
      Text(
          "/KM",
          style = MaterialTheme.typography.bodySmall,
          fontFamily = ContrailOne,
          color = NeutralGrey,
          fontSize = 18.sp)
      Spacer(modifier = Modifier.height(18.dp))
      Divider()
    }

    // Section "Graph and Distance"
    Row(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          // Graph Placeholder
          Column(
              horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                // Simulate the graph as rectangles
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()) {
                      // Fake bars
                      repeat(3) {
                        Box(
                            modifier =
                                Modifier.size(24.dp, (50 + it * 10).dp).background(BlueGradient))
                      }
                    }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()) {
                      // Fake labels
                      Text("5:02", style = MaterialTheme.typography.bodySmall)
                      Text("6:14", style = MaterialTheme.typography.bodySmall)
                      Text("5:30", style = MaterialTheme.typography.bodySmall)
                    }
              }

          Box(
              modifier =
                  Modifier.fillMaxHeight()
                      .width(1.dp)
                      .background(DividerDefaults.color)
                      .padding(horizontal = 8.dp))

          // Distance
          Column(
              horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(
                    "DISTANCE",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = ContrailOne,
                    color = NeutralGrey,
                    fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format("%.2f", distance),
                    style = MaterialTheme.typography.displayMedium,
                    fontFamily = ContrailOne,
                    fontSize = 75.sp,
                    modifier = Modifier.testTag("DistanceValueText"))
                Text(
                    "KILOMETERS",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = ContrailOne,
                    color = NeutralGrey,
                    fontSize = 18.sp)
              }
        }

    Spacer(modifier = Modifier.height(if (paceString.length == 4) 3.dp else 60.dp))

    // Bottom Controls
    RunningBottomBarControl(
        locationOnClick = locationOnClick,
        finishOnClick = finishOnClick,
        resumeOnClick = resumeOnClick,
        pauseOnClick = pauseOnClick,
        isSplit = isSplit,
        isSelected = false,
        PauseTestTag = PauseTestTag,
        FinishTestTag = FinishTestTag,
        ResumeTestTag = ResumeTestTag,
        LocationTestTag = LocationTestTag)
  }
}

@Composable
fun LocationButton(isSelected: Boolean, onClick: () -> Unit, LocationTestTag: String) {
  Box(
      modifier =
          Modifier.shadow(elevation = 5.dp, shape = CircleShape, clip = false)
              .background(color = if (isSelected) BlueLocation else White, shape = CircleShape)
              .clip(CircleShape)
              .size(35.dp)
              .clickable(onClick = onClick)
              .testTag(LocationTestTag),
      contentAlignment = Alignment.Center) {
        Icon(
            modifier =
                Modifier.size(25.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(BlueGradient, blendMode = BlendMode.SrcAtop)
                  }
                },
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
        )
      }
}

@Composable
fun FakeLocationButton() {
  Box(
      modifier =
          Modifier.background(color = Transparent, shape = CircleShape)
              .clip(CircleShape)
              .size(35.dp),
      contentAlignment = Alignment.Center) {
        Icon(
            modifier = Modifier.size(25.dp),
            imageVector = Icons.Outlined.LocationOn,
            tint = Transparent,
            contentDescription = null,
        )
      }
}
