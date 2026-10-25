package com.example.teamval.ui.screens.teams

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.Team
import com.example.teamval.domain.model.TeamMember
import com.example.teamval.domain.model.TeamReputation
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPanel
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.TeamReputationCard
import com.example.teamval.ui.components.TeamMonogram
import com.example.teamval.ui.components.TeamIdentityPanel
import com.example.teamval.ui.components.TeamRoleBadge
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.theme.LocalTeamValVisuals

@Composable
fun TeamDetailScreen(
    state: TeamsUiState,
    teamId: String,
    currentPlayerId: String,
    reputation: TeamReputation,
    onReport: (String) -> Unit,
    onCreateScrim: (String) -> Unit,
    onCustomize: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val team = state.snapshot.team(teamId)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (team == null) {
            Column(
                modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StackPageHeader(
                    stringResource(R.string.teams_hub_title),
                    stringResource(R.string.error_team_not_found),
                    onBack,
                    R.string.back_to_scrims
                )
            }
            return
        }
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                title = team.name,
                subtitle = stringResource(team.region.labelRes),
                onBack = onBack,
                backLabel = R.string.back_to_scrims
            )
            TeamHero(team, isMine = team.id == state.myTeamId)
            TeamMembersPanel(team, currentPlayerId = currentPlayerId)
            StackPanel {
                Text(stringResource(R.string.team_description), style = MaterialTheme.typography.titleMedium)
                Text(
                    team.identity.description.ifBlank { team.description },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (team.id == state.myTeamId) {
                PrimaryButton(
                    text = stringResource(R.string.identity_customize),
                    onClick = onCustomize,
                    modifier = Modifier.fillMaxWidth().testTag("customize_team")
                )
            }
            ScrimHistoryPanel(state, team)
            TeamReputationCard(reputation = reputation, onReport = onReport)
            if (team.id != state.myTeamId) {
                PrimaryButton(
                    text = stringResource(R.string.create_scrim),
                    onClick = { onCreateScrim(team.id) },
                    modifier = Modifier.fillMaxWidth().testTag("propose_scrim")
                )
            } else {
                Text(
                    stringResource(R.string.my_team),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalTeamValVisuals.current.accentText
                )
            }
        }
    }
}

@Composable
private fun TeamHero(team: Team, isMine: Boolean) {
    TeamIdentityPanel(team.identity) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            TeamMonogram(team, size = 72.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(team.tag)
                    if (isMine) StatusBadge(stringResource(R.string.my_team))
                }
                Text(team.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    pluralStringResource(R.plurals.team_member_count, team.memberCount, team.memberCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (team.identity.motto.ifBlank { team.motto }.isNotBlank()) {
            Text(
                team.identity.motto.ifBlank { team.motto },
                style = MaterialTheme.typography.titleMedium,
                color = LocalTeamValVisuals.current.accentText
            )
        }
        if (team.identity.badges.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                team.identity.badges.forEach { badge ->
                    StatusBadge(stringResource(badge.labelRes))
                }
            }
        }
    }
}

@Composable
private fun ScrimHistoryPanel(state: TeamsUiState, team: Team) {
    val history = state.snapshot.scrims.filter { scrim ->
        scrim.involves(team.id) &&
            (scrim.status == ScrimStatus.Confirmed || scrim.status == ScrimStatus.Completed)
    }
    StackPanel(Modifier.testTag("scrim_history")) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.scrim_history_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (history.isNotEmpty()) {
                Text(
                    pluralStringResource(R.plurals.scrim_history_count, history.size, history.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (history.isEmpty()) {
            Text(
                stringResource(R.string.scrim_history_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        history.forEach { scrim ->
            if (scrim != history.first()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        stringResource(
                            R.string.scrim_versus,
                            state.teamName(scrim.homeTeamId),
                            state.teamName(scrim.awayTeamId)
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                    Text(
                        scrim.schedule,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(stringResource(scrim.status.labelRes))
                if (scrim.homeScore != null && scrim.awayScore != null) {
                    Text(
                        stringResource(R.string.score_value, scrim.homeScore, scrim.awayScore),
                        style = MaterialTheme.typography.titleSmall,
                        color = LocalTeamValVisuals.current.accentText
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamMembersPanel(team: Team, currentPlayerId: String?) {
    StackPanel(Modifier.testTag("team_members")) {
        Text(stringResource(R.string.team_members), style = MaterialTheme.typography.titleMedium)
        team.members.forEachIndexed { index, member ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            MemberRow(member, isCurrentPlayer = member.id == currentPlayerId)
        }
    }
}

@Composable
private fun MemberRow(member: TeamMember, isCurrentPlayer: Boolean) {
    val visuals = LocalTeamValVisuals.current
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp).background(visuals.accentGradient, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                member.name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(member.name, style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(member.rank.labelRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TeamRoleBadge(member.role)
        if (isCurrentPlayer) StatusBadge(stringResource(R.string.my_team))
    }
}
