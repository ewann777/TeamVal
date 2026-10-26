package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.domain.model.ProfileBanner
import com.example.teamval.domain.model.ProfilePet
import com.example.teamval.domain.model.ProfileSticker

private val BannerHeight = 64.dp

/** Franja decorativa superior del perfil (cosmético "Banner", etapa 5). */
@Composable
fun ProfileBannerStrip(banner: ProfileBanner, modifier: Modifier = Modifier) {
    if (banner == ProfileBanner.None) return
    Canvas(modifier.fillMaxWidth().height(BannerHeight)) {
        val w = size.width
        val h = size.height
        when (banner) {
            ProfileBanner.Diurno -> {
                drawRect(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFF472B6), Color(0xFF8B5CF6))))
                drawCircle(Color(0xFFFDE68A).copy(alpha = 0.9f), h * 0.30f, Offset(w * 0.16f, h * 1.05f))
                drawCircle(Color(0xFFFFF7ED).copy(alpha = 0.35f), h * 0.14f, Offset(w * 0.52f, h * 0.35f))
                drawCircle(Color(0xFFFFF7ED).copy(alpha = 0.25f), h * 0.10f, Offset(w * 0.70f, h * 0.60f))
            }
            ProfileBanner.Noche -> {
                drawRect(Brush.horizontalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4C1D95))))
                drawCircle(Color(0xFFFEF3C7), h * 0.26f, Offset(w * 0.14f, h * 0.55f))
                drawCircle(Color(0xFF312E81), h * 0.24f, Offset(w * 0.20f, h * 0.45f))
                listOf(0.40f to 0.30f, 0.55f to 0.65f, 0.70f to 0.25f, 0.85f to 0.60f, 0.62f to 0.42f).forEach { (x, y) ->
                    drawCircle(Color.White.copy(alpha = 0.75f), 2.5f, Offset(w * x, h * y))
                }
            }
            ProfileBanner.Estelar -> {
                drawRect(Brush.horizontalGradient(listOf(Color(0xFF3B1E63), Color(0xFF6D28D9), Color(0xFF1E1B4B))))
                val star = Path().apply {
                    moveTo(w * 0.78f, h * 0.22f)
                    lineTo(w * 0.795f, h * 0.40f)
                    lineTo(w * 0.87f, h * 0.46f)
                    lineTo(w * 0.795f, h * 0.52f)
                    lineTo(w * 0.78f, h * 0.70f)
                    lineTo(w * 0.765f, h * 0.52f)
                    lineTo(w * 0.69f, h * 0.46f)
                    lineTo(w * 0.765f, h * 0.40f)
                    close()
                }
                drawPath(star, Color(0xFFFDE68A))
                listOf(0.20f to 0.35f, 0.34f to 0.70f, 0.50f to 0.28f, 0.62f to 0.62f, 0.10f to 0.75f).forEach { (x, y) ->
                    drawCircle(Color.White.copy(alpha = 0.7f), 2f, Offset(w * x, h * y))
                }
            }
            ProfileBanner.None -> Unit
        }
    }
}

