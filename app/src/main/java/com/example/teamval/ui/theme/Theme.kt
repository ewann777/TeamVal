package com.example.teamval.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.AppThemeMode

private val TeamValColorScheme = darkColorScheme(
    primary = Lavender,
    onPrimary = OnLavender,
    primaryContainer = LavenderContainer,
    onPrimaryContainer = SoftLavender,
    secondary = SoftLavender,
    onSecondary = OnLavender,
    secondaryContainer = LavenderContainer,
    onSecondaryContainer = SoftLavender,
    tertiary = Lavender,
    onTertiary = OnLavender,
    background = Charcoal,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalRaised,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = CharcoalSurface,
    surfaceContainerLow = Charcoal,
    surfaceContainerHigh = CharcoalRaised,
    surfaceTint = Lavender,
    outline = Outline,
    outlineVariant = OutlineSubtle
)

fun teamValColorScheme(appearance: AppAppearance, dark: Boolean): ColorScheme {
    val accent = appearance.accentHex.removePrefix("#").toLongOrNull(16)
        ?.takeIf { appearance.isValid() }?.let { Color(0xFF000000 or it) } ?: Lavender
    var primary = accent
    while (dark && primary.luminance() < 0.38f) primary = lerp(primary, Color.White, 0.1f)
    while (!dark && primary.luminance() > 0.16f) primary = lerp(primary, Color.Black, 0.1f)
    val background = if (dark) Charcoal else Color(0xFFF9F7FC)
    val surface = if (dark) CharcoalSurface else Color(0xFFFFFFFF)
    val foreground = if (dark) TextPrimary else Color(0xFF211D29)
    val container = lerp(surface, primary, if (dark) 0.22f else 0.10f)
    return (if (dark) TeamValColorScheme else lightColorScheme()).copy(
        primary = primary,
        onPrimary = if (primary.luminance() > 0.179f) Color.Black else Color.White,
        primaryContainer = container,
        onPrimaryContainer = foreground,
        secondary = primary,
        onSecondary = if (primary.luminance() > 0.179f) Color.Black else Color.White,
        secondaryContainer = container,
        onSecondaryContainer = foreground,
        tertiary = primary,
        onTertiary = if (primary.luminance() > 0.179f) Color.Black else Color.White,
        tertiaryContainer = container,
        onTertiaryContainer = foreground,
        background = background,
        onBackground = foreground,
        surface = surface,
        onSurface = foreground,
        onSurfaceVariant = if (dark) TextSecondary else Color(0xFF5B5365),
        surfaceVariant = if (dark) CharcoalRaised else Color(0xFFECE7F1),
        surfaceContainerLowest = background,
        surfaceContainerLow = background,
        surfaceContainer = surface,
        surfaceContainerHigh = if (dark) CharcoalRaised else Color(0xFFEFEAF4),
        surfaceContainerHighest = if (dark) CharcoalRaised else Color(0xFFE7E1ED),
        surfaceBright = if (dark) CharcoalRaised else Color.White,
        surfaceDim = if (dark) Charcoal else Color(0xFFE7E1ED),
        surfaceTint = primary,
        inversePrimary = accent,
        outline = if (dark) Outline else Color(0xFF797082),
        outlineVariant = if (dark) OutlineSubtle else Color(0xFFD4CDDE)
    )
}

@Composable
fun TeamValTheme(appearance: AppAppearance = AppAppearance(), content: @Composable () -> Unit) {
    val dark = when (appearance.mode) {
        AppThemeMode.Dark -> true
        AppThemeMode.Light -> false
        AppThemeMode.System -> isSystemInDarkTheme()
    }
    val colors = teamValColorScheme(appearance, dark)
    androidx.compose.runtime.CompositionLocalProvider(LocalTeamValVisuals provides teamValVisuals(colors, dark)) {
        MaterialTheme(
            colorScheme = colors,
            typography = teamValTypography(appearance.font),
            shapes = TeamValShapes,
            content = content
        )
    }
}
