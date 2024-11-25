import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

/** Utility class for performing mathematical operations related to pose detection. */
class MathsPoseDetection {

  companion object {

    /**
     * Calculates the angle formed by three pose landmarks.
     *
     * @param a The first pose landmark.
     * @param b The second pose landmark (vertex of the angle).
     * @param c The third pose landmark.
     * @return The angle in degrees.
     */
    fun angle(triplet : Triple<PoseLandmark,PoseLandmark,PoseLandmark>): Double {
      return angle(
        triplet.first, triplet.second, triplet.third)
    }


fun angle(a: Pair<Float, Float>, b: Pair<Float, Float>, c: Pair<Float, Float>): Double {
  return angle(a.first, a.second, b.first, b.second, c.first, c.second)
}

    /**
     * Calculates the angle formed by three pose landmarks.
     *
     * @param a The first pose landmark.
     * @param b The second pose landmark (vertex of the angle).
     * @param c The third pose landmark.
     * @return The angle in degrees.
     */
    fun angle(a: PoseLandmark, b: PoseLandmark, c: PoseLandmark): Double {
      return angle(
          a.position.x, a.position.y, b.position.x, b.position.y, c.position.x, c.position.y)
    }

    /**
     * Calculates the angle formed by three pose landmarks.
     *
     * @param a The first position landmark.
     * @param b The second position landmark (vertex of the angle).
     * @param c The third position landmark.
     * @return The angle in degrees.
     */
    fun angle(a_x: Float, a_y: Float, b_x: Float, b_y: Float, c_x: Float, c_y: Float): Double {
      // Create vectors BA and BC
      val ba = Pair(a_x - b_x, a_y - b_y)
      val bc = Pair(c_x - b_x, c_y - b_y)

      // Calculate dot product of BA and BC
      val dotProduct = ba.first * bc.first + ba.second * bc.second

      // Calculate magnitudes of BA and BC
      val magnitudeBA = sqrt(ba.first * ba.first + ba.second * ba.second)
      val magnitudeBC = sqrt(bc.first * bc.first + bc.second * bc.second)

      // Calculate the angle in radians using the dot product formula
      val epsilon = 1e-6
      val angleRadians =
          if (magnitudeBA > epsilon && magnitudeBC > epsilon)
              acos(dotProduct / (magnitudeBA * magnitudeBC))
          else 0f

      // Convert radians to degrees
      return degrees(angleRadians)
    }

    /**
     * Converts radians to degrees.
     *
     * @param radians The angle in radians.
     * @return The angle in degrees.
     */
    private fun degrees(radians: Float): Double {
      return radians * (180.0 / Math.PI)
    }
  }
}
