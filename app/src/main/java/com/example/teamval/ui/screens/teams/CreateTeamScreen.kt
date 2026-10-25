package com.example.teamval.ui.screens.teams

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
import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.TeamDraft
import com.example.teamval.domain.model.TeamDraftField
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.SelectionField
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.labelRes

@Composable
fun CreateTeamScreen(
    draft: TeamDraft,
    invalidFields: Set<TeamDraftField>,
    onDraftChange: (TeamDraft) -> Unit,
    onCreate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                stringResource(R.string.create_team),
                stringResource(R.string.create_team_subtitle),
                onBack,
                R.string.back_to_scrims
            )
            OutlinedTextField(
                value = draft.name,
                onValueChange = { onDraftChange(draft.copy(name = it)) },
                label = { Text(stringResource(R.string.team_name)) },
                isError = TeamDraftField.Name in invalidFields,
                supportingText = {
                    Text(
                        stringResource(
                            if (TeamDraftField.Name in invalidFields) R.string.team_name_error
                            else R.string.team_name_hint
                        )
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().testTag("team_name")
            )
            OutlinedTextField(
                value = draft.tag,
                onValueChange = { if (it.length <= 5) onDraftChange(draft.copy(tag = it)) },
                label = { Text(stringResource(R.string.team_tag)) },
                isError = TeamDraftField.Tag in invalidFields,
                supportingText = {
                    Text(
                        stringResource(
                            if (TeamDraftField.Tag in invalidFields) R.string.team_tag_error
                            else R.string.team_tag_hint
                        )
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().testTag("team_tag")
            )
            SelectionField(
                label = stringResource(R.string.team_region),
                selected = draft.region,
                options = Region.entries,
                optionLabel = { stringResource(it.labelRes) },
                onSelect = { onDraftChange(draft.copy(region = it)) }
            )
            OutlinedTextField(
                value = draft.motto,
                onValueChange = { onDraftChange(draft.copy(motto = it)) },
                label = { Text(stringResource(R.string.team_motto)) },
                isError = TeamDraftField.Motto in invalidFields,
                supportingText = {
                    Text(
                        stringResource(
                            if (TeamDraftField.Motto in invalidFields) R.string.team_motto_error
                            else R.string.team_motto_hint
                        )
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().testTag("team_motto")
            )
            OutlinedTextField(
                value = draft.description,
                onValueChange = { onDraftChange(draft.copy(description = it)) },
                label = { Text(stringResource(R.string.team_description)) },
                isError = TeamDraftField.Description in invalidFields,
                supportingText = {
                    Text(
                        if (TeamDraftField.Description in invalidFields) stringResource(R.string.team_description_error)
                        else stringResource(R.string.team_description_hint, draft.description.length)
                    )
                },
                minLines = 3,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("team_description")
            )
            PrimaryButton(
                text = stringResource(R.string.save_team),
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth().testTag("save_team")
            )
        }
    }
}
