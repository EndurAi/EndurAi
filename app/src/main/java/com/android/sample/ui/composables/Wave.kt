package com.android.sample.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag

/**
 * Composable function to display a wavy background.
 *
 * @param color Color of the wavy background.
 */
@Composable
fun WavyBackground(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize().testTag("wavyBackground")) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height * 0.3f)
            quadraticBezierTo(width * 0.25f, height * 0.4f, width * 0.5f, height * 0.3f)
            quadraticBezierTo(width * 0.75f, height * 0.2f, width, height * 0.3f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(path = path, color = color)
    }
}