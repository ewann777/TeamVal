package com.example.teamval.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tokens visuales compartidos de la identidad premium de Teamval: degradados de
 * acento, rellenos de vidrio y profundidad de sombras.
 *
 * Todos los valores se derivan del esquema Material activo, de modo que siguen el
 * color de acento, el tema claro/oscuro y el resto de preferencias de apariencia.
 */
@Immutable
data class TeamValVisuals(
    val isDark: Boolean,
    val accent: Color,
    val accentText: Color,
    val accentGradient: Brush,
    val glassFill: Brush,
    val glassBorder: Brush,
    val glowColor: Color,
    val shadowColor: Color,
    val shadowElevation: Dp
)

fun teamValVisuals(colors: ColorScheme, dark: Boolean): TeamValVisuals {
    val accent = colors.primary
    val onAccent = colors.onPrimary
    // El degradado se aleja del color del texto para no comprometer el contraste.
    val awayFromText = if (onAccent.luminance() > 0.5f) Color.Black else Color.White
    val accentText = if (dark) lerp(accent, Color.White, 0.40f) else accent
    val surface = colors.surface
    return TeamValVisuals(
        isDark = dark,
        accent = accent,
        accentText = accentText,
        accentGradient = Brush.linearGradient(
            listOf(
                lerp(accent, awayFromText, 0.16f),
                accent,
                lerp(accent, awayFromText, 0.06f)
            )
        ),
        glassFill = Brush.verticalGradient(
            listOf(surface.copy(alpha = 0.94f), surface.copy(alpha = 0.80f))
        ),
        glassBorder = Brush.linearGradient(
            listOf(
                accent.copy(alpha = 0.38f),
                Color.White.copy(alpha = 0.14f),
                accent.copy(alpha = 0.16f)
            )
        ),
        glowColor = accent.copy(alpha = if (dark) 0.45f else 0.30f),
        shadowColor = if (dark) Color.Black.copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.16f),
        shadowElevation = if (dark) 7.dp else 5.dp
    )
}

/**
 * Reserva para previsualizaciones o composiciones fuera de `TeamValTheme`.
 * En la aplicación siempre lo proporciona el tema raíz.
 */
val LocalTeamValVisuals = staticCompositionLocalOf {
    teamValVisuals(teamValColorScheme(com.example.teamval.domain.model.AppAppearance(), dark = true), dark = true)
}
