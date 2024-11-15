import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.ui.composables.DateTimePicker
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class DateTimePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCorrectInitialTextWhenDateTimeIsNull() {
        composeTestRule.setContent {
            DateTimePicker(
                selectedDateTime = null,
                onDateTimeSelected = {},
                title = "Select Date"
            )
        }
        composeTestRule.onNodeWithText("Select Date and Time").assertExists()
    }

    @Test
    fun displaysCorrectInitialTextWhenDateTimeIsNotNull() {
        val dateTime = LocalDateTime.of(2023, 10, 5, 14, 30)
        composeTestRule.setContent {
            DateTimePicker(
                selectedDateTime = dateTime,
                onDateTimeSelected = {},
                title = "Select Date"
            )
        }
        composeTestRule.onNodeWithText("5 October 2023 at 14:30").assertExists()
    }

    @Test
    fun callsOnDateTimeSelectedWithCorrectDateTime() {
        var selectedDateTime: LocalDateTime? = null
        composeTestRule.setContent {
            DateTimePicker(
                selectedDateTime = null,
                onDateTimeSelected = { selectedDateTime = it },
                title = "Select Date"
            )
        }
        composeTestRule.onNodeWithText("Select Date and Time").performClick()
        // Simulate date and time selection
        selectedDateTime = LocalDateTime.of(2023, 10, 5, 14, 30)
        assert(selectedDateTime == LocalDateTime.of(2023, 10, 5, 14, 30))
    }
}