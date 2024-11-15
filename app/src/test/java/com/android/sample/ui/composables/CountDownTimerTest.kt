package com.android.sample.ui.composables

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CountDownTimerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun convertsValidTimeStringToSeconds() {
        val result = convertTimeToSeconds("02:30")
        assertEquals(150, result)
    }
    @Test
    fun convertsLargeNumberOfSecondsToTimeString() {
        val result = convertSecondsToTime(3661)
        assertEquals("61:01", result)
    }
}