// portions of this code were done with the help of ChatGPT and GitHub Copilot

package com.android.sample.model.video

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing video-related operations.
 *
 * @property videoRepository The repository for video operations.
 */
class VideoViewModel(private val videoRepository: VideoRepository) : ViewModel() {

  private val _videoUrls = MutableStateFlow<List<String>>(emptyList())
  val videoUrls: StateFlow<List<String>>
    get() = _videoUrls.asStateFlow()

  private val _uploadSuccess = MutableStateFlow<String?>(null)
  val uploadSuccess: StateFlow<String?>
    get() = _uploadSuccess.asStateFlow()

  private val _error = MutableStateFlow<String?>(null)
  val error: StateFlow<String?>
    get() = _error.asStateFlow()

    /**
     * Uploads a video and updates the state.
     *
     * @param videoUri The URI of the video to upload.
     */
  fun uploadVideo(videoUri: Uri) {
    videoRepository.uploadVideo(
        videoUri,
        { downloadUrl -> _uploadSuccess.value = downloadUrl },
        { exception -> _error.value = "Upload failed: ${exception.message}" })
  }

    /**
     * Loads the video URLs and updates the state.
     */
  suspend fun loadVideos() {
    withContext(Dispatchers.IO) {
      videoRepository.getVideoUrls(
          { urls -> _videoUrls.update { urls } },
          { exception -> _error.update { "Failed to load videos: ${exception.message}" } })
    }
  }

  // Companion object factory for ViewModel
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Initialize the VideoRepositoryStorage
            val videoRepository = VideoRepositoryStorage()
            return VideoViewModel(videoRepository) as T
          }
        }
  }
}
