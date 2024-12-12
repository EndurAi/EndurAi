package com.android.sample.model.workout

import com.google.gson.*
import java.lang.reflect.Type

class ExerciseDetailAdapter : JsonDeserializer<ExerciseDetail>, JsonSerializer<ExerciseDetail> {

  override fun serialize(
      src: ExerciseDetail?,
      typeOfSrc: Type?,
      context: JsonSerializationContext
  ): JsonElement {
    val jsonObject = JsonObject()
    when (src) {
      is ExerciseDetail.RepetitionBased -> {
        jsonObject.addProperty("type", "RepetitionBased")
        jsonObject.addProperty("repetitions", src.repetitions)
      }
      is ExerciseDetail.TimeBased -> {
        jsonObject.addProperty("type", "TimeBased")
        jsonObject.addProperty("durationInSeconds", src.durationInSeconds)
        jsonObject.addProperty("sets", src.sets)
      }
      null -> TODO()
    }
    return jsonObject
  }

  override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext
  ): ExerciseDetail {
    val jsonObject =
        json?.asJsonObject ?: throw JsonParseException("Invalid JSON for ExerciseDetail")

    return when (jsonObject.get("type").asString) {
      "RepetitionBased" ->
          ExerciseDetail.RepetitionBased(repetitions = jsonObject.get("repetitions").asInt)
      "TimeBased" ->
          ExerciseDetail.TimeBased(
              durationInSeconds = jsonObject.get("durationInSeconds").asInt,
              sets = jsonObject.get("sets").asInt)
      else -> throw JsonParseException("Unknown ExerciseDetail type")
    }
  }
}
