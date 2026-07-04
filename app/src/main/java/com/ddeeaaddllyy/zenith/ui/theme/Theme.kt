package com.ddeeaaddllyy.zenith.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZenithColorScheme = darkColorScheme(
    primary = SoftDove,
    onPrimary = BlackRaspberry,
    primaryContainer = SpicedHotChocolate,
    onPrimaryContainer = SoftDove,
    secondary = MoonRock,
    onSecondary = BlackRaspberry,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = SoftDove,
    tertiary = DarkSienna,
    onTertiary = SoftDove,
    tertiaryContainer = DarkSienna,
    onTertiaryContainer = SoftDove,
    background = BlackRaspberry,
    onBackground = SoftDove,
    surface = SurfaceContainer,
    onSurface = SoftDove,
    surfaceVariant = SpicedHotChocolate,
    onSurfaceVariant = MoonRock,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceElevated,
    surfaceContainerHighest = SurfaceElevated,
    surfaceContainerLow = BlackRaspberry,
    surfaceContainerLowest = BlackRaspberry,
    outline = MoonRock,
    outlineVariant = SpicedHotChocolate,
    error = Color(0xFFA5453F),
    onError = SoftDove
)

/**
 * The app is intentionally always dark — Zenith has no light theme.
 */
@Composable
fun ZenithTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = BlackRaspberry.toArgb()
            window.navigationBarColor = BlackRaspberry.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = ZenithColorScheme,
        typography = Typography,
        content = content
    )
}
