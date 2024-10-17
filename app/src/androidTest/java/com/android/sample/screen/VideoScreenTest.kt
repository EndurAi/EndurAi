import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.video.Video
import com.android.sample.model.video.VideoViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.video.VideoScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class VideoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock navigation actions
    private lateinit var navigationActions: NavigationActions

    // Mock VideoViewModel
    private lateinit var videoViewModel: VideoViewModel

    // Sample video for testing
    private val sampleVideo = Video(
        title = "Sample Video",
        url = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4",
        tag = "Sample",
        thumbnailUrl = "",
        duration = "2:30",
        description = "Sample video description"
    )

    @Before
    fun setUp() {
        // Initialize mock navigation actions
        navigationActions = Mockito.mock(NavigationActions::class.java)

        // Mock VideoViewModel
        videoViewModel = Mockito.mock(VideoViewModel::class.java)

        // Mock selected video flow with the sample video
        `when`(videoViewModel.selectedVideo).thenReturn(MutableStateFlow(sampleVideo))

        // Set the content of the test to the VideoScreen
        composeTestRule.setContent {
            VideoScreen(
                navigationActions = navigationActions,
                videoViewModel = videoViewModel
            )
        }
    }

    @Test
    fun videoScreenDisplaysCorrectly() {
        // Verify the main screen (Scaffold) is displayed
        composeTestRule.onNodeWithTag("videoScreen").assertIsDisplayed()

        // Verify the top app bar is displayed with the back button
        composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()

        // Verify that the video player is displayed
        composeTestRule.onNodeWithTag("playerView").assertIsDisplayed()
    }
}
