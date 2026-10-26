package com.example.teamval.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.teamval.R
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.AppBackground
import com.example.teamval.domain.model.AppFont
import com.example.teamval.domain.model.AppThemeMode
import com.example.teamval.domain.model.AppearancePreset
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.ChoiceChips
import com.example.teamval.ui.components.CustomizationPage
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.SettingsSection
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.labelRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppearanceScreen(state: ProfileUiState, onChange: (AppAppearance) -> Unit, onSave: () -> Unit, onDiscard: () -> Unit, onReset: () -> Unit) {
    val appearance = state.appearanceDraft ?: state.saved?.appearance ?: return
    var customSelected by rememberSaveable { mutableStateOf(false) }
    CustomizationPage(stringResource(R.string.appearance_title), state.saving, state.messageRes, onDiscard, onSave) {
        SettingsSection(stringResource(R.string.appearance_presets), stringResource(R.string.appearance_presets_hint)) {
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
            if (customSelected) Text(stringResource(R.string.preset_custom_hint), style = MaterialTheme.typography.bodyMedium)
        }
        SettingsSection(stringResource(R.string.appearance_preview), stringResource(R.string.appearance_preview_hint)) {
            StatusBadge(stringResource(R.string.preview_live))
            Text(stringResource(R.string.appearance_sample_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.appearance_sample_body), style = MaterialTheme.typography.bodyLarge)
            PrimaryButton(
                text = stringResource(R.string.apply_appearance),
                onClick = onSave,
                enabled = !state.saving
            )
        }
        SettingsSection(stringResource(R.string.appearance_theme)) {
            ChoiceChips(AppThemeMode.entries, { it == appearance.mode }, { onChange(appearance.copy(mode = it)) }, { stringResource(it.labelRes) })
        }
        SettingsSection(stringResource(R.string.appearance_accent), stringResource(R.string.accent_contrast_hint)) {
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
        SettingsSection(stringResource(R.string.appearance_background)) {
            ChoiceChips(AppBackground.entries, { it == appearance.background }, { onChange(appearance.copy(background = it)) }, { stringResource(it.labelRes) })
        }
        SettingsSection(stringResource(R.string.appearance_font), stringResource(R.string.font_license_note)) {
            ChoiceChips(AppFont.entries, { it == appearance.font }, { onChange(appearance.copy(font = it)) }, { stringResource(it.labelRes) })
            Text(stringResource(R.string.font_sample), style = MaterialTheme.typography.bodyLarge)
        }
        TextButton(onClick = { customSelected = false; onReset() }, enabled = !state.saving) { Text(stringResource(R.string.appearance_reset)) }
    }
}
