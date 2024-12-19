package com.android.sample.model.workout

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class WorkoutLocalCacheTest {

  private lateinit var localCache: WorkoutLocalCache
  private val bodyWeightWorkout =
      BodyWeightWorkout(
          workoutId = "w1",
          name = "Test Bodyweight",
          description = "Bodyweight workout",
          warmup = false,
          userIdSet = mutableSetOf("user1"),
          exercises = mutableListOf(),
          date = LocalDateTime.now())

  private val yogaWorkout =
      YogaWorkout(
          workoutId = "w2",
          name = "Test Yoga",
          description = "Yoga workout",
          warmup = true,
          userIdSet = mutableSetOf("user2"),
          exercises = mutableListOf(),
          date = LocalDateTime.now())

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    localCache = WorkoutLocalCache(context)
  }

  @Test
  fun getWorkouts_returns_empty_list_initially() = runTest {
    val cachedWorkouts = localCache.getWorkouts().first()
    assertThat(cachedWorkouts, empty())
  }

  @Test
  fun saveWorkouts_retrieves_saved_workouts() = runTest {
    localCache.saveWorkouts(listOf(bodyWeightWorkout, yogaWorkout))

    val cachedWorkouts = localCache.getWorkouts().first()

    assertThat(cachedWorkouts.size, `is`(2))
    assertThat(cachedWorkouts.any { it.workoutId == "w1" }, `is`(true))
    assertThat(cachedWorkouts.any { it.workoutId == "w2" }, `is`(true))
  }

  @Test
  fun clearWorkouts_removes_cached_data() = runTest {
    localCache.saveWorkouts(listOf(bodyWeightWorkout))
    localCache.clearWorkouts()

    val cachedWorkouts = localCache.getWorkouts().first()
    assertThat(cachedWorkouts, empty())
  }
}
