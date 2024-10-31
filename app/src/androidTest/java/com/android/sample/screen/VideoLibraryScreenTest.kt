// portions of this code were developed with the help of chatgpt

package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoRepository
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.video.VideoLibraryScreen
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class VideoLibraryScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock VideoRepository, VideoViewModel, and NavigationActions
  private val mockVideoRepository = mock(VideoRepository::class.java)
  private val mockVideoViewModel = VideoViewModel(mockVideoRepository)
  private val mockNavigationActions = mock(NavigationActions::class.java)

  @Test
  fun videoLibraryScreenDisplaysTitleAndVideos() = runTest {
    // Initialize Mockito
    MockitoAnnotations.openMocks(this)

    // Sample video data
    val sampleVideos =
        listOf(
            Video("Sample Video 1", "url1", "Body-Weight", "thumb1", "120", "desc1"),
            Video("Sample Video 2", "url2", "Yoga", "thumb2", "180", "desc2"))

    // Mock the videos flow
    whenever(mockVideoRepository.getVideos(anyOrNull(), anyOrNull())).thenAnswer {
      val onSuccess = it.getArgument<Function1<List<Video>, Unit>>(0)
      onSuccess(sampleVideos)
    }

    // Launch the composable in the test
    composeTestRule.setContent {
      VideoLibraryScreen(
          navigationActions = mockNavigationActions, videoViewModel = mockVideoViewModel)
    }

    // Verify that the title is displayed
    composeTestRule.onNodeWithTag("libraryTitle").assertIsDisplayed()

    // Verify that the search bar is displayed
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()

    // Verify that the tag dropdown is displayed
    composeTestRule.onNodeWithTag("tagDropdown").assertIsDisplayed()

    // Verify that the sample video items are displayed
    composeTestRule.onNodeWithTag("videoItemSample Video 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("videoItemSample Video 2").assertIsDisplayed()
  }
}
