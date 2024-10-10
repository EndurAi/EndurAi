package com.android.sample.model.video


import android.net.Uri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.anyOrNull


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
    fun `uploadVideo should update uploadSuccess on success`() = runTest {
        // Arrange
        val videoUri = mock(Uri::class.java)
        val downloadUrl = "http://example.com/video.mp4"
        doAnswer {
            val successCallback = it.getArgument<(String) -> Unit>(1)
            successCallback(downloadUrl)
            null
        }.`when`(videoRepository).uploadVideo(anyOrNull(), anyOrNull(), anyOrNull())

        // Act
        videoViewModel.uploadVideo(videoUri)

        // Assert
        assertEquals(downloadUrl, videoViewModel.uploadSuccess.first())
        assertNull(videoViewModel.error.first())
    }

    @Test
    fun `uploadVideo should update error on failure`() = runTest {
        // Arrange
        val videoUri = mock(Uri::class.java)
        val exception = Exception("Upload failed")
        doAnswer {
            val failureCallback = it.getArgument<(Exception) -> Unit>(2)
            failureCallback(exception)
            null
        }.`when`(videoRepository).uploadVideo(anyOrNull(), anyOrNull(), anyOrNull())

        // Act
        videoViewModel.uploadVideo(videoUri)

        // Assert
        assertEquals("Upload failed: ${exception.message}", videoViewModel.error.first())
        assertNull(videoViewModel.uploadSuccess.first())
    }

    @Test
    fun `loadVideos should update videoUrls on success`() = runTest {
        // Arrange
        val urls = listOf("http://example.com/video1.mp4", "http://example.com/video2.mp4")
        doAnswer {
            val successCallback = it.getArgument<(List<String>) -> Unit>(0)
            successCallback(urls)
            null
        }.`when`(videoRepository).getVideoUrls(any(), any())

        // Act
        videoViewModel.loadVideos()

        // Assert
        assertEquals(urls, videoViewModel.videoUrls.first())
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
        }.`when`(videoRepository).getVideoUrls(any(), any())

        // Act
        videoViewModel.loadVideos()

        // Assert
        assertEquals("Failed to load videos: ${exception.message}", videoViewModel.error.first())
        assertTrue(videoViewModel.videoUrls.first().isEmpty())
    }
}
