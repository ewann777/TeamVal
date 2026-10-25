package com.example.teamval.ui.screens.rewards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.MissionKind
import com.example.teamval.domain.model.MissionRules
import com.example.teamval.domain.model.MissionStatus
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.glassSurface
import com.example.teamval.ui.screens.shop.MissionProgress
import com.example.teamval.ui.screens.shop.PillLabel
import com.example.teamval.ui.screens.shop.color

/**
 * Tarjeta de una misión/recompensa con progreso, estado y acciones
 * (Reclamar, Simular progreso o Requiere verificación).
 */
@Composable
fun MissionCard(
    mission: Mission,
    status: MissionStatus?,
    profile: PlayerProfile,
    onClaim: () -> Unit,
    onSimulate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pending = mission.kind == MissionKind.VerificationPending
    val claimed = status?.claimed == true
    val complete = MissionRules.isComplete(mission, profile, status)
    val progress = MissionRules.progressOf(mission, profile, status)
    val fraction = if (mission.target <= 0) 0f else progress.toFloat() / mission.target

    Column(
        modifier
            .fillMaxWidth()
            .glassSurface(MaterialTheme.shapes.large)
            .padding(16.dp)
            .testTag("mission_${mission.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mission.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (pending) PillLabel(stringResource(R.string.rewards_pending_badge), MaterialTheme.colorScheme.error)
            else if (claimed) PillLabel(stringResource(R.string.rewards_claimed), ItemState.Owned.color)
            else if (complete) PillLabel(stringResource(R.string.rewards_complete), ItemState.Equipped.color)
        }
        Text(
            mission.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            stringResource(R.string.rewards_amount, mission.rewardCoins),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        if (!pending) {
            MissionProgress(fraction)
            Text(
                stringResource(R.string.rewards_progress, progress, mission.target),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                stringResource(R.string.rewards_pending_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                claimed -> Unit
                pending -> Unit
                complete -> PrimaryButton(text = stringResource(R.string.rewards_claim), onClick = onClaim)
                mission.kind == MissionKind.DemoProgress -> OutlinedButton(onClick = onSimulate) {
                    Text(stringResource(R.string.rewards_simulate))
                }
            }
        }
    }
}
