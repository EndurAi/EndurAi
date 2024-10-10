package com.android.sample.model.preferences

interface PreferencesRepository {

  fun init(onSuccess: () -> Unit)

  fun getPreferences(onSuccess: (Preferences) -> Unit, onFailure: (Exception) -> Unit)

  fun updatePreferences(pref: Preferences, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deletePreferences(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
