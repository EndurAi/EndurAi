package com.android.sample.ui.composables

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.android.sample.ui.theme.TitleBlue
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.VerticalBlueGradient
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//@Preview
//@Composable
//private fun preview() {
//
//  val pointsData: List<Point> =
//      listOf(Point(0f, 562f), Point(1f, 1540f), Point(2f, 850f), Point(3f, 200f), Point(4f, 690f))
//  WeekCaloriesLineChart(pointsData)
//}
//
//@SuppressLint("SuspiciousIndentation")
//@Composable
//fun WeekCaloriesLineChart(pointsData: List<Point>) {
//
//  val max = pointsData.maxOf { p -> p.y }.toInt()
//
//  val steps = 5
//
//  val xAxisData =
//      AxisData.Builder()
//          .axisStepSize(100.dp)
//          .backgroundColor(Transparent)
//          .steps(pointsData.size - 1)
//          .labelData { i -> intToDayOfWeek(i) }
//          .labelAndAxisLinePadding(15.dp)
//          .shouldDrawAxisLineTillEnd(true)
//          .build()
//
//  val yAxisData =
//      AxisData.Builder()
//          .steps(steps)
//          .backgroundColor(Transparent)
//          .labelAndAxisLinePadding(30.dp)
//          .labelData { i ->
//            val yScale = max / steps
//            (i * yScale).toString()
//          }
//          .shouldDrawAxisLineTillEnd(true)
//          .build()
//
//  val lineChartData =
//      LineChartData(
//          linePlotData =
//              LinePlotData(
//                  lines =
//                      listOf(
//                          Line(
//                              dataPoints = pointsData,
//                              LineStyle(color = TitleBlue, width = 12f),
//                              IntersectionPoint(color = TitleBlue, radius = 4.dp),
//                              SelectionHighlightPoint(color = TitleBlue),
//                              ShadowUnderLine(alpha = 0.8f, brush = VerticalBlueGradient),
//                              SelectionHighlightPopUp())),
//              ),
//          xAxisData = xAxisData,
//          yAxisData = yAxisData,
//          gridLines = GridLines(color = Transparent),
//          backgroundColor = Transparent)
//  LineChart(modifier = Modifier
//      .fillMaxWidth()
//      .height(300.dp), lineChartData = lineChartData)
//}
//
//fun intToDayOfWeek(day: Int): String {
//  return when (day) {
//    0 -> "MON"
//    1 -> "TUE"
//    2 -> "WED"
//    3 -> "THU"
//    4 -> "FRI"
//    5 -> "SAT"
//    6 -> "SUN"
//    else -> "Invalid"
//  }
//}
//
//
//
//
//@Composable
//internal fun Chart1(uiFramework: UIFramework, modifier: Modifier) {
//    val modelProducer = remember { CartesianChartModelProducer() }
//    LaunchedEffect(Unit) {
//        withContext(Dispatchers.Default) {
//            modelProducer.runTransaction {
//                /* Learn more:
//                https://patrykandpatrick.com/vico/wiki/cartesian-charts/layers/line-layer#data. */
//                lineSeries { series(x, x.map { Random.nextFloat() * 15 }) }
//            }
//        }
//    }
//    when (uiFramework) {
//        UIFramework.Compose -> ComposeChart1(modelProducer, modifier)
//        UIFramework.Views -> ViewChart1(modelProducer, modifier)
//    }
//}
//
//@Composable
//private fun ComposeChart1(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
//    val marker = rememberMarker()
//    CartesianChartHost(
//        chart =
//        rememberCartesianChart(
//            rememberLineCartesianLayer(
//                LineCartesianLayer.LineProvider.series(
//                    LineCartesianLayer.rememberLine(
//                        remember { LineCartesianLayer.LineFill.single(fill(Color(0xffa485e0))) }
//                    )
//                )
//            ),
//            startAxis = VerticalAxis.rememberStart(),
//            bottomAxis =
//            HorizontalAxis.rememberBottom(
//                guideline = null,
//                itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
//            ),
//            marker = marker,
//            layerPadding = cartesianLayerPadding(scalableStart = 16.dp, scalableEnd = 16.dp),
//            persistentMarkers = rememberExtraLambda(marker) { marker at PERSISTENT_MARKER_X },
//        ),
//        modelProducer = modelProducer,
//        modifier = modifier,
//        zoomState = rememberVicoZoomState(zoomEnabled = false),
//    )
//}
//
//@Composable
//private fun ViewChart1(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
//    val marker = rememberMarker()
//    AndroidViewBinding(
//        { inflater, parent, attachToParent ->
//            Chart1Binding.inflate(inflater, parent, attachToParent).apply {
//                with(chartView) {
//                    chart =
//                        chart?.copy(persistentMarkers = { marker at PERSISTENT_MARKER_X }, marker = marker)
//                    this.modelProducer = modelProducer
//                }
//            }
//        },
//        modifier,
//    )
//}
//
//private const val PERSISTENT_MARKER_X = 7f
//
//private val x = (1..7).toList()
//
//internal enum class UIFramework(@StringRes val labelResourceID: Int) {
//    Compose(R.string.compose),
//    Views(R.string.views),
//}

@Composable
fun Charts(modifier: Modifier = Modifier) {

    LineChart(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
        data = listOf(
            Line(
                label = "Windows",
                values = listOf(28.0,41.0,5.0,10.0,35.0),
                color = SolidColor(Color(0xFF23af92)),
                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(width = 2.dp),
            )
        ),
        animationMode = AnimationMode.Together(delayBuilder = {
            it * 500L
        }),
    )



}
