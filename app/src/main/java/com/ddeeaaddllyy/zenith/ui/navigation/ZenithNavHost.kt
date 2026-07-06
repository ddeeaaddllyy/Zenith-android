package com.ddeeaaddllyy.zenith.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.ui.diary.DiaryScreen
import com.ddeeaaddllyy.zenith.ui.diary.DiaryViewModel
import com.ddeeaaddllyy.zenith.ui.profile.ProfileScreen
import com.ddeeaaddllyy.zenith.ui.profile.ProfileViewModel
import com.ddeeaaddllyy.zenith.ui.statistics.StatisticsScreen
import com.ddeeaaddllyy.zenith.ui.statistics.StatisticsViewModel
import com.ddeeaaddllyy.zenith.ui.steps.StepsScreen
import com.ddeeaaddllyy.zenith.ui.steps.StepsViewModel
import com.ddeeaaddllyy.zenith.ui.workout.WorkoutScreen
import com.ddeeaaddllyy.zenith.ui.workout.WorkoutViewModel

@Composable
fun ZenithNavHost(
    navController: NavHostController,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ZenithDestination.STATISTICS.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) },
        exitTransition = { fadeOut(animationSpec = androidx.compose.animation.core.tween(220)) }
    ) {
        composable(ZenithDestination.STATISTICS.route) {
            val viewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModel.factory(container))
            StatisticsScreen(viewModel)
        }
        composable(ZenithDestination.DIARY.route) {
            val viewModel: DiaryViewModel = viewModel(factory = DiaryViewModel.factory(container))
            DiaryScreen(viewModel)
        }
        composable(ZenithDestination.WORKOUT.route) {
            val viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModel.factory(container))
            WorkoutScreen(viewModel)
        }
        composable(ZenithDestination.STEPS.route) {
            val viewModel: StepsViewModel = viewModel(factory = StepsViewModel.factory(container))
            StepsScreen(viewModel)
        }
        composable(ZenithDestination.PROFILE.route) {
            val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(container))
            ProfileScreen(viewModel)
        }
    }
}
