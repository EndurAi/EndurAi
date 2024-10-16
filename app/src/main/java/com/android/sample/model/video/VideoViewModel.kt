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

    // Loads videos and updates the state
    suspend fun loadVideos() {
        withContext(Dispatchers.IO) {
            videoRepository.getVideos(
                { videoList -> _videos.update { videoList } },
                { exception -> _error.update { "Failed to load videos: ${exception.message}" } }
            )
        }
    }

    // Filter videos by tag
    fun filterVideosByTag(tag: String) {
        _videos.update { videos ->
            if (tag == "All") videos else videos.filter { it.tag == tag }
        }
    }

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

