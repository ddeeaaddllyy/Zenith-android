package com.ddeeaaddllyy.zenith.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddeeaaddllyy.zenith.ui.common.SectionLabel
import com.ddeeaaddllyy.zenith.ui.common.ZenithCard
import com.ddeeaaddllyy.zenith.ui.common.formatKcal
import com.ddeeaaddllyy.zenith.ui.common.formatWeight
import com.ddeeaaddllyy.zenith.ui.common.formatWeightDelta
import com.ddeeaaddllyy.zenith.ui.common.weekdayShort

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            LoadingIndicator()
            return@Surface
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Статистика",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item { TodayCaloriesCard(state) }
            item { TodayWorkoutCard(state) }
            item { WeekCard(state) }
            item { WeightCard(state) }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun TodayCaloriesCard(state: StatisticsUiState) {
    ZenithCard {
        SectionLabel("Сегодня")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "${state.caloriesToday}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "из ${state.calorieTarget} ккал",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (state.caloriesRemaining >= 0) "${state.caloriesRemaining} осталось" else "${-state.caloriesRemaining} сверх нормы",
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.caloriesRemaining >= 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
            )
        }
        val progress = if (state.calorieTarget > 0) {
            (state.caloriesToday.toFloat() / state.calorieTarget).coerceIn(0f, 1f)
        } else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun TodayWorkoutCard(state: StatisticsUiState) {
    ZenithCard {
        SectionLabel("Тренировки сегодня")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(label = "Тренировок", value = "${state.workoutsToday}")
            StatItem(label = "Сожжено", value = state.caloriesBurnedToday.formatKcal())
        }
    }
}

@Composable
private fun WeekCard(state: StatisticsUiState) {
    ZenithCard {
        SectionLabel("За неделю")
        CaloriesBarChart(data = state.last7DaysCalories, target = state.calorieTarget)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            state.last7DaysCalories.forEach { day ->
                Text(
                    text = day.date.weekdayShort(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(label = "Тренировок", value = "${state.workoutsThisWeek}")
            StatItem(label = "Сожжено ккал", value = "${state.caloriesBurnedThisWeek}")
        }
    }
}

@Composable
private fun WeightCard(state: StatisticsUiState) {
    ZenithCard {
        SectionLabel("Вес")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = state.currentWeightKg.formatWeight(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Цель: ${state.targetWeightKg.formatWeight()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${state.weightChangeKg.formatWeightDelta()} с начала",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (state.weightHistory.size >= 2) {
            WeightLineChart(points = state.weightHistory, modifier = Modifier.padding(top = 8.dp))
        } else {
            Text(
                text = "Добавьте больше записей веса в профиле, чтобы увидеть график",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
