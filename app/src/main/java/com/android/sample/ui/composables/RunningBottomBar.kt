package com.android.sample.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.White

@Composable
fun CircularButton(onClick: () -> Unit, testTag : String) {

  Box(
      contentAlignment = Alignment.Center,
      modifier =
      Modifier
          .clickable(onClick = onClick)
          .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
          .size(80.dp)
          .clip(CircleShape)
          .background(brush = BlueGradient)
          .testTag(testTag)) {
        Box(
            modifier =
            Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(White))
      }
}

@Composable
fun RunningBottomBar(resumeOnClick: () -> Unit, finishOnClick: () -> Unit, pauseOnClick: () -> Unit, isSplitState : Boolean, PauseTestTag: String, FinishTestTag: String, ResumeTestTag: String) {
    val transition = remember { Animatable(-40f) }
    var isSplit by remember { mutableStateOf(false) }


    LaunchedEffect(isSplit) {
        if (isSplit) {
            transition.animateTo(
                targetValue = 12f,
                animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
            )
        } else {
            transition.snapTo(-40f)
        }
    }

    Row(
        modifier = Modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (!isSplitState ) {
            isSplit = false

            CircularButton(
                onClick = {pauseOnClick(); isSplit = true},
                testTag = PauseTestTag
            )
        } else {
            isSplit = true

            Box(
                modifier = Modifier
                    .offset(x = (-transition.value).dp)
            ) {
                RunningDesignButtonGrey(
                    onClick = {resumeOnClick(); isSplit = false},
                    title = "Resume",
                    testTag = ResumeTestTag
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = transition.value.dp)
            ) {
                RunningDesignButton(
                    onClick = finishOnClick,
                    title = "Finish",
                    testTag = FinishTestTag
                )
            }
        }
    }
}

@Composable
fun RunningBottomBarControl(locationOnClick: () -> Unit, resumeOnClick: () -> Unit, finishOnClick: () -> Unit, pauseOnClick: () -> Unit, isSplit: Boolean, isSelected: Boolean, PauseTestTag: String, FinishTestTag: String, ResumeTestTag: String, LocationTestTag: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        FakeLocationButton()

        // Pause Button (Center)
        RunningBottomBar(resumeOnClick, finishOnClick, pauseOnClick, isSplitState = isSplit, PauseTestTag = PauseTestTag, ResumeTestTag = ResumeTestTag, FinishTestTag = FinishTestTag)

        // Location Button (Right)
        LocationButton(isSelected = isSelected, onClick = locationOnClick, LocationTestTag = LocationTestTag)
    }

}


