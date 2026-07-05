package com.ddeeaaddllyy.zenith.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.ddeeaaddllyy.zenith.domain.model.ActivityLevel
import com.ddeeaaddllyy.zenith.domain.model.Goal
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import com.ddeeaaddllyy.zenith.ui.common.SectionLabel
import com.ddeeaaddllyy.zenith.ui.common.ZenithCard
import com.ddeeaaddllyy.zenith.ui.common.formatKcal
import com.ddeeaaddllyy.zenith.ui.common.formatWeight
import com.ddeeaaddllyy.zenith.ui.common.formatWeightDelta

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    var showWeightDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        val currentProfile = profile
        if (currentProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ZenithBrandHeader() }
                item { UserInfoCard(currentProfile, onEditClick = { showEditDialog = true }) }
                item { WeightCard(currentProfile, onUpdateWeightClick = { showWeightDialog = true }) }
                item { CalorieTargetCard(currentProfile) }
            }
        }
    }

    if (showWeightDialog) {
        UpdateWeightDialog(
            onDismiss = { showWeightDialog = false },
            onConfirm = { weight ->
                viewModel.updateWeight(weight)
                showWeightDialog = false
            }
        )
    }

    if (showEditDialog && profile != null) {
        EditProfileDialog(
            profile = profile!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, age, height, targetWeight, activity, goal ->
                viewModel.updateDetails(name, age, height, targetWeight, activity, goal)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun ZenithBrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "ZENITH",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 5.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun UserInfoCard(profile: UserProfile, onEditClick: () -> Unit) {
    ZenithCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${profile.gender.label} • ${profile.age} лет • ${profile.heightCm} см",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Редактировать профиль",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeightCard(profile: UserProfile, onUpdateWeightClick: () -> Unit) {
    ZenithCard {
        SectionLabel("Вес")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = profile.currentWeightKg.formatWeight(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Начало: ${profile.startWeightKg.formatWeight()}  •  Цель: ${profile.targetWeightKg.formatWeight()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = profile.weightChangeKg.formatWeightDelta(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedButton(
            onClick = onUpdateWeightClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.MonitorWeight, contentDescription = null, modifier = Modifier.width(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Обновить вес")
        }
    }
}

@Composable
private fun CalorieTargetCard(profile: UserProfile) {
    ZenithCard {
        SectionLabel("Параметры")
        InfoRow("Уровень активности", profile.activityLevel.shortLabel)
        InfoRow("Цель", profile.goal.label)
        InfoRow("Дневная норма", profile.dailyCalorieTarget.formatKcal())
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun UpdateWeightDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var weight by remember { mutableStateOf("") }
    val isValid = (weight.toDoubleOrNull() ?: 0.0) in 30.0..300.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Обновить вес", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            OutlinedTextField(
                value = weight,
                onValueChange = { value -> weight = value.filterIndexed { i, c -> c.isDigit() || (c == '.' && !value.take(i).contains('.')) } },
                label = { Text("Текущий вес, кг") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = dialogFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(weight.toDouble()) }, enabled = isValid) {
                Text("Сохранить", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onConfirm: (name: String, age: Int, height: Int, targetWeight: Double, activity: ActivityLevel, goal: Goal) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var age by remember { mutableStateOf(profile.age.toString()) }
    var height by remember { mutableStateOf(profile.heightCm.toString()) }
    var targetWeight by remember { mutableStateOf(profile.targetWeightKg.toString()) }
    var activity by remember { mutableStateOf(profile.activityLevel) }
    var goal by remember { mutableStateOf(profile.goal) }

    val isValid = name.isNotBlank() &&
        (age.toIntOrNull() ?: 0) in 10..100 &&
        (height.toIntOrNull() ?: 0) in 100..250 &&
        (targetWeight.toDoubleOrNull() ?: 0.0) in 30.0..300.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Редактировать профиль", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    singleLine = true,
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { c -> c.isDigit() } },
                    label = { Text("Возраст") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it.filter { c -> c.isDigit() } },
                    label = { Text("Рост, см") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetWeight,
                    onValueChange = { value -> targetWeight = value.filterIndexed { i, c -> c.isDigit() || (c == '.' && !value.take(i).contains('.')) } },
                    label = { Text("Целевой вес, кг") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = dialogFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    Goal.entries.forEachIndexed { index, g ->
                        SegmentedButton(
                            selected = goal == g,
                            onClick = { goal = g },
                            shape = SegmentedButtonDefaults.itemShape(index, Goal.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) { Text(g.label, style = MaterialTheme.typography.labelSmall) }
                    }
                }
                ActivityLevelDropdown(selected = activity, onSelected = { activity = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name.trim(),
                        age.toInt(),
                        height.toInt(),
                        targetWeight.toDouble(),
                        activity,
                        goal
                    )
                },
                enabled = isValid
            ) {
                Text("Сохранить", color = MaterialTheme.colorScheme.primary)
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
private fun ActivityLevelDropdown(selected: ActivityLevel, onSelected: (ActivityLevel) -> Unit) {
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        androidx.compose.material3.DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ActivityLevel.entries.forEach { level ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(level.label) },
                    onClick = {
                        onSelected(level)
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
