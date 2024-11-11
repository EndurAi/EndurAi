package com.android.sample.ui.composables

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.io.File

/**
 * A composable that displays two videos simultaneously, one from a local file and one from a URL.
 *
 * @param file The local video file to play.
 * @param url The URL of the remote video to play.
 * @param context The context of the application.
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun DualVideoPlayer(file: File, url: String, context: Context) {
  Column(modifier = Modifier.fillMaxSize()) {
    // Video from File
    val exoPlayer1 = remember { ExoPlayer.Builder(context).build() }
    AndroidView(
        modifier = Modifier.weight(1f).fillMaxWidth(), // Each video takes half the screen height
        factory = {
          PlayerView(context).apply {
            player =
                exoPlayer1.apply {
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

    DisposableEffect(exoPlayer1) { onDispose { exoPlayer1.release() } }

    // Video from URL
    val exoPlayer2 = remember { ExoPlayer.Builder(context).build() }
    AndroidView(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        factory = {
          PlayerView(context).apply {
            player =
                exoPlayer2.apply {
                  val mediaItem =
                      if (url.isNotBlank()) MediaItem.fromUri(url)
                      else MediaItem.fromUri("".toUri()) // Provide a dummy or empty URI
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

    DisposableEffect(exoPlayer2) { onDispose { exoPlayer2.release() } }
  }
}
