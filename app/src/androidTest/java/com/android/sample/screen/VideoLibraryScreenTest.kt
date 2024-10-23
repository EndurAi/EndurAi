package com.android.sample.ui.video

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class VideoLibraryScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock VideoViewModel and NavigationActions
  private val mockVideoViewModel = mock(VideoViewModel::class.java)
  private val mockNavigationActions = mock(NavigationActions::class.java)

  @Test
  fun videoLibraryScreen_displaysTitleAndVideos() {
    // Initialize Mockito
    MockitoAnnotations.openMocks(this)

    // Sample video data
    val sampleVideos =
        listOf(
            Video("Sample Video 1", "url1", "Body-Weight", "thumb1", "120", "desc1"),
            Video("Sample Video 2", "url2", "Yoga", "thumb2", "180", "desc2"))

    // Mock the videos flow
    `when`(mockVideoViewModel.videos).thenReturn(MutableStateFlow(sampleVideos))

    // Launch the composable in the test
    composeTestRule.setContent {
      VideoLibraryScreen(
          navigationActions = mockNavigationActions, videoViewModel = mockVideoViewModel)
    }

    // Verify that the title is displayed
    composeTestRule.onNodeWithTag("library_title").assertIsDisplayed()

    // Verify that the search bar is displayed
    composeTestRule.onNodeWithTag("search_bar").assertIsDisplayed()

    // Verify that the tag dropdown is displayed
    composeTestRule.onNodeWithTag("tag_dropdown").assertIsDisplayed()

    // Verify that the sample video items are displayed
    composeTestRule.onNodeWithTag("video_item_Sample Video 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("video_item_Sample Video 2").assertIsDisplayed()
  }
}
