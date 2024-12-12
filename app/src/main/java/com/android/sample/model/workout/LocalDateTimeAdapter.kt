package com.android.sample.model.workout

import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  override fun serialize(
      src: LocalDateTime?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
  ): JsonElement {
    val serialized = src?.format(formatter)
    Log.d("LocalDateTimeAdapter", "Serializing LocalDateTime: $serialized")
    return JsonPrimitive(serialized)
  }

  override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
  ): LocalDateTime {
    val dateTimeString = json?.asString
    Log.d("LocalDateTimeAdapter", "Deserializing LocalDateTime: $dateTimeString")
    return LocalDateTime.parse(dateTimeString, formatter)
  }
}
