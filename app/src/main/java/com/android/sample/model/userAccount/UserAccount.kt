package com.android.sample.model.userAccount

import com.google.firebase.Timestamp
import java.util.Date

// Data class to represent a user Account
data class UserAccount(
    val userId: String = "", // Unique user ID
    val firstName: String = "",
    val lastName: String = "",
    val height: Float = 0f,
    val heightUnit: HeightUnit = HeightUnit.CM,
    val weight: Float = 0f,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val gender: Gender = Gender.MALE,
    val birthDate: Timestamp = Timestamp(Date()), // Default to current time
    var profileImageUrl: String = "", // URL to the image stored on firebase
    val friends: Set<String> = setOf(), // Set of user IDs of friends
    val sentRequests: Set<String> = setOf(),       // IDs of users to whom this user has sent friend requests
    val receivedRequests: Set<String> = setOf()   // IDs of users who sent friend requests to this user
)

// Enum for height units
enum class HeightUnit {
  CM,
  METER,
  INCHES
}

// Enum for weight units
enum class WeightUnit {
  KG,
  LBS
}

// Enum for gender
enum class Gender {
  MALE,
  FEMALE
}
