package com.android.sample.model.preferences

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class PreferencesViewModelTest {

  private lateinit var repository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel

  val defaultPreferences = Preferences(unity = UnitsSystem.METRIC, weight = WeightUnit.KG)
  val updatedPreferences = Preferences(unity = UnitsSystem.IMPERIAL, weight = WeightUnit.LBS)

  @Before
  fun setUp() {
    repository = mock(PreferencesRepository::class.java)
    preferencesViewModel = PreferencesViewModel(repository)
  }

  @Test
  fun preferencesFlowShouldStartWithDefaultValues() = runBlocking {
    val preferences = preferencesViewModel.preferences.first()
    assertThat(preferences, `is`(defaultPreferences))
  }

  @Test
  fun getPreferencesCallsRepository() {
    preferencesViewModel.getPreferences()
    verify(repository).getPreferences(any(), any())
  }

  @Test
  fun updatePreferencesCallsRepository() {
    preferencesViewModel.updatePreferences(updatedPreferences)
    verify(repository).updatePreferences(eq(updatedPreferences), any(), any())
  }

  @Test
  fun deletePreferencesCallsRepository() {
    preferencesViewModel.deletePreferences()
    verify(repository).deletePreferences(any(), any())
  }

  @Test
  fun preferencesFlowShouldUpdateAfterGetPreferences() = runBlocking {
    `when`(repository.getPreferences(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (Preferences) -> Unit
      onSuccess(updatedPreferences)
    }

    preferencesViewModel.getPreferences()
    val preferences = preferencesViewModel.preferences.first()
    assertThat(preferences, `is`(updatedPreferences))
  }
}
