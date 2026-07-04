package com.ddeeaaddllyy.zenith.ui.workout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ddeeaaddllyy.zenith.analytics.Analytic
import com.ddeeaaddllyy.zenith.domain.model.WorkoutEntry
import com.ddeeaaddllyy.zenith.domain.model.WorkoutType
import com.ddeeaaddllyy.zenith.ui.common.DaySelector
import com.ddeeaaddllyy.zenith.ui.common.SwipeToDeleteItem
import com.ddeeaaddllyy.zenith.ui.common.ZenithCard
import com.ddeeaaddllyy.zenith.ui.common.formatKcal

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить тренировку")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Тренировки",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                DaySelector(
                    selectedDate = state.selectedDate,
                    onPreviousDay = viewModel::onPreviousDay,
                    onNextDay = viewModel::onNextDay,
                    onDateSelected = viewModel::selectDate
                )
            }
            item { WorkoutSummaryCard(state) }

            if (state.entries.isEmpty()) {
                item {
                    Text(
                        text = "Тренировок за этот день пока нет",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            } else {
                items(state.entries, key = { it.id }) { entry ->
                    WorkoutEntryRow(entry = entry, onDelete = { viewModel.deleteEntry(entry) })
                }
            }
        }

        if (showAddDialog) {
            AddWorkoutDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { type, duration, calories, note ->
                    viewModel.addEntry(type, duration, calories, note)
                    showAddDialog = false

                    Analytic.sendWorkoutAnalytic()
                }
            )
        }
    }
}

@Composable
private fun WorkoutSummaryCard(state: WorkoutUiState) {
    ZenithCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${state.entries.size}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (state.entries.size == 1) "тренировка" else "тренировок",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Сожжено: ${state.totalCaloriesBurned.formatKcal()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WorkoutEntryRow(entry: WorkoutEntry, onDelete: () -> Unit) {
    SwipeToDeleteItem(
        confirmText = "Тренировка «${entry.type.label}» будет удалена безвозвратно.",
        onDelete = onDelete
    ) {
        ZenithCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.type.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${entry.durationMinutes} мин  •  ${entry.caloriesBurned.formatKcal()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!entry.note.isNullOrBlank()) {
                        Text(
                            text = entry.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: WorkoutType, duration: Int, calories: Int, note: String?) -> Unit
) {
    var type by remember { mutableStateOf(WorkoutType.STRENGTH) }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val isValid = (duration.toIntOrNull() ?: -1) > 0 && (calories.toIntOrNull() ?: -1) >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Новая тренировка", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WorkoutTypePicker(selected = type, onSelected = { type = it })
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter { c -> c.isDigit() } },
                    label = { Text("Длительность, мин") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter { c -> c.isDigit() } },
                    label = { Text("Сожжено калорий") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка (необязательно)") },
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(type, duration.toInt(), calories.toIntOrNull() ?: 0, note.trim().ifBlank { null })
                },
                enabled = isValid
            ) {
                Text("Добавить", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun WorkoutTypePicker(selected: WorkoutType, onSelected: (WorkoutType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            WorkoutType.entries.forEach { workoutType ->
                DropdownMenuItem(
                    text = { Text(workoutType.label) },
                    onClick = {
                        onSelected(workoutType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary
)
