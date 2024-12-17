package com.android.sample.model.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesViewModel(private val repository: PreferencesRepository, private val localCache: PreferencesLocalCache) : ViewModel() {

  companion object {
    val defaultPreferences = Preferences(unitsSystem = UnitsSystem.METRIC, weight = WeightUnit.KG)
  }

  private val preferences_ = MutableStateFlow<Preferences?>(defaultPreferences)
  open val preferences: StateFlow<Preferences?> = preferences_.asStateFlow()

  init {
    repository.init { getPreferences() }
  }

  fun updatePreferences(prefs: Preferences) {
    if (prefs == preferences_.value) return
    repository.updatePreferences(prefs, onSuccess = { getPreferences() }, onFailure = {})
  }

  fun getPreferences() {
    repository.getPreferences(
        onSuccess = { prefs -> preferences_.value = prefs },
        onFailure = { e -> Log.e("DEB", "Error occurred while getting preferences: $e") })
  }

  fun deletePreferences() {
    repository.deletePreferences(onSuccess = { getPreferences() }, onFailure = {})
  }
}
