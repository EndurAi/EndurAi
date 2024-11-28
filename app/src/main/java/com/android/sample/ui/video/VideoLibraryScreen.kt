package com.android.sample.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.composables.CustomSearchBar
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
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column {
                    // Blue gradient search bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp, vertical = 0.dp), // Reduced vertical padding
                        shadowElevation = 8.dp, // Add elevation
                        shape = RectangleShape // Rounded corners only at the bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1E88E5), // Start of gradient
                                            Color(0xFF42A5F5)  // End of gradient
                                        )
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp) // Reduced vertical padding
                        ) {
                            // Search bar row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 4.dp) // Reduced vertical padding
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { navigationActions.goBack() }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search", color = Color.White) },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedPlaceholderColor = Color.White,
                                        unfocusedPlaceholderColor = Color.White,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                if (searchQuery.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable { searchQuery = "" }
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                TagDropdown(
                                    selectedTag = selectedTag,
                                    onTagSelected = { selectedTag = it }
                                )
                            }
                        }
                    }









                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtered video list with fading effect
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                    ) {
                        items(
                            videoList.filter { video ->
                                video.title.contains(searchQuery, ignoreCase = true) &&
                                        (selectedTag == "All" || video.tag == selectedTag)
                            }
                        ) { video ->
                            val index = videoList.indexOf(video)
                            val alpha = calculateAlpha(index, listState)
                            VideoListItem(
                                video = video,
                                onClick = {
                                    videoViewModel.selectVideo(video)
                                    navigationActions.navigateTo(Screen.VIDEO)
                                },
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth()
                                    .graphicsLayer(alpha = alpha)
                            )
                        }
                    }

                    // Fake bottom rectangle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Gray)
                    )
                }
            }
        }
    )
}

fun calculateAlpha(index: Int, listState: LazyListState): Float {
    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
    val itemInfo = visibleItemsInfo.find { it.index == index }
    return if (itemInfo != null) {
        val viewportHeight = listState.layoutInfo.viewportEndOffset
        val itemOffset = itemInfo.offset + itemInfo.size / 2
        val distanceToBottom = viewportHeight - itemOffset
        val fadeDistance = 400f // Adjust this value as needed
        (distanceToBottom / fadeDistance).coerceIn(0f, 1f)
    } else {
        1f
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
        Box(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp) // Invisible clickable area
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = selectedTag, color = Color.White)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.White,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E88E5)) // Blue background for the entire menu
        ) {
            listOf("All", "Body-Weight", "Warmup", "Yoga").forEach { tag ->
                DropdownMenuItem(
                    text = { Text(text = tag, color = Color.White) }, // White text
                    onClick = {
                        expanded = false
                        onTagSelected(tag)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E88E5)) // Blue background for each item
                )
            }
        }
    }
}

/** Composable function to display a video item in the list. */
@Composable
fun VideoListItem(video: Video, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp) // More spacing between items
            .clickable(onClick = onClick)
            .then(modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Increased shadow
    ) {
        Row(
            modifier = Modifier
                .height(150.dp) // Fixed height for consistent size
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box for the thumbnail and play button
            Box(
                modifier = Modifier
                    .fillMaxHeight() // Occupy the full height of the card
                    .weight(0.7f) // 70% of the card's width
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(video.thumbnailUrl).build(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier
                        .fillMaxSize() // Full height and width within the Box
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)), // Rounded corners for the left side
                    contentScale = ContentScale.Crop
                )

                // Play button overlay
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Button",
                    modifier = Modifier
                        .align(Alignment.Center) // Center the icon on the thumbnail
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Blue.copy(alpha = 0.6f)) // Semi-transparent background
                        .padding(4.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between thumbnail and text

            // Text content column
            Column(
                modifier = Modifier
                    .weight(0.5f) // Remaining 30% of the card's width
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    fontSize = 16.sp, // Adjusted font size for better balance
                    maxLines = 2,
                    color = Color.Black

                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = tagColor(video.tag)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 100.dp) // Adjust the elevation as needed
               ) {
                    Text(
                        text = video.tag,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/** Function to map tags to their respective colors. */
fun tagColor(tag: String): Color {
    return when (tag) {
        "Body-Weight" -> Color(0xFF9C27B0) // Purple for Body-Weight (harmonizes with blue)
        "Warmup" -> Color(0xFF81D4FA) // Light cyan for Warmup (soft blue, fits the theme)
        "Yoga" -> Color(0xFF42A5F5) // Medium blue for Yoga (distinct from Warmup, still fits the blue theme)
        else -> Color(0xFFB0BEC5) // Light gray for untagged (neutral, blends well)
    }
}
