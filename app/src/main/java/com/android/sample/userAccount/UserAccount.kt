package com.android.sample.userAccount

import java.util.Date



// Data class to represent a user Account
data class UserAccount(
    val firstName: String,
    val lastName: String,
    val height: Float,
    val heightUnit: HeightUnit,
    val weight: Float,
    val weightUnit: WeightUnit,
    val gender: Gender,
    val birthDate: Date,
    val profileImageUrl: String // URL to the image stored on firebase
)

// Enum for height units
enum class HeightUnit {
    CM, METER, INCHES
}

// Enum for weight units
enum class WeightUnit {
    KG, LBS
}

// Enum for gender
enum class Gender {
    MALE, FEMALE
}