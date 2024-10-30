package com.android.sample.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

/**
 * A composable function that displays an image using a resource ID.
 *
 * @param painter The resource ID of the drawable to be displayed.
 * @param contentDescription A description of the image for accessibility purposes.
 * @param modifier A modifier to adjust the appearance or layout of the image.
 */
@Composable
fun ImageComposable(
    @DrawableRes painter: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
  Image(
      painter = painterResource(id = painter),
      contentDescription = contentDescription,
      modifier = modifier)
}
