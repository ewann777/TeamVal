package com.example.teamval.ui.screens.stacks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.teamval.R
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackCategory
import com.example.teamval.domain.model.StackFilters
import com.example.teamval.domain.model.StackSearch
import com.example.teamval.ui.components.AccentFilterChip
import com.example.teamval.ui.components.EmptyState
import com.example.teamval.ui.components.PrimaryButton
import com.example.teamval.ui.components.StackFiltersPanel
import com.example.teamval.ui.components.StackPageHeader
import com.example.teamval.ui.components.StackPostCard
import com.example.teamval.ui.components.labelRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StackExplorerScreen(
    state: StacksUiState,
    currentPlayerId: String,
    currentPlayerName: String,
    onFiltersChange: (StackFilters) -> Unit,
    onCategoryChange: (StackCategory?) -> Unit,
    onShowOwnPosts: (Boolean) -> Unit,
    onCreate: () -> Unit,
    onRequests: (String?) -> Unit,
    onJoin: (String) -> Unit,
    onSimulate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val filters = state.local.filters
    val ownOnly = state.local.ownPostsOnly
    val category = state.local.category
    val search = StackSearch(
        category = category,
        filters = filters,
        ownPostsOnly = ownOnly,
        playerId = currentPlayerId
    )
    val posts = search.apply(state.snapshot.posts)

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
            item(key = "categories") {
                // Pestañas de grupo sin ningún panel detrás: la barra no pinta
                // fondo ni filete, de modo que el hueco que rodea a los botones
                // enseña exactamente el fondo real de la aplicación (Liso,
                // Aurora o Estrellas) en vez de un rectángulo de color.
                //
                // Dejan de ser fijas (stickyHeader) a propósito: un elemento
                // fijo necesita un fondo opaco para que las publicaciones no se
                // vean por detrás, y ese fondo es precisamente el bloque que se
                // quiere eliminar. Al integrarse en el listado, cada elemento
                // se coloca debajo del anterior y nunca hay solape, así que no
                // hace falta cubrir nada.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .testTag("category_bar")
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StackCategory.entries.forEach { option ->
                            val selected = category == option
                            val shape = MaterialTheme.shapes.medium
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(shape)
                                    .background(if (selected) colors.primary else colors.surface)
                                    .then(
                                        if (selected) Modifier
                                        else Modifier.border(BorderStroke(1.dp, colors.outlineVariant), shape)
                                    )
                                    .selectable(
                                        selected = selected,
                                        role = Role.Button,
                                        // Pulsar la pestaña activa la deselecciona
                                        // y vuelve a mostrar todos los grupos.
                                        onClick = { onCategoryChange(if (selected) null else option) }
                                    )
                                    .testTag("category_${option.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(option.labelRes),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (selected) colors.onPrimary else colors.onSurface
                                )
                            }
                        }
                    }
                }
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
                    val descriptionRes = when {
                        filters.isActive -> R.string.no_stacks_filtered
                        category != null -> R.string.no_category_stacks
                        ownOnly -> R.string.no_own_stacks
                        else -> R.string.no_stacks_any
                    }
                    EmptyState(
                        title = stringResource(R.string.no_stacks_title),
                        description = stringResource(descriptionRes)
                    ) {
                        when {
                            filters.isActive -> TextButton(onClick = { onFiltersChange(StackFilters()) }) {
                                Text(stringResource(R.string.clear_filters))
                            }
                            category != null -> TextButton(onClick = { onCategoryChange(null) }) {
                                Text(stringResource(R.string.see_all_categories))
                            }
                            else -> Button(onClick = onCreate) { Text(stringResource(R.string.create_stack)) }
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
