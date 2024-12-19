package com.android.sample.ui.composables

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.model.workout.WorkoutType
import com.android.sample.ui.theme.LegendBodyweight
import com.android.sample.ui.theme.LegendRunning
import com.android.sample.ui.theme.LegendYoga
import com.android.sample.ui.theme.TopBarBlue
import com.android.sample.ui.theme.Transparent
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun Charts(data : List<Double>, labelTitle : String ) {

    LineChart(
        modifier = Modifier
            .height(150.dp)
            .width(200.dp),
        data = listOf(
            Line(
                label = labelTitle,
                values = data,
                color = SolidColor(TopBarBlue),
                firstGradientFillColor = LegendBodyweight.copy(alpha = .8f),
                secondGradientFillColor = Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 500,
                drawStyle = DrawStyle.Stroke(width = 2.dp),
            )
        ),
        animationMode = AnimationMode.Together(delayBuilder = {
            it * 500L
        }),
        labelProperties = LabelProperties(
            enabled = true,
    textStyle = MaterialTheme.typography.labelSmall,
    padding = 1.dp,
    labels = listOf(
        stringResource(R.string.mon).first().toString(),
        stringResource(R.string.tue).first().toString(),
        stringResource(R.string.wed).first().toString(),
        stringResource(R.string.thu).first().toString(),
        stringResource(R.string.fri).first().toString(),
        stringResource(R.string.sat).first().toString(),
        stringResource(R.string.son).first().toString()),
            rotation = LabelProperties.Rotation(degree = 0f),
    )
        , gridProperties = GridProperties(enabled = false),
        labelHelperPadding = 5.dp

    )



}

@Composable
fun PieChartWorkoutType(
    frequency: Map<WorkoutType, Double>
) {
    val data = listOf(
        frequency.get(WorkoutType.BODY_WEIGHT)
            ?.let { Pie(label = stringResource(R.string.TitleTabBody),data = it, color = LegendBodyweight, selectedColor = LegendBodyweight) },
        frequency.get(WorkoutType.YOGA)
            ?.let { Pie(label = stringResource(R.string.TitleTabYoga),data = it, color = LegendYoga, selectedColor = LegendYoga) },
        frequency.get(WorkoutType.RUNNING)?.let {
            Pie(label = stringResource(R.string.TitleTabRunning),data = it, color = LegendRunning,
                selectedColor = LegendRunning)
        },
    )

    SmallLegend()

    Spacer(Modifier.height(10.dp))

    PieChart(
        modifier = Modifier.size(200.dp),
        data = data as List<Pie>,
        onPieClick = {},
        selectedScale = 1.2f,
        scaleAnimEnterSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        colorAnimEnterSpec = tween(300),
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        spaceDegree = 0f,
        selectedPaddingDegree = 4f,
        style = Pie.Style.Stroke(width = 30.dp)
    )

}
