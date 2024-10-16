package com.android.sample.model.video

// Updated Video data class
data class Video(
    val title: String = "",          // Default value for title
    val url: String = "",            // Default value for url
    val tag: String = "All",         // Default value for tag
    val thumbnailUrl: String = "",   // Default value for thumbnailUrl
    val duration: String = "",       // Default value for duration
    val description: String = "",    // Default value for description
    val isLiked: Boolean = false,    // Default value for isLiked
    val isSaved: Boolean = false,    // Default value for isSaved
    val isViewed: Boolean = false,   // Default value for isViewed
    val isDownloaded: Boolean = false, // Default value for isDownloaded
    val isShared: Boolean = false,   // Default value for isShared
    val isReported: Boolean = false, // Default value for isReported
    val isSubscribed: Boolean = false, // Default value for isSubscribed
    val isPremium: Boolean = false   // Default value for isPremium
) {
    // No-argument constructor for Firestore
    constructor() : this(
        title = "",
        url = "",
        tag = "All",
        thumbnailUrl = "",
        duration = "",
        description = "",
        isLiked = false,
        isSaved = false,
        isViewed = false,
        isDownloaded = false,
        isShared = false,
        isReported = false,
        isSubscribed = false,
        isPremium = false
    )
}
