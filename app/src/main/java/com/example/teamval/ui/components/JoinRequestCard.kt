package com.example.teamval.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.JoinRequest
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackPost

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JoinRequestCard(
    request: JoinRequest,
    post: StackPost,
    canManage: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    StackPanel(modifier) {
        Text(post.title, style = MaterialTheme.typography.titleMedium)
        Text(
            if (canManage) request.applicant.name else stringResource(R.string.stack_host, post.owner.name),
            style = MaterialTheme.typography.titleLarge
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(stringResource(request.status.labelRes))
            StatusBadge(stringResource(request.applicant.rank.labelRes))
        }
        Text(
            pluralStringResource(R.plurals.stack_occupancy, post.capacity, post.memberIds.size, post.capacity),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (canManage && request.status == RequestStatus.Pending) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton(onClick = onAccept, text = stringResource(R.string.accept_request), enabled = !post.isFull)
                OutlinedButton(onClick = onReject) { Text(stringResource(R.string.reject_request)) }
            }
            if (post.isFull) {
                Text(stringResource(R.string.request_full_hint), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
