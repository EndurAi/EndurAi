package com.android.sample.model.userAccount

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extension for accessing DataStore
val Context.userAccountDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_account")

class UserAccountLocalCache(private val context: Context) {
    private val gson = Gson()
    private val userAccountKey = stringPreferencesKey("user_account")

    fun getUserAccount(): Flow<UserAccount?> = context.userAccountDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[userAccountKey]?.let { gson.fromJson(it, UserAccount::class.java) }
        }

    suspend fun saveUserAccount(userAccount: UserAccount) {
        context.userAccountDataStore.edit { preferences ->
            preferences[userAccountKey] = gson.toJson(userAccount)
        }
    }

    suspend fun clearUserAccount() {
        context.userAccountDataStore.edit { it.clear() }
    }
}