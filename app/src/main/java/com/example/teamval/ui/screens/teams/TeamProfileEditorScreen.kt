package com.example.teamval.ui.screens.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.BannerStyle
import com.example.teamval.domain.model.CosmeticContext
import com.example.teamval.domain.model.GradientStyle
import com.example.teamval.domain.model.IdentityDraftField
import com.example.teamval.domain.model.MemberStyle
import com.example.teamval.domain.model.ProfileFrame
import com.example.teamval.domain.model.SectionLayout
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.domain.model.TeamBackground
import com.example.teamval.domain.model.TeamBadge
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.applyToTeamIdentity
import com.example.teamval.domain.model.isEquipped
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.ColorSwatchRow
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.TeamIdentityPreview
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.components.primarySwatches
import com.example.teamval.ui.components.secondarySwatches
import com.example.teamval.ui.components.shopChipLabel

/** Team profile editor with live preview, save, cancel and restore actions. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamProfileEditorScreen(
    identity: TeamIdentity,
    invalid: Set<IdentityDraftField>,
    teamName: String,
    teamTag: String,
    regionLabel: String,
    modifier: Modifier = Modifier,
    ownedTeamCosmetics: List<ShopItem> = emptyList(),
    lockedTeamVariants: Set<String> = emptySet(),
    onDraftChange: (TeamIdentity) -> Unit,
    onSave: () -> Boolean,
    onReset: () -> Unit,
    onDiscard: () -> Unit,
    onEditLogo: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                stringResource(R.string.identity_edit_title),
                stringResource(R.string.identity_edit_subtitle),
                onBack,
                R.string.back_to_scrims
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.identity_preview_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TeamIdentityPreview(
                    identity = identity,
                    teamName = teamName,
                    teamTag = teamTag,
                    regionLabel = regionLabel,
                    modifier = Modifier.fillMaxWidth().testTag("identity_preview")
                )
            }

            OutlinedButton(
                onClick = onEditLogo,
                modifier = Modifier.fillMaxWidth().testTag("edit_logo")
            ) { Text(stringResource(R.string.logo_editor_title)) }

            EditorSection(title = stringResource(R.string.identity_colors)) {
                ColorSwatchRow(
                    swatches = primarySwatches,
                    selected = identity.primaryColor,
                    tag = "identity_primary",
                    onSelect = { onDraftChange(identity.copy(primaryColor = it)) }
                )
                ColorSwatchRow(
                    swatches = secondarySwatches,
                    selected = identity.secondaryColor,
                    tag = "identity_secondary",
                    onSelect = { onDraftChange(identity.copy(secondaryColor = it)) }
                )
                Text(
                    stringResource(R.string.logo_gradient),
                    style = MaterialTheme.typography.labelLarge
                )
                ChipRow {
                    GradientStyle.entries.forEach { style ->
                        AccentFilterChip(
                            selected = identity.gradient == style,
                            onClick = { onDraftChange(identity.copy(gradient = style)) },
                            label = stringResource(style.labelRes)
                        )
                    }
                }
            }

            EditorSection(title = stringResource(R.string.identity_banner)) {
                ChipRow {
                    BannerStyle.entries.forEach { style ->
                        AccentFilterChip(
                            selected = identity.banner == style,
                            onClick = { onDraftChange(identity.copy(banner = style)) },
                            label = stringResource(style.labelRes)
                        )
                    }
                }
                Text(
                    stringResource(R.string.identity_background),
                    style = MaterialTheme.typography.labelLarge
                )
                ChipRow {
                    TeamBackground.entries.forEach { style ->
                        AccentFilterChip(
                            selected = identity.background == style,
                            onClick = { onDraftChange(identity.copy(background = style)) },
                            label = shopChipLabel(stringResource(style.labelRes), style.name, lockedTeamVariants),
                            enabled = style.name !in lockedTeamVariants
                        )
                    }
                }
                Text(
                    stringResource(R.string.identity_frame),
                    style = MaterialTheme.typography.labelLarge
                )
                ChipRow {
                    ProfileFrame.entries.forEach { frame ->
                        AccentFilterChip(
                            selected = identity.frame == frame,
                            onClick = { onDraftChange(identity.copy(frame = frame)) },
                            label = shopChipLabel(stringResource(frame.labelRes), frame.name, lockedTeamVariants),
                            enabled = frame.name !in lockedTeamVariants
                        )
                    }
                }
            }

            EditorSection(title = stringResource(R.string.identity_badges)) {
                Text(
                    stringResource(R.string.identity_badges_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ChipRow {
                    TeamBadge.entries.forEach { badge ->
                        val selected = badge in identity.badges
                        AccentFilterChip(
                            selected = selected,
                            onClick = {
                                val next = if (selected) identity.badges - badge
                                else if (identity.badges.size < 3) identity.badges + badge
                                else identity.badges
                                onDraftChange(identity.copy(badges = next))
                            },
                            label = shopChipLabel(stringResource(badge.labelRes), badge.name, lockedTeamVariants),
                            enabled = badge.name !in lockedTeamVariants
                        )
                    }
                }
            }

            // Cosméticos de equipo comprados en la tienda (etapa 5): se aplican
            // desde aquí respetando el flujo del editor (guardar/cancelar) y la
            // condición de capitán para poder editar la identidad del equipo.
            if (ownedTeamCosmetics.isNotEmpty()) {
                EditorSection(title = stringResource(R.string.shop_team_cosmetics)) {
                    Text(
                        stringResource(R.string.shop_team_cosmetics_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ChipRow {
                        ownedTeamCosmetics.forEach { item ->
                            val binding = item.binding
                            if (binding != null) {
                                val equipped = binding.isEquipped(CosmeticContext(teamIdentity = identity))
                                AccentFilterChip(
                                    selected = equipped,
                                    onClick = {
                                        onDraftChange(binding.applyToTeamIdentity(identity, equip = !equipped))
                                    },
                                    label = stringResource(
                                        if (equipped) R.string.shop_equipped_named else R.string.shop_equip_named,
                                        item.name
                                    ),
                                    modifier = Modifier.testTag("team_cosmetic_${item.id}")
                                )
                            }
                        }
                    }
                }
            }

            EditorSection(title = stringResource(R.string.identity_motto)) {
                OutlinedTextField(
                    value = identity.motto,
                    onValueChange = { onDraftChange(identity.copy(motto = it)) },
                    label = { Text(stringResource(R.string.identity_motto)) },
                    isError = IdentityDraftField.Motto in invalid,
                    supportingText = {
                        Text(
                            stringResource(
                                if (IdentityDraftField.Motto in invalid) R.string.team_motto_error
                                else R.string.team_motto_hint
                            )
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("identity_motto")
                )
                OutlinedTextField(
                    value = identity.description,
                    onValueChange = { onDraftChange(identity.copy(description = it)) },
                    label = { Text(stringResource(R.string.identity_description)) },
                    isError = IdentityDraftField.Description in invalid,
                    supportingText = {
                        Text(
                            if (IdentityDraftField.Description in invalid) {
                                stringResource(R.string.team_description_error)
                            } else {
                                stringResource(R.string.team_description_hint, identity.description.length)
                            }
                        )
                    },
                    minLines = 2,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("identity_description")
                )
            }

            EditorSection(title = stringResource(R.string.identity_layout)) {
                ChipRow {
                    SectionLayout.entries.forEach { layout ->
                        AccentFilterChip(
                            selected = identity.layout == layout,
                            onClick = { onDraftChange(identity.copy(layout = layout)) },
                            label = stringResource(layout.labelRes)
                        )
                    }
                }
                Text(
                    stringResource(R.string.identity_member_style),
                    style = MaterialTheme.typography.labelLarge
                )
                ChipRow {
                    MemberStyle.entries.forEach { style ->
                        AccentFilterChip(
                            selected = identity.memberStyle == style,
                            onClick = { onDraftChange(identity.copy(memberStyle = style)) },
                            label = stringResource(style.labelRes)
                        )
                    }
                }
            }

            PrimaryButton(
                text = stringResource(R.string.identity_save),
                onClick = { onSave() },
                modifier = Modifier.fillMaxWidth().testTag("save_identity")
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f).testTag("reset_identity")
                ) { Text(stringResource(R.string.identity_reset)) }
                OutlinedButton(
                    onClick = {
                        onDiscard()
                        onBack()
                    },
                    modifier = Modifier.weight(1f).testTag("cancel_identity")
                ) { Text(stringResource(R.string.identity_cancel)) }
            }
        }
    }
}

@Composable
private fun EditorSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { content() }
}
