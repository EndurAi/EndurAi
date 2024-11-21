package com.android.sample.model.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CalendarViewModel : ViewModel() {
  private val _selectedDate =
      MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
  val selectedDate: StateFlow<LocalDate> = _selectedDate

  fun updateSelectedDate(date: LocalDate) {
    _selectedDate.value = date
  }
}
