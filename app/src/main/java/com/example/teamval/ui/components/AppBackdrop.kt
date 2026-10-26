package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.teamval.domain.model.AppBackground
import com.example.teamval.ui.theme.LocalTeamValVisuals
import com.example.teamval.ui.theme.hueShift
import kotlin.random.Random

/**
 * Fondo global de la aplicación: color uniforme del tema, aurora o estrellas.
 *
 * La causa histórica de que las tres opciones se confundieran era que «Liso»
 * también dibujaba orbes de color, «Aurora» solo suavizaba el borde de esos
 * mismos orbes y «Cuadrícula» dibujaba líneas casi invisibles acompañadas de un
 * resplandor. Hoy cada estilo tiene dibujo propio y [AppBackgroundView] es la
 * única implementación, compartida con las miniaturas de selección para que
 * representen exactamente el fondo real.
 */
@Composable
fun AppBackdrop(style: AppBackground, content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
        AppBackgroundView(style, Modifier.fillMaxSize())
        content()
    }
}

/**
 * Dibujo de fondo reutilizable. Mantiene siempre el color del tema como base y
 * añade encima el ornamento propio de cada estilo:
 *
 * - `Plain`: sin ornamento, color uniforme.
 * - `Aurora`: cuatro resplandores radiales derivados del color de acento, con
 *   degradados suaves y visibles en ambos temas.
 * - `Stars`: campo de estrellas pequeñas con sutiles diferencias de tamaño y
 *   brillo; se dibuja una sola vez, sin animación, para priorizar rendimiento.
 */
@Composable
fun AppBackgroundView(style: AppBackground, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val visuals = LocalTeamValVisuals.current
    Box(modifier.background(colors.background)) {
        when (style) {
            AppBackground.Plain -> Unit
            AppBackground.Aurora -> Canvas(Modifier.fillMaxSize()) {
                drawAurora(visuals.accent, visuals.isDark)
            }
            AppBackground.Stars -> Canvas(Modifier.fillMaxSize()) {
                drawStarField(visuals.isDark)
            }
        }
    }
}

/** Manchas de aurora suaves y luminosas, todas derivadas del acento activo. */
private fun DrawScope.drawAurora(accent: Color, dark: Boolean) {
    val maxDim = maxOf(size.width, size.height)
    // En tema claro se rebaja la intensidad para no comprometer la lectura del
    // texto que se apoya directamente sobre el fondo.
    val strength = if (dark) 1f else 0.62f
    fun glow(color: Color, center: Offset, radius: Float, alpha: Float) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha * strength), color.copy(alpha = 0f)),
                center = center,
                radius = radius
            )
        )
    }
    // Tono principal arriba a la derecha, tonos vecinos abajo y el complementario
    // en el centro: coherente con el tema y alejado de la esquina superior
    // izquierda, donde suele haber texto directo sobre el fondo.
    glow(accent, Offset(size.width * 0.86f, size.height * 0.12f), maxDim * 0.56f, 0.60f)
    glow(hueShift(accent, 45f), Offset(size.width * 0.10f, size.height * 0.74f), maxDim * 0.50f, 0.46f)
    glow(hueShift(accent, -40f), Offset(size.width * 0.64f, size.height * 0.96f), maxDim * 0.44f, 0.38f)
    glow(hueShift(accent, 170f), Offset(size.width * 0.38f, size.height * 0.44f), maxDim * 0.32f, 0.24f)
}

/**
 * Cielo de una sola capa, estable entre redibujos y sin animación.
 *
 * La retícula con desplazamiento aleatorio reparte las estrellas sin racimos
 * (una por celda como máximo) y la semilla fija garantiza que el mismo fondo
 * se dibuje siempre igual. El resultado son unas 530 estrellas diminutas y
 * bien repartidas en una pantalla completa, en lugar de los ~1500 puntos
 * apretados del diseño anterior.
 *
 * - Tema oscuro: marfil, azul claro y blanco, con un halo muy suave solo en
 *   las estrellas más brillantes.
 * - Tema claro: tinta azul pizarra muy transparente, de modo que nunca se
 *   lean como puntos negros sobre el fondo claro.
 */
private fun DrawScope.drawStarField(dark: Boolean) {
    val cell = 15.dp.toPx()
    val cols = maxOf(1, (size.width / cell).toInt())
    val rows = maxOf(1, (size.height / cell).toInt())
    val darkTints = listOf(Color(0xFFFFF6E6), Color(0xFFDCEBFF), Color(0xFFFFFFFF))
    val lightTint = Color(0xFF33465C)
    val random = Random(0x57415253)
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            // Se consumen siempre los mismos números aleatorios por celda para
            // que el patrón sea determinista independientemente del descarte.
            val present = random.nextFloat()
            val dx = random.nextFloat()
            val dy = random.nextFloat()
            val bright = random.nextFloat()
            val sizeRoll = random.nextFloat()
            val alphaRoll = random.nextFloat()
            val tintRoll = if (dark) random.nextInt(darkTints.size) else 0
            if (present > 0.33f) continue
            val center = Offset((col + 0.15f + dx * 0.7f) * cell, (row + 0.15f + dy * 0.7f) * cell)
            val isBright = bright < 0.18f
            // Tamaños pequeños y delicados: 0,6–1,7 dp de radio (≈1,5–4,5 px).
            val radius = (if (isBright) 1.15f.dp else 0.65f.dp).toPx() +
                sizeRoll * (if (isBright) 0.55f.dp else 0.35f.dp).toPx()
            val alpha = if (dark) {
                if (isBright) 0.70f + alphaRoll * 0.22f else 0.28f + alphaRoll * 0.26f
            } else {
                if (isBright) 0.26f + alphaRoll * 0.10f else 0.12f + alphaRoll * 0.12f
            }
            val tint = if (dark) darkTints[tintRoll] else lightTint
            if (dark && isBright) {
                drawCircle(color = tint.copy(alpha = alpha * 0.25f), radius = radius * 2.8f, center = center)
            }
            drawCircle(color = tint.copy(alpha = alpha), radius = radius, center = center)
        }
    }
}
