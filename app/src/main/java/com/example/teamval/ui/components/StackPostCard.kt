package com.example.teamval.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackCategory
import com.example.teamval.domain.model.StackPost
import com.example.teamval.ui.theme.LocalTeamValVisuals

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StackPostCard(
    post: StackPost,
    isOwner: Boolean,
    isMember: Boolean,
    requestStatus: RequestStatus?,
    pendingCount: Int,
    onJoin: () -> Unit,
    onManage: () -> Unit,
    onSimulate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visuals = LocalTeamValVisuals.current
    val colors = MaterialTheme.colorScheme
    StackPanel(modifier) {
        Text(
            stringResource(R.string.stack_host, post.owner.name),
            style = MaterialTheme.typography.labelLarge,
            color = visuals.accentText
        )
        Text(post.title, style = MaterialTheme.typography.titleLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(stringResource(post.category.labelRes))
            StatusBadge(stringResource(post.rank.labelRes))
            StatusBadge(stringResource(post.region.labelRes))
            StatusBadge(stringResource(post.mode.labelRes))
        }
        Text(post.description, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Text(
            stringResource(R.string.stack_members_line, post.currentMembers, post.capacity),
            style = MaterialTheme.typography.titleMedium
        )
        TeamRoster(
            members = post.currentMembers,
            capacity = post.capacity,
            legend = stringResource(
                R.string.stack_group_legend,
                post.currentMembers,
                post.availableSlots,
                post.playersMissingForFullTeam
            )
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (post.availableSlots > 0) {
                Text(
                    pluralStringResource(R.plurals.stack_slots, post.availableSlots, post.availableSlots),
                    style = MaterialTheme.typography.labelLarge,
                    color = visuals.accentText
                )
            } else {
                Text(
                    stringResource(R.string.stack_no_slots),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant
                )
            }
        }
        Text(
            if (post.playersMissingForFullTeam > 0) {
                pluralStringResource(
                    R.plurals.stack_team_coverage,
                    post.playersMissingForFullTeam,
                    post.capacity,
                    post.playersMissingForFullTeam
                )
            } else {
                stringResource(R.string.stack_team_complete)
            },
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )
        LinearProgressIndicator(
            progress = { post.currentMembers.toFloat() / post.capacity },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = colors.primary,
            trackColor = colors.primary.copy(alpha = 0.16f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            gapSize = 6.dp
        )
        if (isOwner) {
            PrimaryButton(
                text = pluralStringResource(R.plurals.manage_requests, pendingCount, pendingCount),
                onClick = onManage,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedButton(onClick = onSimulate, enabled = !post.isFull, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.simulate_request))
            }
            if (post.isFull) Text(stringResource(R.string.stack_full), color = colors.onSurfaceVariant)
        } else {
            val labelRes = when {
                requestStatus != null -> requestStatus.labelRes
                isMember -> R.string.stack_member
                post.isFull -> R.string.stack_full
                else -> R.string.join_stack
            }
            PrimaryButton(
                text = stringResource(labelRes),
                onClick = onJoin,
                enabled = requestStatus == null && !isMember && !post.isFull,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Equipo completo de 5 representado como cinco plazas: ocupadas (integrantes
 * actuales), libres con borde de acento (cupos que ofrece la publicación) y
 * apagadas (jugadores que aún faltan para completar 5 fuera de este grupo).
 */
@Composable
private fun TeamRoster(members: Int, capacity: Int, legend: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val visuals = LocalTeamValVisuals.current
    Row(
        modifier = modifier.fillMaxWidth().semantics { contentDescription = legend },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(StackCategory.FULL_TEAM) { index ->
            val occupied = index < members
            val openSlot = !occupied && index < capacity
            Box(
                Modifier
                    .size(16.dp)
                    .background(
                        color = when {
                            occupied -> colors.primary
                            openSlot -> Color.Transparent
                            else -> colors.surface
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (occupied) 0.dp else 1.5.dp,
                        color = when {
                            occupied -> colors.primary
                            openSlot -> visuals.accentText
                            else -> colors.outlineVariant
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}
