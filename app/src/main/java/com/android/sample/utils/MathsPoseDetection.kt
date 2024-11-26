import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.acos
import kotlin.math.sqrt

/** Utility class for performing mathematical operations related to pose detection. */
class MathsPoseDetection {

  companion object {

    /**
     * Calculates the angle formed by three pose landmarks in 3D space.
     *
     * @param a The first pose landmark.
     * @param b The second pose landmark (vertex of the angle).
     * @param c The third pose landmark.
     * @return The angle in degrees.
     */
    fun angle(triplet: Triple<PoseLandmark, PoseLandmark, PoseLandmark>): Double {
      return angle(triplet.first, triplet.second, triplet.third)
    }

    /**
     * Calculates the angle formed by three points in 3D space represented as float arrays.
     *
     * @param a The first point (x, y, z).
     * @param b The second point (x, y, z) (vertex of the angle).
     * @param c The third point (x, y, z).
     * @return The angle in degrees.
     */
    fun angle(a: Triple<Float,Float,Float>, b: Triple<Float,Float,Float>, c:Triple<Float,Float,Float>): Double {
      return angle(
    a.first, a.second, a.third,
    b.first, b.second, b.third,
    c.first, c.second, c.third
      )
    }

    /**
     * Calculates the angle formed by three pose landmarks in 3D space.
     *
     * @param a The first pose landmark.
     * @param b The second pose landmark (vertex of the angle).
     * @param c The third pose landmark.
     * @return The angle in degrees.
     */
    fun angle(a: PoseLandmark, b: PoseLandmark, c: PoseLandmark): Double {
      return angle(
        a.position3D.x, a.position3D.y, a.position3D.z,
        b.position3D.x, b.position3D.y, b.position3D.z,
        c.position3D.x, c.position3D.y, c.position3D.z
      )
    }

    /**
     * Calculates the angle formed by three points in 3D space.
     *
     * @param a_x The x-coordinate of the first point.
     * @param a_y The y-coordinate of the first point.
     * @param a_z The z-coordinate of the first point.
     * @param b_x The x-coordinate of the second point (vertex of the angle).
     * @param b_y The y-coordinate of the second point (vertex of the angle).
     * @param b_z The z-coordinate of the second point (vertex of the angle).
     * @param c_x The x-coordinate of the third point.
     * @param c_y The y-coordinate of the third point.
     * @param c_z The z-coordinate of the third point.
     * @return The angle in degrees.
     */
    fun angle(a_x: Float, a_y: Float, a_z: Float,
              b_x: Float, b_y: Float, b_z: Float,
              c_x: Float, c_y: Float, c_z: Float): Double {
      // Create vectors BA and BC
      val ba = floatArrayOf(a_x - b_x, a_y - b_y, a_z - b_z)
      val bc = floatArrayOf(c_x - b_x, c_y - b_y, c_z - b_z)

      // Calculate dot product of BA and BC
      val dotProduct = ba[0] * bc[0] + ba[1] * bc[1] + ba[2] * bc[2]

      // Calculate magnitudes of BA and BC
      val magnitudeBA = sqrt(ba[0] * ba[0] + ba[1] * ba[1] + ba[2] * ba[2])
      val magnitudeBC = sqrt(bc[0] * bc[0] + bc[1] * bc[1] + bc[2] * bc[2])

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