// portions of this code were done with the help of ChatGPT and GitHub Copilot
package com.android.sample.ui.video

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.NavigationActions

/** Composable function to display the video screen. */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Video Screen composable function to display the video screen.
 * @param navigationActions
 * @param videoViewModel
 */
fun VideoScreen(
    navigationActions: NavigationActions,
    videoViewModel: VideoViewModel = viewModel(factory = VideoViewModel.Factory)
) {
  val context = LocalContext.current
  Scaffold(
      modifier = Modifier.testTag("videoScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Videos Library", modifier = Modifier.testTag("videosLibraryTitle")) },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      content = { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().testTag("videoContentBox")) {
          videoViewModel.selectedVideo.value?.let { VideoPlayer(url = it.url, context = context) }
        }
      })
}

/**
 * Composable function to display a video player using ExoPlayer.
 *
 * @param url The URL of the video to be played.
 * @param context The context used to create the ExoPlayer instance.
 */
@Composable
fun VideoPlayer(url: String, context: Context) {
  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      val mediaItem = MediaItem.fromUri(url)
      setMediaItem(mediaItem)
      prepare()
      playWhenReady = true
    }
  }

  DisposableEffect(
      AndroidView(
          modifier = Modifier.fillMaxSize().testTag("playerView"),
          factory = { PlayerView(context).apply { player = exoPlayer } })) {
        onDispose { exoPlayer.release() }
      }
}
