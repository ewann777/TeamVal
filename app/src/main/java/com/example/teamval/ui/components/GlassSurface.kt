package com.example.teamval.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.teamval.ui.theme.LocalTeamValVisuals

/**
 * Superficie translúcida con borde de luz y sombra suave: el acabado de vidrio
 * que unifica tarjetas, paneles y secciones de la aplicación.
 */
@Composable
fun Modifier.glassSurface(
    shape: Shape = MaterialTheme.shapes.large,
    elevated: Boolean = true
): Modifier {
    val visuals = LocalTeamValVisuals.current
    return this
        .then(
            if (elevated) {
                Modifier.shadow(
                    elevation = visuals.shadowElevation,
                    shape = shape,
                    ambientColor = visuals.shadowColor,
                    spotColor = visuals.shadowColor
                )
            } else {
                Modifier
            }
        )
        .background(visuals.glassFill, shape)
        .border(1.dp, visuals.glassBorder, shape)
}

/** Filete luminoso que separa bloques o corona una superficie inferior. */
@Composable
fun HairlineDivider(modifier: Modifier = Modifier) {
    val visuals = LocalTeamValVisuals.current
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        visuals.accent.copy(alpha = 0.55f),
                        Color.Transparent
                    )
                )
            )
    )
}

/** Barra de acento en degradado que encabeza títulos y secciones. */
@Composable
fun AccentBar(modifier: Modifier = Modifier, height: Dp = 26.dp) {
    val visuals = LocalTeamValVisuals.current
    Box(
        modifier
            .width(4.dp)
            .height(height)
            .background(visuals.accentGradient, RoundedCornerShape(50))
    )
}
