package com.ddeeaaddllyy.zenith.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.ui.graphics.vector.ImageVector

enum class ZenithDestination(val route: String, val label: String, val icon: ImageVector) {
    STATISTICS("statistics", "Статистика", Icons.Filled.BarChart),
    DIARY("diary", "Калории", Icons.Filled.RestaurantMenu),
    WORKOUT("workout", "Тренировки", Icons.Filled.FitnessCenter),
    PROFILE("profile", "Профиль", Icons.Filled.Person)
}
