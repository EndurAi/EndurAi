package com.android.sample.mlUtils.MathsPoseDetection

import MathsPoseDetection
import com.android.sample.mlUtils.MyPoseLandmark
import org.junit.Assert.assertEquals
import org.junit.Test

class LastTimeWindowTest {
  @Test
  fun getLastDurationRetrunsCorrectWindow() {
    val timeLimit = 200L
    val landmarks: ArrayList<ArrayList<MyPoseLandmark>> = ArrayList()
    // fill the list

    for (i in 0 until 200) {
      landmarks.add(ArrayList())
      for (joint in 0 until 32) {
        landmarks[i].add(MyPoseLandmark(0f, 0f, 0f, 0f, i.toLong()))
      }
    }

    val actual = MathsPoseDetection.getLastDuration(timeLimit, landmarks)
    val expectation = landmarks

    assertEquals(expectation, actual)
  }

  @Test
  fun getLastDurationRetrunsCorrectWindowOnEmptyList() {
    val timeLimit = 200L
    val landmarks: ArrayList<ArrayList<MyPoseLandmark>> = ArrayList()
    // fill the list

    val actual = MathsPoseDetection.getLastDuration(timeLimit, landmarks)
    val expectation = landmarks

    assertEquals(expectation, actual)
  }
}
