package com.example.teamval.ui.screens.scrims

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.ScrimDraft
import com.example.teamval.domain.model.ScrimDraftField
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.SelectionField
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.screens.teams.TeamsUiState

@Composable
fun CreateScrimScreen(
    state: TeamsUiState,
    draft: ScrimDraft,
    invalidFields: Set<ScrimDraftField>,
    onDraftChange: (ScrimDraft) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rivals = state.snapshot.teams.filter { it.id != state.myTeamId }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                stringResource(R.string.create_scrim),
                stringResource(R.string.create_scrim_subtitle),
                onBack,
                R.string.back_to_scrims
            )
            if (state.myTeamId == null) {
                Text(
                    stringResource(R.string.error_no_team),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SelectionField(
                    label = stringResource(R.string.scrim_opponent),
                    selected = draft.opponentTeamId,
                    options = listOf("") + rivals.map { it.id },
                    optionLabel = { id -> if (id.isBlank()) stringResource(R.string.select_opponent) else state.teamName(id) },
                    onSelect = { onDraftChange(draft.copy(opponentTeamId = it)) },
                    modifier = Modifier.testTag("scrim_opponent")
                )
                if (ScrimDraftField.Opponent in invalidFields) {
                    Text(
                        stringResource(R.string.scrim_opponent_error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        stringResource(R.string.scrim_opponent_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            SelectionField(
                label = stringResource(R.string.scrim_mode_label),
                selected = draft.mode,
                options = GameMode.entries,
                optionLabel = { stringResource(it.labelRes) },
                onSelect = { onDraftChange(draft.copy(mode = it)) }
            )
            OutlinedTextField(
                value = draft.schedule,
                onValueChange = { onDraftChange(draft.copy(schedule = it)) },
                label = { Text(stringResource(R.string.scrim_schedule)) },
                isError = ScrimDraftField.Schedule in invalidFields,
                supportingText = {
                    Text(
                        stringResource(
                            if (ScrimDraftField.Schedule in invalidFields) R.string.scrim_schedule_error
                            else R.string.scrim_schedule_hint
                        )
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().testTag("scrim_schedule")
            )
            OutlinedTextField(
                value = draft.notes,
                onValueChange = { onDraftChange(draft.copy(notes = it)) },
                label = { Text(stringResource(R.string.scrim_notes)) },
                isError = ScrimDraftField.Notes in invalidFields,
                supportingText = {
                    Text(
                        if (ScrimDraftField.Notes in invalidFields) stringResource(R.string.scrim_notes_error)
                        else stringResource(R.string.scrim_notes_hint, draft.notes.length)
                    )
                },
                minLines = 2,
                maxLines = 4,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("scrim_notes")
            )
            PrimaryButton(
                text = stringResource(R.string.send_scrim),
                onClick = onSend,
                modifier = Modifier.fillMaxWidth().testTag("send_scrim")
            )
        }
    }
}
