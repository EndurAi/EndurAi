package com.android.sample.screen

//
// @OptIn(ExperimentalCoroutinesApi::class)
// class VideoScreenTest {
//
//  @get:Rule val composeTestRule = createComposeRule()
//
//  private lateinit var navigationActions: NavigationActions
//  private lateinit var videoRepository: VideoRepository
//  private lateinit var videoViewModel: VideoViewModel
//
//  private val testDispatcher = StandardTestDispatcher()
//
//  // Sample video for testing
//  private val sampleVideo =
//      Video(
//          title = "Sample Video",
//          url = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4",
//          tag = "Sample",
//          thumbnailUrl = "",
//          duration = "2:30",
//          description = "Sample video description")
//
//  @Before
//  fun setUp() {
//    // Set main dispatcher to test dispatcher
//    Dispatchers.setMain(testDispatcher)
//
//    // Initialize mock navigation actions
//    navigationActions = Mockito.mock(NavigationActions::class.java)
//
//    // Mock VideoRepository
//    videoRepository = Mockito.mock(VideoRepository::class.java)
//
//    // Set up the mock to return a list of videos using explicit nullable types
//    `when`(
//            videoRepository.getVideos(
//                Mockito.any<(List<Video>) -> Unit>(), Mockito.any<(Exception) -> Unit>()))
//        .thenAnswer {
//          val onSuccess = it.arguments[0] as (List<Video>) -> Unit
//          onSuccess(listOf(sampleVideo))
//        }
//
//    // Use the real VideoViewModel with the mocked repository
//    videoViewModel = VideoViewModel(videoRepository)
//
//    // Set the content of the test to the VideoScreen
//    composeTestRule.setContent {
//      VideoScreen(navigationActions = navigationActions, videoViewModel = videoViewModel)
//    }
//  }
//
//  @After
//  fun tearDown() {
//    Dispatchers.resetMain() // Reset the main dispatcher after tests
//  }
//
//  @Test
//  fun videoScreenDisplaysCorrectly() = runTest {
//    // Verify the main screen (Scaffold) is displayed
//    composeTestRule.onNodeWithTag("videoScreen").assertIsDisplayed()
//
//    // Verify the top app bar is displayed with the back button
//    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
//
//    // Verify that the video player is displayed
//    composeTestRule.onNodeWithTag("playerView").assertIsDisplayed()
//  }
// }
