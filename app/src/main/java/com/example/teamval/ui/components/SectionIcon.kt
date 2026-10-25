package com.example.teamval.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teamval.ui.theme.LocalTeamValVisuals

/** Icono de sección sobre placa en degradado de acento, con halo suave. */
@Composable
fun SectionIcon(@DrawableRes iconRes: Int, modifier: Modifier = Modifier) {
    val visuals = LocalTeamValVisuals.current
    val shape = MaterialTheme.shapes.medium
    Surface(
        modifier = modifier
            .size(48.dp)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = visuals.glowColor,
                spotColor = visuals.glowColor
            ),
        shape = shape,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Box(
            Modifier.background(visuals.accentGradient, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
