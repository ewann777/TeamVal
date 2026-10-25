package com.example.teamval.ui.screens.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.Scrim
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.Team
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.EmptyState
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPanel
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.TeamMonogram
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.theme.LocalTeamValVisuals

@Composable
fun TeamsHubScreen(
    state: TeamsUiState,
    onSegmentChange: (HubSegment) -> Unit,
    onCreateTeam: () -> Unit,
    onOpenTeam: (String) -> Unit,
    onCreateScrim: (String?) -> Unit,
    onOpenScrim: (String) -> Unit,
    onSimulateScrim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                stringResource(R.string.teams_hub_title),
                stringResource(R.string.teams_hub_subtitle)
            )
            Text(
                stringResource(R.string.team_demo_notice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccentFilterChip(
                    selected = state.local.segment == HubSegment.Teams,
                    onClick = { onSegmentChange(HubSegment.Teams) },
                    label = stringResource(R.string.teams_segment),
                    modifier = Modifier.testTag("segment_teams")
                )
                AccentFilterChip(
                    selected = state.local.segment == HubSegment.Scrims,
                    onClick = { onSegmentChange(HubSegment.Scrims) },
                    label = stringResource(R.string.scrims_segment),
                    modifier = Modifier.testTag("segment_scrims")
                )
            }
            when (state.local.segment) {
                HubSegment.Teams -> TeamsSegment(state, onCreateTeam = onCreateTeam, onOpenTeam = onOpenTeam)
                HubSegment.Scrims -> ScrimsSegment(
                    state,
                    onCreateTeam = onCreateTeam,
                    onCreateScrim = onCreateScrim,
                    onOpenScrim = onOpenScrim,
                    onSimulateScrim = onSimulateScrim
                )
            }
        }
    }
}

@Composable
private fun TeamsSegment(state: TeamsUiState, onCreateTeam: () -> Unit, onOpenTeam: (String) -> Unit) {
    val myTeam = state.myTeam
    if (myTeam == null) {
        EmptyState(
            title = stringResource(R.string.no_team_title),
            description = stringResource(R.string.no_team_description),
            action = { PrimaryButton(text = stringResource(R.string.create_team), onClick = onCreateTeam, modifier = Modifier.testTag("create_team")) }
        )
    } else {
        StackPanel {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                TeamMonogram(myTeam, size = 64.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.my_team), style = MaterialTheme.typography.labelMedium, color = LocalTeamValVisuals.current.accentText)
                    Text(myTeam.name, style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(myTeam.tag)
                        Text(
                            stringResource(myTeam.region.labelRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (myTeam.motto.isNotBlank()) Text(myTeam.motto, style = MaterialTheme.typography.bodyMedium)
            Text(
                pluralStringResource(R.plurals.team_member_count, myTeam.memberCount, myTeam.memberCount) +
                    " · " + stringResource(R.string.role_captain),
                style = MaterialTheme.typography.labelLarge
            )
            PrimaryButton(
                text = stringResource(R.string.open_my_team),
                onClick = { onOpenTeam(myTeam.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Text(stringResource(R.string.rival_teams), style = MaterialTheme.typography.titleMedium)
    state.snapshot.teams
        .filter { it.id != state.myTeamId }
        .forEach { rival -> RivalTeamCard(rival, onOpen = { onOpenTeam(rival.id) }) }
}

@Composable
private fun RivalTeamCard(team: Team, onOpen: () -> Unit) {
    StackPanel(Modifier.testTag("rival_card_${team.id}")) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            TeamMonogram(team, size = 56.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(team.name, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(team.tag)
                    Text(
                        stringResource(team.region.labelRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Text(team.motto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            pluralStringResource(R.plurals.team_member_count, team.memberCount, team.memberCount),
            style = MaterialTheme.typography.labelMedium
        )
        OutlinedButton(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.open_team))
        }
    }
}

@Composable
private fun ScrimsSegment(
    state: TeamsUiState,
    onCreateTeam: () -> Unit,
    onCreateScrim: (String?) -> Unit,
    onOpenScrim: (String) -> Unit,
    onSimulateScrim: () -> Unit
) {
    if (state.myTeamId == null) {
        EmptyState(
            title = stringResource(R.string.no_team_title),
            description = stringResource(R.string.error_no_team),
            action = { PrimaryButton(text = stringResource(R.string.create_team), onClick = onCreateTeam, modifier = Modifier.testTag("create_team")) }
        )
        return
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        PrimaryButton(
            text = stringResource(R.string.create_scrim),
            onClick = { onCreateScrim(null) },
            modifier = Modifier.testTag("create_scrim")
        )
        OutlinedButton(onClick = onSimulateScrim) { Text(stringResource(R.string.simulate_scrim)) }
    }
    if (state.myScrims.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.scrims_empty_title),
            description = stringResource(R.string.scrims_empty_description)
        )
    } else {
        state.myScrims.sortedBy { statusOrder(it.status) }.forEach { scrim ->
            ScrimCard(
                scrim = scrim,
                state = state,
                onOpen = { onOpenScrim(scrim.id) }
            )
        }
    }
}

private fun statusOrder(status: ScrimStatus): Int = when (status) {
    ScrimStatus.Proposed -> 0
    ScrimStatus.Confirmed -> 1
    ScrimStatus.Completed -> 2
    ScrimStatus.Cancelled -> 3
}

@Composable
private fun ScrimCard(scrim: Scrim, state: TeamsUiState, onOpen: () -> Unit) {
    val homeName = state.teamName(scrim.homeTeamId)
    val awayName = state.teamName(scrim.awayTeamId)
    val incoming = scrim.awayTeamId == state.myTeamId
    StackPanel(Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(stringResource(scrim.status.labelRes))
            StatusBadge(
                stringResource(if (incoming) R.string.scrims_incoming else R.string.scrims_outgoing)
            )
        }
        Text(
            stringResource(R.string.scrim_versus, homeName, awayName),
            style = MaterialTheme.typography.titleMedium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                stringResource(R.string.scrim_mode_label) + ": " + stringResource(scrim.mode.labelRes),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            stringResource(R.string.scrim_schedule_label) + ": " + scrim.schedule,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (scrim.status == ScrimStatus.Completed && scrim.homeScore != null && scrim.awayScore != null) {
            Text(
                stringResource(R.string.score_value, scrim.homeScore, scrim.awayScore),
                style = MaterialTheme.typography.titleMedium,
                color = LocalTeamValVisuals.current.accentText
            )
        }
        OutlinedButton(onClick = onOpen, modifier = Modifier.fillMaxWidth().testTag("manage_scrim")) {
            Text(stringResource(R.string.manage_scrim))
        }
    }
}
