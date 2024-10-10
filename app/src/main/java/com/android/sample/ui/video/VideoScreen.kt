// portions of this code were done with the help of ChatGPT and GitHub Copilot
package com.android.sample.ui.video

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun VideoScreen(
    navigationActions: NavigationActions,
    videoViewModel: VideoViewModel =
        viewModel(factory = VideoViewModel.Factory) // Using ViewModel in Compose
) {
  val context = LocalContext.current
  val videoUrls by videoViewModel.videoUrls.collectAsState(initial = emptyList())

  // Trigger loadVideos() when this Composable is first composed
  LaunchedEffect(Unit) { videoViewModel.loadVideos() }

  Scaffold(
      modifier = Modifier.testTag("videoScreen"),
      content = { padding ->
        Box(
            modifier =
                Modifier.padding(padding)
                    .fillMaxSize() // Ensure the video player takes the full screen size
            ) {
              if (videoUrls.isNotEmpty()) {
                // Call the Composable that hosts ExoPlayer
                VideoPlayer(url = videoUrls[0], context = context)
              } else {
                Text("Loading video...", modifier = Modifier.padding(16.dp))
              }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_OF_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      })
}

@Composable
fun VideoPlayer(url: String, context: Context) {
  // Create and manage the ExoPlayer instance using remember
  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      // Load the media item using the provided URL
      val mediaItem = MediaItem.fromUri(url)
      setMediaItem(mediaItem)
      prepare()
      playWhenReady = true
    }
  }

  // Use DisposableEffect to release the player when the Composable is removed from the UI
  DisposableEffect(
      AndroidView(
          modifier = Modifier.fillMaxSize(),
          factory = { PlayerView(context).apply { player = exoPlayer } })) {
        onDispose { exoPlayer.release() }
      }
}
