package com.android.sample.model.preferences

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Preferences(val unitsSystem: UnitsSystem, val weight: WeightUnit)

enum class UnitsSystem {
  METRIC,
  IMPERIAL
}

enum class WeightUnit {
  LBS,
  KG
}
