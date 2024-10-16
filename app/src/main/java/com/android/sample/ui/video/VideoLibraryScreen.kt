package com.android.sample.ui.video
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLibraryScreen(
    navigationActions: NavigationActions,
    videoViewModel: VideoViewModel
) {
    val videoList by videoViewModel.videos.collectAsState(initial = emptyList())
    LaunchedEffect(Unit) { videoViewModel.loadVideos() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }

    Scaffold(
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
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Row to align Search bar and Tag dropdown
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            modifier = Modifier.weight(1f) // Takes up available space
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TagDropdown(
                            selectedTag = selectedTag,
                            onTagSelected = { selectedTag = it },
                            modifier = Modifier.width(100.dp) // Fixes width for the dropdown
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtered video list
                    LazyColumn {
                        items(videoList.filter { video ->
                            (selectedTag == "All" || video.tag == selectedTag) &&
                                    video.title.contains(searchQuery, ignoreCase = true)
                        }) { video ->
                            VideoListItem(
                                video = video,
                                onClick = {
                                    videoViewModel.selectVideo(video)
                                    navigationActions.navigateTo(Screen.VIDEO)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF0F0F0))
            .padding(8.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text("Search", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun TagDropdown(selectedTag: String, onTagSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Text(text = selectedTag, color = Color.Gray)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("All", "Body-Weight", "Warmup", "Yoga").forEach { tag ->
                DropdownMenuItem(
                    text = { Text(text = tag) },
                    onClick = {
                        expanded = false
                        onTagSelected(tag)
                    }
                )
            }
        }
    }
}

@Composable
fun VideoListItem(video: Video, onClick: () -> Unit) {
    // Modified card with thumbnail on the right and larger size
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title and Tag on the left
            Column(
                modifier = Modifier.weight(1f) // Take up available space
            ) {
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

            // Larger thumbnail on the right
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUrl)
                    .build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .size(80.dp) // Increased size
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



