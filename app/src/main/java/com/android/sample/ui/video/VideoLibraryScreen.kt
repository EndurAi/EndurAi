// Portions of this code were developed with the help of ChatGPT and github copilot

package com.android.sample.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

/** Screen to display the video library. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLibraryScreen(navigationActions: NavigationActions, videoViewModel: VideoViewModel) {
    val videoList by videoViewModel.videos.collectAsState(initial = emptyList())
    LaunchedEffect(Unit) { videoViewModel.loadVideos() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Videos Library", Modifier.semantics { testTag = "library_title" }) },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.goBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Row to align Search bar and Tag dropdown
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .semantics { testTag = "search_bar" }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TagDropdown(
                            selectedTag = selectedTag,
                            onTagSelected = { selectedTag = it },
                            modifier = Modifier
                                .width(100.dp)
                                .semantics { testTag = "tag_dropdown" }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtered video list
                    LazyColumn(modifier = Modifier.semantics { testTag = "video_list" }) {
                        items(
                            videoList.filter { video ->
                                (selectedTag == "All" || video.tag == selectedTag) &&
                                        video.title.contains(searchQuery, ignoreCase = true)
                            }) { video ->
                            VideoListItem(
                                video = video,
                                onClick = {
                                    videoViewModel.selectVideo(video)
                                    navigationActions.navigateTo(Screen.VIDEO)
                                },
                                modifier = Modifier.semantics { testTag = "video_item_${video.title}" }
                            )
                        }
                    }
                }
            }
        })
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier =
        modifier.clip(RoundedCornerShape(24.dp)).background(Color(0xFFF0F0F0)).padding(8.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    androidx.compose.material3.Text("Search", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
                innerTextField()
            })
    }
}

@Composable
fun TagDropdown(
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Text(text = selectedTag, color = Color.Gray)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("All", "Body-Weight", "Warmup", "Yoga").forEach { tag ->
                DropdownMenuItem(
                    text = { Text(text = tag) },
                    onClick = {
                        expanded = false
                        onTagSelected(tag)
                    })
            }
        }
    }
}

@Composable
fun VideoListItem(video: Video, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // Modified card with thumbnail on the right and larger size
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
            .then(modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.tag,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(tagColor(video.tag))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(video.thumbnailUrl).build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

fun tagColor(tag: String): Color {
    return when (tag) {
        "Body-Weight" -> Color.Red
        "Warmup" -> Color.Green
        "Yoga" -> Color.Cyan
        else -> Color.Gray
    }
}
