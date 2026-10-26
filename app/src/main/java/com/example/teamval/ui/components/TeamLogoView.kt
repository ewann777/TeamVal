package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.teamval.data.storage.TeamLogoStorage
import com.example.teamval.domain.model.BuiltinLogo
import com.example.teamval.domain.model.GradientStyle
import com.example.teamval.domain.model.ImageCropMath
import com.example.teamval.domain.model.ImageLogo
import com.example.teamval.domain.model.LogoFrame
import com.example.teamval.domain.model.LogoShape
import com.example.teamval.domain.model.LogoSource
import com.example.teamval.domain.model.LogoSymbol
import com.example.teamval.domain.model.TeamLogo
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** Converts a packed ARGB value from the domain model into a Compose color. */
fun argb(value: Long): Color = Color(value.toInt())

private fun readableOn(background: Color): Color {
    val luminance = 0.299f * background.red + 0.587f * background.green + 0.114f * background.blue
    return if (luminance > 0.55f) Color(0xFF15131F) else Color.White
}

/**
 * Renders a team logo: builtin designs are drawn with Canvas (original symbols,
 * gradients, initials, background and frame) and uploaded images keep their
 * proportional crop inside circular or square frames.
 */
@Composable
fun TeamLogoView(
    logo: TeamLogo,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storage = remember(context) { TeamLogoStorage(context) }
    val image = remember(logo.image?.fileName, context) {
        logo.image?.fileName?.let { storage.bitmapFor(it)?.asImageBitmap() }
    }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val initialsStyle = MaterialTheme.typography.titleLarge

    Canvas(modifier.size(size)) {
        val dim = this.size.minDimension
        val usesImage = logo.source == LogoSource.Image && logo.image != null && image != null
        if (usesImage) {
            drawLogoImage(image!!, logo.image!!, dim, density)
        } else {
            drawBuiltin(logo.builtin, dim, textMeasurer, density, initialsStyle)
        }
    }
}

private fun DrawScope.drawLogoImage(
    image: androidx.compose.ui.graphics.ImageBitmap,
    config: ImageLogo,
    dim: Float,
    density: androidx.compose.ui.unit.Density
) {
    val rect = ImageCropMath.sourceRect(image.width.toFloat(), image.height.toFloat(), dim, config.crop)
    val srcX = rect.x.roundToInt().coerceIn(0, image.width - 1)
    val srcY = rect.y.roundToInt().coerceIn(0, image.height - 1)
    val srcW = rect.width.roundToInt().coerceIn(1, image.width - srcX)
    val srcH = rect.height.roundToInt().coerceIn(1, image.height - srcY)
    clipPath(shapePath(config.shape, dim)) {
        drawImage(
            image = image,
            srcOffset = IntOffset(srcX, srcY),
            srcSize = IntSize(srcW, srcH),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(dim.roundToInt(), dim.roundToInt())
        )
    }
    drawFrame(config.frame, config.shape, dim, Color.White, Color.White)
}

