// portions of this code were done with the help of ChatGPT and Github Copilot

package com.android.sample.model.video

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class VideoRepositoryStorage(
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : VideoRepository {

    private val storageRef: StorageReference = storageReference
    private val videosCollection = firestore.collection("videos") // Firestore collection for metadata

    // Upload video to Firebase Storage and store metadata in Firestore
    // Not currently used in the application but will be used in a feature in the future
    override fun uploadVideo(
        video: Video,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val videoRef = storageRef.child("user_videos/${video.url}")
        videoRef
            .putFile(video.url.toUri())
            .addOnSuccessListener {
                videoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Store metadata after successful video upload
                        val videoMetadata = Video(
                            title = video.title,
                            url = video.url,
                            tag = video.tag,
                            thumbnailUrl = video.thumbnailUrl,
                            duration = video.duration,
                            description = video.description
                        )

                        // Save metadata to Firestore
                        videosCollection.add(videoMetadata)
                            .addOnSuccessListener { documentReference ->
                                onSuccess(uri.toString()) // Pass the download URL
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                    .addOnFailureListener { exception -> onFailure(exception) }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // Retrieve all video URLs with metadata from Firestore
    override fun getVideos(onSuccess: (List<Video>) -> Unit, onFailure: (Exception) -> Unit) {
        videosCollection.get()
            .addOnSuccessListener { result ->
                val videoList = result.documents.mapNotNull { doc ->
                    doc.toObject(Video::class.java) // Deserializing to Video class
                }
                onSuccess(videoList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
