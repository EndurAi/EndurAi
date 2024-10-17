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

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>>
        get() = _videos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?>
        get() = _error.asStateFlow()

    private val selectedVideo_ = MutableStateFlow<Video?>(null)
    open val selectedVideo: StateFlow<Video?> = selectedVideo_.asStateFlow()

    /**
     * Load videos from the repository.
     */
    suspend fun loadVideos() {
        withContext(Dispatchers.IO) {
            videoRepository.getVideos(
                { videoList -> _videos.update { videoList } },
                { exception -> _error.update { "Failed to load videos: ${exception.message}" } }
            )
        }
    }

    /**
     * Select a video.
     *
     * @param video The video to select.
     */

    fun selectVideo(video: Video) {
        selectedVideo_.value = video
    }


    /**
     * Factory for creating instances of [VideoViewModel].
     *
     * This factory method is used to create a new instance of [VideoViewModel] with a
     * [VideoRepositoryStorage] as the repository.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val videoRepository = VideoRepositoryStorage()
                return VideoViewModel(videoRepository) as T
            }
        }
    }
}

