package com.android.sample.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.White

@Composable
fun ToggleButtonAchievements(modifier: Modifier = Modifier, onClick: () -> Unit) {
    var selected by remember { mutableStateOf(true) }

    val offsetX by animateFloatAsState(targetValue = if (selected) 0f else 1f)

    Box(
        modifier =
        Modifier.height(40.dp)
            .width(250.dp)
            .padding(horizontal = 16.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(30.dp), clip = false)
            .background(color = White, shape = RoundedCornerShape(30.dp))
            .testTag("BottomBar"),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
            modifier
                .width(200.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(25.dp))
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(30.dp), clip = false)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.CenterStart) {
            // Animated circle for selection
            Box(
                modifier =
                Modifier.offset(x = 100.dp * offsetX) // Moves between 0 and 100 dp
                    .fillMaxHeight()
                    .width(100.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(30.dp), clip = false)
                    .background(BlueGradient))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                // Stats Text
                Box(
                    modifier =
                    Modifier.fillMaxHeight()
                        .testTag("StatsButton")
                        .width(100.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .clickable {
                            if (!selected) {
                                onClick()
                            }
                            selected = true
                        },
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.stats),
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) Color.White else Color(0xFF1E50A0))
                }

                // History Text
                Box(
                    modifier =
                    Modifier.fillMaxHeight()
                        .testTag("HistoryButton")
                        .width(100.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .clickable {
                            if (selected) {
                                onClick()
                            }
                            selected = false
                        },
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.history),
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!selected) Color.White else Color(0xFF1E50A0))
                }
            }
        }
    }
}