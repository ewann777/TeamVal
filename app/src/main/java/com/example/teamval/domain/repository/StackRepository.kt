package com.example.teamval.domain.repository

import com.example.teamval.domain.model.Player
import com.example.teamval.domain.model.StackDraft
import com.example.teamval.domain.model.StackSnapshot
import kotlinx.coroutines.flow.StateFlow

enum class StackError {
    InvalidDraft, NotFound, NotOwner, OwnStack, Full, DuplicateRequest,
    AlreadyMember, RequestResolved, NoCandidates
}

sealed interface StackResult {
    data class Success(val id: String) : StackResult
    data class Failure(val error: StackError) : StackResult
}

interface StackRepository {
    val currentPlayer: Player
    val snapshot: StateFlow<StackSnapshot>

    fun createPost(draft: StackDraft): StackResult
    fun requestToJoin(postId: String): StackResult
    fun resolveRequest(requestId: String, accept: Boolean): StackResult
    fun updatePlayerName(name: String)

    /** Demo-only input: generates a fictitious applicant for one of the user's posts. */
    fun simulateIncomingRequest(postId: String): StackResult
}
