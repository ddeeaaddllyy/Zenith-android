package com.ddeeaaddllyy.zenith.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun CaloriesBarChart(
    data: List<DayCalories>,
    target: Int,
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary
    val targetColor = MaterialTheme.colorScheme.onSurfaceVariant
    val maxValue = (data.maxOfOrNull { it.calories } ?: 0).coerceAtLeast(target).coerceAtLeast(1)

    Box(modifier = modifier.fillMaxWidth().height(120.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val barCount = data.size
            if (barCount == 0) return@Canvas
            val gap = 8.dp.toPx()
            val barWidth = (size.width - gap * (barCount - 1)) / barCount

            val targetY = size.height - (target.toFloat() / maxValue) * size.height
            drawLine(
                color = targetColor,
                start = Offset(0f, targetY),
                end = Offset(size.width, targetY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
            )

            data.forEachIndexed { index, day ->
                val barHeight = (day.calories.toFloat() / maxValue) * size.height
                val left = index * (barWidth + gap)
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight.coerceAtLeast(3f)),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }
    }
}

@Composable
fun WeightLineChart(
    points: List<WeightPoint>,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val dotColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        if (points.size < 2) return@Canvas
        val minWeight = points.minOf { it.weightKg }
        val maxWeight = points.maxOf { it.weightKg }
        val range = (maxWeight - minWeight).coerceAtLeast(0.5)
        val stepX = size.width / (points.size - 1)

        val offsets = points.mapIndexed { index, point ->
            val x = index * stepX
            val normalized = (point.weightKg - minWeight) / range
            val y = size.height - (normalized * size.height).toFloat()
            Offset(x, y)
        }

        for (i in 0 until offsets.size - 1) {
            drawLine(
                color = lineColor,
                start = offsets[i],
                end = offsets[i + 1],
                strokeWidth = 3.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
        offsets.forEach { offset ->
            drawCircle(color = dotColor, radius = 4.dp.toPx(), center = offset)
        }
    }
}

@Composable
fun MonthlyWorkoutsChart(
    data: List<MonthlyWorkoutStat>,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val maxValue = (data.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        val barCount = data.size
        if (barCount == 0) return@Canvas
        val gap = 10.dp.toPx()
        val barWidth = (size.width - gap * (barCount - 1)) / barCount

        data.forEachIndexed { index, month ->
            val barHeight = (month.count.toFloat() / maxValue) * size.height
            val left = index * (barWidth + gap)
            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight.coerceAtLeast(3f)),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }
    }
}
