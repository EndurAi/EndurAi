package com.android.sample.ui.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.sample.R
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.animations.DumbbellAnimation
import com.android.sample.ui.composables.AnimatedText
import com.android.sample.ui.composables.BottomBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.BodyWeightTag
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.LightGrey
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.WarmUpTag
import com.android.sample.ui.theme.White
import com.android.sample.ui.theme.YogaTag

/** Screen to display the video library. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLibraryScreen(navigationActions: NavigationActions, videoViewModel: VideoViewModel) {
  val videoList by videoViewModel.videos.collectAsState(initial = emptyList())
  val isLoading by videoViewModel.loading.collectAsState()
  val error by videoViewModel.error.collectAsState()

  LaunchedEffect(Unit) { videoViewModel.loadVideos() }

  var searchQuery by remember { mutableStateOf("") }
  var selectedTag by remember { mutableStateOf("All") }

  Scaffold(
      bottomBar = { BottomBar(navigationActions = navigationActions) },
      floatingActionButton = {
          Box(
              modifier = Modifier
                  .padding(16.dp)

          ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Box(
                      modifier = Modifier
                          .width(175.dp) // Set a fixed width for the text box to prevent everything from moving
                          .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                          .padding(8.dp)
                  ) {
                      AnimatedText(
                          modifier = Modifier.testTag("coachText"),
                          text = "Hey, want to have some feedback on your work ?",
                          style = MaterialTheme.typography.bodySmall.copy(color = White)
                      )
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  FloatingActionButton(
                      onClick = { /* Action when clicked */ },
                      shape = CircleShape,
                      containerColor = Transparent,
                      contentColor = Transparent,
                      modifier = Modifier.background(BlueGradient, shape = CircleShape)
                          .shadow(8.dp, shape = CircleShape)
                          .testTag("coachButton")
                  ) {
                      Image(
                          painter = painterResource(id = R.drawable.endurai_coach),
                          contentDescription = "Coach",
                          modifier = Modifier.size(150.dp).clip(CircleShape)
                      )
                  }
              }
          }
      },
      content = { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().testTag("topBar")) {
          when {
            isLoading ->
                DumbbellAnimation(
                    modifier = Modifier.testTag("loadingIndicator").align(Alignment.Center))
            error != null -> {
              Text(
                  text = "An error occurred",
                  modifier = Modifier.align(Alignment.Center),
                  color = MaterialTheme.colorScheme.error)
            }
            else -> {
              Column {
                // Blue gradient search bar
                TopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedTag = selectedTag,
                    onTagSelected = { selectedTag = it },
                    navigationActions = navigationActions)

                Spacer(modifier = Modifier.height(16.dp))

                // Filtered video list with fading effect
                VideoList(
                    videoList = videoList,
                    searchQuery = searchQuery,
                    selectedTag = selectedTag,
                    videoViewModel = videoViewModel,
                    navigationActions = navigationActions)

                // Fake bottom rectangle for bottom bar
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Gray))
              }
            }
          }
        }
      })
}
/** Function to calculate the alpha value for the fading effect. */
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

/** Composable function to display a dropdown menu for selecting tags. */
@Composable
fun TagDropdown(
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    Box(
        modifier =
            Modifier.testTag("tagDropdownButton").clickable { expanded = true }.padding(8.dp)) {
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
        modifier =
            Modifier.testTag("dropdownMenu")
                .background(DarkBlue) // Blue background for the entire menu
        ) {
          listOf("All", "Body-Weight", "Warmup", "Yoga").forEach { tag ->
            DropdownMenuItem(
                text = { Text(text = tag, color = Color.White) }, // White text
                onClick = {
                  expanded = false
                  onTagSelected(tag)
                },
                modifier =
                    Modifier.fillMaxWidth()
                        .background(DarkBlue) // Blue background for each item
                        .testTag("dropdownMenuItem_$tag"))
          }
        }
  }
}

