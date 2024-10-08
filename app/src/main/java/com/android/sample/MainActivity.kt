//package com.android.videoView.setVideoURI
//
//import com.android.sample.R
//
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.MediaController
//import android.widget.VideoView
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_main)
////
////        // Reference to the VideoView in your layout
////        val videoView = findViewById<VideoView>(R.id.videoView)
////        if (videoView == null) {
////            Log.e("MainActivity", "VideoView not found")
////            return
////        } else {
////            Log.d("MainActivity", "VideoView found")
////        }
////
////        // MediaController to control play/pause/seek
////        val mediaController = MediaController(this)
////        mediaController.setAnchorView(videoView)
////        videoView.setMediaController(mediaController)
////
////        // Initialize Firebase Storage
////        val storage = Firebase.storage
////        val storageRef = storage.reference
////
////        // Reference to the video file in Google Cloud Storage
////        val videoRef = storageRef.child("my_video.mp4")
////
////        // Get the download URL
////
////        videoRef.downloadUrl.addOnSuccessListener { uri ->
////            // Log the download URL
////            Log.d("MainActivity", "Download URL: $uri")
////
////            // Load video using the retrieved URL
////            videoView.setVideoURI(uri)
////            videoView.requestFocus()
////
////            // Log VideoView state
////            videoView.setOnPreparedListener {
////                Log.d("MainActivity", "VideoView is prepared")
////                videoView.start() // Start video when prepared
////            }
////        }.addOnFailureListener { exception ->
////            // Handle errors
////            Log.e("MainActivity", "Error getting download URL", exception)
////        }
////
////        // Error handling for video playback issues
////        videoView.setOnErrorListener { _, what, extra ->
////            Log.e("MainActivity", "Error occurred: what=$what, extra=$extra")
////            true
////        }
//
//    }
//}



package com.android.sample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reference to the VideoView in your layout
        val videoView = findViewById<VideoView>(R.id.videoView)
        if (videoView == null) {
            Log.e("MainActivity", "VideoView not found")
            return
        } else {
            Log.d("MainActivity", "VideoView found")
        }

        // MediaController to control play/pause/seek
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Load the video from the res/raw folder
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.my_video)

        // Log the video URI
        Log.d("MainActivity", "Local Video URI: $videoUri")

        // Set the video URI to the VideoView
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()

        // Log VideoView state and start video playback when ready
        videoView.setOnPreparedListener {
            Log.d("MainActivity", "VideoView is prepared")
            videoView.start() // Start video when prepared
        }

        // Error handling for video playback issues
        videoView.setOnErrorListener { _, what, extra ->
            Log.e("MainActivity", "Error occurred: what=$what, extra=$extra")
            true
        }
    }
}

