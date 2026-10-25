package com.example.teamval.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.BannerStyle
import com.example.teamval.domain.model.IdentitySection
import com.example.teamval.domain.model.MemberStyle
import com.example.teamval.domain.model.ProfileFrame
import com.example.teamval.domain.model.TeamBackground
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.sectionOrder
import com.example.teamval.ui.theme.LocalTeamValVisuals

private val previewMembers = listOf("Astra#VAL", "Orbe#CL", "Pixel#BR", "Nix#MX")

/**
 * Live preview of a team profile: banner, background, frame, badges, colors and the
 * selected layout/member presentation, rendered in the configured section order.
 */
@Composable
fun TeamIdentityPreview(
    identity: TeamIdentity,
    teamName: String,
    teamTag: String,
    regionLabel: String,
    modifier: Modifier = Modifier
) {
    val primary = argb(identity.primaryColor)
    val secondary = argb(identity.secondaryColor)
    TeamIdentityPanel(identity, modifier) {
        identity.sectionOrder().forEach { section ->
            when (section) {
                IdentitySection.Banner -> IdentityHeader(
                    identity, teamName, teamTag, regionLabel, primary, secondary
                )
                IdentitySection.Motto -> if (identity.motto.isNotBlank()) {
                    Text(identity.motto, style = MaterialTheme.typography.titleMedium,
                        color = LocalTeamValVisuals.current.accentText)
                }
                IdentitySection.Description -> Text(identity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                IdentitySection.Members -> MembersPreview(identity.memberStyle)
            }
        }
    }
}

/** Misma superficie de identidad en la vista previa y en la ficha guardada. */
@Composable
fun TeamIdentityPanel(
    identity: TeamIdentity,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val primary = argb(identity.primaryColor)
    val secondary = argb(identity.secondaryColor)
    val cardShape = RoundedCornerShape(26.dp)

    val framed = when (identity.frame) {
        ProfileFrame.None -> modifier
        ProfileFrame.Ring -> modifier.border(1.5.dp, primary, cardShape)
        ProfileFrame.Glow -> modifier.shadow(16.dp, cardShape, ambientColor = primary, spotColor = primary)
        ProfileFrame.Gradient ->
            modifier.border(1.5.dp, Brush.linearGradient(listOf(primary, secondary)), cardShape)
        // Marcos de logo de equipos de la tienda (etapa 5).
        ProfileFrame.Orbit ->
            modifier.border(2.dp, primary, cardShape)
                .shadow(14.dp, cardShape, ambientColor = secondary, spotColor = primary)
        ProfileFrame.Prism ->
            modifier.border(2.5.dp, Brush.linearGradient(listOf(primary, secondary, primary)), cardShape)
    }

    Box(framed.fillMaxWidth().clip(cardShape)) {
        TeamProfileBackground(identity.background, primary, secondary, Modifier.matchParentSize())
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun TeamProfileBackground(
    style: TeamBackground,
    primary: Color,
    secondary: Color,
    modifier: Modifier = Modifier
) {
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    Box(modifier) {
        when (style) {
            TeamBackground.Plain -> Box(Modifier.fillMaxSize().background(base))
            TeamBackground.Dusk -> Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(base, primary.copy(alpha = 0.28f)))
                )
            )
            TeamBackground.Aurora -> {
                Box(Modifier.fillMaxSize().background(base))
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        primary.copy(alpha = 0.20f),
                        radius = size.width * 0.55f,
                        center = Offset(size.width * 0.1f, -size.height * 0.08f)
                    )
                    drawCircle(
                        secondary.copy(alpha = 0.16f),
                        radius = size.width * 0.5f,
                        center = Offset(size.width * 0.95f, size.height * 0.45f)
                    )
                }
            }
            TeamBackground.Grid -> {
                Box(Modifier.fillMaxSize().background(base))
                Canvas(Modifier.fillMaxSize()) {
                    val step = 26.dp.toPx()
                    val lineColor = Color.Gray.copy(alpha = 0.22f)
                    var x = 0f
                    while (x < size.width) {
                        drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), 1f)
                        x += step
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1f)
                        y += step
                    }
                }
            }
            // Decoración de equipos de la tienda (etapa 5).
            TeamBackground.Atardecer -> Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(base, Color(0xFFF59E0B).copy(alpha = 0.30f), Color(0xFF7C2D12).copy(alpha = 0.45f))
                    )
                )
            )
        }
    }
}

@Composable
private fun IdentityHeader(
    identity: TeamIdentity,
    teamName: String,
    teamTag: String,
    regionLabel: String,
    primary: Color,
    secondary: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Canvas(Modifier.fillMaxWidth().height(84.dp).clip(RoundedCornerShape(16.dp))) {
            when (identity.banner) {
                BannerStyle.Solid -> drawRect(primary)
                BannerStyle.Gradient -> drawRect(Brush.linearGradient(listOf(primary, secondary)))
                BannerStyle.Aurora -> {
                    drawRect(Brush.linearGradient(listOf(primary, secondary)))
                    drawCircle(
                        Color.White.copy(alpha = 0.20f),
                        radius = size.height * 0.95f,
                        center = Offset(size.width * 0.82f, 0f)
                    )
                    drawCircle(
                        Color.White.copy(alpha = 0.10f),
                        radius = size.height * 0.7f,
                        center = Offset(size.width * 0.2f, size.height)
                    )
                }
                BannerStyle.Grid -> {
                    drawRect(Brush.linearGradient(listOf(primary, secondary)))
                    val step = 18.dp.toPx()
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            Color.White.copy(alpha = 0.16f),
                            Offset(x, 0f), Offset(x, size.height), 1f
                        )
                        x += step
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            Color.White.copy(alpha = 0.16f),
                            Offset(0f, y), Offset(size.width, y), 1f
                        )
                        y += step
                    }
                }
                BannerStyle.Graphite -> {
                    drawRect(
                        Brush.verticalGradient(listOf(Color(0xFF2C2C34), Color(0xFF111115)))
                    )
                    val step = 22.dp.toPx()
                    var offset = -size.height
                    while (offset < size.width) {
                        drawLine(
                            Color.White.copy(alpha = 0.07f),
                            Offset(offset, size.height), Offset(offset + size.height, 0f), 1f
                        )
                        offset += step
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            TeamIdentityLogo(identity, size = 64.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(teamName, style = MaterialTheme.typography.titleLarge, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(teamTag)
                    Text(
                        regionLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (identity.badges.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                identity.badges.forEach { badge ->
                    StatusBadge(stringResource(badge.labelRes))
                }
            }
        }
    }
}

@Composable
private fun MembersPreview(style: MemberStyle) {
    val visuals = LocalTeamValVisuals.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.identity_members_preview),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        when (style) {
            MemberStyle.List -> previewMembers.forEach { MemberLine(it) }
            MemberStyle.Grid -> previewMembers.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { name ->
                        Box(Modifier.weight(1f)) { MemberLine(name) }
                    }
                    if (row.size == 1) Box(Modifier.weight(1f))
                }
            }
            MemberStyle.Cards -> previewMembers.forEach { name ->
                Row(
                    Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(30.dp).background(visuals.accentGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            name.first().uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(name, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun MemberLine(name: String) {
    val visuals = LocalTeamValVisuals.current
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(30.dp).background(visuals.accentGradient, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name.first().uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, modifier = Modifier.weight(1f))
    }
}
