package com.android.sample.screen

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.mlFeedback.CoachCaptureScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class CoachCaptureScreenTest {
    private lateinit var mockCameraViewModel: CameraViewModel
    private lateinit var navigationActions: NavigationActions

    @get:Rule val composeTestRule = createComposeRule()
    @Before
    fun setUp() {
        navigationActions = mock()
    }

    @Test
    fun displayAllComponentsOnFirstScreen() {
        composeTestRule.setContent {
            val cameraViewModel = CameraViewModel(LocalContext.current)
            CoachCaptureScreen(
                cameraViewModel = cameraViewModel,
                navigationActions = navigationActions
            )
        }
        composeTestRule.onNodeWithTag("coachCaptureScreen").assertIsDisplayed()
    }
}