package com.example.teamval.ui.screens.stacks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.EmptyState
import com.example.teamval.ui.components.JoinRequestCard
import com.example.teamval.ui.components.StackPageHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StackRequestsScreen(
    state: StacksUiState,
    currentPlayerId: String,
    postId: String?,
    onShowSent: (Boolean) -> Unit,
    onResolve: (String, Boolean) -> Unit,
    onSimulate: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posts = state.snapshot.posts.associateBy { it.id }
    val selectedPost = posts[postId]
    val sent = postId == null && state.local.sentRequestsOnly
    val requests = state.snapshot.requests.filter { request ->
        val post = posts[request.postId]
        post != null && (postId == null || request.postId == postId) &&
            (if (sent) request.applicant.id == currentPlayerId else post.owner.id == currentPlayerId)
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth().testTag("stack_requests"),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                StackPageHeader(
                    stringResource(R.string.requests_title),
                    selectedPost?.title ?: stringResource(R.string.requests_subtitle),
                    onBack
                )
            }
            item(key = "demo") {
                Text(
                    stringResource(if (sent) R.string.sent_demo_hint else R.string.received_demo_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (postId == null) {
                item(key = "tabs") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AccentFilterChip(
                            selected = !sent,
                            onClick = { onShowSent(false) },
                            label = stringResource(R.string.received_requests)
                        )
                        AccentFilterChip(
                            selected = sent,
                            onClick = { onShowSent(true) },
                            label = stringResource(R.string.sent_requests)
                        )
                    }
                }
            }
            if (selectedPost?.owner?.id == currentPlayerId) {
                item(key = "simulate") {
                    OutlinedButton(onClick = { onSimulate(selectedPost.id) }, enabled = !selectedPost.isFull) {
                        Text(stringResource(R.string.simulate_request))
                    }
                }
            }
            if (requests.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        stringResource(R.string.no_requests_title),
                        stringResource(if (sent) R.string.no_sent_requests else R.string.no_received_requests)
                    )
                }
            }
            items(requests, key = { it.id }) { request ->
                val post = posts.getValue(request.postId)
                JoinRequestCard(
                    request = request,
                    post = post,
                    canManage = post.owner.id == currentPlayerId,
                    onAccept = { onResolve(request.id, true) },
                    onReject = { onResolve(request.id, false) },
                    modifier = Modifier.testTag("request-${request.id}")
                )
            }
        }
    }
}
