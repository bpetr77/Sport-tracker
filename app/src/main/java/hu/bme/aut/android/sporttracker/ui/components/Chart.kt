package hu.bme.aut.android.sporttracker.ui.components

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

@Composable
fun SpeedChart(ListOfData: List<Double>, xStep: Float = 30f) {
    if (ListOfData.isEmpty()) {
        // Handle empty list case, e.g., show a message or an empty chart
        return
    }
    val pointsData = ListOfData.mapIndexed { index, data ->
        co.yml.charts.common.model.Point(index.toFloat(), data.toFloat())
    }
    // Check if all data points are the same
//    val allSame = ListOfData.distinct().size == 1
//    val pointsData = ListOfData.mapIndexed { index, data ->
//        val adjustedData = if (allSame) data + (index * 0.0001) else data
//        co.yml.charts.common.model.Point(index.toFloat(), adjustedData.toFloat())
//    }

    val min = (ListOfData.minOrNull() ?: 0).toFloat()
    val max = (ListOfData.maxOrNull() ?: 0).toFloat()
    val stepSize = (((max - min) / 4)).toFloat()


    //val yAxisLabels = listOf(min, min + stepSize, min + (stepSize * 2), min + (stepSize * 3), max)


    val formattedMin = String.format("%.1f", min)
    val formattedMax = String.format("%.1f", max)

    val yAxisLabels = listOf(
        formattedMin,
        String.format("%.1f", min + stepSize),
        String.format("%.1f", min + (stepSize * 2)),
        String.format("%.1f", min + (stepSize * 3)),
        formattedMax
    )



    val xAxisData = AxisData.Builder()
        .axisStepSize(xStep.dp)
        .backgroundColor(Color.Gray)
        .steps(pointsData.size - 1)
        //.labelData { if (it == 0) "  0" else it.toString() } TODO: if the data is too big to show all the labels then dont show all of them
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
                    lineStyle = LineStyle(),
                    selectionHighlightPoint = SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
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