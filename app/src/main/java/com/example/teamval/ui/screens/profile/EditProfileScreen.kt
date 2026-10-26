package com.example.teamval.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.teamval.R
import com.example.teamval.domain.model.AvatarFrame
import com.example.teamval.domain.model.AvatarStyle
import com.example.teamval.domain.model.FavoriteAgent
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ProfileBackground
import com.example.teamval.domain.model.ProfileBadge
import com.example.teamval.domain.model.ProfileField
import com.example.teamval.domain.model.Region
import com.example.teamval.ui.components.ChoiceChips
import com.example.teamval.ui.components.CustomizationPage
import com.example.teamval.ui.components.PlayerAvatar
import com.example.teamval.ui.components.ProfileHero
import com.example.teamval.ui.components.SelectionField
import com.example.teamval.ui.components.SettingsSection
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.components.shopChipLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    state: ProfileUiState,
    lockedVariants: Set<String> = emptySet(),
    onChange: (PlayerProfile) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    val profile = state.profileDraft ?: state.saved?.profile ?: return
    CustomizationPage(stringResource(R.string.edit_profile), state.saving, state.messageRes, onDiscard, onSave,
        messageIsError = state.messageIsError) {
        ProfileHero(profile, Modifier.testTag("profile_preview"))
        SettingsSection(stringResource(R.string.profile_identity)) {
            OutlinedTextField(
                value = profile.name, onValueChange = { onChange(profile.copy(name = it)) },
                label = { Text(stringResource(R.string.profile_name)) }, singleLine = true,
                isError = ProfileField.Name in state.invalidFields,
                supportingText = { Text(stringResource(R.string.profile_name_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("profile_name"), enabled = !state.saving
            )
            OutlinedTextField(
                value = profile.headline, onValueChange = { onChange(profile.copy(headline = it)) },
                label = { Text(stringResource(R.string.profile_headline)) },
                isError = ProfileField.Headline in state.invalidFields,
                supportingText = { Text(stringResource(R.string.profile_headline_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("profile_headline"), enabled = !state.saving
            )
            OutlinedTextField(
                value = profile.biography, onValueChange = { onChange(profile.copy(biography = it)) },
                label = { Text(stringResource(R.string.biography_title)) }, minLines = 3, maxLines = 6,
                isError = ProfileField.Biography in state.invalidFields,
                supportingText = { Text(stringResource(R.string.profile_bio_hint, profile.biography.length)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("profile_biography"), enabled = !state.saving
            )
            SelectionField(stringResource(R.string.favorite_agent), profile.agent, FavoriteAgent.entries, { it.name }, { onChange(profile.copy(agent = it)) })
            SelectionField(stringResource(R.string.filter_region), profile.region, Region.entries, { stringResource(it.labelRes) }, { onChange(profile.copy(region = it)) })
        }
        SettingsSection(stringResource(R.string.profile_avatar)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AvatarStyle.entries.forEach { avatar ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (profile.avatar == avatar) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        modifier = Modifier.selectable(profile.avatar == avatar, role = Role.RadioButton, onClick = { onChange(profile.copy(avatar = avatar)) })
                    ) {
                        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            PlayerAvatar(avatar, profile.frame)
                            Text(stringResource(avatar.labelRes), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        SettingsSection(stringResource(R.string.profile_frame)) {
            ChoiceChips(
                AvatarFrame.entries,
                { it == profile.frame },
                { onChange(profile.copy(frame = it)) },
                { shopChipLabel(stringResource(it.labelRes), it.name, lockedVariants) },
                { it.name !in lockedVariants }
            )
        }
        SettingsSection(stringResource(R.string.profile_background)) {
            ChoiceChips(
                ProfileBackground.entries,
                { it == profile.background },
                { onChange(profile.copy(background = it)) },
                { shopChipLabel(stringResource(it.labelRes), it.name, lockedVariants) },
                { it.name !in lockedVariants }
            )
        }
        SettingsSection(stringResource(R.string.profile_badges), stringResource(R.string.badges_demo_hint)) {
            ChoiceChips(
                ProfileBadge.entries,
                { it in profile.badges },
                { onChange(profile.copy(badges = if (it in profile.badges) profile.badges - it else profile.badges + it)) },
                { shopChipLabel(stringResource(it.labelRes), it.name, lockedVariants) },
                { it.name !in lockedVariants }
            )
        }
    }
}
