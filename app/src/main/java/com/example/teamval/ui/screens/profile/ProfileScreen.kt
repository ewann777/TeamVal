package com.example.teamval.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.Reputation
import com.example.teamval.domain.model.ValorantStats
import com.example.teamval.ui.components.AccentBar
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.ProfileHero
import com.example.teamval.ui.components.ReputationPanel
import com.example.teamval.ui.components.SettingsSection
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.theme.LocalTeamValVisuals

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profile: PlayerProfile,
    stats: ValorantStats,
    reputation: Reputation,
    onEdit: () -> Unit,
    onAppearance: () -> Unit,
    onOpenInventory: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 840.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp).testTag("player_profile"),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccentBar(height = 30.dp)
                Text(stringResource(R.string.profile_title), style = MaterialTheme.typography.headlineMedium)
            }
            ProfileHero(profile)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton(text = stringResource(R.string.edit_profile), onClick = onEdit)
                OutlinedButton(onClick = onAppearance, modifier = Modifier.testTag("profile_appearance")) { Text(stringResource(R.string.appearance_title)) }
                OutlinedButton(onClick = onOpenInventory, modifier = Modifier.testTag("profile_inventory")) {
                    Text(stringResource(R.string.profile_inventory))
                }
            }
            Text(stringResource(R.string.appearance_access_hint), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            SettingsSection(stringResource(R.string.biography_title)) {
                Text(profile.biography.ifBlank { stringResource(R.string.biography_empty) }, style = MaterialTheme.typography.bodyLarge)
            }
            SettingsSection(stringResource(R.string.valorant_stats), stringResource(R.string.stats_demo)) {
                StatusBadge(stringResource(stats.rank.labelRes))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    listOf(
                        R.string.stat_matches to stats.matches.toString(),
                        R.string.stat_wins to "${stats.winRate}%",
                        R.string.stat_kd to stringResource(R.string.stat_kd_value, stats.kd),
                        R.string.stat_headshots to "${stats.headshotPercent}%"
                    ).forEach { (label, value) ->
                        Column(Modifier.widthIn(min = 110.dp).weight(1f)) {
                            Text(value, style = MaterialTheme.typography.headlineMedium, color = LocalTeamValVisuals.current.accentText)
                            Text(stringResource(label), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            ReputationPanel(reputation)
        }
    }
}
