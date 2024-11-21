package com.android.sample.model.userAccount

import com.android.sample.model.ml.PoseFeedback
import com.android.sample.model.ml.RepetitionExerciseFeedback
import com.android.sample.model.ml.StaticHoldFeedback
import org.junit.Assert.*
import org.junit.Test

class FeedbackMLTest {

  @Test
  fun testPoseFeedback() {
    val poseFeedback =
        PoseFeedback(
            balanceScore = 85.0,
            alignmentFeedback = mapOf("left_knee" to "aligned", "right_knee" to "misaligned"),
            exerciseName = "Tree Pose",
            duration = 60.0,
            numberOfRepetitions = 1,
            accuracyScore = 90.0)

    poseFeedback.balanceScore?.let { assertEquals(85.0, it, 0.0) }
    assertEquals("aligned", poseFeedback.alignmentFeedback["left_knee"])
    assertEquals("misaligned", poseFeedback.alignmentFeedback["right_knee"])
    assertEquals("Tree Pose", poseFeedback.exerciseName)
    assertEquals(60.0, poseFeedback.duration, 0.0)
    assertEquals(1, poseFeedback.numberOfRepetitions)
    assertEquals(90.0, poseFeedback.accuracyScore, 0.0)
  }

  @Test
  fun testRepetitionExerciseFeedback() {
    val repetitionFeedback =
        RepetitionExerciseFeedback(
            angleThreshold = 45.0,
            jointFeedback = mapOf("elbow" to "good", "shoulder" to "needs improvement"),
            exerciseName = "Bicep Curl",
            duration = 30.0,
            numberOfRepetitions = 15,
            accuracyScore = 85.0)

    repetitionFeedback.angleThreshold?.let { assertEquals(45.0, it, 0.0) }
    assertEquals("good", repetitionFeedback.jointFeedback["elbow"])
    assertEquals("needs improvement", repetitionFeedback.jointFeedback["shoulder"])
    assertEquals("Bicep Curl", repetitionFeedback.exerciseName)
    assertEquals(30.0, repetitionFeedback.duration, 0.0)
    assertEquals(15, repetitionFeedback.numberOfRepetitions)
    assertEquals(85.0, repetitionFeedback.accuracyScore, 0.0)
  }

  @Test
  fun testStaticHoldFeedback() {
    val staticHoldFeedback =
        StaticHoldFeedback(
            stabilityScore = 95.0,
            holdTime = 120.0,
            exerciseName = "Plank",
            duration = 120.0,
            numberOfRepetitions = 1,
            accuracyScore = 92.0)

    staticHoldFeedback.stabilityScore?.let { assertEquals(95.0, it, 0.0) }
    staticHoldFeedback.holdTime?.let { assertEquals(120.0, it, 0.0) }
    assertEquals("Plank", staticHoldFeedback.exerciseName)
    assertEquals(120.0, staticHoldFeedback.duration, 0.0)
    assertEquals(1, staticHoldFeedback.numberOfRepetitions)
    assertEquals(92.0, staticHoldFeedback.accuracyScore, 0.0)
  }
}
