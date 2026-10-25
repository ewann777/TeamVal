package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.teamval.domain.model.ImageCrop
import com.example.teamval.domain.model.ImageCropMath
import com.example.teamval.domain.model.LogoShape
import com.example.teamval.ui.theme.LocalTeamValVisuals
import kotlin.math.roundToInt

/**
 * Square viewport with pan and pinch-zoom: the image keeps its aspect ratio at all
 * times (proportional crop, never stretched) and can be previewed circular/square.
 */
@Composable
fun ImageCropView(
    image: ImageBitmap?,
    crop: ImageCrop,
    shape: LogoShape,
    frameSize: Dp,
    onCropChange: (ImageCrop) -> Unit,
    modifier: Modifier = Modifier,
    interactive: Boolean = true
) {
    val density = LocalDensity.current
    val framePx = with(density) { frameSize.toPx() }
    val currentCrop by rememberUpdatedState(crop)
    val placeholder = MaterialTheme.colorScheme.surfaceContainerHigh

    Box(modifier.size(frameSize).clip(shapeModifier(shape))) {
        Canvas(
            Modifier.fillMaxSize().then(
                if (interactive) Modifier.pointerInput(image, framePx) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val source = image ?: return@detectTransformGestures
                        var next = ImageCropMath.zoom(currentCrop, zoom)
                        next = ImageCropMath.pan(
                            next,
                            source.width.toFloat(),
                            source.height.toFloat(),
                            framePx,
                            pan.x,
                            pan.y
                        )
                        onCropChange(next)
                    }
                } else Modifier
            )
        ) {
            val dim = this.size.minDimension
            if (image == null) {
                drawRect(placeholder)
            } else {
                val rect = ImageCropMath.sourceRect(
                    image.width.toFloat(), image.height.toFloat(), dim, crop
                )
                val srcX = rect.x.roundToInt().coerceIn(0, image.width - 1)
                val srcY = rect.y.roundToInt().coerceIn(0, image.height - 1)
                val srcW = rect.width.roundToInt().coerceIn(1, image.width - srcX)
                val srcH = rect.height.roundToInt().coerceIn(1, image.height - srcY)
                clipPath(framePath(shape, dim)) {
                    drawImage(
                        image = image,
                        srcOffset = IntOffset(srcX, srcY),
                        srcSize = IntSize(srcW, srcH),
                        dstOffset = IntOffset.Zero,
                        dstSize = IntSize(dim.roundToInt(), dim.roundToInt())
                    )
                }
            }
        }
    }
}

private fun shapeModifier(shape: LogoShape) =
    if (shape == LogoShape.Circle) CircleShape else RoundedCornerShape(22)

private fun framePath(shape: LogoShape, size: Float): Path {
    val path = Path()
    if (shape == LogoShape.Circle) {
        path.addOval(androidx.compose.ui.geometry.Rect(0f, 0f, size, size))
    } else {
        val radius = size * 0.22f
        path.addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                androidx.compose.ui.geometry.Rect(0f, 0f, size, size),
                androidx.compose.ui.geometry.CornerRadius(radius, radius)
            )
        )
    }
    return path
}

/** Thin border preview helper reused by the crop screen. */
@Composable
fun CropFrameBorder(shape: LogoShape, frameSize: Dp, modifier: Modifier = Modifier) {
    val color = LocalTeamValVisuals.current.accentText
    Canvas(modifier.size(frameSize)) {
        val dim = this.size.minDimension
        val stroke = 3.dp.toPx()
        val path = framePath(shape, dim)
        drawPath(path, color = color, style = Stroke(width = stroke))
        drawPath(path, color = color.copy(alpha = 0.35f), style = Stroke(width = stroke * 3f))
    }
}
