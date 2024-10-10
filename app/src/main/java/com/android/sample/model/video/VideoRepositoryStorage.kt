// portions of this code were done with the help of ChatGPT and Github Copilot

package com.android.sample.model.video

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class VideoRepositoryStorage(private val storageReference: StorageReference = FirebaseStorage.getInstance().reference) : VideoRepository {

    private val storageRef: StorageReference = storageReference

    override fun uploadVideo(
        videoUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val videoRef = storageRef.child("videos/${videoUri.lastPathSegment}")
        videoRef.putFile(videoUri)
            .addOnSuccessListener {
                videoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        onSuccess(uri.toString()) // Pass the download URL
                    }
                    .addOnFailureListener { exception -> onFailure(exception) }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    override fun getVideoUrls(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        val templateVideosRef = storageRef.child("template_videos")
        templateVideosRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isNotEmpty()) {
                    val firstVideoRef = listResult.items[0]
                    firstVideoRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            onSuccess(listOf(uri.toString())) // Return the first video URL as a list
                        }
                        .addOnFailureListener { exception -> onFailure(exception) }
                } else {
                    onFailure(Exception("No videos found in the template_videos directory"))
                }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}

