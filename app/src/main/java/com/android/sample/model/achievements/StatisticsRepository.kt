package com.android.sample.model.achievements

/**
 * Interface for managing the statistics of a user. This repository provides methods for adding
 * workout statistics to the repository and also retrieve them.
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
   * @param onSuccess A lambda function that is called with the retrieved [List<WorkoutStatistics>]
   *   object when the operation is successful.
   * @param onFailure A lambda function that is called with an [Exception] if the operation fails.
   */
  fun getStatistics(onSuccess: (List<WorkoutStatistics>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates the user's statistics in the persistent storage.
   *
   * @param workout The statistics to be added to repository.
   * @param onSuccess A lambda function that is called when the update is successful.
   * @param onFailure A lambda function that is called with an [Exception] if the update fails.
   */
  fun addWorkoutStatistics(
      workout: WorkoutStatistics,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
