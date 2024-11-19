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
    composeTestRule.setContent { DeleteConfirmationDialog(onConfirm = {}, onDismiss = {}) }

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

    composeTestRule.setContent {
      DeleteConfirmationDialog(onConfirm = { confirmClicked = true }, onDismiss = {})
    }

    composeTestRule.onNodeWithText(context.getString(R.string.Confirm)).performClick()

    assert(confirmClicked)
  }

  @Test
  fun dismissButtonTriggersAction() {
    var dismissClicked = false

    composeTestRule.setContent {
      DeleteConfirmationDialog(onConfirm = {}, onDismiss = { dismissClicked = true })
    }

    composeTestRule.onNodeWithText(context.getString(R.string.Cancel)).performClick()

    assert(dismissClicked)
  }
}
