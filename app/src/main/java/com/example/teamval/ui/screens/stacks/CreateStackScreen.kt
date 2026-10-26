package com.example.teamval.ui.screens.stacks

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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.DraftField
import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.StackCategory
import com.example.teamval.domain.model.StackDraft
import com.example.teamval.domain.model.ValorantRank
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.SelectionField
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.labelRes

@Composable
fun CreateStackScreen(
    draft: StackDraft,
    invalidFields: Set<DraftField>,
    onDraftChange: (StackDraft) -> Unit,
    onPublish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(stringResource(R.string.create_stack), stringResource(R.string.create_stack_subtitle), onBack)
            OutlinedTextField(
                value = draft.title,
                onValueChange = { onDraftChange(draft.copy(title = it)) },
                label = { Text(stringResource(R.string.post_title)) },
                isError = DraftField.Title in invalidFields,
                supportingText = {
                    Text(stringResource(if (DraftField.Title in invalidFields) R.string.title_error else R.string.title_hint))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().testTag("post_title")
            )
            OutlinedTextField(
                value = draft.description,
                onValueChange = { onDraftChange(draft.copy(description = it)) },
                label = { Text(stringResource(R.string.post_description)) },
                isError = DraftField.Description in invalidFields,
                supportingText = {
                    Text(
                        if (DraftField.Description in invalidFields) stringResource(R.string.description_error)
                        else stringResource(R.string.description_hint, draft.description.length)
                    )
                },
                minLines = 3,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().testTag("post_description")
            )
            SelectionField(
                label = stringResource(R.string.filter_rank), selected = draft.rank, options = ValorantRank.entries,
                optionLabel = { stringResource(it.labelRes) }, onSelect = { onDraftChange(draft.copy(rank = it)) }
            )
            SelectionField(
                label = stringResource(R.string.filter_region), selected = draft.region, options = Region.entries,
                optionLabel = { stringResource(it.labelRes) }, onSelect = { onDraftChange(draft.copy(region = it)) }
            )
            SelectionField(
                label = stringResource(R.string.filter_mode), selected = draft.mode, options = GameMode.entries,
                optionLabel = { stringResource(it.labelRes) }, onSelect = { onDraftChange(draft.copy(mode = it)) }
            )
            SelectionField(
                label = stringResource(R.string.post_capacity), selected = draft.capacity, options = (2..5).toList(),
                optionLabel = {
                    stringResource(
                        R.string.capacity_option_with_category,
                        pluralStringResource(R.plurals.capacity_option, it, it),
                        stringResource(StackCategory.ofCapacity(it).labelRes)
                    )
                },
                onSelect = { onDraftChange(draft.copy(capacity = it)) }
            )
            val missing = StackCategory.FULL_TEAM - draft.capacity
            Text(
                if (missing > 0) {
                    pluralStringResource(
                        R.plurals.create_capacity_summary,
                        missing,
                        draft.capacity,
                        draft.capacity - 1,
                        missing
                    )
                } else {
                    stringResource(R.string.create_capacity_summary_full, draft.capacity, draft.capacity - 1)
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                stringResource(R.string.create_capacity_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PrimaryButton(
                text = stringResource(R.string.publish_stack),
                onClick = onPublish,
                modifier = Modifier.fillMaxWidth().testTag("publish_stack")
            )
        }
    }
}
