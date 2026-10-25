package com.example.teamval.data.repository

import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.JoinRequest
import com.example.teamval.domain.model.Player
import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.RequestStatus
import com.example.teamval.domain.model.StackDraft
import com.example.teamval.domain.model.StackFilters
import com.example.teamval.domain.model.StackPost
import com.example.teamval.domain.model.StackSnapshot
import com.example.teamval.domain.model.ValorantRank
import com.example.teamval.domain.repository.StackError
import com.example.teamval.domain.repository.StackResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryStackRepositoryTest {
    private val me = Player("me", "Nova", ValorantRank.Gold)
    private val other = Player("other", "Luna", ValorantRank.Platinum)
    private val third = Player("third", "Echo", ValorantRank.Silver)
    private val draft = StackDraft(title = "  Equipo nocturno  ", description = "  Jugamos para mejorar juntos.  ")

    private fun post(owner: Player = me, capacity: Int = 2) = StackPost(
        "post", owner, "Equipo nocturno", "Jugamos para mejorar juntos.",
        ValorantRank.Gold, Region.LatamSouth, GameMode.Competitive, capacity
    )

    @Test
    fun creationValidatesAndNormalizesWithoutPublishingInvalidData() {
        val repository = InMemoryStackRepository(me, StackSnapshot())
        listOf(
            draft.copy(title = "    "), draft.copy(title = "a".repeat(61)),
            draft.copy(description = "corta"), draft.copy(description = "a".repeat(301)),
            draft.copy(capacity = 1), draft.copy(capacity = 6)
        ).forEach {
            assertEquals(StackResult.Failure(StackError.InvalidDraft), repository.createPost(it))
        }
        assertTrue(repository.snapshot.value.posts.isEmpty())
        assertTrue(repository.createPost(draft) is StackResult.Success)
        val created = repository.snapshot.value.posts.single()
        assertEquals("Equipo nocturno", created.title)
        assertEquals("Jugamos para mejorar juntos.", created.description)
        assertEquals(setOf(me.id), created.memberIds)
        assertEquals(4, created.availableSlots)
    }

    @Test
    fun filtersCombineAllCriteriaAndCanBeCleared() {
        val post = post()
        assertTrue(StackFilters().matches(post))
        val filters = StackFilters(ValorantRank.Gold, Region.LatamSouth, GameMode.Competitive)
        assertTrue(filters.matches(post))
        assertFalse(filters.copy(rank = ValorantRank.Silver).matches(post))
        assertFalse(filters.copy(region = Region.Europe).matches(post))
        assertFalse(filters.copy(mode = GameMode.Unrated).matches(post))
        assertFalse(StackFilters().isActive)
    }

    @Test
    fun joiningIsPendingDoesNotConsumeCapacityAndCannotBeDuplicated() {
        val repository = InMemoryStackRepository(me, StackSnapshot(posts = listOf(post(other))))
        assertTrue(repository.requestToJoin("post") is StackResult.Success)
        assertEquals(RequestStatus.Pending, repository.snapshot.value.requests.single().status)
        assertEquals(1, repository.snapshot.value.posts.single().availableSlots)
        assertEquals(StackResult.Failure(StackError.DuplicateRequest), repository.requestToJoin("post"))
        assertEquals(1, repository.snapshot.value.requests.size)
    }

    @Test
    fun joiningOwnFullMissingOrAlreadyJoinedStacksIsRejected() {
        val owned = InMemoryStackRepository(me, StackSnapshot(posts = listOf(post())))
        assertEquals(StackResult.Failure(StackError.OwnStack), owned.requestToJoin("post"))
        assertEquals(StackResult.Failure(StackError.NotFound), owned.requestToJoin("missing"))
        val fullPost = post(other).copy(memberIds = setOf(other.id, third.id))
        val full = InMemoryStackRepository(me, StackSnapshot(posts = listOf(fullPost)))
        assertEquals(StackResult.Failure(StackError.Full), full.requestToJoin("post"))
        val joined = InMemoryStackRepository(me, StackSnapshot(posts = listOf(post(other).copy(memberIds = setOf(other.id, me.id)))))
        assertEquals(StackResult.Failure(StackError.AlreadyMember), joined.requestToJoin("post"))
    }

    @Test
    fun acceptingLastSlotUpdatesBothRecordsAndPreventsOverbooking() {
        val repository = InMemoryStackRepository(
            me, StackSnapshot(listOf(post()), listOf(JoinRequest("a", "post", other), JoinRequest("b", "post", third)))
        )
        assertTrue(repository.resolveRequest("a", true) is StackResult.Success)
        val snapshot = repository.snapshot.value
        assertEquals(setOf(me.id, other.id), snapshot.posts.single().memberIds)
        assertEquals(RequestStatus.Accepted, snapshot.requests.first().status)
        assertEquals(StackResult.Failure(StackError.Full), repository.resolveRequest("b", true))
        assertEquals(StackResult.Failure(StackError.RequestResolved), repository.resolveRequest("a", true))
        assertEquals(2, repository.snapshot.value.posts.single().memberIds.size)
        // A remaining pending request can still be rejected even when the stack is full.
        assertTrue(repository.resolveRequest("b", false) is StackResult.Success)
        assertEquals(RequestStatus.Rejected, repository.snapshot.value.requests.last().status)
    }

    @Test
    fun rejectionIsTerminalAndKeepsSlotsAvailable() {
        val repository = InMemoryStackRepository(me, StackSnapshot(listOf(post()), listOf(JoinRequest("a", "post", other))))
        assertTrue(repository.resolveRequest("a", false) is StackResult.Success)
        assertEquals(1, repository.snapshot.value.posts.single().availableSlots)
        assertEquals(RequestStatus.Rejected, repository.snapshot.value.requests.single().status)
        assertEquals(StackResult.Failure(StackError.RequestResolved), repository.resolveRequest("a", true))
    }

    @Test
    fun onlyOwnerCanResolveOrSimulateRequests() {
        val repository = InMemoryStackRepository(me, StackSnapshot(listOf(post(other)), listOf(JoinRequest("a", "post", me))))
        assertEquals(StackResult.Failure(StackError.NotOwner), repository.resolveRequest("a", true))
        assertEquals(StackResult.Failure(StackError.NotOwner), repository.resolveRequest("a", false))
        assertEquals(StackResult.Failure(StackError.NotOwner), repository.simulateIncomingRequest("post"))
        assertEquals(StackResult.Failure(StackError.NotFound), repository.resolveRequest("missing", false))
        assertEquals(RequestStatus.Pending, repository.snapshot.value.requests.single().status)
    }

    @Test
    fun simulatedApplicantsAreUniqueAndExhaustionDoesNotChangeState() {
        val repository = InMemoryStackRepository(me, StackSnapshot(listOf(post())), listOf(other, third))
        assertTrue(repository.simulateIncomingRequest("post") is StackResult.Success)
        assertTrue(repository.simulateIncomingRequest("post") is StackResult.Success)
        val snapshot = repository.snapshot.value
        assertEquals(setOf(other.id, third.id), snapshot.requests.map { it.applicant.id }.toSet())
        assertEquals(StackResult.Failure(StackError.NoCandidates), repository.simulateIncomingRequest("post"))
        assertEquals(snapshot, repository.snapshot.value)
    }

    @Test
    fun profileRenameUpdatesExistingPostsAndRequestsWithoutChangingIdentity() {
        val repository = InMemoryStackRepository(me, StackSnapshot(listOf(post(), post(other).copy(id = "other-post")), listOf(JoinRequest("a", "other-post", me))))
        repository.updatePlayerName("Nuevo nombre")
        assertEquals("Nuevo nombre", repository.currentPlayer.name)
        assertEquals(me.id, repository.currentPlayer.id)
        assertEquals("Nuevo nombre", repository.snapshot.value.posts.first().owner.name)
        assertEquals(other, repository.snapshot.value.posts.last().owner)
        assertEquals("Nuevo nombre", repository.snapshot.value.requests.single().applicant.name)
        assertEquals(RequestStatus.Pending, repository.snapshot.value.requests.single().status)
    }

    @Test
    fun acceptingOnFullStackAlsoBlocksNewSimulatedApplicants() {
        val repository = InMemoryStackRepository(
            me, StackSnapshot(listOf(post()), listOf(JoinRequest("a", "post", other))), listOf(third)
        )
        repository.resolveRequest("a", true)
        assertEquals(StackResult.Failure(StackError.Full), repository.simulateIncomingRequest("post"))
        assertEquals(1, repository.snapshot.value.requests.size)
    }
}
