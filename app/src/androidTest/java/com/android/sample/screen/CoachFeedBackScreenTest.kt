package com.android.sample.screen

import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.camera.CameraViewModel
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.mock

class CoachFeedBackScreenTest {
    private lateinit var mockCameraViewModel: CameraViewModel
    private lateinit var navigationActions: NavigationActions

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        navigationActions = mock()
        mockCameraViewModel = mock()
    }
}