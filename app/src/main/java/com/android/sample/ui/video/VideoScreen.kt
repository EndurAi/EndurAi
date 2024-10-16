// portions of this code were done with the help of ChatGPT and GitHub Copilot
package com.android.sample.ui.video

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_OF_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    navigationActions: NavigationActions,
    video: Video,
    videoViewModel: VideoViewModel =
        viewModel(factory = VideoViewModel.Factory) // Using ViewModel in Compose
) {

    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.testTag("videoScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Videos Library") },
                navigationIcon = {
                    IconButton(onClick = {
                        navigationActions.goBack()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier =
                Modifier.padding(padding)
                    .fillMaxSize() // Ensure the video player takes the full screen size
            ) {
//                if (video.isNotEmpty()) {
//                    // Call the Composable that hosts ExoPlayer
//                    VideoPlayer(url = videoUrls[0], context = context)
//                } else {
//                    Text("Loading video...", modifier = Modifier.padding(16.dp))
//                }
                VideoPlayer(url = video.url, context = context)
            }
        },
    )
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




