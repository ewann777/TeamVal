package com.example.teamval.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.Reputation

@Composable
fun ReputationPanel(reputation: Reputation) {
    SettingsSection(stringResource(R.string.reputation_title), stringResource(R.string.reputation_demo)) {
        Text(stringResource(R.string.reputation_average, reputation.average), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        listOf(
            R.string.reputation_communication to reputation.communication,
            R.string.reputation_teamwork to reputation.teamwork,
            R.string.reputation_attitude to reputation.attitude
        ).forEach { (label, score) ->
            Text(stringResource(R.string.reputation_score, stringResource(label), score), style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                gapSize = 6.dp
            )
        }
        reputation.reviews.forEach { review ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(review.author, style = MaterialTheme.typography.titleMedium)
                val starsDescription = stringResource(R.string.rating_stars, review.stars)
                Text(
                    "★".repeat(review.stars) + "☆".repeat(5 - review.stars),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { contentDescription = starsDescription }
                )
                Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
