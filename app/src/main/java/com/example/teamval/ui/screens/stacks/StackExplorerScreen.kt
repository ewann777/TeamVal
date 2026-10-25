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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackFilters
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.EmptyState
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackFiltersPanel
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPostCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StackExplorerScreen(
    state: StacksUiState,
    currentPlayerId: String,
    currentPlayerName: String,
    onFiltersChange: (StackFilters) -> Unit,
    onShowOwnPosts: (Boolean) -> Unit,
    onCreate: () -> Unit,
    onRequests: (String?) -> Unit,
    onJoin: (String) -> Unit,
    onSimulate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val filters = state.local.filters
    val ownOnly = state.local.ownPostsOnly
    val posts = state.snapshot.posts.filter {
        (if (ownOnly) it.owner.id == currentPlayerId else it.owner.id != currentPlayerId) && filters.matches(it)
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth().testTag("stack_explorer"),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                StackPageHeader(stringResource(R.string.stacks_title), stringResource(R.string.stacks_subtitle))
            }
            item(key = "actions") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryButton(onClick = onCreate, text = stringResource(R.string.create_stack))
                    OutlinedButton(onClick = { onRequests(null) }) { Text(stringResource(R.string.requests_title)) }
                }
            }
            item(key = "demo") {
                Text(
                    stringResource(R.string.stack_demo_notice, currentPlayerName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item(key = "tabs") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccentFilterChip(
                        selected = !ownOnly,
                        onClick = { onShowOwnPosts(false) },
                        label = stringResource(R.string.explore_stacks)
                    )
                    AccentFilterChip(
                        selected = ownOnly,
                        onClick = { onShowOwnPosts(true) },
                        label = stringResource(R.string.my_posts)
                    )
                }
            }
            item(key = "filter_toggle") {
                OutlinedButton(onClick = { showFilters = !showFilters }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(
                        when {
                            showFilters -> R.string.hide_filters
                            filters.isActive -> R.string.show_active_filters
                            else -> R.string.show_filters
                        }
                    ))
                }
            }
            if (showFilters) {
                item(key = "filters") { StackFiltersPanel(filters, onFiltersChange) }
            }
            item(key = "count") {
                Text(
                    pluralStringResource(R.plurals.stack_result_count, posts.size, posts.size),
                    style = MaterialTheme.typography.titleMedium,
                    color = com.example.teamval.ui.theme.LocalTeamValVisuals.current.accentText
                )
            }
            if (posts.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        title = stringResource(R.string.no_stacks_title),
                        description = stringResource(if (filters.isActive) R.string.no_stacks_filtered else R.string.no_own_stacks)
                    ) {
                        if (filters.isActive) {
                            TextButton(onClick = { onFiltersChange(StackFilters()) }) { Text(stringResource(R.string.clear_filters)) }
                        } else {
                            Button(onClick = onCreate) { Text(stringResource(R.string.create_stack)) }
                        }
                    }
                }
            }
            items(posts, key = { "post-${it.id}" }) { post ->
                val requests = state.snapshot.requests.filter { it.postId == post.id }
                StackPostCard(
                    post = post,
                    isOwner = post.owner.id == currentPlayerId,
                    isMember = currentPlayerId in post.memberIds,
                    requestStatus = requests.find { it.applicant.id == currentPlayerId }?.status,
                    pendingCount = requests.count { it.status == RequestStatus.Pending },
                    onJoin = { onJoin(post.id) },
                    onManage = { onRequests(post.id) },
                    onSimulate = { onSimulate(post.id) },
                    modifier = Modifier.testTag("post-${post.id}")
                )
            }
        }
    }
}
