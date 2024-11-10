package com.android.sample.ui.composables

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.io.File

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun DualVideoPlayer(file: File, url: String, context: Context) {
  Column(modifier = Modifier.fillMaxSize()) {
    // Video from File
    AndroidView(
        modifier = Modifier.weight(1f).fillMaxWidth(), // Each video takes half the screen height
        factory = {
          PlayerView(context).apply {
            player =
                ExoPlayer.Builder(context).build().apply {
                  val mediaItem = MediaItem.fromUri(file.toUri())
                  setMediaItem(mediaItem)
                  prepare()
                  playWhenReady = true
                }
            useController = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
          }
        })

    // Video from URL
    AndroidView(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        factory = {
          PlayerView(context).apply {
            player =
                ExoPlayer.Builder(context).build().apply {
                  val mediaItem = MediaItem.fromUri(url)
                  setMediaItem(mediaItem)
                  prepare()
                  playWhenReady = true
                }
            useController = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
          }
        })
  }
  // You might need to add DisposableEffect for each ExoPlayer to release them properly
}