/** Sticker decorativo del perfil (etapa 5). */
@Composable
fun ProfileStickerBadge(sticker: ProfileSticker, modifier: Modifier = Modifier) {
    if (sticker == ProfileSticker.None) return
    val accent = MaterialTheme.colorScheme.primary
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.12f),
        contentColor = Color.White,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f))
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(Modifier.size(14.dp)) {
                when (sticker) {
                    ProfileSticker.GG -> {
                        val bubble = Path().apply {
                            addRoundRect(RoundRect(Rect(0f, 1f, size.width, size.height * 0.76f), CornerRadius(4f)))
                            moveTo(size.width * 0.30f, size.height * 0.74f)
                            lineTo(size.width * 0.44f, size.height)
                            lineTo(size.width * 0.54f, size.height * 0.74f)
                            close()
                        }
                        drawPath(bubble, accent)
                    }
                    ProfileSticker.Fuego -> {
                        val flame = Path().apply {
                            moveTo(size.width * 0.5f, 0f)
                            lineTo(size.width * 0.88f, size.height * 0.60f)
                            lineTo(size.width * 0.70f, size.height)
                            lineTo(size.width * 0.30f, size.height)
                            lineTo(size.width * 0.12f, size.height * 0.60f)
                            close()
                        }
                        drawPath(flame, Color(0xFFF97316))
                        drawCircle(Color(0xFFFDE68A), size.width * 0.16f, Offset(size.width * 0.5f, size.height * 0.70f))
                    }
                    ProfileSticker.None -> Unit
                }
            }
            Text(stringResource(sticker.labelRes), style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Mascota decorativa del perfil (etapa 5): dibujo original, solo estética. */
@Composable
fun ProfilePetView(pet: ProfilePet, modifier: Modifier = Modifier) {
    if (pet == ProfilePet.None) return
    Canvas(modifier.size(54.dp)) {
        when (pet) {
            ProfilePet.Bruno -> {
                val fur = Color(0xFFE8B98A)
                val ink = Color(0xFF3B2B22)
                val earLeft = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.34f)
                    lineTo(size.width * 0.26f, size.height * 0.06f)
                    lineTo(size.width * 0.44f, size.height * 0.26f)
                    close()
                }
                val earRight = Path().apply {
                    moveTo(size.width * 0.82f, size.height * 0.34f)
                    lineTo(size.width * 0.74f, size.height * 0.06f)
                    lineTo(size.width * 0.56f, size.height * 0.26f)
                    close()
                }
                drawPath(earLeft, fur)
                drawPath(earRight, fur)
                drawCircle(fur, size.minDimension * 0.40f, Offset(size.width * 0.5f, size.height * 0.58f))
                drawCircle(Color(0xFFF6D7BD), size.minDimension * 0.20f, Offset(size.width * 0.5f, size.height * 0.68f))
                drawCircle(ink, size.minDimension * 0.055f, Offset(size.width * 0.36f, size.height * 0.52f))
                drawCircle(ink, size.minDimension * 0.055f, Offset(size.width * 0.64f, size.height * 0.52f))
                drawCircle(Color(0xFF9A5B3F), size.minDimension * 0.045f, Offset(size.width * 0.5f, size.height * 0.66f))
                drawLine(ink, Offset(size.width * 0.14f, size.height * 0.62f), Offset(size.width * 0.34f, size.height * 0.66f), 2f)
                drawLine(ink, Offset(size.width * 0.86f, size.height * 0.62f), Offset(size.width * 0.66f, size.height * 0.66f), 2f)
            }
            ProfilePet.Nube -> {
                val cloud = Color(0xFFE7EAFB)
                val ink = Color(0xFF3B3554)
                drawCircle(cloud, size.minDimension * 0.26f, Offset(size.width * 0.30f, size.height * 0.56f))
                drawCircle(cloud, size.minDimension * 0.30f, Offset(size.width * 0.52f, size.height * 0.46f))
                drawCircle(cloud, size.minDimension * 0.24f, Offset(size.width * 0.74f, size.height * 0.58f))
                drawRoundRect(
                    cloud,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.52f),
                    size = Size(size.width * 0.64f, size.height * 0.26f),
                    cornerRadius = CornerRadius(size.height * 0.16f)
                )
                drawCircle(ink, size.minDimension * 0.05f, Offset(size.width * 0.40f, size.height * 0.55f))
                drawCircle(ink, size.minDimension * 0.05f, Offset(size.width * 0.62f, size.height * 0.55f))
                drawLine(
                    ink,
                    Offset(size.width * 0.44f, size.height * 0.68f),
                    Offset(size.width * 0.58f, size.height * 0.68f),
                    3f
                )
            }
            ProfilePet.None -> Unit
        }
    }
}
