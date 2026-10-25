package com.example.teamval.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RequestStatus
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
            StatusBadge(stringResource(post.rank.labelRes))
            StatusBadge(stringResource(post.region.labelRes))
            StatusBadge(stringResource(post.mode.labelRes))
        }
        Text(post.description, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Text(
            pluralStringResource(R.plurals.stack_occupancy, post.capacity, post.memberIds.size, post.capacity),
            style = MaterialTheme.typography.labelLarge
        )
        LinearProgressIndicator(
            progress = { post.memberIds.size.toFloat() / post.capacity },
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
