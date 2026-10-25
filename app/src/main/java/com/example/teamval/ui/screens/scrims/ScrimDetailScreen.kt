package com.example.teamval.ui.screens.scrims

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RatingBlock
import com.example.teamval.domain.model.Scrim
import com.example.teamval.domain.model.ScrimResultDraft
import com.example.teamval.domain.model.ScrimResultField
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPanel
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.components.messageRes
import com.example.teamval.ui.screens.teams.TeamsUiState
import com.example.teamval.ui.theme.LocalTeamValVisuals

@Composable
fun ScrimDetailScreen(
    state: TeamsUiState,
    scrimId: String,
    ratingBlock: RatingBlock?,
    hasRated: Boolean,
    onRespond: (String, Boolean) -> Unit,
    onCancel: (String) -> Unit,
    onResultChange: (String, ScrimResultDraft) -> Unit,
    onComplete: (String) -> Unit,
    onRate: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrim = state.snapshot.scrim(scrimId)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (scrim == null) {
                StackPageHeader(
                    stringResource(R.string.scrims_segment),
                    stringResource(R.string.error_team_not_found),
                    onBack,
                    R.string.back_to_scrims
                )
                return@Column
            }
            val homeName = state.teamName(scrim.homeTeamId)
            val awayName = state.teamName(scrim.awayTeamId)
            StackPageHeader(
                title = stringResource(R.string.scrim_versus, homeName, awayName),
                subtitle = stringResource(scrim.status.labelRes),
                onBack = onBack,
                backLabel = R.string.back_to_scrims
            )
            ScrimInfoPanel(scrim, homeName = homeName, awayName = awayName)
            ScrimActions(
                scrim = scrim,
                state = state,
                ratingBlock = ratingBlock,
                hasRated = hasRated,
                onRespond = onRespond,
                onCancel = onCancel,
                onResultChange = onResultChange,
                onComplete = onComplete,
                onRate = onRate
            )
            Text(
                stringResource(R.string.scrim_demo_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScrimInfoPanel(scrim: Scrim, homeName: String, awayName: String) {
    StackPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(stringResource(scrim.status.labelRes))
            StatusBadge(stringResource(scrim.mode.labelRes))
        }
        DetailRow(stringResource(R.string.scrim_schedule_label), scrim.schedule)
        DetailRow(stringResource(R.string.scrim_home_team), homeName)
        DetailRow(stringResource(R.string.scrim_away_team), awayName)
        if (scrim.notes.isNotBlank()) {
            DetailRow(stringResource(R.string.scrim_notes_label), scrim.notes)
        }
        if (scrim.status == ScrimStatus.Completed && scrim.homeScore != null && scrim.awayScore != null) {
            Text(
                stringResource(R.string.score_value, scrim.homeScore, scrim.awayScore),
                style = MaterialTheme.typography.headlineLarge,
                color = LocalTeamValVisuals.current.accentText
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (value.isNotBlank()) Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ScrimActions(
    scrim: Scrim,
    state: TeamsUiState,
    ratingBlock: RatingBlock?,
    hasRated: Boolean,
    onRespond: (String, Boolean) -> Unit,
    onCancel: (String) -> Unit,
    onResultChange: (String, ScrimResultDraft) -> Unit,
    onComplete: (String) -> Unit,
    onRate: (String) -> Unit
) {
    val isParticipant = state.myTeamId != null && scrim.involves(state.myTeamId)
    val isReceiver = scrim.awayTeamId == state.myTeamId
    if (!isParticipant) {
        Text(
            stringResource(R.string.error_not_participant),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        return
    }
    when {
        scrim.status == ScrimStatus.Proposed && isReceiver -> StackPanel {
            PrimaryButton(
                text = stringResource(R.string.accept_scrim),
                onClick = { onRespond(scrim.id, true) },
                modifier = Modifier.fillMaxWidth().testTag("accept_scrim")
            )
            OutlinedButton(
                onClick = { onRespond(scrim.id, false) },
                modifier = Modifier.fillMaxWidth().testTag("decline_scrim")
            ) { Text(stringResource(R.string.decline_scrim)) }
        }

        scrim.status == ScrimStatus.Proposed -> StackPanel {
            Text(
                stringResource(R.string.scrim_pending_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = { onCancel(scrim.id) },
                modifier = Modifier.fillMaxWidth().testTag("cancel_scrim")
            ) { Text(stringResource(R.string.cancel_scrim)) }
        }

        scrim.status == ScrimStatus.Confirmed -> {
            val draft = state.resultDraftFor(scrim.id)
            StackPanel {
                Text(stringResource(R.string.register_result), style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ScoreField(
                        value = draft.homeScore,
                        label = stringResource(R.string.score_home),
                        isError = ScrimResultField.HomeScore in state.local.resultInvalid &&
                            state.local.resultScrimId == scrim.id,
                        onValueChange = { onResultChange(scrim.id, draft.copy(homeScore = it)) },
                        modifier = Modifier.weight(1f).testTag("score_home")
                    )
                    ScoreField(
                        value = draft.awayScore,
                        label = stringResource(R.string.score_away),
                        isError = ScrimResultField.AwayScore in state.local.resultInvalid &&
                            state.local.resultScrimId == scrim.id,
                        onValueChange = { onResultChange(scrim.id, draft.copy(awayScore = it)) },
                        modifier = Modifier.weight(1f).testTag("score_away")
                    )
                }
                if (state.local.resultInvalid.isNotEmpty() && state.local.resultScrimId == scrim.id) {
                    Text(
                        stringResource(R.string.error_invalid_result),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                PrimaryButton(
                    text = stringResource(R.string.complete_scrim),
                    onClick = { onComplete(scrim.id) },
                    modifier = Modifier.fillMaxWidth().testTag("complete_scrim")
                )
                OutlinedButton(
                    onClick = { onCancel(scrim.id) },
                    modifier = Modifier.fillMaxWidth().testTag("cancel_scrim")
                ) { Text(stringResource(R.string.cancel_scrim)) }
                Text(
                    stringResource(R.string.scrim_cancel_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        scrim.status == ScrimStatus.Completed -> StackPanel {
            Text(
                stringResource(R.string.scrim_final_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            when {
                hasRated -> Text(
                    stringResource(R.string.rating_already_sent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalTeamValVisuals.current.accentText
                )
                ratingBlock == null -> PrimaryButton(
                    text = stringResource(R.string.scrim_rate_action),
                    onClick = { onRate(scrim.id) },
                    modifier = Modifier.fillMaxWidth().testTag("rate_scrim")
                )
                else -> Text(
                    stringResource(ratingBlock.messageRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> StackPanel {
            Text(
                stringResource(R.string.scrim_final_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScoreField(
    value: String,
    label: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> if (input.all { it.isDigit() } && input.length <= 2) onValueChange(input) },
        label = { Text(label) },
        isError = isError,
        supportingText = { Text(stringResource(R.string.score_hint)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        modifier = modifier
    )
}
