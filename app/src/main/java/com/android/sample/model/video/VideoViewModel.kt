// portions of this code were done with the help of ChatGPT

package com.android.sample.model.video

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VideoViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    private val _videoUrls = MutableLiveData<List<String>>()
    val videoUrls: LiveData<List<String>> get() = _videoUrls

    private val _uploadSuccess = MutableLiveData<String>()
    val uploadSuccess: LiveData<String> get() = _uploadSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Calls repository to upload video and posts success or error
    fun uploadVideo(videoUri: Uri) {
        videoRepository.uploadVideo(videoUri, { downloadUrl ->
            _uploadSuccess.postValue(downloadUrl)
        }, { exception ->
            _error.postValue("Upload failed: ${exception.message}")
        })
    }

    // Calls repository to fetch all video URLs and posts success or error
    fun loadVideos() {
        videoRepository.getVideoUrls({ urls ->
            _videoUrls.postValue(urls)
        }, { exception ->
            _error.postValue("Failed to load videos: ${exception.message}")
        })
    }

    // Companion object factory
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Here, initialize the VideoRepositoryStorage, or inject another one
                val videoRepository = VideoRepositoryStorage()
                return VideoViewModel(videoRepository) as T
            }
        }
    }
}
