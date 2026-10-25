package com.example.teamval.ui.screens.shop

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.ItemCategory
import com.example.teamval.domain.model.ItemRarity
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.theme.LocalTeamValVisuals

/** Color de la rareza estética (decorativa, sin efectos competitivos). */
val ItemRarity.color: Color
    get() = when (this) {
        ItemRarity.Common -> Color(0xFF9CA3AF)
        ItemRarity.Rare -> Color(0xFF60A5FA)
        ItemRarity.Epic -> Color(0xFFA78BFA)
        ItemRarity.Legendary -> Color(0xFFFBBF24)
    }

val ItemState.color: Color
    get() = when (this) {
        ItemState.Blocked -> Color(0xFFF59E0B)
        ItemState.Available -> Color(0xFF9CA3AF)
        ItemState.Owned -> Color(0xFF60A5FA)
        ItemState.Equipped -> Color(0xFF34D399)
    }

/** Cantidad de Timbal Coins con icono original. */
@Composable
fun CoinAmount(amount: Int, modifier: Modifier = Modifier, showSign: Boolean = false) {
    val visuals = LocalTeamValVisuals.current
    val sign = if (showSign && amount > 0) "+" else ""
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_coin),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = visuals.accentText
        )
        Text(
            "$sign$amount",
            style = MaterialTheme.typography.titleMedium,
            color = visuals.accentText
        )
    }
}

/** Píldora con la rareza del artículo. */
@Composable
fun RarityBadge(rarity: ItemRarity, modifier: Modifier = Modifier) {
    Pill(
        text = stringResource(rarity.labelRes),
        color = rarity.color,
        modifier = modifier,
        testTagSuffix = "rarity_${rarity.name.lowercase()}"
    )
}

/** Píldora con el estado del artículo para el jugador. */
@Composable
fun ItemStateBadge(state: ItemState, modifier: Modifier = Modifier) {
    Pill(
        text = stringResource(state.labelRes),
        color = state.color,
        modifier = modifier,
        testTagSuffix = "state_${state.name.lowercase()}"
    )
}