/** Composable function to display a video item in the list. */
@Composable
fun VideoListItem(video: Video, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 12.dp) // More spacing between items
              .clickable(onClick = onClick)
              .then(modifier),
      shape = RoundedCornerShape(16.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Increased shadow
      ) {
        Row(
            modifier = Modifier.height(150.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
              // Box for the thumbnail and play button
              Box(
                  modifier =
                      Modifier.fillMaxHeight() // Occupy the full height of the card
                          .weight(0.7f) // 70% of the card's width
                  ) {
                    AsyncImage(
                        model =
                            ImageRequest.Builder(LocalContext.current)
                                .data(video.thumbnailUrl)
                                .build(),
                        contentDescription = "Video Thumbnail",
                        modifier =
                            Modifier.fillMaxSize() // Full height and width within the Box
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        bottomStart = 16.dp)), // Rounded corners for the left side
                        contentScale = ContentScale.Crop)

                    // Play button overlay
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Button",
                        modifier =
                            Modifier.align(Alignment.Center) // Center the icon on the thumbnail
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Blue.copy(alpha = 0.6f)) // Semi-transparent background
                                .padding(4.dp)
                                .testTag("playButton"),
                        tint = Color.White)
                  }

              Spacer(modifier = Modifier.width(16.dp)) // Space between thumbnail and text

              // Text content column
              Column(
                  modifier = Modifier.weight(0.5f).fillMaxHeight().testTag("videoContentColumn"),
                  verticalArrangement = Arrangement.Center) {
                    Text(
                        text = video.title,
                        style =
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        fontSize = 16.sp,
                        maxLines = 2,
                        color = Color.Black)

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = tagColor(video.tag)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 100.dp)) {
                          Text(
                              text = video.tag,
                              color = Color.White,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                              fontSize = 12.sp)
                        }
                  }
            }
      }
}

/** Function to map tags to their respective colors. */
fun tagColor(tag: String): Color {
  return when (tag) {
    "Body-Weight" -> BodyWeightTag // Purple for Body-Weight (harmonizes with blue)
    "Warmup" -> WarmUpTag // Light cyan for Warmup (soft blue, fits the theme)
    "Yoga" -> YogaTag // Medium blue for Yoga (distinct from Warmup, still fits the blue theme)
    else -> LightGrey // Light gray for untagged (neutral, blends well)
  }
}

/** Composable function to display the top bar with search and tag dropdown. */
@Composable
fun TopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    navigationActions: NavigationActions
) {
  Surface(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 0.dp, vertical = 0.dp)
              .testTag("searchBarSurface"),
      shadowElevation = 8.dp,
      shape = RectangleShape) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .background(BlueGradient)
                    .testTag("searchBarBox")
                    .padding(horizontal = 16.dp, vertical = 8.dp)) {
              // Search bar row
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.testTag("searchBarRow")
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(24.dp))
                          .background(Color.Transparent)
                          .padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier =
                            Modifier.testTag("backButton").size(24.dp).clickable {
                              navigationActions.goBack()
                            })

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search", color = Color.White) },
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedPlaceholderColor = Color.White,
                                unfocusedPlaceholderColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = Color.White),
                        modifier = Modifier.weight(1f).testTag("searchField"))

                    Spacer(modifier = Modifier.width(8.dp))

                    if (searchQuery.isNotEmpty()) {
                      Icon(
                          imageVector = Icons.Default.Close,
                          contentDescription = "Clear",
                          tint = Color.White,
                          modifier = Modifier.size(24.dp).clickable { onSearchQueryChange("") })
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TagDropdown(
                        selectedTag = selectedTag,
                        onTagSelected = onTagSelected,
                        modifier = Modifier.testTag("tagDropdown"))
                  }
            }
      }
}

/** Composable function to display the list of videos. */
@Composable
fun VideoList(
    videoList: List<Video>,
    searchQuery: String,
    selectedTag: String,
    videoViewModel: VideoViewModel,
    navigationActions: NavigationActions
) {
  val listState = rememberLazyListState()
  LazyColumn(state = listState, modifier = Modifier.padding(horizontal = 16.dp)) {
    items(
        videoList.filter { video ->
          video.title.contains(searchQuery, ignoreCase = true) &&
              (selectedTag == "All" || video.tag == selectedTag)
        }) { video ->
          val index = videoList.indexOf(video)
          val alpha = calculateAlpha(index, listState)
          VideoListItem(
              video = video,
              onClick = {
                videoViewModel.selectVideo(video)
                navigationActions.navigateTo(Screen.VIDEO)
              },
              modifier =
                  Modifier.padding(vertical = 4.dp)
                      .fillMaxWidth()
                      .graphicsLayer(alpha = alpha)
                      .testTag("videoItem_${video.title}"))
        }
  }
}
