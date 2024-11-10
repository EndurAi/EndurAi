package com.android.sample.model.calendar

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class CalendarViewModelTest {

  private lateinit var calendarViewModel: CalendarViewModel

  @Before
  fun setUp() {
    calendarViewModel = CalendarViewModel()
  }

  @Test
  fun changeSelectedDate() = runBlocking {
    // Given a new date
    val newDate = LocalDate(2023, 11, 10)

    // When updating the selected date
    calendarViewModel.updateSelectedDate(newDate)

    // Retrieve the updated selected date
    val selectedDate = calendarViewModel.selectedDate.first()

    // Then check that the selected date matches the new date
    assertThat(selectedDate, `is`(newDate))
  }
}
