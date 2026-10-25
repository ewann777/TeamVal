package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.AvatarFrame
import com.example.teamval.domain.model.AvatarStyle

@Composable
fun PlayerAvatar(style: AvatarStyle, frame: AvatarFrame, modifier: Modifier = Modifier) {
    val accent = MaterialTheme.colorScheme.primary
    val description = stringResource(R.string.avatar_description, stringResource(style.labelRes), stringResource(frame.labelRes))
    Canvas(modifier.size(94.dp).semantics { contentDescription = description }) {
        withTransform({ scale(size.width / 100f, size.height / 100f, pivot = Offset.Zero) }) {
            // Halo exterior suave: el avatar parece emitir luz.
            drawCircle(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.40f), accent.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(50f, 50f),
                    radius = 56f
                ),
                56f,
                Offset(50f, 50f)
            )
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.5f), Color(0xFF171720)), center = Offset(50f, 50f), radius = 50f), 45f, Offset(50f, 50f))
            if (frame != AvatarFrame.None) {
                when (frame) {
                    AvatarFrame.None -> Unit
                    AvatarFrame.Halo ->
                        drawCircle(Brush.linearGradient(listOf(accent, Color.White, accent)), 47f, Offset(50f, 50f), style = Stroke(3f))
                    AvatarFrame.Double -> {
                        drawCircle(Brush.linearGradient(listOf(accent, Color.White, accent)), 47f, Offset(50f, 50f), style = Stroke(3f))
                        drawCircle(accent.copy(alpha = 0.75f), 40f, Offset(50f, 50f), style = Stroke(1f))
                    }
                    // Cosméticos de la tienda (etapa 5).
                    AvatarFrame.Lavanda ->
                        drawCircle(Brush.linearGradient(listOf(Color(0xFFC4B5FD), Color(0xFFF5F3FF), Color(0xFFA78BFA))), 47f, Offset(50f, 50f), style = Stroke(3.5f))
                    AvatarFrame.Solar -> {
                        drawCircle(Brush.linearGradient(listOf(Color(0xFFFBBF24), Color(0xFFF97316), Color(0xFFFBBF24))), 47f, Offset(50f, 50f), style = Stroke(4f))
                        drawCircle(Color(0xFFFDE68A).copy(alpha = 0.55f), 41f, Offset(50f, 50f), style = Stroke(1.5f))
                    }
                    AvatarFrame.Neon -> {
                        drawCircle(Brush.linearGradient(listOf(accent, Color(0xFF22D3EE), accent)), 47f, Offset(50f, 50f), style = Stroke(3f))
                        drawCircle(Color(0xFF22D3EE).copy(alpha = 0.85f), 41f, Offset(50f, 50f), style = Stroke(2.5f))
                    }
                }
            }
            val ink = Color(0xFFF4EDFF)
            when (style) {
                AvatarStyle.Nova -> {
                    val star = Path().apply {
                        moveTo(50f, 20f); lineTo(59f, 41f); lineTo(80f, 50f); lineTo(59f, 59f)
                        lineTo(50f, 80f); lineTo(41f, 59f); lineTo(20f, 50f); lineTo(41f, 41f); close()
                    }
                    drawPath(star, ink)
                    drawCircle(accent, 6f, Offset(50f, 50f))
                }
                AvatarStyle.Orbit -> {
                    drawCircle(ink, 18f, Offset(50f, 50f))
                    drawOval(accent, Offset(17f, 37f), androidx.compose.ui.geometry.Size(66f, 26f), style = Stroke(4f))
                    drawCircle(ink, 4f, Offset(75f, 25f))
                }
                AvatarStyle.Fox -> {
                    val fox = Path().apply {
                        moveTo(25f, 23f); lineTo(47f, 36f); lineTo(53f, 36f); lineTo(75f, 23f)
                        lineTo(72f, 60f); lineTo(50f, 79f); lineTo(28f, 60f); close()
                    }
                    drawPath(fox, ink)
                    drawLine(Color(0xFF282036), Offset(34f, 50f), Offset(44f, 55f), 4f)
                    drawLine(Color(0xFF282036), Offset(66f, 50f), Offset(56f, 55f), 4f)
                    drawCircle(Color(0xFF282036), 4f, Offset(50f, 67f))
                }
                AvatarStyle.Sentinel -> {
                    val shield = Path().apply {
                        moveTo(50f, 22f); lineTo(73f, 32f); lineTo(69f, 61f)
                        lineTo(50f, 79f); lineTo(31f, 61f); lineTo(27f, 32f); close()
                    }
                    drawPath(shield, ink, style = Stroke(4f))
                    drawLine(accent, Offset(50f, 34f), Offset(50f, 66f), 5f)
                    drawLine(accent, Offset(38f, 45f), Offset(62f, 45f), 5f)
                }
            }
        }
    }
}
