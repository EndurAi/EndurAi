import org.junit.Assert.assertEquals
import org.junit.Test

class MathsPoseDetectionTest {

  @Test
  fun `angle_shouldReturn180Degrees_AngleIsFlat`() {
    val a_x = 0f
    val a_y = 0f
    val b_x = 1f
    val b_y = 0f
    val c_x = 2f
    val c_y = 0f

    val angle = MathsPoseDetection.angle(a_x, a_y, a_z = 0f, b_x, b_y, b_z = 0f, c_x, c_y, c_z = 0f)
    assertEquals(180.0, angle, 0.001)
  }

  @Test
  fun `angle_shouldReturn90Degrees_whenPointsFormRightAngle`() {
    val a_x = 0f
    val a_y = 1f
    val b_x = 0f
    val b_y = 0f
    val c_x = 1f
    val c_y = 0f

    val angle = MathsPoseDetection.angle(a_x, a_y, a_z = 0f, b_x, b_y, b_z = 0f, c_x, c_y, c_z = 0f)
    assertEquals(90.0, angle, 0.001)
  }

  @Test
  fun `angle_shouldReturn180Degrees_whenPointsAreCollinearButReversed`() {
    val a_x = 2f
    val a_y = 0f
    val b_x = 1f
    val b_y = 0f
    val c_x = 0f
    val c_y = 0f

    val angle = MathsPoseDetection.angle(a_x, a_y, a_z = 0f, b_x, b_y, b_z = 0f, c_x, c_y, c_z = 0f)
    assertEquals(180.0, angle, 0.001)
  }

  @Test
  fun `angle_shouldReturnCorrectAngle_whenPointsFormAcuteAngle`() {
    val a_x = 1f
    val a_y = 1f
    val b_x = 0f
    val b_y = 0f
    val c_x = 1f
    val c_y = 0f

    val angle = MathsPoseDetection.angle(a_x, a_y, a_z = 0f, b_x, b_y, b_z = 0f, c_x, c_y, c_z = 0f)
    assertEquals(45.0, angle, 0.001)
  }
}
