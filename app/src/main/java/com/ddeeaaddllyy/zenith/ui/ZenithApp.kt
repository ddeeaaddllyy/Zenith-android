package com.ddeeaaddllyy.zenith.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ddeeaaddllyy.zenith.di.AppContainer
import com.ddeeaaddllyy.zenith.domain.model.AppTheme
import com.ddeeaaddllyy.zenith.domain.model.UserProfile
import com.ddeeaaddllyy.zenith.ui.navigation.ZenithDestination
import com.ddeeaaddllyy.zenith.ui.navigation.ZenithNavHost
import com.ddeeaaddllyy.zenith.ui.onboarding.OnboardingScreen
import com.ddeeaaddllyy.zenith.ui.onboarding.OnboardingViewModel
import com.ddeeaaddllyy.zenith.ui.theme.NoticeDarkRed
import com.ddeeaaddllyy.zenith.ui.theme.OnNoticeDarkRed
import com.ddeeaaddllyy.zenith.ui.theme.ZenithTheme
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

    val theme = (gateState as? ProfileGateState.Ready)?.profile?.theme ?: AppTheme.CHOCOLATTE

    ZenithTheme(theme = theme) {
        when (gateState) {
            is ProfileGateState.Loading -> LoadingScreen()
            is ProfileGateState.NeedsOnboarding -> {
                val viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.factory(container))
                OnboardingScreen(viewModel = viewModel, onFinished = {})
            }
            is ProfileGateState.Ready -> MainScaffold(container)
        }
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
    val session by container.nedovolenAccountRepository.session.collectAsState(initial = null)
    var bannerDismissed by remember { mutableStateOf(false) }
    val showBanner = session == null && !bannerDismissed

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AnimatedVisibility(
                visible = showBanner,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                NotLoggedInNotice(onDismiss = { bannerDismissed = true })
            }
        },
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
                        alwaysShowLabel = false,
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = {
                            Text(
                                destination.label,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Visible,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                            )
                        },
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

@Composable
private fun NotLoggedInNotice(onDismiss: () -> Unit) {
    // Dark red regardless of the selected app theme, but a quiet row — not an alarm banner.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(NoticeDarkRed)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Прогресс не сохранится без входа в аккаунт",
                style = MaterialTheme.typography.bodySmall,
                color = OnNoticeDarkRed,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Скрыть",
                    tint = OnNoticeDarkRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
