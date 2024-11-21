package com.android.sample.ui.composables

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.junit.Rule
import org.junit.Test

class DualVideoPlayerTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displaysBothVideosWhenFileAndUrlAreValid() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val file = File(context.cacheDir, "test_video.mp4")
    val url = "http://example.com/test_video.mp4"

    composeTestRule.setContent { DualVideoPlayer(file, url, context) }

    composeTestRule.onNodeWithTag("VideoFromFile").assertIsDisplayed()
    composeTestRule.onNodeWithTag("VideoFromUrl").assertIsDisplayed()
  }
}
