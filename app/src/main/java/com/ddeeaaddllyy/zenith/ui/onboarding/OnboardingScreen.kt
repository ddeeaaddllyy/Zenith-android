package com.ddeeaaddllyy.zenith.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.ddeeaaddllyy.zenith.domain.model.Gender
import com.ddeeaaddllyy.zenith.domain.model.Goal
import com.ddeeaaddllyy.zenith.ui.common.SectionLabel
import com.ddeeaaddllyy.zenith.ui.common.ZenithCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "ZENITH",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Расскажите немного о себе — это поможет точно рассчитать вашу дневную норму калорий.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ZenithCard {
                SectionLabel("Основное")
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Имя") },
                    singleLine = true,
                    colors = zenithFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    Gender.entries.forEachIndexed { index, gender ->
                        SegmentedButton(
                            selected = state.gender == gender,
                            onClick = { viewModel.onGenderChange(gender) },
                            shape = SegmentedButtonDefaults.itemShape(index, Gender.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) { Text(gender.label) }
                    }
                }
                OutlinedTextField(
                    value = state.age,
                    onValueChange = viewModel::onAgeChange,
                    label = { Text("Возраст") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = zenithFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ZenithCard {
                SectionLabel("Параметры тела")
                OutlinedTextField(
                    value = state.heightCm,
                    onValueChange = viewModel::onHeightChange,
                    label = { Text("Рост, см") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = zenithFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.weightKg,
                    onValueChange = viewModel::onWeightChange,
                    label = { Text("Текущий вес, кг") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = zenithFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.targetWeightKg,
                    onValueChange = viewModel::onTargetWeightChange,
                    label = { Text("Целевой вес, кг") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = zenithFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ZenithCard {
                SectionLabel("Активность и цель")
                ActivityLevelPicker(
                    selected = state.activityLevel,
                    onSelected = viewModel::onActivityLevelChange
                )

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    Goal.entries.forEachIndexed { index, goal ->
                        SegmentedButton(
                            selected = state.goal == goal,
                            onClick = { viewModel.onGoalChange(goal) },
                            shape = SegmentedButtonDefaults.itemShape(index, Goal.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) { Text(goal.shortLabel, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
                    }
                }
            }

            Button(
                onClick = { viewModel.submit(onFinished) },
                enabled = state.isValid && !state.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Начать")
                }
            }
        }
    }
}

@Composable
private fun ActivityLevelPicker(
    selected: ActivityLevel,
    onSelected: (ActivityLevel) -> Unit
) {
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
            ActivityLevel.entries.forEach { level ->
                DropdownMenuItem(
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
private fun zenithFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary
)
