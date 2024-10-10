package com.android.sample.model.video

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class VideoRepositoryStorageTest {

  private lateinit var videoRepository: VideoRepositoryStorage
  private lateinit var mockStorageReference: StorageReference
  private lateinit var mockListResult: ListResult

  @Before
  fun setup() {
    mockStorageReference = Mockito.mock(StorageReference::class.java)
    mockListResult = Mockito.mock(ListResult::class.java)
    videoRepository = VideoRepositoryStorage(mockStorageReference)
  }

  @Test
  fun getVideoUrls_shouldReturnAllVideoUrls() {
    // Arrange
    val mockUriTask1: Task<Uri> = Tasks.forResult(Uri.parse("https://mock-video-url1.com"))
    val mockUriTask2: Task<Uri> = Tasks.forResult(Uri.parse("https://mock-video-url2.com"))
    val mockVideoRef1: StorageReference = Mockito.mock(StorageReference::class.java)
    val mockVideoRef2: StorageReference = Mockito.mock(StorageReference::class.java)
    Mockito.`when`(mockStorageReference.child("template_videos")).thenReturn(mockStorageReference)
    Mockito.`when`(mockStorageReference.listAll()).thenReturn(Tasks.forResult(mockListResult))
    Mockito.`when`(mockListResult.items).thenReturn(listOf(mockVideoRef1, mockVideoRef2))
    Mockito.`when`(mockVideoRef1.downloadUrl).thenReturn(mockUriTask1)
    Mockito.`when`(mockVideoRef2.downloadUrl).thenReturn(mockUriTask2)

    // Act & Assert
    videoRepository.getVideoUrls(
        { urls ->
          assert(urls.size == 2)
          assert(urls.contains("https://mock-video-url1.com"))
          assert(urls.contains("https://mock-video-url2.com"))
        },
        { assert(false) { "Should not call onFailure" } })
  }
}
