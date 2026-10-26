package com.example.teamval.ui.screens.stacks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamval.R
import com.example.teamval.domain.model.DraftField
import com.example.teamval.domain.model.StackCategory
import com.example.teamval.domain.model.StackDraft
import com.example.teamval.domain.model.StackFilters
import com.example.teamval.domain.model.StackSnapshot
import com.example.teamval.domain.repository.StackRepository
import com.example.teamval.domain.repository.StackResult
import com.example.teamval.ui.components.messageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class StackNotice(val id: Long, val textRes: Int)

data class StacksLocalState(
    val filters: StackFilters = StackFilters(),
    /** Pestaña activa del explorador: null muestra todas las categorías. */
    val category: StackCategory? = null,
    val ownPostsOnly: Boolean = false,
    val sentRequestsOnly: Boolean = false,
    val draft: StackDraft = StackDraft(),
    val invalidFields: Set<DraftField> = emptySet(),
    val notice: StackNotice? = null
)

data class StacksUiState(
    val snapshot: StackSnapshot,
    val local: StacksLocalState = StacksLocalState()
)

class StacksViewModel(private val repository: StackRepository) : ViewModel() {
    val currentPlayer get() = repository.currentPlayer
    fun updatePlayerName(name: String) = repository.updatePlayerName(name)
    private val local = MutableStateFlow(StacksLocalState())
    private var noticeId = 0L

    val uiState = combine(repository.snapshot, local, ::StacksUiState).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StacksUiState(repository.snapshot.value)
    )

    fun setFilters(filters: StackFilters) = local.update { it.copy(filters = filters) }

    /** Cambia la pestaña de categoría; pulsa la activa para volver a ver todas. */
    fun showCategory(category: StackCategory?) = local.update { it.copy(category = category) }
    fun showOwnPosts(own: Boolean) = local.update { it.copy(ownPostsOnly = own) }
    fun showSentRequests(sent: Boolean) = local.update { it.copy(sentRequestsOnly = sent) }

    fun startCreating() = local.update {
        it.copy(draft = StackDraft(), invalidFields = emptySet())
    }

    fun updateDraft(draft: StackDraft) = local.update {
        it.copy(draft = draft, invalidFields = it.invalidFields.intersect(draft.invalidFields()))
    }

    fun createPost(): Boolean {
        val draft = local.value.draft
        val errors = draft.invalidFields()
        if (errors.isNotEmpty()) {
            local.update { it.copy(invalidFields = errors) }
            return false
        }
        val result = repository.createPost(draft)
        if (result is StackResult.Success) {
            local.update {
                // La pestaña salta a la categoría creada para que el post sea visible.
                it.copy(
                    ownPostsOnly = true,
                    category = StackCategory.ofCapacity(draft.capacity),
                    filters = StackFilters(),
                    draft = StackDraft(),
                    invalidFields = emptySet()
                )
            }
        }
        report(result, R.string.stack_created)
        return result is StackResult.Success
    }

    fun requestToJoin(postId: String) = report(repository.requestToJoin(postId), R.string.request_sent)

    fun simulateRequest(postId: String) = report(
        repository.simulateIncomingRequest(postId), R.string.request_simulated
    )

    fun resolveRequest(requestId: String, accept: Boolean) = report(
        repository.resolveRequest(requestId, accept),
        if (accept) R.string.request_accepted_feedback else R.string.request_rejected_feedback
    )

    fun dismissNotice(id: Long) = local.update {
        if (it.notice?.id == id) it.copy(notice = null) else it
    }

    private fun report(result: StackResult, successRes: Int) {
        val textRes = when (result) {
            is StackResult.Success -> successRes
            is StackResult.Failure -> result.error.messageRes
        }
        local.update { it.copy(notice = StackNotice(++noticeId, textRes)) }
    }
}
