package com.android.sample.mlUtils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.pose.PoseLandmark
import androidx.compose.foundation.Canvas
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

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



    val ALL_JOINTS: Set<Triple<Int, Int, Int>> = setOf(
      Triple(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
      Triple(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
      Triple(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER),
      Triple(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER),
      Triple(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
      Triple(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
      Triple(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
      Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE),
      Triple(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
      Triple(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.LEFT_SHOULDER),
      Triple(PoseLandmark.RIGHT_HIP, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
      Triple(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
    )


    @Composable
    fun DrawBody(
      lastPose : List<MyPoseLandmark>,
      wrongJoints: Set<Triple<Int,Int,Int>>,
      cumulatedOffset : Offset = Offset.Zero,
      jointColor : Color = Color.Blue,
      jointStroke: Float = 5f,
      lineColor : Color = Color.Magenta,
      lineStroke : Float = 5f,
      wrongJointColor : Color = Color.Blue,
      wrongJointStroke: Float = 5f,
      wronglineColor : Color = Color.Magenta,
      wronglineStroke : Float = 5f,
      modifier: Modifier = Modifier
    ){

      Canvas(modifier){

        ALL_JOINTS.forEach { triple ->
          //get the 3 points position
          val a = lastPose[triple.first]
          val b = lastPose[triple.second]
          val c = lastPose[triple.third]
          val isWrong = wrongJoints.contains(triple)
          val roundColor = if (isWrong.not()) jointColor else wrongJointColor
          val roundStroke = if (isWrong.not()) jointStroke else wrongJointStroke
          val currentLineColor = if (isWrong.not()) lineColor else wronglineColor
          val currentLineStroke = if (isWrong.not()) lineStroke else wronglineStroke


          //draw the 3 points
          val offset = 1f
          drawCircle(
            color =roundColor,
            radius = roundStroke,
            center = Offset(a.x * offset +cumulatedOffset.x, a.y * offset+cumulatedOffset.y)
          )

          drawCircle(
            color =roundColor,
            radius = roundStroke,
            center = Offset(b.x * offset+cumulatedOffset.x, b.y * offset+cumulatedOffset.y)
          )

          drawCircle(
            color =roundColor,
            radius = roundStroke,
            center = Offset(c.x * offset+cumulatedOffset.x, c.y * offset+cumulatedOffset.y)
          )

          // Draw a red line from a to b
          drawLine(
            color = currentLineColor,
            start = Offset(a.x * offset+cumulatedOffset.x, a.y * offset+cumulatedOffset.y),
            end = Offset(b.x * offset+cumulatedOffset.x, b.y * offset+cumulatedOffset.y),
            strokeWidth = currentLineStroke
          )

// Draw a red line from b to c
          drawLine(
            color = currentLineColor,
            start = Offset(b.x * offset+cumulatedOffset.x, b.y * offset+cumulatedOffset.y),
            end = Offset(c.x * offset+cumulatedOffset.x, c.y * offset+cumulatedOffset.y),
            strokeWidth = currentLineStroke
          )


        }

      }
    }























  }


}
