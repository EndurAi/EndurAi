package com.android.sample.model.workout

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class WorkoutTypeAdapter : JsonDeserializer<Workout>, JsonSerializer<Workout> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Workout? {
        val jsonObject = json?.asJsonObject
            ?: throw JsonParseException("Invalid JSON for Workout")

        // Get the "type" field to determine the subclass
        val type = jsonObject.get("type")?.asString
            ?: throw JsonParseException("Missing 'type' field in Workout JSON")

        // Deserialize based on the type
        return when (type) {
            "BodyWeightWorkout" -> context?.deserialize(jsonObject, BodyWeightWorkout::class.java)
            "YogaWorkout" -> context?.deserialize(jsonObject, YogaWorkout::class.java)
            "WarmUp" -> context?.deserialize(jsonObject, WarmUp::class.java)
            "RunningWorkout" -> context?.deserialize(jsonObject, RunningWorkout::class.java)
            else -> throw JsonParseException("Unknown workout type: $type")
        }
    }

    override fun serialize(
        src: Workout?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        // Serialize the workout as JSON and add the "type" property
        val jsonObject = context?.serialize(src)?.asJsonObject
            ?: throw JsonParseException("Failed to serialize Workout")

        jsonObject.addProperty("type", src!!::class.simpleName) // Add the type as a property

        Log.d("WorkoutTypeAdapter", "Serialized JSON: $jsonObject")
        return jsonObject
    }
}