package com.android.sample.model.video

data class Video(
    val title: String,
    val url: String,
    val tag: String = "All",
    val thumbnailUrl: String = "",
    val duration: String = "",
    val description: String = ""
)