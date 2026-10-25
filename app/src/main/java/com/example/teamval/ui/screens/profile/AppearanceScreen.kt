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
    CustomizationPage(stringResource(R.string.appearance_title), state.saving, state.messageRes, onDiscard, onSave) {
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
        SettingsSection(stringResource(R.string.appearance_font)) {
            ChoiceChips(AppFont.entries, { it == appearance.font }, { onChange(appearance.copy(font = it)) }, { stringResource(it.labelRes) })
        }
        TextButton(onClick = onReset, enabled = !state.saving) { Text(stringResource(R.string.appearance_reset)) }
    }
}
