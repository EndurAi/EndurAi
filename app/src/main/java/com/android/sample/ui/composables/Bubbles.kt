package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import com.android.sample.ui.theme.MediumBlue


// Define constants for dimensions
private val LargeCircleSize = 330.dp
private val LargeCircleOffsetX = 250.dp
private val LargeCircleOffsetY = 550.dp
private val SmallCircleSize = 200.dp
private val SmallCircleOffsetX = 120.dp
private val SmallCircleOffsetY = 700.dp

@Composable
fun Bubbles() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Large Circle
        Box(
            modifier = Modifier
                .size(LargeCircleSize)
                .offset(x = LargeCircleOffsetX, y = LargeCircleOffsetY)
                .background(
                    color = MediumBlue.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(LargeCircleSize / 2)
                )
        )
        // Smaller Circle
        Box(
            modifier = Modifier
                .size(SmallCircleSize)
                .offset(x = SmallCircleOffsetX, y = SmallCircleOffsetY)
                .background(
                    color = MediumBlue.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(SmallCircleSize / 2)
                )
        )
    }
}