package com.android.sample.ui.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteConfirmationDialogTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val context = InstrumentationRegistry.getInstrumentation().targetContext

  @Test
  fun dialogDisplaysCorrectText() {
    // Arrange: Set up the dialog
    composeTestRule.setContent { DeleteConfirmationDialog(onConfirm = {}, onDismiss = {}) }

    // Assert: Check that the title and message are displayed
    composeTestRule
        .onNodeWithText(context.getString(R.string.ConfirmDeleteTitle))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(context.getString(R.string.ConfirmDeleteMessage))
        .assertIsDisplayed()
  }

  @Test
  fun confirmButtonTriggersAction() {
    var confirmClicked = false

    // Arrange: Set up the dialog
    composeTestRule.setContent {
      DeleteConfirmationDialog(onConfirm = { confirmClicked = true }, onDismiss = {})
    }

    // Act: Click the confirm button
    composeTestRule.onNodeWithText(context.getString(R.string.Confirm)).performClick()

    // Assert: Confirm button action is triggered
    assert(confirmClicked)
  }

  @Test
  fun dismissButtonTriggersAction() {
    var dismissClicked = false

    // Arrange: Set up the dialog
    composeTestRule.setContent {
      DeleteConfirmationDialog(onConfirm = {}, onDismiss = { dismissClicked = true })
    }

    // Act: Click the dismiss button
    composeTestRule.onNodeWithText(context.getString(R.string.Cancel)).performClick()

    // Assert: Dismiss button action is triggered
    assert(dismissClicked)
  }
}
