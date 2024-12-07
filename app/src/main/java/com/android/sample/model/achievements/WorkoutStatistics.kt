package com.android.sample.model.achievements

import com.android.sample.model.workout.Workout
import com.android.sample.model.workout.WorkoutType
import java.time.LocalDateTime

class WorkoutStatistics(
    val duration: Int, // Minutes
    val date: LocalDateTime,
    val caloriesBurnt: Int,
    val type: WorkoutType
)