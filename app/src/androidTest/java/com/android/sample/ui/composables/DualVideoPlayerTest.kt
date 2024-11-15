package com.android.sample.ui.composables

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.media3.common.util.UnstableApi
import androidx.test.core.app.ApplicationProvider
import com.android.sample.ui.composables.DualVideoPlayer
import org.junit.Rule
import org.junit.Test
import java.io.File

class DualVideoPlayerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysBothVideosWhenFileAndUrlAreValid() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val file = File(context.cacheDir, "test_video.mp4")
        val url = "http://example.com/test_video.mp4"

        composeTestRule.setContent {
            DualVideoPlayer(file, url, context)
        }

        composeTestRule.onNodeWithTag("VideoFromFile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("VideoFromUrl").assertIsDisplayed()
    }

}