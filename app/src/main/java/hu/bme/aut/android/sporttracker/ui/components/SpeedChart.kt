package hu.bme.aut.android.sporttracker.ui.components

import android.graphics.Point
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
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
import hu.bme.aut.android.sporttracker.ui.screens.Settings.TourStartedSettingsViewModel

@Composable
fun SpeedChart(tourStartedSettingsViewModel: TourStartedSettingsViewModel) {
    val speedHistory = tourStartedSettingsViewModel.getSpeedHistory()
    val pointsData = speedHistory.mapIndexed { index, speed ->
        co.yml.charts.common.model.Point(index.toFloat(), speed.toFloat())
    }

    val maxSpeed = ((speedHistory.maxOrNull() ?: 0f) * 10f).toInt() / 10f
    val stepSize = ((maxSpeed / 4) * 10f).toInt() / 10f

    val yAxisLabels = listOf(0f, stepSize, stepSize * 2, stepSize * 3, maxSpeed)

    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .backgroundColor(Color.Gray)
        .steps(pointsData.size - 1)
        .labelData { if (it == 0) "  0" else it.toString() }
        .startPadding(12.dp)
        //.labelAndAxisLinePadding(10.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .backgroundColor(Color.Gray)
        .steps(4)
        .labelData { yAxisLabels[it].toString() }
        //.labelAndAxisLinePadding(10.dp)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = Color.LightGray.copy(alpha = 0.3f)),
        backgroundColor = Color.Gray
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        lineChartData = lineChartData
    )
}