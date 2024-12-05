package com.android.sample.mlUtils.MathsPoseDetection

import com.android.sample.mlUtils.countAlternates
import org.junit.Assert.assertEquals
import org.junit.Test

class MlCoachTest {
  @Test
  fun `count alternates gives correct value when there is no overlaps`() {

    val listA = listOf(1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1).map { it == 1 }
    val listB = listA.map { it.not() }
    val targetValue = 4
    val matrix = listOf(listA, listB)
    assertEquals(targetValue, countAlternates(matrix))
  }

  @Test
  fun `count alternates gives correct value when there are overlaps`() {
    val listB = listOf(1, 0, 1, 1, 0, 1).map { it == 1 } // duplicate at index = 2
    val listA = listOf(0, 1, 1, 0, 1, 0).map { it == 1 }

    val targetValue = 2
    val matrix = listOf(listA, listB)
    assertEquals(targetValue, countAlternates(matrix))
  }
}
