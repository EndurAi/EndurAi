// portions of this code were developed with the help of chatgpt

package com.android.sample.screen

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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

    // Verify that the search bar is displayed
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBarSurface").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBarBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("searchField")
        .assertIsDisplayed()
        .assertIsNotFocused()
        .assertHasClickAction()
        .performClick()
        .assertIsFocused()
    composeTestRule.onNodeWithTag("tagDropdown").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tagDropdownButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()
    composeTestRule.onNodeWithTag("dropdownMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("dropdownMenuItem_All").assertIsDisplayed().assertHasClickAction()
    composeTestRule
        .onNodeWithTag("dropdownMenuItem_Body-Weight")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("dropdownMenuItem_Warmup")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("dropdownMenuItem_Yoga")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("videoItem_Sample Video 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("videoItem_Sample Video 2").assertIsDisplayed()
  }
}
