package com.example.teamval.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.teamval.ui.theme.LocalTeamValVisuals

/**
 * Botón principal con degradado de acento, halo suave y estado deshabilitado
 * coherente con Material 3. Es el CTA de mayor jerarquía de cada pantalla.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {    val visuals = LocalTeamValVisuals.current
    val colors = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.extraLarge
    val fill: Brush = if (enabled) {
        visuals.accentGradient
    } else {
        Brush.horizontalGradient(listOf(colors.surfaceVariant, colors.surfaceVariant))
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.then(
            if (enabled) {
                Modifier.shadow(
                    elevation = visuals.shadowElevation + 5.dp,
                    shape = shape,
                    ambientColor = visuals.glowColor,
                    spotColor = visuals.glowColor
                )
            } else {
                Modifier
            }
        ),
        shape = shape,
        color = Color.Transparent,
        contentColor = if (enabled) colors.onPrimary else colors.onSurfaceVariant
    ) {
        Box(
            Modifier
                .background(fill, shape)
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}
