package com.android.sample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.sample.model.video.VideoViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {

    private lateinit var videoViewModel: VideoViewModel
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)

        // Initialize ViewModel using the companion object factory
        videoViewModel = ViewModelProvider(this, VideoViewModel.Factory).get(VideoViewModel::class.java)

        // Observe the video URLs
        videoViewModel.videoUrls.observe(this) { videoUrls ->
            if (videoUrls.isNotEmpty()) {
                playVideo(videoUrls[0])  // Play the first (and only) video URL
            }
        }

        // Observe upload success
        videoViewModel.uploadSuccess.observe(this) { downloadUrl ->
            Toast.makeText(this, "Upload successful: $downloadUrl", Toast.LENGTH_SHORT).show()
        }

        // Observe error messages
        videoViewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Load the single video URL
        videoViewModel.loadVideos()
    }

    private fun playVideo(url: String) {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)

        // Prepare and play the video
        player.prepare()
        player.playWhenReady = true
    }
}
