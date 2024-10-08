package com.android.sample.model.preferences

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Preferences(
    val unity : UnitySystem,
    val weight : WeightUnit
)

enum class UnitySystem { METRIC, IMPERIAL }

enum class WeightUnit { LBS, KG }