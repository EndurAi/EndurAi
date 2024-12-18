package com.android.sample.mlUtils

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.android.sample.R
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.LightBlue
import com.google.mlkit.vision.pose.PoseLandmark

class PoseDetectionJoints {
  companion object {
    /*
    LR Elbow - Shoulder - Hip
     */
    val LEFT_ELBOW_SHOULDER_HIP =
        Triple(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)
    val RIGHT_ELBOW_SHOULDER_HIP =
        Triple(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)

    /*
    LR wrist - Elbow - Shoulder
    */
    val LEFT_WRIST_ELBOW_SHOULDER =
        Triple(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER)
    val RIGHT_WRIST_ELBOW_SHOULDER =
        Triple(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER)

    /*
    LR Shoulder - Hip - Knee
    */
    val LEFT_SHOULDER_HIP_KNEE =
        Triple(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
    val RIGHT_SHOULDER_HIP_KNEE =
        Triple(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)

    /*
    LR Hip - Knee - ankle
    */
    val LEFT_HIP_KNEE_ANKLE =
        Triple(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE)
    val RIGHT_HIP_KNEE_ANKLE =
        Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)

    /*
    LR Elbow - Shoulder - Oposite Shoulder
    */
    val LEFT_ELBOW_SHOULDER_OPPSHOULDER =
        Triple(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER)
    val RIGHT_ELBOW_SHOULDER_OPPSHOULDER =
        Triple(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.LEFT_SHOULDER)

    /*
    LR OppHip-Hip- Knee
    */
    val LEFT_OPPHIP_HIP_KNEE =
        Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
    val RIGHT_OPPHIP_HIP_KNEE =
        Triple(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)

    val ALL_JOINTS_LINKS: Set<Triple<Int, Int, Int>> =
        setOf(
            Triple(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
            Triple(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
            Triple(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER),
            Triple(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER),
            Triple(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
            Triple(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
            Triple(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
            Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE),
            Triple(
                PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
            Triple(
                PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.LEFT_SHOULDER),
            Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
            Triple(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE))

    /**
     * Composable function to draw the body skeleton based on the provided pose landmarks.
     *
     * @param lastPose A list of `MyPoseLandmark` representing the last detected pose landmarks.
     * @param wrongJointsLinks A set of `Triple` representing the joints that are incorrectly
     *   positioned.
     * @param cumulatedOffset An `Offset` to adjust the position of the skeleton.
     * @param jointColor The color to use for correctly positioned joints.
     * @param jointStroke The stroke width to use for correctly positioned joints.
     * @param lineColor The color to use for correctly positioned lines.
     * @param lineStroke The stroke width to use for correctly positioned lines.
     * @param wrongJointColor The color to use for incorrectly positioned joints.
     * @param wrongJointStroke The stroke width to use for incorrectly positioned joints.
     * @param wronglineColor The color to use for incorrectly positioned lines.
     * @param wronglineStroke The stroke width to use for incorrectly positioned lines.
     * @param modifier The `Modifier` to be applied to the `Canvas`.
     */
    @Composable
    fun DrawBody(
        lastPose: List<MyPoseLandmark>,
        wrongJointsLinks: Set<Triple<Int, Int, Int>>,
        cumulatedOffset: Offset = Offset.Zero,
        jointColor: Color = DarkBlue,
        jointStroke: Float = 10f,
        lineColor: Color = LightBlue,
        lineStroke: Float = 8f,
        wrongJointColor: Color = Color.Yellow,
        wrongJointStroke: Float = 10f,
        wronglineColor: Color = Color.Red,
        wronglineStroke: Float = 8f,
        modifier: Modifier = Modifier
    ) {
      var toastWasDisplayed by remember { mutableStateOf(false) }

      val context = LocalContext.current
      if (toastWasDisplayed.not()) {
        Toast.makeText(context, "Drag to move the skeleton", Toast.LENGTH_LONG).show()
      }
      var alpha by remember { mutableStateOf(1f) }
      LaunchedEffect(Unit) {
        for (i in 100 downTo 0) {
          alpha = i / 100f
          kotlinx.coroutines.delay(10)
        }
      }
      Image(
          painter = painterResource(id = R.drawable.baseline_touch_app_24),
          contentDescription = "Touch image",
          modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).alpha(alpha))

      Canvas(modifier) {
        if (toastWasDisplayed.not()) {
          toastWasDisplayed = true
        }

        val wrongJoints: Set<Int> =
            wrongJointsLinks.flatMap { listOf(it.first, it.second, it.third) }.toSet()
        val wrongLines: MutableSet<Pair<Int, Int>> = mutableSetOf()
        wrongJointsLinks.forEach { (a, b, c) ->
          wrongLines += a to b
          wrongLines += b to c
        }

        ALL_JOINTS_LINKS.forEach { triple ->
          // get the 3 points position
          val a = lastPose[triple.first]
          val b = lastPose[triple.second]
          val c = lastPose[triple.third]
          val isWrongJoint1 = triple.first in wrongJoints
          val isWrongJoint2 = triple.first in wrongJoints
          val isWrongJoint3 = triple.first in wrongJoints

          val line1IsWrong =
              (wrongLines.contains(triple.first to triple.second) ||
                  wrongLines.contains(triple.second to triple.first))
          val line2IsWrong =
              (wrongLines.contains(triple.second to triple.third) ||
                  wrongLines.contains(triple.third to triple.second))

          // draw the 3 points
          val offset = 1f
          drawCircle(
              color = if (isWrongJoint1) wrongJointColor else jointColor,
              radius = if (isWrongJoint1) wrongJointStroke else jointStroke,
              center = Offset(a.x * offset + cumulatedOffset.x, a.y * offset + cumulatedOffset.y))

          drawCircle(
              color = if (isWrongJoint2) wrongJointColor else jointColor,
              radius = if (isWrongJoint2) wrongJointStroke else jointStroke,
              center = Offset(b.x * offset + cumulatedOffset.x, b.y * offset + cumulatedOffset.y))

          drawCircle(
              color = if (isWrongJoint3) wrongJointColor else jointColor,
              radius = if (isWrongJoint3) wrongJointStroke else jointStroke,
              center = Offset(c.x * offset + cumulatedOffset.x, c.y * offset + cumulatedOffset.y))

          // Draw a red line from a to b
          drawLine(
              color = if (line1IsWrong) wronglineColor else lineColor,
              start = Offset(a.x * offset + cumulatedOffset.x, a.y * offset + cumulatedOffset.y),
              end = Offset(b.x * offset + cumulatedOffset.x, b.y * offset + cumulatedOffset.y),
              strokeWidth = if (line1IsWrong) wronglineStroke else lineStroke)

          // Draw a red line from b to c
          drawLine(
              color = if (line2IsWrong) wronglineColor else lineColor,
              start = Offset(b.x * offset + cumulatedOffset.x, b.y * offset + cumulatedOffset.y),
              end = Offset(c.x * offset + cumulatedOffset.x, c.y * offset + cumulatedOffset.y),
              strokeWidth = if (line2IsWrong) wronglineStroke else lineStroke)
        }
      }
    }
  }
}
