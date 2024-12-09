package com.android.sample.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.android.sample.R
import com.android.sample.mlUtils.ExerciseFeedBack
import com.android.sample.mlUtils.MyPoseLandmark
import com.android.sample.mlUtils.PoseDetectionJoints
import com.android.sample.model.camera.CameraViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.io.File

/** A class that provides composables for displaying and interacting with the camera feed. */
class CameraFeedBack {

  companion object {
    private var recording: Recording? = null

    /**
     * A composable function that displays the camera screen.
     *
     * This function handles camera permission requests and displays the camera preview if the
     * permission is granted.
     *
     * @param cameraViewModel The ViewModel that manages the camera state.
     * @param modifier The modifier to be applied to the layout.
     */
    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    public fun CameraScreen(
      cameraViewModel: CameraViewModel,
      modifier: Modifier = Modifier,
      poseDetectionRequired: Boolean = false, // Not yet used,
      exerciseCriterions: ExerciseFeedBack.Companion.ExerciseCriterion? = null
    ) {


      val cameraPermissionState: PermissionState =
          rememberPermissionState(android.Manifest.permission.CAMERA)

      if (cameraPermissionState.status.isGranted) {
        Box(modifier = modifier) {
          CameraBody(cameraViewModel, poseDetectionRequired = poseDetectionRequired)
        }
      } else {
        LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
      }
    }

    /**
     * A composable function that displays the camera preview and a button to switch cameras.
     *
     * @param cameraViewModel The ViewModel that manages the camera state.
     */
    @Composable
    fun CameraBody(cameraViewModel: CameraViewModel, poseDetectionRequired: Boolean, exerciseCriterions: ExerciseFeedBack.Companion.ExerciseCriterion? = null) {

      val context = LocalContext.current
      val lifecycleOwner = LocalLifecycleOwner.current
      var lastPose by remember { mutableStateOf<List<MyPoseLandmark>>(ArrayList()) } // latest position of the joints
      var displayedJoints by remember { mutableStateOf(setOf<Triple<Int, Int, Int>>()) } // joints that need to be displayed






      lifecycleOwner.lifecycleScope.launch {
        cameraViewModel.lastPose.collect{
          lastPose = it
          val assesment = ExerciseFeedBack.assessLandMarks(it, exerciseCriterions!!)
          assesment.second.forEach {

          }

        }
      }



      Scaffold(
          modifier = Modifier.fillMaxSize(),
          floatingActionButton = {
            FloatingActionButton(
                onClick = { cameraViewModel.switchCamera() }, modifier = Modifier.size(30.dp)) {
                  Image(
                      painter = painterResource(id = R.drawable.baseline_flip_camera_android_24),
                      contentDescription = "Switch camera")
                }
          }){ pd: PaddingValues ->
  Box(modifier = Modifier
    .padding(pd)
    .fillMaxSize()) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
          PreviewView(context)
              .apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT)
                setBackgroundColor(android.graphics.Color.BLACK)
                scaleType = PreviewView.ScaleType.FIT_START
              }
              .also { previewView ->
                previewView.controller = cameraViewModel.cameraController.value
                // cameraViewModel.cameraController.value.bindToLifecycle(lifecycleOwner)
              }
        })

    Canvas(modifier = Modifier) {
      val off = 15
      if (lastPose.isNotEmpty()) {
        drawCircle(
          color = androidx.compose.ui.graphics.Color.Yellow,
          radius = 5f,
          center = Offset(0f/(16/9),0f/(16/9))
        )
        lastPose.forEach {
          drawCircle(
            color = androidx.compose.ui.graphics.Color.Yellow,
            radius = 5f,
            center = Offset(it.x*0.628f,it.y*0.628f)
          )
        }
      }
    }
  }
}

      // Bind the camera feedback stream to the composable and unbind when it the composable is no
      // longer displayed
      DisposableEffect(lifecycleOwner) {
        cameraViewModel.cameraController.value.bindToLifecycle(lifecycleOwner)
        onDispose { cameraViewModel.cameraController.value.unbind() }
      }
    }

    /**
     * Starts or stops recording a video.
     *
     * @param cameraController The LifecycleCameraController used to control the camera.
     * @param context The application context.
     */
    fun recordVideo(cameraController: LifecycleCameraController, context: Context) {

      val outputFile = File(context.filesDir.path + "/record.mp4")

      if (recording != null) {
        recording?.stop()
        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
        recording = null
        return
      }

      recording =
          cameraController.startRecording(
              FileOutputOptions.Builder(outputFile).build(),
              AudioConfig.AUDIO_DISABLED,
              ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                  is VideoRecordEvent.Finalize -> {
                    if (event.hasError()) {
                      recording?.close()
                      recording = null
                      Toast.makeText(
                              context, "An error happened while recording.", Toast.LENGTH_SHORT)
                          .show()
                    } else {
                      Toast.makeText(context, "Video is recorded successfully.", Toast.LENGTH_SHORT)
                          .show()
                    }
                  }
                }
              }
    }
  }
}
