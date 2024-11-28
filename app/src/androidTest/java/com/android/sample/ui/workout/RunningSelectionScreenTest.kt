import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.workout.RunningSelectionScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class RunningSelectionScreenTest {
  val navigationActions = mock(NavigationActions::class.java)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent { RunningSelectionScreen(navigationActions = navigationActions) }
  }

  @Test
  fun runningSelectionScreen_displaysButtonsAndImage() {

    composeTestRule.onNodeWithTag("withoutPathButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("createNewPathButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loadPathButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("runningSilhouette").assertIsDisplayed()
  }

  @Test
  fun withoutPathButton_clickable() {
    composeTestRule.onNodeWithTag("withoutPathButton").assertHasClickAction()
  }

  @Test
  fun createNewPathButton_clickable() {
    composeTestRule.onNodeWithTag("createNewPathButton").assertHasClickAction()
  }

  @Test
  fun loadPathButton_clickable() {
    composeTestRule.onNodeWithTag("loadPathButton").assertHasClickAction()
  }
}
