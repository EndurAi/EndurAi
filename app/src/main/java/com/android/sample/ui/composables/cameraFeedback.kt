package com.android.sample.ui.composables

import android.content.Context
import android.provider.MediaStore.Audio
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.camera.view.video.ExperimentalVideo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.api.Distribution.BucketOptions.Linear
import java.io.File

class CameraFeedBack {

  companion object {
    private var recording: Recording? = null


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    public fun CameraScreen() {

      val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

      if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.size(30.dp)) {
          CameraBody()
        }
      }
    }

    @androidx.annotation.OptIn(ExperimentalVideo::class)
    @Composable
    fun CameraBody() {

      val context = LocalContext.current
      val lifecycleOwner = LocalLifecycleOwner.current
      val cameraController = remember { LifecycleCameraController(context).apply {
        setEnabledUseCases(CameraController.VIDEO_CAPTURE)
      } }

      cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // Use the fron cam


      Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
          FloatingActionButton(onClick = {
            recordVideo(
              cameraController,
              context
            )
          }) { Text("hi") }
        }
      ) { pd: PaddingValues ->

        AndroidView(
          modifier = Modifier.padding(pd).fillMaxSize(),
          factory = { context ->
            PreviewView(context).apply {
              layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )
              setBackgroundColor(android.graphics.Color.BLACK)
              scaleType = PreviewView.ScaleType.FILL_START
            }.also { previewView ->
              previewView.controller = cameraController
              cameraController.bindToLifecycle(lifecycleOwner)

            }
          })


      }

    }


    @androidx.annotation.OptIn(ExperimentalVideo::class)
    fun recordVideo(cameraController: LifecycleCameraController, context: Context) {

      val outputFile = File(context.filesDir.path + "/record.mp4")

      if (recording != null) {
        recording?.stop()
        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
        recording = null
        return
      }

      recording = cameraController.startRecording(
        FileOutputOptions.Builder(outputFile).build(),
        AudioConfig.AUDIO_DISABLED,
        ContextCompat.getMainExecutor(context)
      ) { event ->
        when (event) {
          is VideoRecordEvent.Finalize -> {
            if (event.hasError()) {
              recording?.close()
              recording = null
              Toast.makeText(context, "An error happened while recording.", Toast.LENGTH_SHORT)
                .show()
            } else {
              Toast.makeText(context, "Video is recorded successfully.", Toast.LENGTH_SHORT).show()
            }
          }


        }
      }


    }
  }
}