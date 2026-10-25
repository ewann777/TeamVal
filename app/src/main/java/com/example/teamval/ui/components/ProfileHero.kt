package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileBackground
import com.example.teamval.domain.model.ProfileBanner
import com.example.teamval.domain.model.ProfileCardStyle
import com.example.teamval.ui.theme.LocalTeamValVisuals

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileHero(profile: PlayerProfile, modifier: Modifier = Modifier) {
    val visuals = LocalTeamValVisuals.current
    val colors = when (profile.background) {
        ProfileBackground.Nebula -> listOf(Color(0xFF4C386C), Color(0xFF201C30))
        ProfileBackground.Ocean -> listOf(Color(0xFF174E61), Color(0xFF17232F))
        ProfileBackground.Sunset -> listOf(Color(0xFF783E50), Color(0xFF302132))
        ProfileBackground.Graphite -> listOf(Color(0xFF3B3B43), Color(0xFF19191E))
        // Fondos cosméticos de la tienda (etapa 5).
        ProfileBackground.Pastel -> listOf(Color(0xFF8B7BA8), Color(0xFF4A4458))
        ProfileBackground.Aurora -> listOf(Color(0xFF35707E), Color(0xFF1B2A3A))
        ProfileBackground.Galaxia -> listOf(Color(0xFF3B1E63), Color(0xFF171033))
    }
    val shape = MaterialTheme.shapes.extraLarge
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = visuals.shadowElevation + 4.dp,
                shape = shape,
                ambientColor = visuals.shadowColor,
                spotColor = visuals.shadowColor
            ),
        shape = shape,
        color = Color.Transparent,
        contentColor = Color.White
    ) {
        Box {
            // Fondo del perfil con orbes de luz suaves para dar profundidad.
            Box(Modifier.matchParentSize().background(Brush.linearGradient(colors))) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        Color.White.copy(alpha = 0.10f),
                        radius = size.minDimension * 0.55f,
                        center = Offset(size.width * 0.95f, -size.height * 0.1f)
                    )
                    drawCircle(
                        Color.White.copy(alpha = 0.06f),
                        radius = size.minDimension * 0.40f,
                        center = Offset(size.width * 0.02f, size.height * 1.12f)
                    )
                    if (profile.background == ProfileBackground.Galaxia) {
                        // Destellos del fondo cósmico Galaxia (cosmético de la tienda).
                        listOf(
                            0.10f to 0.22f, 0.28f to 0.12f, 0.46f to 0.30f, 0.66f to 0.14f,
                            0.84f to 0.34f, 0.16f to 0.62f, 0.36f to 0.78f, 0.58f to 0.66f,
                            0.76f to 0.84f, 0.92f to 0.58f, 0.50f to 0.92f, 0.24f to 0.42f
                        ).forEachIndexed { index, (fx, fy) ->
                            drawCircle(
                                Color.White.copy(alpha = if (index % 3 == 0) 0.55f else 0.30f),
                                radius = if (index % 4 == 0) 2.4f else 1.6f,
                                center = Offset(size.width * fx, size.height * fy)
                            )
                        }
                    }
                }
            }
            // Tarjeta Cambio: franjas diagonales bajo el contenido.
            if (profile.card == ProfileCardStyle.Cambio) {
                Box(Modifier.matchParentSize()) {
                    Canvas(Modifier.matchParentSize()) {
                        val stripe = 26.dp.toPx()
                        var x = -size.height
                        while (x < size.width) {
                            drawLine(
                                Color.White.copy(alpha = 0.06f),
                                Offset(x, 0f),
                                Offset(x + size.height, size.height),
                                strokeWidth = stripe / 2f
                            )
                            x += stripe
                        }
                    }
                }
            }
            if (profile.banner != ProfileBanner.None) {
                ProfileBannerStrip(profile.banner, Modifier.align(Alignment.TopStart))
            }
            Column(
                Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp,
                    top = if (profile.banner != ProfileBanner.None) 64.dp + 24.dp else 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(stringResource(R.string.profile_eyebrow), style = MaterialTheme.typography.labelMedium, color = Color(0xFFE1DAEF))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    PlayerAvatar(profile.avatar, profile.frame)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(profile.name.ifBlank { stringResource(R.string.profile_name_placeholder) }, style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(profile.region.labelRes), style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE1DAEF))
                        Text(stringResource(R.string.favorite_agent_value, profile.agent.name), style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (profile.headline.isNotBlank()) Text(profile.headline, style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    profile.badges.sortedBy { it.ordinal }.forEach { badge -> StatusBadge(stringResource(badge.labelRes)) }
                    ProfileStickerBadge(profile.sticker)
                    ProfilePetView(profile.pet)
                }
            }
            val accentText = LocalTeamValVisuals.current.accentText
            val (borderWidth, borderBrush) = when (profile.card) {
                ProfileCardStyle.Base -> 1.dp to Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.06f))
                )
                ProfileCardStyle.Especial -> 2.dp to Brush.linearGradient(
                    listOf(accentText, accentText.copy(alpha = 0.35f), accentText)
                )
                ProfileCardStyle.Cambio -> 1.5.dp to Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.42f), accentText.copy(alpha = 0.45f))
                )
            }
            Box(
                Modifier.matchParentSize().border(borderWidth, borderBrush, shape)
            )
        }
    }
}
