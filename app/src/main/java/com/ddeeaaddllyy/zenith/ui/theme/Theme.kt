package com.ddeeaaddllyy.zenith.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ddeeaaddllyy.zenith.domain.model.AppTheme

private val ChocolatteScheme = darkColorScheme(
    primary = SoftDove,
    onPrimary = BlackRaspberry,
    primaryContainer = SpicedHotChocolate,
    onPrimaryContainer = SoftDove,
    secondary = MoonRock,
    onSecondary = BlackRaspberry,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = SoftDove,
    tertiary = SoftDove,
    onTertiary = BlackRaspberry,
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

private val EarlyWinterScheme = darkColorScheme(
    primary = PeachesAndCream,
    onPrimary = Concrete,
    primaryContainer = WarmSteel,
    onPrimaryContainer = Dawn,
    secondary = Rosewood,
    onSecondary = Concrete,
    secondaryContainer = WarmSteel,
    onSecondaryContainer = Dawn,
    tertiary = Rosewood,
    onTertiary = Concrete,
    tertiaryContainer = WarmSteel,
    onTertiaryContainer = Dawn,
    background = Concrete,
    onBackground = Dawn,
    surface = Color(0xFF474D63),
    onSurface = Dawn,
    surfaceVariant = WarmSteel,
    onSurfaceVariant = Rosewood,
    surfaceContainer = Color(0xFF474D63),
    surfaceContainerHigh = WarmSteel,
    surfaceContainerHighest = WarmSteel,
    surfaceContainerLow = Color(0xFF3F4457),
    surfaceContainerLowest = Color(0xFF383C4D),
    outline = Rosewood,
    outlineVariant = WarmSteel,
    error = Color(0xFFA5453F),
    onError = Dawn
)

private val BloomingPeriodScheme = darkColorScheme(
    primary = UsuKoubaiBlossom,
    onPrimary = WarmFog,
    primaryContainer = MeadowMauve,
    onPrimaryContainer = WarmFog,
    secondary = BerryGood,
    onSecondary = SoldierGreen,
    secondaryContainer = MeadowMauve,
    onSecondaryContainer = WarmFog,
    tertiary = MeadowMauve,
    onTertiary = WarmFog,
    tertiaryContainer = UsuKoubaiBlossom,
    onTertiaryContainer = WarmFog,
    background = SoldierGreen,
    onBackground = WarmFog,
    surface = Color(0xFF474519),
    onSurface = WarmFog,
    surfaceVariant = MeadowMauve,
    onSurfaceVariant = BerryGood,
    surfaceContainer = Color(0xFF474519),
    surfaceContainerHigh = MeadowMauve,
    surfaceContainerHighest = MeadowMauve,
    surfaceContainerLow = Color(0xFF3D3B15),
    surfaceContainerLowest = Color(0xFF343210),
    outline = MeadowMauve,
    outlineVariant = Color(0xFF474519),
    error = Color(0xFFA5453F),
    onError = WarmFog
)

private val ObsidianTideScheme = darkColorScheme(
    primary = ObsidianSeafoam,
    onPrimary = ObsidianBackground,
    primaryContainer = ObsidianSteel,
    onPrimaryContainer = ObsidianIce,
    secondary = ObsidianSteel,
    onSecondary = ObsidianIce,
    secondaryContainer = ObsidianSurface,
    onSecondaryContainer = ObsidianIce,
    tertiary = ObsidianIce,
    onTertiary = ObsidianBackground,
    tertiaryContainer = ObsidianSteel,
    onTertiaryContainer = ObsidianIce,
    background = ObsidianBackground,
    onBackground = ObsidianIce,
    surface = ObsidianSurface,
    onSurface = ObsidianIce,
    surfaceVariant = ObsidianSteel,
    onSurfaceVariant = ObsidianIce,
    surfaceContainer = ObsidianSurface,
    surfaceContainerHigh = ObsidianSteel,
    surfaceContainerHighest = ObsidianSteel,
    surfaceContainerLow = ObsidianBackground,
    surfaceContainerLowest = ObsidianBackground,
    outline = ObsidianSteel,
    outlineVariant = ObsidianSurface,
    error = Color(0xFFA5453F),
    onError = ObsidianIce
)

fun colorSchemeFor(theme: AppTheme): ColorScheme = when (theme) {
    AppTheme.CHOCOLATTE -> ChocolatteScheme
    AppTheme.EARLY_WINTER -> EarlyWinterScheme
    AppTheme.BLOOMING_PERIOD -> BloomingPeriodScheme
    AppTheme.OBSIDIAN_TIDE -> ObsidianTideScheme
}

/**
 * The app is intentionally always dark — every Zenith theme is a dark theme, just a different mood.
 */
@Composable
fun ZenithTheme(theme: AppTheme = AppTheme.CHOCOLATTE, content: @Composable () -> Unit) {
    val targetScheme = colorSchemeFor(theme)
    val animatedScheme = targetScheme.copy(
        primary = animateColorAsState(targetScheme.primary, tween(400), label = "primary").value,
        background = animateColorAsState(targetScheme.background, tween(400), label = "background").value,
        surface = animateColorAsState(targetScheme.surface, tween(400), label = "surface").value,
        surfaceVariant = animateColorAsState(targetScheme.surfaceVariant, tween(400), label = "surfaceVariant").value,
        onBackground = animateColorAsState(targetScheme.onBackground, tween(400), label = "onBackground").value,
        onSurface = animateColorAsState(targetScheme.onSurface, tween(400), label = "onSurface").value,
        tertiary = animateColorAsState(targetScheme.tertiary, tween(400), label = "tertiary").value,
        tertiaryContainer = animateColorAsState(targetScheme.tertiaryContainer, tween(400), label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(targetScheme.onTertiaryContainer, tween(400), label = "onTertiaryContainer").value
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = targetScheme.background.toArgb()
            window.navigationBarColor = targetScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = Typography,
        content = content
    )
}
