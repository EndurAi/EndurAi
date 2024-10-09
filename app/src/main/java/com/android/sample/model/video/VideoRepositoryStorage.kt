// portions of this code were done with the help of ChatGPT

package com.android.sample.model.video


import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class VideoRepositoryStorage : VideoRepository {

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    // Uploads video to Firebase Storage
    override fun uploadVideo(videoUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val videoRef = storageRef.child("videos/${videoUri.lastPathSegment}")

        videoRef.putFile(videoUri)
            .addOnSuccessListener { taskSnapshot ->
                videoRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())  // Pass the download URL
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun getVideoUrls(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        // Reference to the template_videos directory
        val templateVideosRef = storageRef.child("template_videos")

        // List all files in the template_videos directory
        templateVideosRef.listAll()
            .addOnSuccessListener { listResult ->
                // Check if there are any files in the directory
                if (listResult.items.isNotEmpty()) {
                    // Get the first video (assuming the first item is a video file)
                    val firstVideoRef = listResult.items[0]

                    // Get the download URL for the first video
                    firstVideoRef.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(listOf(uri.toString()))  // Return the first video URL as a list
                    }.addOnFailureListener { exception ->
                        onFailure(exception)  // Handle failure to get download URL
                    }
                } else {
                    // No files found in the directory
                    onFailure(Exception("No videos found in the template_videos directory"))
                }
            }.addOnFailureListener { exception ->
                // Handle failure to list files
                onFailure(exception)
            }
    }
}