/** Píldora de color reutilizable para estados. */
@Composable
fun PillLabel(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.55f))
    ) {
        Text(
            text,
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun Pill(text: String, color: Color, modifier: Modifier = Modifier, testTagSuffix: String = "") {
    PillLabel(text, color, modifier)
}

/**
 * Arte original de la previsualización del artículo: fondo con el tono de la
 * rareza y una silueta dibujada según la categoría (sin imágenes externas).
 */
@Composable
fun CosmeticArt(item: ShopItem, modifier: Modifier = Modifier, compact: Boolean = false) {
    val rarity = item.rarity.color
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier
            .fillMaxWidth()
            .height(if (compact) 96.dp else 132.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(rarity.copy(alpha = 0.30f), rarity.copy(alpha = 0.08f))))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Brillo superior.
            drawCircle(Color.White.copy(alpha = 0.10f), radius = h * 0.9f, center = Offset(w * 0.85f, -h * 0.35f))
            val ink = Color.White.copy(alpha = 0.92f)
            when (item.category) {
                ItemCategory.AvatarFrame -> {
                    drawCircle(ink.copy(alpha = 0.25f), h * 0.26f, Offset(w * 0.5f, h * 0.5f))
                    drawCircle(rarity, h * 0.34f, Offset(w * 0.5f, h * 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(6f))
                    drawCircle(Color.White.copy(alpha = 0.8f), h * 0.10f, Offset(w * 0.5f, h * 0.5f))
                }
                ItemCategory.ProfileBackground -> {
                    drawRect(Brush.linearGradient(listOf(rarity.copy(alpha = 0.85f), rarity.copy(alpha = 0.25f))))
                    drawCircle(Color.White.copy(alpha = 0.55f), 4f, Offset(w * 0.25f, h * 0.30f))
                    drawCircle(Color.White.copy(alpha = 0.40f), 3f, Offset(w * 0.62f, h * 0.22f))
                    drawCircle(Color.White.copy(alpha = 0.50f), 3f, Offset(w * 0.78f, h * 0.55f))
                    drawCircle(Color.White.copy(alpha = 0.35f), 4f, Offset(w * 0.38f, h * 0.68f))
                }
                ItemCategory.Banner -> {
                    drawRect(rarity.copy(alpha = 0.85f), topLeft = Offset(w * 0.12f, h * 0.30f), size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.40f))
                    drawCircle(Color.White.copy(alpha = 0.35f), h * 0.14f, Offset(w * 0.22f, h * 0.70f))
                }
                ItemCategory.Badge -> {
                    val star = Path().apply {
                        val cx = w * 0.5f
                        val cy = h * 0.5f
                        val outer = h * 0.30f
                        val inner = outer * 0.45f
                        for (i in 0 until 10) {
                            val r = if (i % 2 == 0) outer else inner
                            val angle = Math.toRadians((i * 36 - 90).toDouble())
                            val x = (cx + r * Math.cos(angle)).toFloat()
                            val y = (cy + r * Math.sin(angle)).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(star, rarity)
                }
                ItemCategory.Sticker -> {
                    drawRoundRect(
                        ink.copy(alpha = 0.90f),
                        topLeft = Offset(w * 0.20f, h * 0.32f),
                        size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.36f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(h * 0.18f)
                    )
                    drawCircle(rarity, h * 0.07f, Offset(w * 0.5f, h * 0.5f))
                }
                ItemCategory.Pet -> {
                    drawCircle(ink.copy(alpha = 0.85f), h * 0.26f, Offset(w * 0.5f, h * 0.56f))
                    val ear = Path().apply {
                        moveTo(w * 0.34f, h * 0.38f)
                        lineTo(w * 0.38f, h * 0.16f)
                        lineTo(w * 0.52f, h * 0.32f)
                        close()
                    }
                    val ear2 = Path().apply {
                        moveTo(w * 0.66f, h * 0.38f)
                        lineTo(w * 0.62f, h * 0.16f)
                        lineTo(w * 0.48f, h * 0.32f)
                        close()
                    }
                    drawPath(ear, ink.copy(alpha = 0.85f))
                    drawPath(ear2, ink.copy(alpha = 0.85f))
                    drawCircle(rarity, h * 0.05f, Offset(w * 0.42f, h * 0.54f))
                    drawCircle(rarity, h * 0.05f, Offset(w * 0.58f, h * 0.54f))
                }
                ItemCategory.PlayerCard -> {
                    drawRoundRect(
                        ink.copy(alpha = 0.90f),
                        topLeft = Offset(w * 0.16f, h * 0.24f),
                        size = androidx.compose.ui.geometry.Size(w * 0.68f, h * 0.52f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
                    )
                    var x = -h
                    while (x < w) {
                        drawLine(rarity.copy(alpha = 0.55f), Offset(x, h * 0.76f), Offset(x + h, h * 0.24f), 5f)
                        x += 22f
                    }
                }
                ItemCategory.TeamDecoration -> {
                    val shield = Path().apply {
                        moveTo(w * 0.5f, h * 0.18f)
                        lineTo(w * 0.72f, h * 0.30f)
                        lineTo(w * 0.70f, h * 0.62f)
                        lineTo(w * 0.5f, h * 0.82f)
                        lineTo(w * 0.30f, h * 0.62f)
                        lineTo(w * 0.28f, h * 0.30f)
                        close()
                    }
                    drawPath(shield, rarity)
                }
                ItemCategory.TeamLogoFrame -> {
                    drawCircle(rarity, h * 0.34f, Offset(w * 0.5f, h * 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(7f))
                    drawCircle(Color.White.copy(alpha = 0.75f), h * 0.10f, Offset(w * 0.5f, h * 0.5f))
                    drawCircle(accent.copy(alpha = 0.8f), h * 0.20f, Offset(w * 0.5f, h * 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(3f))
                }
                ItemCategory.VisualTheme -> {
                    listOf(rarity, Color(0xFF22D3EE), Color(0xFFF472B6), Color(0xFF34D399)).forEachIndexed { index, color ->
                        val cx = w * (0.28f + 0.15f * index)
                        drawCircle(color, h * 0.13f, Offset(cx, h * 0.5f))
                    }
                }
                ItemCategory.Bundle -> {
                    drawRoundRect(
                        ink.copy(alpha = 0.90f),
                        topLeft = Offset(w * 0.24f, h * 0.34f),
                        size = androidx.compose.ui.geometry.Size(w * 0.52f, h * 0.44f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                    )
                    drawLine(rarity, Offset(w * 0.5f, h * 0.34f), Offset(w * 0.5f, h * 0.78f), 8f)
                    drawLine(rarity, Offset(w * 0.24f, h * 0.50f), Offset(w * 0.76f, h * 0.50f), 8f)
                }
            }
        }
    }
}

/** Fila de categorías deslizables compartida por tienda e inventario. */
@Composable
fun CategoryChips(current: String?, onSelect: (String?) -> Unit) {
    Column(Modifier.testTag("shop_categories"), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.shop_categories), style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                com.example.teamval.ui.components.AccentFilterChip(
                    selected = current == null,
                    onClick = { onSelect(null) },
                    label = stringResource(R.string.shop_all)
                )
            }
            items(ItemCategory.entries) { category ->
                com.example.teamval.ui.components.AccentFilterChip(
                    selected = current == category.name,
                    onClick = { onSelect(category.name) },
                    label = stringResource(category.labelRes)
                )
            }
        }
    }
}

/** Progreso lineal fino para misiones. */
@Composable
fun MissionProgress(fraction: Float, modifier: Modifier = Modifier) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.6f))))
        )
    }
}

/** Etiqueta breve de categoría o texto secundario. */
@Composable
fun MetaText(@StringRes res: Int, modifier: Modifier = Modifier) {
    Text(
        stringResource(res),
        modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/** Efecto de desbloqueo: aviso breve cuando un artículo se compra. */
@Composable
fun UnlockOverlay(itemName: String?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = itemName != null,
        enter = scaleIn(tween(220)) + fadeIn(tween(220)),
        exit = scaleOut(tween(180)) + fadeOut(tween(180)),
        modifier = modifier
    ) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.40f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    stringResource(R.string.shop_unlocked, itemName.orEmpty()),
                    Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
