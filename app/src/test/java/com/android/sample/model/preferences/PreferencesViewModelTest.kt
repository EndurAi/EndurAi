package com.android.sample.model.preferences

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
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
  private lateinit var localCache: PreferencesLocalCache

  private lateinit var preferencesViewModel: PreferencesViewModel

  val defaultPreferences = Preferences(unitsSystem = UnitsSystem.METRIC, weight = WeightUnit.KG)
  val updatedPreferences = Preferences(unitsSystem = UnitsSystem.IMPERIAL, weight = WeightUnit.LBS)

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(StandardTestDispatcher())
    localCache = mock(PreferencesLocalCache::class.java)
    // Return a valid Flow for the local cache
    `when`(localCache.getPreferences()).thenReturn(flowOf(null))
    repository = mock(PreferencesRepository::class.java)
    preferencesViewModel = PreferencesViewModel(repository, localCache)
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

    val initialPreferences = preferencesViewModel.preferences.first()
    assertThat(initialPreferences, `is`(defaultPreferences))

    preferencesViewModel.getPreferences()
    val preferences = preferencesViewModel.preferences.first()
    assertThat(preferences, `is`(updatedPreferences))
  }
}
