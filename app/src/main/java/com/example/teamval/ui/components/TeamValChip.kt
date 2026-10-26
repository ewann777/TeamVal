package com.example.teamval.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.example.teamval.ui.theme.LocalTeamValVisuals
import com.example.teamval.ui.theme.contrastingColor

/**
 * Chip con estado seleccionado iluminado por el acento activo. Se usa en
 * filtros, pestañas y editores de personalización para mantener un único estilo.
 */
@Composable
fun AccentFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val visuals = LocalTeamValVisuals.current
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colors.surface.copy(alpha = 0.65f),
            labelColor = colors.onSurface,
            selectedContainerColor = lerp(colors.surface, colors.primary, 0.14f),
            selectedLabelColor = contrastingColor(visuals.accentText, lerp(colors.surface, colors.primary, 0.14f)),
            disabledContainerColor = colors.surface.copy(alpha = 0.4f),
            disabledLabelColor = colors.onSurfaceVariant.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            1.dp,
            if (selected) colors.primary.copy(alpha = 0.85f) else colors.outlineVariant
        )
    )
}
