package com.example.teamval.ui.screens.ratings

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPanel
import com.example.teamval.ui.components.StarsDisplay
import com.example.teamval.ui.components.StatusBadge
import com.example.teamval.ui.components.StarRatingRow
import com.example.teamval.ui.components.TeamLogoView
import com.example.teamval.ui.components.hintRes
import com.example.teamval.ui.components.labelRes
import com.example.teamval.ui.components.messageRes

/**
 * Ratings screen: rival identity, five conduct categories (1–5 stars), an optional
 * comment subject to community rules and the confirmation state after sending.
 * Blocked states (not completed, already rated, own team…) are shown explicitly.
 */
@Composable
fun RateScrimScreen(
    state: RateUiState,
    onScoreChange: (RatingCategory, Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rivalLabel = state.rivalName.ifBlank { "—" }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StackPageHeader(
                title = stringResource(R.string.rating_title, rivalLabel),
                subtitle = stringResource(R.string.rating_subtitle),
                onBack = onBack,
                backLabel = R.string.back_to_scrims
            )

            when {
                state.sent -> RatingConfirmation(rivalLabel, onBack)
                !state.exists -> StackPanel {
                    Text(
                        stringResource(R.string.rating_block_no_scrim),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                state.block != null -> StackPanel {
                    Text(
                        stringResource(state.block.messageRes),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.rating_rules_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> RatingForm(state, onScoreChange, onCommentChange, onSubmit)
            }
        }
    }
}

@Composable
private fun RatingConfirmation(rivalLabel: String, onBack: () -> Unit) {
    StackPanel(Modifier.testTag("rating_sent")) {
        Text(
            stringResource(R.string.rating_sent_title),
            style = MaterialTheme.typography.titleLarge
        )
        StarsDisplay(5f, size = 26.dp)
        Text(
            stringResource(R.string.rating_sent_body, rivalLabel),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PrimaryButton(
            text = stringResource(R.string.rating_sent_action),
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().testTag("rating_back")
        )
    }
}

@Composable
private fun RatingForm(
    state: RateUiState,
    onScoreChange: (RatingCategory, Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        StackPanel {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogoView(state.rivalLogo, size = 56.dp, modifier = Modifier.testTag("rival_logo"))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(state.rivalName, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(state.rivalTag)
                        if (state.completed) {
                            StatusBadge(stringResource(R.string.scrim_completed))
                        }
                    }
                }
            }
            Text(
                stringResource(R.string.rating_rules_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RatingCategory.entries.forEach { category ->
            StarRatingRow(
                categoryLabel = stringResource(category.labelRes),
                hint = stringResource(category.hintRes),
                tag = category.name.lowercase(),
                value = state.scores[category] ?: 3,
                onValueChange = { onScoreChange(category, it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = state.comment,
            onValueChange = onCommentChange,
            label = { Text(stringResource(R.string.rating_comment)) },
            isError = state.commentError != null,
            supportingText = {
                Text(
                    state.commentError?.let { stringResource(it.messageRes) }
                        ?: stringResource(R.string.rating_comment_hint, state.comment.length)
                )
            },
            minLines = 3,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().testTag("rating_comment")
        )

        state.failureRes?.let { res ->
            Text(
                stringResource(res),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        PrimaryButton(
            text = stringResource(R.string.rating_submit),
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().testTag("submit_rating")
        )
    }
}
