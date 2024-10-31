package com.android.sample.model.video

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoTest {

  @Test
  fun testVideoDefaultValues() {
    val video = Video()

    assertEquals("", video.title)
    assertEquals("", video.url)
    assertEquals("All", video.tag)
    assertEquals("", video.thumbnailUrl)
    assertEquals("", video.duration)
    assertEquals("", video.description)
    assertEquals(false, video.isLiked)
    assertEquals(false, video.isSaved)
    assertEquals(false, video.isViewed)
    assertEquals(false, video.isDownloaded)
    assertEquals(false, video.isShared)
    assertEquals(false, video.isReported)
    assertEquals(false, video.isSubscribed)
    assertEquals(false, video.isPremium)
  }

  @Test
  fun testVideoCustomValues() {
    val video =
        Video(
            title = "Test Title",
            url = "http://test.url",
            tag = "Test Tag",
            thumbnailUrl = "http://test.thumbnail.url",
            duration = "10:00",
            description = "Test Description",
            isLiked = true,
            isSaved = true,
            isViewed = true,
            isDownloaded = true,
            isShared = true,
            isReported = true,
            isSubscribed = true,
            isPremium = true)

    assertEquals("Test Title", video.title)
    assertEquals("http://test.url", video.url)
    assertEquals("Test Tag", video.tag)
    assertEquals("http://test.thumbnail.url", video.thumbnailUrl)
    assertEquals("10:00", video.duration)
    assertEquals("Test Description", video.description)
    assertEquals(true, video.isLiked)
    assertEquals(true, video.isSaved)
    assertEquals(true, video.isViewed)
    assertEquals(true, video.isDownloaded)
    assertEquals(true, video.isShared)
    assertEquals(true, video.isReported)
    assertEquals(true, video.isSubscribed)
    assertEquals(true, video.isPremium)
  }
}
