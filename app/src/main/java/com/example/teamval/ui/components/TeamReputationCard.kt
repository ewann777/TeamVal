package com.example.teamval.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.TeamReputation
import com.example.teamval.ui.theme.LocalTeamValVisuals

/**
 * Reputation summary of a team: overall score, rating count, per-category
 * averages, reputation badges and recent comments with the report action.
 */
@Composable
fun TeamReputationCard(
    reputation: TeamReputation,
    onReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visuals = LocalTeamValVisuals.current
    StackPanel(modifier.testTag("reputation_card")) {
        Text(stringResource(R.string.reputation_team_title), style = MaterialTheme.typography.titleMedium)

        if (reputation.count == 0) {
            Text(
                stringResource(R.string.reputation_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.reputation_overall, formatStars(reputation.overall)),
                    style = MaterialTheme.typography.headlineMedium,
                    color = visuals.accentText
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    StarsDisplay(reputation.overall)
                    Text(
                        pluralStringResource(
                            R.plurals.reputation_count, reputation.count, reputation.count
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (reputation.badges.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        stringResource(R.string.reputation_badges),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        reputation.badges.forEach { badge ->
                            StatusBadge(stringResource(badge.labelRes))
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                RatingCategory.entries.forEach { category ->
                    val average = reputation.categoryAverages.getValue(category)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(
                                R.string.reputation_category,
                                stringResource(category.labelRes),
                                formatStars(average)
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        LinearProgressIndicator(
                            progress = { (average / 5f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(7.dp)
                        )
                    }
                }
            }

            val comments = reputation.recentComments()
            if (comments.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.reputation_recent),
                        style = MaterialTheme.typography.titleSmall
                    )
                    comments.forEach { rating ->
                        Column(
                            Modifier.fillMaxWidth()
                                .border(1.dp, visuals.glassBorder, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    rating.captainName,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                if (rating.reported) {
                                    Text(
                                        stringResource(R.string.reputation_reported),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    TextButton(
                                        onClick = { onReport(rating.id) },
                                        modifier = Modifier.testTag("report_${rating.id}")
                                    ) {
                                        Text(stringResource(R.string.reputation_report))
                                    }
                                }
                            }
                            Text(rating.comment, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Text(
                stringResource(R.string.reputation_demo_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
