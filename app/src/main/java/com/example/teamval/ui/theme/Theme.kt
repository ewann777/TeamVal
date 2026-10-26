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
import com.example.teamval.domain.model.AppPalette

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
        ?.takeIf { appearance.isValid() }?.let { Color(0xFF000000 or it) } ?: ValorantRed
    val legacy = appearance.palette == AppPalette.Legacy
    val background = if (!dark) {
        if (legacy) Color(0xFFF9F7FC) else Color(0xFFF7F4EF)
    } else when (appearance.palette) {
        AppPalette.Classic -> CarbonBlue
        AppPalette.Legacy -> Charcoal
        AppPalette.Crimson -> Color(0xFF1B1019)
        AppPalette.Ocean -> Color(0xFF0B1C2B)
    }
    val surface = if (!dark) Color.White else if (legacy) CharcoalSurface
        else lerp(background, Color(0xFF345064), 0.16f)
    val raised = if (!dark) {
        if (legacy) Color(0xFFECE7F1) else Color(0xFFE8E5DF)
    } else if (legacy) CharcoalRaised else lerp(background, Color(0xFF345064), 0.30f)
    var primary = accent
    if (legacy) {
        while (dark && primary.luminance() < 0.38f) primary = lerp(primary, Color.White, 0.1f)
        while (!dark && primary.luminance() > 0.16f) primary = lerp(primary, Color.Black, 0.1f)
    } else {
        primary = if (
            appearance.palette == AppPalette.Classic &&
            dark &&
            accent == ValorantRed
        ) {
            accent
        } else {
            contrastingColor(accent, raised)
        }
    }
    val foreground = if (dark) { if (legacy) TextPrimary else Ivory }
        else if (legacy) Color(0xFF211D29) else CarbonBlue
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
        onSurfaceVariant = if (dark) { if (legacy) TextSecondary else Color(0xFFC4CDD5) }
            else if (legacy) Color(0xFF5B5365) else Color(0xFF46535E),
        surfaceVariant = raised,
        surfaceContainerLowest = background,
        surfaceContainerLow = background,
        surfaceContainer = surface,
        surfaceContainerHigh = raised,
        surfaceContainerHighest = raised,
        surfaceBright = if (dark) raised else Color.White,
        surfaceDim = background,
        surfaceTint = primary,
        inversePrimary = accent,
        outline = if (dark) Color(0xFF8595A4) else SupportBlueGray,
        outlineVariant = if (dark) Color(0xFF344653) else Color(0xFFC8CDD1)
    )
}

fun contrastRatio(a: Color, b: Color): Float =
    (maxOf(a.luminance(), b.luminance()) + 0.05f) / (minOf(a.luminance(), b.luminance()) + 0.05f)

/** Ajusta solo la luminosidad necesaria; conserva el rojo de marca cuando cumple. */
fun contrastingColor(color: Color, background: Color, minimum: Float = 4.5f): Color {
    val destination = if (contrastRatio(Color.White, background) > contrastRatio(Color.Black, background)) Color.White else Color.Black
    var result = color
    repeat(100) {
        if (contrastRatio(result, background) >= minimum) return result
        result = lerp(result, destination, 0.05f)
    }
    return destination
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
