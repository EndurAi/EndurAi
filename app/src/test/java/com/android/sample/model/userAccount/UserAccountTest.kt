package com.android.sample.model.userAccount

import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

class UserAccountTest {

  @Test
  fun testUserAccountCreation() {
    // Arrange: Create a Date object for birthDate
    val birthDate = Date()

    // Act: Initialize a UserAccount instance
    val user =
        UserAccount(
            userId = "001",
            firstName = "John",
            lastName = "Doe",
            height = 180f,
            heightUnit = HeightUnit.CM,
            weight = 75f,
            weightUnit = WeightUnit.KG,
            gender = Gender.MALE,
            birthDate = birthDate,
            profileImageUrl = "https://firebase.storage/user123/profile.jpg")

    // Assert: Check that the values are correctly stored
    assertEquals("001", user.userId)
    assertEquals("John", user.firstName)
    assertEquals("Doe", user.lastName)
    assertEquals(180f, user.height)
    assertEquals(HeightUnit.CM, user.heightUnit)
    assertEquals(75f, user.weight)
    assertEquals(WeightUnit.KG, user.weightUnit)
    assertEquals(Gender.MALE, user.gender)
    assertEquals(birthDate, user.birthDate)
    assertEquals("https://firebase.storage/user123/profile.jpg", user.profileImageUrl)
  }
}
