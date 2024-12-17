package com.android.sample.model.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test

class PreferencesLocalCacheTest {

    private lateinit var localCache: PreferencesLocalCache
    private val preferences =
        Preferences(unitsSystem = UnitsSystem.METRIC, weight = WeightUnit.KG)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        localCache = PreferencesLocalCache(context)
    }

    @Test
    fun getPreferences_returns_cached_data() = runTest {
        localCache.savePreferences(preferences)

        val cachedPreferences = localCache.getPreferences().first()

        // Verify the cached preferences match the original data
        assertThat(cachedPreferences, `is`(preferences))
    }

    @Test
    fun clearPreferences_removes_cached_data() = runTest {
        localCache.savePreferences(preferences)

        localCache.clearPreferences()

        // Verify the cache is cleared
        val cachedPreferences = localCache.getPreferences().first()
        assertThat(cachedPreferences, `is`(PreferencesViewModel.defaultPreferences))
    }

    @Test
    fun getPreferences_returns_default_when_no_data_cached() = runTest {
        val cachedPreferences = localCache.getPreferences().first()

        // Verify the returned preferences are the default ones
        assertThat(cachedPreferences, `is`(PreferencesViewModel.defaultPreferences))
    }
}