package com.example.teamval.ui.theme

import androidx.compose.ui.graphics.Color

val ValorantRed = Color(0xFFFF4655)
val CarbonBlue = Color(0xFF0F1923)
val Ivory = Color(0xFFECE8E1)
val SupportBlueGray = Color(0xFF5A6672)

val Lavender = Color(0xFFC4B5FD)
val OnLavender = Color(0xFF261B40)
val LavenderContainer = Color(0xFF39304F)
val SoftLavender = Color(0xFFEADDFF)
val Charcoal = Color(0xFF17171C)
val CharcoalSurface = Color(0xFF222229)
val CharcoalRaised = Color(0xFF2D2C36)
val TextPrimary = Color(0xFFF5F1FA)
val TextSecondary = Color(0xFFC7C2D0)
val Outline = Color(0xFF938B9F)
val OutlineSubtle = Color(0xFF49434F)

/**
 * Rota el tono de [color] conservando saturación, brillo y alfa.
 * Se usa para derivar los tonos vecinos de las auroras a partir del acento
 * activo, de modo que el fondo siga siendo coherente con el tema elegido.
 */
fun hueShift(color: Color, degrees: Float): Color {
    val red = color.red
    val green = color.green
    val blue = color.blue
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val delta = max - min
    val hue = when {
        delta == 0f -> 0f
        max == red -> ((green - blue) / delta) % 6f
        max == green -> (blue - red) / delta + 2f
        else -> (red - green) / delta + 4f
    } * 60f
    val saturation = if (max == 0f) 0f else delta / max
    val shifted = (((hue + degrees) % 360f) + 360f) % 360f
    val chroma = max * saturation
    val x = chroma * (1f - kotlin.math.abs((shifted / 60f) % 2f - 1f))
    val match = max - chroma
    val (r, g, b) = when ((shifted / 60f).toInt()) {
        0 -> Triple(chroma, x, 0f)
        1 -> Triple(x, chroma, 0f)
        2 -> Triple(0f, chroma, x)
        3 -> Triple(0f, x, chroma)
        4 -> Triple(x, 0f, chroma)
        else -> Triple(chroma, 0f, x)
    }
    return Color(r + match, g + match, b + match, color.alpha)
}
