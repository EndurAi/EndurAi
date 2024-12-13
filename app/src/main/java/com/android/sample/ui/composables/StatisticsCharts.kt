package com.android.sample.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.android.sample.ui.theme.TitleBlue
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.VerticalBlueGradient

@Preview
@Composable
private fun preview() {

  val pointsData: List<Point> =
      listOf(Point(0f, 562f), Point(1f, 1540f), Point(2f, 850f), Point(3f, 200f), Point(4f, 690f))
  WeekCaloriesLineChart(pointsData)
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun WeekCaloriesLineChart(pointsData: List<Point>) {

  val max = pointsData.maxOf { p -> p.y }.toInt()

  val steps = 5

  val xAxisData =
      AxisData.Builder()
          .axisStepSize(100.dp)
          .backgroundColor(Transparent)
          .steps(pointsData.size - 1)
          .labelData { i -> intToDayOfWeek(i) }
          .labelAndAxisLinePadding(15.dp)
          .shouldDrawAxisLineTillEnd(true)
          .build()

  val yAxisData =
      AxisData.Builder()
          .steps(steps)
          .backgroundColor(Transparent)
          .labelAndAxisLinePadding(30.dp)
          .labelData { i ->
            val yScale = max / steps
            (i * yScale).toString()
          }
          .shouldDrawAxisLineTillEnd(true)
          .build()

  val lineChartData =
      LineChartData(
          linePlotData =
              LinePlotData(
                  lines =
                      listOf(
                          Line(
                              dataPoints = pointsData,
                              LineStyle(color = TitleBlue, width = 12f),
                              IntersectionPoint(color = TitleBlue, radius = 4.dp),
                              SelectionHighlightPoint(color = TitleBlue),
                              ShadowUnderLine(alpha = 0.8f, brush = VerticalBlueGradient),
                              SelectionHighlightPopUp())),
              ),
          xAxisData = xAxisData,
          yAxisData = yAxisData,
          gridLines = GridLines(color = Transparent),
          backgroundColor = Transparent)
  LineChart(modifier = Modifier.fillMaxWidth().height(300.dp), lineChartData = lineChartData)
}

fun intToDayOfWeek(day: Int): String {
  return when (day) {
    0 -> "MON"
    1 -> "TUE"
    2 -> "WED"
    3 -> "THU"
    4 -> "FRI"
    5 -> "SAT"
    6 -> "SUN"
    else -> "Invalid"
  }
}
