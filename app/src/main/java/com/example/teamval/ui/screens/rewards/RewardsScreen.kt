package com.example.teamval.ui.screens.rewards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.MissionKind
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.screens.shop.EconomyUiState

/**
 * Pantalla de recompensas gratuitas: disponibles, reclamadas y pendientes de
 * verificación, con progreso y botón "Simular progreso" para la demo.
 */
@Composable
fun RewardsScreen(
    state: EconomyUiState,
    missions: List<Mission>,
    onBack: () -> Unit,
    onClaim: (Mission) -> Unit,
    onSimulate: (Mission) -> Unit,
    modifier: Modifier = Modifier
) {
    fun claimed(mission: Mission) = state.missionStatusOf(mission)?.claimed == true
    val available = missions.filter { it.kind != MissionKind.VerificationPending && !claimed(it) }
    val claimedList = missions.filter { claimed(it) }
    val pending = missions.filter { it.kind == MissionKind.VerificationPending }

    Column(
        modifier
            .widthIn(max = 840.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StackPageHeader(
            title = stringResource(R.string.rewards_title),
            subtitle = stringResource(R.string.rewards_subtitle),
            onBack = onBack,
            backLabel = R.string.shop_title
        )
        Text(
            stringResource(R.string.rewards_demo_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        RewardSection(stringResource(R.string.rewards_available), "rewards_available") {
            if (available.isEmpty()) {
                EmptyRewardText()
            } else available.forEach { mission ->
                MissionCard(
                    mission = mission,
                    status = state.missionStatusOf(mission),
                    profile = state.profile,
                    onClaim = { onClaim(mission) },
                    onSimulate = { onSimulate(mission) }
                )
            }
        }

        RewardSection(stringResource(R.string.rewards_claimed_section), "rewards_claimed") {
            if (claimedList.isEmpty()) {
                EmptyRewardText()
            } else claimedList.forEach { mission ->
                MissionCard(
                    mission = mission,
                    status = state.missionStatusOf(mission),
                    profile = state.profile,
                    onClaim = { onClaim(mission) },
                    onSimulate = { onSimulate(mission) }
                )
            }
        }

        RewardSection(stringResource(R.string.rewards_pending_section), "rewards_pending") {
            if (pending.isEmpty()) {
                EmptyRewardText()
            } else pending.forEach { mission ->
                MissionCard(
                    mission = mission,
                    status = state.missionStatusOf(mission),
                    profile = state.profile,
                    onClaim = { onClaim(mission) },
                    onSimulate = { onSimulate(mission) }
                )
            }
        }
    }
}

@Composable
private fun RewardSection(title: String, tag: String, content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().testTag(tag),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun EmptyRewardText() {
    Text(
        stringResource(R.string.rewards_empty),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
