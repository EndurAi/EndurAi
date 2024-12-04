package com.android.sample.mlUtils

data class MyPoseLandmark(val x: Float,val y: Float, val z: Float, val presenceLikelyhood : Float, val timeStamp : Long ){
fun toTriple(): Triple<Float, Float, Float> = Triple(x, y, z)
}

