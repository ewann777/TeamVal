package com.example.teamval.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.ui.components.FeatureCard
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.SectionIcon
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.theme.LocalTeamValVisuals
import com.example.teamval.ui.theme.TeamValTheme

@Composable
fun HomeScreen(
    onOpenStacks: () -> Unit,
    onOpenScrims: () -> Unit,
    onOpenStore: () -> Unit,
    modifier: Modifier = Modifier,
    balance: Int = 0,
    onOpenWallet: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            BrandHeader()
            BalancePill(balance = balance, onClick = onOpenWallet)
            WelcomeCard(onOpenStacks = onOpenStacks)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.example.teamval.ui.components.AccentBar(height = 22.dp)
                    Text(
                        text = stringResource(R.string.home_explore),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() }
                    )
                }
                Text(
                    text = stringResource(R.string.home_explore_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FeatureCard(
                    title = stringResource(R.string.nav_stacks),
                    description = stringResource(R.string.home_stacks_description),
                    iconRes = R.drawable.ic_team,
                    onClick = onOpenStacks,
                    comingSoon = false
                )
                FeatureCard(
                    title = stringResource(R.string.nav_scrims),
                    description = stringResource(R.string.home_scrims_description),
                    iconRes = R.drawable.ic_challenge,
                    onClick = onOpenScrims,
                    comingSoon = false
                )
                FeatureCard(
                    title = stringResource(R.string.nav_store),
                    description = stringResource(R.string.home_store_description),
                    iconRes = R.drawable.ic_store,
                    onClick = onOpenStore,
                    comingSoon = false
                )
            }
            Text(
                text = stringResource(R.string.home_footer),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun BrandHeader() {
    val visuals = LocalTeamValVisuals.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionIcon(R.drawable.ic_team)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .background(visuals.accentGradient, CircleShape)
                )
            }
            Text(
                text = stringResource(R.string.brand_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Saldo de Timbal Coins accesible desde el inicio (etapa 5). */
@Composable
private fun BalancePill(balance: Int, onClick: () -> Unit) {
    val visuals = LocalTeamValVisuals.current
    Row(
        Modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("home_balance"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_coin),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = visuals.accentText
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_balance_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.coin_name),
                style = MaterialTheme.typography.titleMedium,
                color = visuals.accentText
            )
        }
        Text(
            text = balance.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = visuals.accentText
        )
    }
}

@Composable
private fun WelcomeCard(onOpenStacks: () -> Unit) {
    val visuals = LocalTeamValVisuals.current
    val colors = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.extraLarge
    val start = lerp(colors.background, colors.primary, if (visuals.isDark) 0.22f else 0.08f)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = visuals.shadowElevation + 4.dp,
                shape = shape,
                ambientColor = visuals.shadowColor,
                spotColor = visuals.shadowColor
            ),
        shape = shape,
        color = Color.Transparent,
        contentColor = colors.onBackground
    ) {
        Box {
            Box(
                Modifier.matchParentSize().background(
                    Brush.linearGradient(listOf(start, colors.primaryContainer, colors.surface))
                )
            )
            // Orbes luminosos con caída suave: profundidad sin coste de desenfoque.
            Box(Modifier.matchParentSize()) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        visuals.accent.copy(alpha = 0.30f),
                        radius = size.minDimension * 0.60f,
                        center = Offset(size.width * 0.92f, -size.height * 0.18f)
                    )
                    drawCircle(
                        visuals.accentText.copy(alpha = 0.16f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width * 0.06f, size.height * 1.15f)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(visuals.accentGradient, CircleShape)
                    )
                    Text(
                        text = stringResource(R.string.home_eyebrow),
                        style = MaterialTheme.typography.labelMedium,
                        color = visuals.accentText
                    )
                }
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = stringResource(R.string.home_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant
                )
                PrimaryButton(
                    text = stringResource(R.string.home_action),
                    onClick = onOpenStacks
                )
            }
            Box(Modifier.matchParentSize().border(1.dp, visuals.glassBorder, shape))
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    TeamValTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(onOpenStacks = {}, onOpenScrims = {}, onOpenStore = {})
        }
    }
}
