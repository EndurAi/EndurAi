package com.android.sample.model.achievements

class Statistics(
    val workouts: List<WorkoutStatistics>
) {

    fun getTotalWorkouts(): Int {
        return workouts.size
    }

    fun getDates(){}
}