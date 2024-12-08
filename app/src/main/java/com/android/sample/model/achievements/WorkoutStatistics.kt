package com.android.sample.model.achievements

import com.android.sample.model.workout.WorkoutType
import java.time.LocalDateTime

/**
 * This class stores useful statistics for a given workout. The goal of this class is to be
 * initialized and use after completing a workout so that the achievement screen can display some
 * general statistics
 */
class WorkoutStatistics(
    val id: String,
    val date: LocalDateTime,
    val duration: Int = 0, // Not implemented for the moment
    val caloriesBurnt: Int,
    val type: WorkoutType
)
