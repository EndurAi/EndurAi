package com.android.sample.endToend

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.MainActivity
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MLEndToEndTest {
    private lateinit var navigationActions: NavigationActions
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.putExtra("START_DESTINATION", Route.MAIN)
        val context = ApplicationProvider.getApplicationContext<Context>()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @Test
    fun mlEndToEndTest() {
        composeTestRule.onNodeWithTag("Video").assertIsDisplayed().performClick()

    }
}