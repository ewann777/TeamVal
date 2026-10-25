package com.example.teamval.data.repository

import com.example.teamval.data.fake.DemoStackData
import com.example.teamval.domain.model.JoinRequest
import com.example.teamval.domain.model.Player
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackDraft
import com.example.teamval.domain.model.StackPost
import com.example.teamval.domain.model.StackSnapshot
import com.example.teamval.domain.repository.StackError
import com.example.teamval.domain.repository.StackRepository
import com.example.teamval.domain.repository.StackResult
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A session-local demo store. Each mutation publishes posts and requests atomically. */
class InMemoryStackRepository(
    currentPlayer: Player = DemoStackData.currentPlayer,
    initialSnapshot: StackSnapshot = DemoStackData.snapshot(),
    private val candidates: List<Player> = DemoStackData.candidates
) : StackRepository {
    private var player = currentPlayer
    override val currentPlayer: Player get() = player
    private val state = MutableStateFlow(initialSnapshot)
    override val snapshot = state.asStateFlow()

    @Synchronized
    override fun updatePlayerName(name: String) {
        player = player.copy(name = name)
        state.value = state.value.copy(
            posts = state.value.posts.map { if (it.owner.id == player.id) it.copy(owner = player) else it },
            requests = state.value.requests.map { if (it.applicant.id == player.id) it.copy(applicant = player) else it }
        )
    }

    @Synchronized
    override fun createPost(draft: StackDraft): StackResult {
        if (draft.invalidFields().isNotEmpty()) return failure(StackError.InvalidDraft)
        val post = StackPost(
            id = UUID.randomUUID().toString(),
            owner = currentPlayer,
            title = draft.title.trim(),
            description = draft.description.trim(),
            rank = draft.rank,
            region = draft.region,
            mode = draft.mode,
            capacity = draft.capacity
        )
        state.value = state.value.copy(posts = listOf(post) + state.value.posts)
        return StackResult.Success(post.id)
    }

    @Synchronized
    override fun requestToJoin(postId: String): StackResult {
        val post = state.value.posts.find { it.id == postId } ?: return failure(StackError.NotFound)
        if (post.owner.id == currentPlayer.id) return failure(StackError.OwnStack)
        return addRequest(post, currentPlayer)
    }

    @Synchronized
    override fun simulateIncomingRequest(postId: String): StackResult {
        val post = state.value.posts.find { it.id == postId } ?: return failure(StackError.NotFound)
        if (post.owner.id != currentPlayer.id) return failure(StackError.NotOwner)
        if (post.isFull) return failure(StackError.Full)
        val candidate = candidates.firstOrNull { player ->
            player.id !in post.memberIds && player.id != post.owner.id &&
                state.value.requests.none { it.postId == postId && it.applicant.id == player.id }
        } ?: return failure(StackError.NoCandidates)
        return addRequest(post, candidate)
    }

    private fun addRequest(post: StackPost, applicant: Player): StackResult {
        if (applicant.id in post.memberIds) return failure(StackError.AlreadyMember)
        if (state.value.requests.any { it.postId == post.id && it.applicant.id == applicant.id }) {
            return failure(StackError.DuplicateRequest)
        }
        if (post.isFull) return failure(StackError.Full)
        val request = JoinRequest(UUID.randomUUID().toString(), post.id, applicant)
        state.value = state.value.copy(requests = listOf(request) + state.value.requests)
        return StackResult.Success(request.id)
    }

    @Synchronized
    override fun resolveRequest(requestId: String, accept: Boolean): StackResult {
        val snapshot = state.value
        val request = snapshot.requests.find { it.id == requestId } ?: return failure(StackError.NotFound)
        val post = snapshot.posts.find { it.id == request.postId } ?: return failure(StackError.NotFound)
        if (post.owner.id != currentPlayer.id) return failure(StackError.NotOwner)
        if (request.status != RequestStatus.Pending) return failure(StackError.RequestResolved)
        if (accept && post.isFull) return failure(StackError.Full)
        if (accept && request.applicant.id in post.memberIds) return failure(StackError.AlreadyMember)

        state.value = snapshot.copy(
            posts = snapshot.posts.map {
                if (accept && it.id == post.id) it.copy(memberIds = it.memberIds + request.applicant.id) else it
            },
            requests = snapshot.requests.map {
                if (it.id == requestId) {
                    it.copy(status = if (accept) RequestStatus.Accepted else RequestStatus.Rejected)
                } else it
            }
        )
        return StackResult.Success(requestId)
    }

    private fun failure(error: StackError) = StackResult.Failure(error)
}
