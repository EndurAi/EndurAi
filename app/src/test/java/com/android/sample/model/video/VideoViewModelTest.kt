package com.android.sample.model.video

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class VideoViewModelTest {

  private lateinit var videoViewModel: VideoViewModel
  private lateinit var videoRepository: VideoRepository

  @Before
  fun setup() {
    videoRepository = mock(VideoRepository::class.java)
    videoViewModel = VideoViewModel(videoRepository)
  }

  @Test
  fun `loadVideos should update videos on success`() = runTest {
    // Arrange
    val videos =
        listOf(
            Video(
                "Title 1",
                "http://example.com/video1.mp4",
                "Tag1",
                "http://example.com/thumb1.jpg",
                "120",
                "Description 1"),
            Video(
                "Title 2",
                "http://example.com/video2.mp4",
                "Tag2",
                "http://example.com/thumb2.jpg",
                "240",
                "Description 2"))
    doAnswer {
          val successCallback = it.getArgument<(List<Video>) -> Unit>(0)
          successCallback(videos)
          null
        }
        .`when`(videoRepository)
        .getVideos(any(), any())

    // Act
    videoViewModel.loadVideos()

    // Assert
    assertEquals(videos, videoViewModel.videos.first())
    assertNull(videoViewModel.error.first())
  }

  @Test
  fun `loadVideos should update error on failure`() = runTest {
    // Arrange
    val exception = Exception("Failed to load videos")
    doAnswer {
          val failureCallback = it.getArgument<(Exception) -> Unit>(1)
          failureCallback(exception)
          null
        }
        .`when`(videoRepository)
        .getVideos(any(), any())

    // Act
    videoViewModel.loadVideos()

    // Assert
    assertEquals("Failed to load videos: ${exception.message}", videoViewModel.error.first())
    assertTrue(videoViewModel.videos.first().isEmpty())
  }

  @Test
  fun `selectVideo should update selectedVideo`() = runTest {
    // Arrange
    val selectedVideo =
        Video(
            "Title",
            "http://example.com/video.mp4",
            "Tag",
            "http://example.com/thumb.jpg",
            "120",
            "Description")

    // Act
    videoViewModel.selectVideo(selectedVideo)

    // Assert
    assertEquals(selectedVideo, videoViewModel.selectedVideo.first())
  }
}
