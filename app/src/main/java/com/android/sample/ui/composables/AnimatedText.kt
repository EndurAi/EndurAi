package com.android.sample.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * A composable function that displays text with a typewriter animation effect.
 *
 * @param modifier The modifier to be applied to the Text composable.
 * @param text The full text to be displayed with the animation.
 * @param style The style to be applied to the text. Default is a font size of 16.sp.
 */
@Composable
fun AnimatedText(modifier: Modifier = Modifier, text: String, style: TextStyle = TextStyle(fontSize = 16.sp)) {
    var displayedText by remember { mutableStateOf("") }
    val textLength = text.length

    LaunchedEffect(text) {
        for (i in 1..textLength) {
            displayedText = text.substring(0, i)
            delay(50) // Adjust the delay to control the speed of the animation
        }
    }

    Text(text = displayedText, style = style, modifier = modifier)
}