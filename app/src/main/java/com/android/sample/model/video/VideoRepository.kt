// portions of this code were done with the help of ChatGPT and GitHub Copilot
package com.android.sample.model.video

import android.net.Uri

interface VideoRepository {

  /**
   * Uploads a video to the repository.
   *
   * @param videoUri The URI of the video to upload.
   * @param onSuccess Callback function to be invoked with the download URL on successful upload.
   * @param onFailure Callback function to be invoked with an exception if the upload fails.
   */
  fun uploadVideo(video: Video, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves video URLs from the repository.
   *
   * @param onSuccess Callback function to be invoked with a list of video URLs on successful
   *   retrieval.
   * @param onFailure Callback function to be invoked with an exception if the retrieval fails.
   */
  fun getVideos(onSuccess: (List<Video>) -> Unit, onFailure: (Exception) -> Unit)
}
