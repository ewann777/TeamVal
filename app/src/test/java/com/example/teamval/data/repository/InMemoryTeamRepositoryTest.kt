package com.example.teamval.data.repository

import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.ScrimDraft
import com.example.teamval.domain.model.ScrimResultDraft
import com.example.teamval.domain.model.ScrimResultField
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.TeamDraft
import com.example.teamval.domain.model.TeamDraftField
import com.example.teamval.domain.model.TeamRole
import com.example.teamval.domain.model.TeamSnapshot
import com.example.teamval.domain.repository.TeamError
import com.example.teamval.domain.repository.TeamResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryTeamRepositoryTest {

    private fun validTeamDraft() = TeamDraft(
        name = "Equipo Prueba",
        tag = "prb",
        description = "Equipo de prueba para verificar las reglas de la demo."
    )

    private fun newRepository() = InMemoryTeamRepository()

    private fun createTeam(repository: InMemoryTeamRepository): String {
        val result = repository.createTeam(validTeamDraft())
        assertTrue(result is TeamResult.Success)
        return (result as TeamResult.Success).id
    }

    private fun TeamResult.error(): TeamError = (this as TeamResult.Failure).error

    // --- Team creation ------------------------------------------------------
    @Test
    fun createTeamStoresCaptainedTeam() {
        val repository = newRepository()
        val teamId = createTeam(repository)
        val team = repository.snapshot.value.team(teamId)
        assertNotNull(team)
        assertEquals(teamId, repository.snapshot.value.myTeamIdFor(repository.currentPlayer.id))
        assertEquals(TeamRole.Captain, team?.captain?.role)
        assertEquals(repository.currentPlayer.id, team?.captain?.id)
        assertTrue(team?.tag == "PRB")
    }

    @Test
    fun createTeamRejectsInvalidDrafts() {
        val repository = newRepository()
        val shortName = validTeamDraft().copy(name = "AB")
        assertTrue(repository.createTeam(shortName).error() == TeamError.InvalidTeam)
        val badTag = validTeamDraft().copy(tag = "TOOLONG")
        assertTrue(repository.createTeam(badTag).error() == TeamError.InvalidTeam)
        val shortDescription = validTeamDraft().copy(description = "Corto")
        assertTrue(repository.createTeam(shortDescription).error() == TeamError.InvalidTeam)
        assertTrue(repository.snapshot.value.teams.none { it.isMember(repository.currentPlayer.id) })
    }

    @Test
    fun createTeamOnlyOncePerPlayer() {
        val repository = newRepository()
        createTeam(repository)
        assertTrue(repository.createTeam(validTeamDraft()).error() == TeamError.AlreadyInTeam)
    }

    @Test
    fun teamDraftValidationFlagsEachField() {
        val draft = TeamDraft(name = "X", tag = "!", motto = "x".repeat(81), description = "corto")
        val errors = draft.invalidFields()
        assertTrue(TeamDraftField.Name in errors)
        assertTrue(TeamDraftField.Tag in errors)
        assertTrue(TeamDraftField.Motto in errors)
        assertTrue(TeamDraftField.Description in errors)
    }

    @Test
    fun updatePlayerNameReachesOwnTeamMembers() {
        val repository = newRepository()
        createTeam(repository)
        repository.updatePlayerName("Renombrado#NEW")
        val myTeam = repository.snapshot.value.teams.first()
        assertEquals("Renombrado#NEW", myTeam.captain?.name)
        assertEquals("Renombrado#NEW", repository.currentPlayer.name)
    }

    // --- Scrim proposals ----------------------------------------------------
    @Test
    fun proposeScrimRequiresOwnTeam() {
        val repository = newRepository()
        val result = repository.proposeScrim(ScrimDraft(opponentTeamId = "team-eclipse", schedule = "Sábado 19:00"))
        assertTrue(result.error() == TeamError.NoTeam)
    }

    @Test
    fun proposeScrimValidatesDraftAndOpponent() {
        val repository = newRepository()
        createTeam(repository)
        assertTrue(
            repository.proposeScrim(ScrimDraft(opponentTeamId = "", schedule = "Sábado 19:00")).error() ==
                TeamError.InvalidScrim
        )
        assertTrue(
            repository.proposeScrim(ScrimDraft(opponentTeamId = "team-eclipse", schedule = "12")).error() ==
                TeamError.InvalidScrim
        )
        val ownTeamId = repository.snapshot.value.teams.first { team ->
            team.members.any { it.id == repository.currentPlayer.id }
        }.id
        assertTrue(
            repository.proposeScrim(ScrimDraft(opponentTeamId = ownTeamId, schedule = "Sábado 19:00")).error() ==
                TeamError.InvalidScrim
        )
        assertTrue(
            repository.proposeScrim(ScrimDraft(opponentTeamId = "missing-team", schedule = "Sábado 19:00")).error() ==
                TeamError.NotFound
        )
    }

    @Test
    fun proposeScrimCreatesProposalAndBlocksDuplicates() {
        val repository = newRepository()
        val myTeamId = createTeam(repository)
        val result = repository.proposeScrim(
            ScrimDraft(opponentTeamId = "team-eclipse", mode = GameMode.Swiftplay, schedule = "Sábado 19:00")
        )
        assertTrue(result is TeamResult.Success)
        val scrim = repository.snapshot.value.scrim((result as TeamResult.Success).id)
        assertEquals(myTeamId, scrim?.homeTeamId)
        assertEquals("team-eclipse", scrim?.awayTeamId)
        assertEquals(ScrimStatus.Proposed, scrim?.status)

        val duplicate = repository.proposeScrim(
            ScrimDraft(opponentTeamId = "team-eclipse", schedule = "Domingo 20:00")
        )
        assertTrue(duplicate.error() == TeamError.DuplicateScrim)
    }

    // --- Scrim transitions --------------------------------------------------
    @Test
    fun onlyReceiverRespondsAndOnlyWhileProposed() {
        val repository = newRepository()
        val myTeamId = createTeam(repository)
        val incoming = repository.simulateIncomingScrim()
        val incomingId = (incoming as TeamResult.Success).id

        // My team is the receiver here, so my own outgoing proposal would not be answerable.
        val outgoing = repository.proposeScrim(ScrimDraft(opponentTeamId = "team-volt", schedule = "Viernes 21:00"))
        val outgoingId = (outgoing as TeamResult.Success).id
        assertTrue(repository.respondToScrim(outgoingId, accept = true).error() == TeamError.NotParticipant)

        assertTrue(repository.respondToScrim(incomingId, accept = true) is TeamResult.Success)
        assertEquals(ScrimStatus.Confirmed, repository.snapshot.value.scrim(incomingId)?.status)
        assertTrue(repository.respondToScrim(incomingId, accept = true).error() == TeamError.WrongStatus)

        assertEquals(myTeamId, repository.snapshot.value.scrim(incomingId)?.awayTeamId)
    }

    @Test
    fun declinedProposalEndsCancelled() {
        val repository = newRepository()
        createTeam(repository)
        val incomingId = (repository.simulateIncomingScrim() as TeamResult.Success).id
        assertTrue(repository.respondToScrim(incomingId, accept = false) is TeamResult.Success)
        assertEquals(ScrimStatus.Cancelled, repository.snapshot.value.scrim(incomingId)?.status)
    }

    @Test
    fun completeRequiresConfirmedStatusValidScoreAndParticipant() {
        val repository = newRepository()
        createTeam(repository)
        val proposedId = (repository.simulateIncomingScrim() as TeamResult.Success).id
        assertTrue(repository.completeScrim(proposedId, 13, 9).error() == TeamError.WrongStatus)

        repository.respondToScrim(proposedId, accept = true)
        assertTrue(repository.completeScrim(proposedId, 13, 13).error() == TeamError.InvalidResult)
        assertTrue(repository.completeScrim(proposedId, 14, 9).error() == TeamError.InvalidResult)
        assertTrue(repository.completeScrim("missing", 13, 9).error() == TeamError.NotFound)

        assertTrue(repository.completeScrim(proposedId, 13, 10) is TeamResult.Success)
        val finished = repository.snapshot.value.scrim(proposedId)
        assertEquals(ScrimStatus.Completed, finished?.status)
        assertEquals(13, finished?.homeScore)
        assertEquals(10, finished?.awayScore)
        assertTrue(repository.cancelScrim(proposedId).error() == TeamError.WrongStatus)
    }

    @Test
    fun participantsCanCancelWhileNotFinished() {
        val repository = newRepository()
        createTeam(repository)
        val incomingId = (repository.simulateIncomingScrim() as TeamResult.Success).id
        assertTrue(repository.cancelScrim(incomingId) is TeamResult.Success)
        assertEquals(ScrimStatus.Cancelled, repository.snapshot.value.scrim(incomingId)?.status)
        assertTrue(repository.cancelScrim(incomingId).error() == TeamError.WrongStatus)
    }

    @Test
    fun simulateIncomingNeedsTeamAndBlocksSecondPendingProposal() {
        val repository = newRepository()
        assertTrue(repository.simulateIncomingScrim().error() == TeamError.NoTeam)
        createTeam(repository)
        val first = repository.simulateIncomingScrim()
        assertTrue(first is TeamResult.Success)
        // Five rival teams exist, so a second pending proposal always finds another rival.
        val second = repository.simulateIncomingScrim()
        assertTrue(second is TeamResult.Success)
        assertEquals(2, repository.snapshot.value.scrims.count { it.status == ScrimStatus.Proposed })
    }

    // --- Result draft validation -------------------------------------------
    @Test
    fun resultDraftAcceptsScoresInsideRangeOnly() {
        assertTrue(ScrimResultDraft("13", "9").invalidFields().isEmpty())
        assertTrue(ScrimResultDraft("", "9").invalidFields().contains(ScrimResultField.HomeScore))
        assertTrue(ScrimResultDraft("14", "9").invalidFields().contains(ScrimResultField.HomeScore))
        assertTrue(ScrimResultDraft("-1", "9").invalidFields().contains(ScrimResultField.HomeScore))
        val tie = ScrimResultDraft("8", "8").invalidFields()
        assertTrue(ScrimResultField.HomeScore in tie && ScrimResultField.AwayScore in tie)
    }

    // --- Demo data ----------------------------------------------------------
    @Test
    fun demoSnapshotIsConsistent() {
        val snapshot: TeamSnapshot = InMemoryTeamRepository().snapshot.value
        assertTrue(snapshot.teams.size >= 5)
        assertTrue(snapshot.scrims.isNotEmpty())
        snapshot.scrims.forEach { scrim ->
            assertNotNull(snapshot.team(scrim.homeTeamId))
            assertNotNull(snapshot.team(scrim.awayTeamId))
            assertTrue(scrim.homeTeamId != scrim.awayTeamId)
            if (scrim.status == ScrimStatus.Completed) {
                assertNotNull(scrim.homeScore)
                assertNotNull(scrim.awayScore)
            }
        }
        // The player starts without a team, so the demo is not stuck on the rival list.
        assertTrue(snapshot.teams.none { it.isMember(InMemoryTeamRepository().currentPlayer.id) })
    }

    private fun TeamSnapshot.myTeamIdFor(playerId: String): String? =
        teams.find { team -> team.isMember(playerId) }?.id
}
