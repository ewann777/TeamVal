package com.example.teamval.ui.components

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.teamval.domain.model.AppBackground
import com.example.teamval.ui.theme.LocalTeamValVisuals

/**
 * Fondo global de la aplicación. Los orbes de luz aportan profundidad y, en el
 * estilo «Aurora», se difuminan de verdad con `Modifier.blur` en Android 12+;
 * en versiones anteriores se mantienen los degradados suaves equivalentes.
 */
@Composable
fun AppBackdrop(style: AppBackground, content: @Composable BoxScope.() -> Unit) {
    val visuals = LocalTeamValVisuals.current
    val colors = MaterialTheme.colorScheme
    val accent = colors.primary
    Box(Modifier.fillMaxSize().background(colors.background)) {
        val orbAlpha = when (style) {
            AppBackground.Aurora -> if (visuals.isDark) 0.22f else 0.16f
            AppBackground.Plain -> if (visuals.isDark) 0.12f else 0.08f
            AppBackground.Grid -> 0f
        }
        if (orbAlpha > 0f) {
            val blurModifier = if (style == AppBackground.Aurora && Build.VERSION.SDK_INT >= 31) {
                Modifier.blur(64.dp)
            } else {
                Modifier
            }
            Canvas(blurModifier.fillMaxSize()) {
                drawCircle(
                    accent.copy(alpha = orbAlpha),
                    radius = size.height * 0.8f,
                    center = Offset(size.width * 1.02f, -size.height * 0.12f)
                )
                drawCircle(
                    accent.copy(alpha = orbAlpha * 0.55f),
                    radius = size.height * 0.62f,
                    center = Offset(-size.width * 0.15f, size.height * 1.08f)
                )
            }
        }
        if (style == AppBackground.Grid) {
            Canvas(Modifier.fillMaxSize()) {
                val gap = 32.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(accent.copy(alpha = 0.07f), Offset(x, 0f), Offset(x, size.height))
                    x += gap
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(accent.copy(alpha = 0.07f), Offset(0f, y), Offset(size.width, y))
                    y += gap
                }
                // Brillo muy tenue para dar volumen a la cuadrícula.
                drawCircle(
                    accent.copy(alpha = 0.06f),
                    radius = size.height * 0.7f,
                    center = Offset(size.width * 1.05f, -size.height * 0.1f)
                )
            }
        }
        content()
    }
}
