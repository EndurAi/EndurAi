package com.android.sample.ui.mlFeedback

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.android.sample.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.io.IOException
import kotlin.reflect.jvm.internal.impl.incremental.components.Position

@SuppressLint("MutableCollectionMutableState")
@Preview
@Composable
fun JointsScreen() {
  val context = LocalContext.current
  val path = "android.resource://" + context.packageName + "/" + R.raw.image
  val uri = path.toUri()
  val options = PoseDetectorOptions.Builder()
    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
    .build()

  val poseDetector = PoseDetection.getClient(options)
  lateinit var image: InputImage

  var landmarks = remember { mutableStateListOf<PoseLandmark>() }

  try {
    image = InputImage.fromFilePath(context, uri)
  } catch (e: IOException) {
    e.printStackTrace()
  }

  poseDetector.process(image)
    .addOnSuccessListener { pose ->
      Log.d("MLDEBUG", "JointsScreen: YES")
     landmarks.addAll(pose.allPoseLandmarks)

    }
    .addOnFailureListener { e ->
      Log.d("MLDEBUG", "JointsScreen: $e")
    }

  val bitmap = BitmapFactory.decodeStream(context.resources.openRawResource(R.raw.image))

  if (landmarks.isNotEmpty()) {

    Canvas(modifier = Modifier.fillMaxSize()) {
      drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawBitmap(bitmap, 0f, 0f, null)
        landmarks.forEach { landmark->
          drawCircle(
            color = Color.Red,
            radius = 3f,
            center = androidx.compose.ui.geometry.Offset(landmark.position.x, landmark.position.y)
          )
        }

      }
    }
  }
}







private val retriever by lazy(::MediaMetadataRetriever)

private fun loadAndPrepareVideo() {

  val resources = LocalContext.current.resources
  val assetFileDescriptor = resources.openRawResourceFd(R.raw.image)
  retriever.setDataSource(
    assetFileDescriptor.fileDescriptor,
    assetFileDescriptor.startOffset,
    assetFileDescriptor.length
  )


}

private fun getRawResourceUriString(@RawRes rawResourceId: Int): String {
  val packageName = packageName
  return "android.resource://$packageName/raw/" + resources.getResourceEntryName(rawResourceId)
}