private fun DrawScope.drawBuiltin(
    logo: BuiltinLogo,
    dim: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    density: androidx.compose.ui.unit.Density,
    initialsStyle: TextStyle
) {
    val primary = argb(logo.primaryColor)
    val secondary = argb(logo.secondaryColor)
    val background = argb(logo.background)
    val shape = shapePath(logo.shape, dim)

    clipPath(shape) {
        drawRect(background)
        if (logo.gradient != GradientStyle.None) {
            drawRect(
                brush = Brush.linearGradient(
                    listOf(primary.copy(alpha = 0.38f), secondary.copy(alpha = 0.12f), Color.Transparent)
                )
            )
        }
    }

    val brush = when (logo.gradient) {
        GradientStyle.None -> SolidColor(primary)
        GradientStyle.Linear -> Brush.linearGradient(listOf(primary, secondary))
        GradientStyle.Radial -> Brush.radialGradient(listOf(primary, secondary))
    }

    val hasInitials = logo.initials.isNotBlank()
    val symbolSize = (dim * 0.44f * logo.scale).coerceAtMost(dim * 0.78f)
    val symbolCenter = Offset(dim / 2f, if (hasInitials) dim * 0.38f else dim / 2f)
    drawSymbol(logo.symbol, symbolCenter, symbolSize, brush)

    if (hasInitials) {
        val fontSize = with(density) { (dim * 0.19f).toSp() }
        val style = initialsStyle.copy(
            color = readableOn(background),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
        val layout = textMeasurer.measure(AnnotatedString(logo.initials.uppercase()), style)
        drawText(
            layout,
            topLeft = Offset((dim - layout.size.width) / 2f, dim * 0.64f)
        )
    }

    drawFrame(logo.frame, logo.shape, dim, primary, secondary)
}

private fun DrawScope.drawFrame(
    frame: LogoFrame,
    shape: LogoShape,
    dim: Float,
    primary: Color,
    secondary: Color
) {
    when (frame) {
        LogoFrame.None -> Unit
        LogoFrame.Solid -> drawPath(
            shapePath(shape, dim, inset = dim * 0.035f),
            color = secondary,
            style = Stroke(width = dim * 0.05f)
        )
        LogoFrame.Glow -> {
            val path = shapePath(shape, dim, inset = dim * 0.035f)
            drawPath(path, color = primary.copy(alpha = 0.16f), style = Stroke(width = dim * 0.16f))
            drawPath(path, color = primary.copy(alpha = 0.28f), style = Stroke(width = dim * 0.10f))
            drawPath(path, color = secondary, style = Stroke(width = dim * 0.045f))
        }
        LogoFrame.Double -> {
            drawPath(
                shapePath(shape, dim, inset = dim * 0.025f),
                color = primary,
                style = Stroke(width = dim * 0.055f)
            )
            drawPath(
                shapePath(shape, dim, inset = dim * 0.085f),
                color = secondary,
                style = Stroke(width = dim * 0.02f)
            )
        }
    }
}

private fun shapePath(shape: LogoShape, size: Float, inset: Float = 0f): Path {
    val path = Path()
    val rect = Rect(inset, inset, size - inset, size - inset)
    when (shape) {
        LogoShape.Circle -> path.addOval(rect)
        LogoShape.Square -> {
            val radius = (size - inset * 2f) * 0.22f
            path.addRoundRect(
                androidx.compose.ui.geometry.RoundRect(rect, CornerRadius(radius, radius))
            )
        }
    }
    return path
}

private fun DrawScope.drawSymbol(
    symbol: LogoSymbol,
    center: Offset,
    size: Float,
    brush: Brush
) {
    when (symbol) {
        LogoSymbol.Crosshair -> {
            val radius = size / 2f
            drawCircle(
                brush, radius = radius, center = center,
                style = Stroke(width = size * 0.10f, cap = StrokeCap.Round)
            )
            val inner = radius * 0.5f
            val outer = radius * 1.2f
            listOf(0.0, 90.0, 180.0, 270.0).forEach { degrees ->
                val radians = Math.toRadians(degrees)
                val dx = cos(radians).toFloat()
                val dy = sin(radians).toFloat()
                drawLine(
                    brush,
                    start = center + Offset(dx * inner, dy * inner),
                    end = center + Offset(dx * outer, dy * outer),
                    strokeWidth = size * 0.10f,
                    cap = StrokeCap.Round
                )
            }
            drawCircle(brush, radius = size * 0.07f, center = center)
        }
        LogoSymbol.Orbit -> {
            val radius = size / 2f
            withTransform({ rotate(degrees = 40f, pivot = center) }) {
                drawOval(
                    brush,
                    topLeft = Offset(center.x - radius, center.y - radius * 0.52f),
                    size = Size(radius * 2f, radius * 1.04f),
                    style = Stroke(width = size * 0.085f)
                )
            }
            drawCircle(brush, radius = radius * 0.42f, center = center)
        }
        else -> {
            drawPath(symbolPath(symbol, center, size), brush)
        }
    }
}

/** Original symbol outlines in unit coordinates (-1..1), all drawn in the app. */
private fun symbolPath(symbol: LogoSymbol, center: Offset, size: Float): Path {
    val path = Path()
    val half = size / 2f
    fun px(x: Float) = center.x + x * half
    fun py(y: Float) = center.y + y * half
    when (symbol) {
        LogoSymbol.Star -> {
            for (i in 0 until 10) {
                val angle = Math.toRadians((-90 + i * 36).toDouble())
                val radius = if (i % 2 == 0) 1.0 else 0.42
                val x = (cos(angle) * radius).toFloat()
                val y = (sin(angle) * radius).toFloat()
                if (i == 0) path.moveTo(px(x), py(y)) else path.lineTo(px(x), py(y))
            }
            path.close()
        }
        LogoSymbol.Bolt -> {
            path.moveTo(px(-0.18f), py(-1f))
            path.lineTo(px(0.62f), py(-0.35f))
            path.lineTo(px(0.18f), py(-0.12f))
            path.lineTo(px(0.42f), py(1f))
            path.lineTo(px(-0.58f), py(0.2f))
            path.lineTo(px(-0.1f), py(0f))
            path.close()
        }
        LogoSymbol.Hexagon -> {
            for (i in 0 until 6) {
                val angle = Math.toRadians((-90 + i * 60).toDouble())
                val x = cos(angle).toFloat()
                val y = sin(angle).toFloat()
                if (i == 0) path.moveTo(px(x), py(y)) else path.lineTo(px(x), py(y))
            }
            path.close()
        }
        LogoSymbol.Shield -> {
            path.moveTo(px(0f), py(-1f))
            path.lineTo(px(0.72f), py(-0.62f))
            path.lineTo(px(0.72f), py(0.08f))
            path.lineTo(px(0f), py(1f))
            path.lineTo(px(-0.72f), py(0.08f))
            path.lineTo(px(-0.72f), py(-0.62f))
            path.close()
        }
        LogoSymbol.Diamond -> {
            path.moveTo(px(0f), py(-1f))
            path.lineTo(px(1f), py(0f))
            path.lineTo(px(0f), py(1f))
            path.lineTo(px(-1f), py(0f))
            path.close()
        }
        LogoSymbol.Triangle -> {
            path.moveTo(px(0f), py(-1f))
            path.lineTo(px(0.92f), py(0.72f))
            path.lineTo(px(-0.92f), py(0.72f))
            path.close()
        }
        else -> path.close()
    }
    return path
}
