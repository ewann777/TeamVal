package com.example.teamval.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.teamval.R
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.AppBackground
import com.example.teamval.domain.model.AppThemeMode
import com.example.teamval.domain.model.AppearancePreset
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.AppBackgroundView
import com.example.teamval.ui.components.ChoiceChips
import com.example.teamval.ui.components.CustomizationPage
import com.example.teamval.ui.components.SettingsSection
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.theme.LocalTeamValVisuals

/**
 * Apariencia de la aplicación: una única sección de colores y estilo (presets,
 * tema, acento y vista previa) más la selección de fondo con miniaturas fieles.
 * El selector de tipografía desapareció; el estilo moderno (Outfit + Exo 2) es
 * el único que ofrece la interfaz.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppearanceScreen(state: ProfileUiState, onChange: (AppAppearance) -> Unit, onSave: () -> Unit, onDiscard: () -> Unit, onReset: () -> Unit) {
    val appearance = state.appearanceDraft ?: state.saved?.appearance ?: return
    var customSelected by rememberSaveable { mutableStateOf(false) }
    CustomizationPage(stringResource(R.string.appearance_title), state.saving, state.messageRes, onDiscard, onSave,
        messageIsError = state.messageIsError) {
        SettingsSection(stringResource(R.string.appearance_colors_style), stringResource(R.string.appearance_colors_style_hint)) {
            AppearancePreview()
            // El tema va el primero: es la opción de mayor impacto y debe verse
            // nada de abrir la pantalla, sin necesidad de desplazar.
            GroupLabel(stringResource(R.string.appearance_theme))
            ChoiceChips(AppThemeMode.entries, { it == appearance.mode }, { onChange(appearance.copy(mode = it)) }, { stringResource(it.labelRes) })
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppearancePreset.entries.forEach { preset ->
                    val label = when (preset) {
                        AppearancePreset.Classic -> R.string.preset_classic
                        AppearancePreset.Premium -> R.string.preset_premium
                        AppearancePreset.IntenseRed -> R.string.preset_red
                        AppearancePreset.BlueCharcoal -> R.string.preset_blue
                        AppearancePreset.ElegantLight -> R.string.preset_light
                    }
                    val preview = preset.apply(appearance)
                    AccentFilterChip(
                        selected = !customSelected && preset.matches(appearance),
                        onClick = { customSelected = false; onChange(preview) },
                        label = stringResource(label),
                        enabled = !state.saving,
                        modifier = Modifier.testTag("preset_${preset.name}"),
                        leadingIcon = {
                            Box(Modifier.size(16.dp).background(Color(0xFF000000 or preview.accentHex.toLong(16)), CircleShape))
                        }
                    )
                }
                AccentFilterChip(
                    selected = customSelected || AppearancePreset.entries.none { it.matches(appearance) },
                    onClick = { customSelected = true },
                    label = stringResource(R.string.preset_custom),
                    enabled = !state.saving,
                    modifier = Modifier.testTag("preset_Custom")
                )
            }
            if (customSelected) Text(stringResource(R.string.preset_custom_hint), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            GroupLabel(stringResource(R.string.appearance_accent), stringResource(R.string.accent_contrast_hint))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "FF4655" to R.string.color_valorant,
                    "C4B5FD" to R.string.color_lavender, "88DCC4" to R.string.color_mint,
                    "8DCBFF" to R.string.color_sky, "FFBBA2" to R.string.color_peach,
                    "F5A9D0" to R.string.color_rose, "E6D488" to R.string.color_gold
                ).forEach { (hex, label) ->
                    AccentFilterChip(
                        selected = appearance.normalized().accentHex == hex,
                        onClick = { onChange(appearance.copy(accentHex = hex)) },
                        label = stringResource(label),
                        enabled = !state.saving,
                        modifier = Modifier.testTag("accent_$hex"),
                        leadingIcon = { Box(Modifier.size(16.dp).background(Color(0xFF000000 or hex.toLong(16)), CircleShape)) }
                    )
                }
            }
            OutlinedTextField(
                value = appearance.accentHex,
                onValueChange = { onChange(appearance.copy(accentHex = it)) },
                label = { Text(stringResource(R.string.custom_color)) }, singleLine = true,
                isError = state.invalidColor,
                supportingText = { Text(stringResource(if (state.invalidColor) R.string.custom_color_error else R.string.custom_color_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("accent_hex"), enabled = !state.saving
            )
        }
        SettingsSection(stringResource(R.string.appearance_background), stringResource(R.string.appearance_background_hint)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppBackground.entries.forEach { style ->
                    BackgroundOption(
                        style = style,
                        selected = style == appearance.background,
                        enabled = !state.saving,
                        onSelect = { onChange(appearance.copy(background = style)) }
                    )
                }
            }
        }
        TextButton(onClick = { customSelected = false; onReset() }, enabled = !state.saving) { Text(stringResource(R.string.appearance_reset)) }
    }
}

/** Etiqueta de grupo dentro de la sección de colores y estilo. */
@Composable
private fun GroupLabel(title: String, description: String? = null) {
    Column(
        Modifier.padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

/**
 * Vista previa de la selección: muestra la tipografía, el acento y el aviso de
 * vista previa en vivo. El fondo real también se previsualiza porque la
 * preferencia sin guardar ya se aplica a toda la pantalla.
 */
@Composable
private fun AppearancePreview() {
    val colors = MaterialTheme.colorScheme
    val visuals = LocalTeamValVisuals.current
    val shape = MaterialTheme.shapes.medium
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface.copy(alpha = 0.75f))
            .border(BorderStroke(1.dp, colors.outlineVariant), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(stringResource(R.string.preview_live))
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.size(20.dp)
                    .clip(CircleShape)
                    .background(visuals.accent)
                    .border(1.dp, colors.outlineVariant, CircleShape)
                    .testTag("accent_swatch")
            )
        }
        Text(
            stringResource(R.string.appearance_sample_title),
            style = MaterialTheme.typography.headlineMedium,
            color = visuals.accentText
        )
        Text(
            stringResource(R.string.appearance_sample_body),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
        Text(
            stringResource(R.string.font_license_note),
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )
    }
}

/**
 * Opción de fondo con miniatura dibujada por [AppBackgroundView], la misma
 * implementación que usa la aplicación: la miniatura no puede mentir.
 */
@Composable
private fun BackgroundOption(style: AppBackground, selected: Boolean, enabled: Boolean, onSelect: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val visuals = LocalTeamValVisuals.current
    val shape = MaterialTheme.shapes.medium
    val thumbShape = MaterialTheme.shapes.small
    Column(
        Modifier
            .width(108.dp)
            .clip(shape)
            .background(if (selected) lerp(colors.surface, colors.primary, 0.16f) else colors.surface.copy(alpha = 0.7f))
            .border(
                BorderStroke(if (selected) 2.dp else 1.dp, if (selected) colors.primary else colors.outlineVariant),
                shape
            )
            .selectable(selected = selected, enabled = enabled, role = Role.Button, onClick = onSelect)
            .padding(8.dp)
            .testTag("background_${style.name}")
    ) {
        Box(
            Modifier.fillMaxWidth().height(64.dp)
                .clip(thumbShape)
                .border(1.dp, colors.outlineVariant.copy(alpha = 0.5f), thumbShape)
                .testTag("background_thumb_${style.name}")
        ) {
            AppBackgroundView(style, Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            if (selected) Box(Modifier.size(8.dp).clip(CircleShape).background(colors.primary))
            Text(
                stringResource(style.labelRes),
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) visuals.accentText else colors.onSurface,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
