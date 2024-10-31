// portions of this code were done with the help of ChatGPT and GitHub Copilot
package com.android.sample.model.video

/**
 * Data class for Video
 *
 * @param title Title of the video
 * @param url URL of the video
 * @param tag Tag of the video
 * @param thumbnailUrl Thumbnail URL of the video
 * @param duration Duration of the video
 * @param description Description of the video
 * @param isLiked Liked status of the video
 * @param isSaved Saved status of the video
 * @param isViewed Viewed status of the video
 * @param isDownloaded Downloaded status of the video
 * @param isShared Shared status of the video
 * @param isReported Reported status of the video
 * @param isSubscribed Subscribed status of the video
 * @param isPremium Premium status of the video
 */
data class Video(
    val title: String = "", // Default value for title
    val url: String = "", // Default value for url
    val tag: String = "All", // Default value for tag
    val thumbnailUrl: String = "", // Default value for thumbnailUrl
    val duration: String = "", // Default value for duration
    val description: String = "", // Default value for description
    val isLiked: Boolean = false, // Default value for isLiked
    val isSaved: Boolean = false, // Default value for isSaved
    val isViewed: Boolean = false, // Default value for isViewed
    val isDownloaded: Boolean = false, // Default value for isDownloaded
    val isShared: Boolean = false, // Default value for isShared
    val isReported: Boolean = false, // Default value for isReported
    val isSubscribed: Boolean = false, // Default value for isSubscribed
    val isPremium: Boolean = false // Default value for isPremium
) {
  // Constructor for firebase
  constructor() :
      this(
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
          isPremium = false)
}
