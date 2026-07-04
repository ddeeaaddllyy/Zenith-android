package com.ddeeaaddllyy.zenith.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import com.ddeeaaddllyy.zenith.ui.navigation.ZenithDestination
import com.ddeeaaddllyy.zenith.ui.navigation.ZenithNavHost
import com.ddeeaaddllyy.zenith.ui.onboarding.OnboardingScreen
import com.ddeeaaddllyy.zenith.ui.onboarding.OnboardingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private sealed interface ProfileGateState {
    data object Loading : ProfileGateState
    data object NeedsOnboarding : ProfileGateState
    data class Ready(val profile: UserProfile) : ProfileGateState
}

@Composable
fun ZenithApp(container: AppContainer) {
    val gateState by produceState<ProfileGateState>(initialValue = ProfileGateState.Loading, container) {
        container.userRepository.profile.collect { profile ->
            value = if (profile == null) ProfileGateState.NeedsOnboarding else ProfileGateState.Ready(profile)
        }
    }

    when (gateState) {
        is ProfileGateState.Loading -> LoadingScreen()
        is ProfileGateState.NeedsOnboarding -> {
            val viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.factory(container))
            OnboardingScreen(viewModel = viewModel, onFinished = {})
        }
        is ProfileGateState.Ready -> MainScaffold(container)
    }
}

@Composable
private fun LoadingScreen() {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun MainScaffold(container: AppContainer) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                ZenithDestination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        ZenithNavHost(
            navController = navController,
            container = container,
            modifier = Modifier.padding(padding)
        )
    }
}
