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

// TODO: Inicializálni a speedHistory-t a TourStartedSettingsViewModel-ben 1 db 0-val így lesz egy 0 és egy max érték is a diagrammon
@Composable
fun SpeedChart(tourStartedSettingsViewModel: TourStartedSettingsViewModel) {
    val speedHistory = tourStartedSettingsViewModel.getSpeedHistory()
    val pointsData = speedHistory.mapIndexed { index, speed ->
        co.yml.charts.common.model.Point(index.toFloat(), speed.toFloat())
    }

    val uniqueSpeeds = speedHistory.distinct().sorted()
    val yAxisSteps = uniqueSpeeds.size

    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .backgroundColor(Color.LightGray)
        .steps(pointsData.size - 1)
        .labelData { it.toString() }
        .labelAndAxisLinePadding(10.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .backgroundColor(Color.LightGray)
        .steps(yAxisSteps - 1)
        .labelData { uniqueSpeeds[it].toString() }
        .labelAndAxisLinePadding(10.dp)
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
        gridLines = GridLines(color = Color.Gray.copy(alpha = 0.3f)),
        backgroundColor = Color.White
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}