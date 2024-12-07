package com.android.sample.model.achievements

import com.android.sample.model.preferences.Preferences

/**
 * Interface for managing the statistics of a user. This repository provides methods for adding workout statistics
 * to the repository and also retrieve them.
 */
interface StatisticsRepository {
    /**
     * Initializes the repository, setting up any necessary resources or authentication listeners.
     *
     * @param onSuccess A lambda function that is called when initialization is successful.
     */
    fun init(onSuccess: () -> Unit)

    /**
     * Retrieves the user's stastistics.
     *
     * @param onSuccess A lambda function that is called with the retrieved [Preferences] object when
     *   the operation is successful.
     * @param onFailure A lambda function that is called with an [Exception] if the operation fails.
     */
    fun getStatistics(onSuccess: (List<WorkoutStatistics>) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Updates the user's preferences in the persistent storage.
     *
     * @param pref The [Preferences] object containing the updated preferences to be saved.
     * @param onSuccess A lambda function that is called when the update is successful.
     * @param onFailure A lambda function that is called with an [Exception] if the update fails.
     */
    fun updatePreferences(pref: Preferences, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Deletes the user's preferences from the persistent storage.
     *
     * @param onSuccess A lambda function that is called when the deletion is successful.
     * @param onFailure A lambda function that is called with an [Exception] if the deletion fails.
     */
    fun deletePreferences(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}