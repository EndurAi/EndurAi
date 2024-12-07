package com.android.sample.model.achievements

import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import java.time.LocalDateTime

/**
 * This class stores useful statistics for a given workout.
 * The goal of this class is to be initialized and use after completing a workout
 * so that the achievement screen can display some general statistics
 */
class WorkoutStatistics(
    val id: String,
    val duration: Int, // Minutes
    val date: LocalDateTime,
    val caloriesBurnt: Int,
    val type: WorkoutType
)