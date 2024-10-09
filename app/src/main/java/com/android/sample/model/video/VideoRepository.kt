// portions of this code were done with the help of ChatGPT and GitHub Copilot
package com.android.sample.model.video

import android.net.Uri

interface VideoRepository {

    fun uploadVideo(videoUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)

    fun getVideoUrls(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit)

}
