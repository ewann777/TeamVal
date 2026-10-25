package com.example.teamval.data.repository

import com.example.teamval.data.fake.DemoStackData
import com.example.teamval.data.fake.DemoTeamData
import com.example.teamval.domain.model.Player
import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.Scrim
import com.example.teamval.domain.model.ScrimDraft
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.Team
import com.example.teamval.domain.model.TeamDraft
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.TeamMember
import com.example.teamval.domain.model.TeamRole
import com.example.teamval.domain.model.TeamSnapshot
import com.example.teamval.domain.model.ValorantRank
import com.example.teamval.domain.repository.TeamError
import com.example.teamval.domain.repository.TeamRepository
import com.example.teamval.domain.repository.TeamResult
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Session-local demo store for teams and scrims. Mutations publish the whole snapshot. */
class InMemoryTeamRepository(
    currentPlayer: Player = DemoStackData.currentPlayer,
    initialSnapshot: TeamSnapshot = TeamSnapshot(DemoTeamData.teams, DemoTeamData.scrims)
) : TeamRepository {
    private var player = currentPlayer
    override val currentPlayer: Player get() = player
    private val state = MutableStateFlow(initialSnapshot)
    override val snapshot = state.asStateFlow()
    private var incomingCount = 0

    private val myTeam: Team?
        get() = state.value.teams.find { team -> team.isMember(player.id) }

    @Synchronized
    override fun updatePlayerName(name: String) {
        player = player.copy(name = name)
        state.value = state.value.copy(
            teams = state.value.teams.map { team ->
                if (team.isMember(player.id)) {
                    team.copy(members = team.members.map {
                        if (it.id == player.id) it.copy(name = name) else it
                    })
                } else team
            }
        )
    }

    @Synchronized
    override fun updateTeamIdentity(teamId: String, identity: TeamIdentity) {
        state.value = state.value.copy(
            teams = state.value.teams.map { team ->
                if (team.id == teamId) team.copy(identity = identity) else team
            }
        )
    }

    @Synchronized
    override fun createTeam(draft: TeamDraft): TeamResult {
        if (draft.invalidFields().isNotEmpty()) return failure(TeamError.InvalidTeam)
        if (myTeam != null) return failure(TeamError.AlreadyInTeam)
        val team = Team(
            id = UUID.randomUUID().toString(),
            name = draft.name.trim(),
            tag = draft.tag.trim().uppercase(),
            region = draft.region,
            motto = draft.motto.trim(),
            description = draft.description.trim(),
            members = listOf(TeamMember(player.id, player.name, TeamRole.Captain, ValorantRank.Gold))
        )
        state.value = state.value.copy(teams = listOf(team) + state.value.teams)
        return TeamResult.Success(team.id)
    }

    @Synchronized
    override fun proposeScrim(draft: ScrimDraft): TeamResult {
        val mine = myTeam ?: return failure(TeamError.NoTeam)
        if (draft.invalidFields(mine.id).isNotEmpty()) return failure(TeamError.InvalidScrim)
        val rival = state.value.team(draft.opponentTeamId) ?: return failure(TeamError.NotFound)
        if (rival.id == mine.id) return failure(TeamError.OpponentIsOwn)
        val duplicated = state.value.scrims.any {
            it.status == ScrimStatus.Proposed && it.involves(mine.id) && it.involves(rival.id)
        }
        if (duplicated) return failure(TeamError.DuplicateScrim)
        val scrim = Scrim(
            id = UUID.randomUUID().toString(),
            homeTeamId = mine.id,
            awayTeamId = rival.id,
            mode = draft.mode,
            schedule = draft.schedule.trim(),
            notes = draft.notes.trim(),
            status = ScrimStatus.Proposed
        )
        state.value = state.value.copy(scrims = listOf(scrim) + state.value.scrims)
        return TeamResult.Success(scrim.id)
    }

    @Synchronized
    override fun respondToScrim(scrimId: String, accept: Boolean): TeamResult {
        val mine = myTeam ?: return failure(TeamError.NoTeam)
        val scrim = state.value.scrim(scrimId) ?: return failure(TeamError.NotFound)
        if (scrim.awayTeamId != mine.id) return failure(TeamError.NotParticipant)
        if (scrim.status != ScrimStatus.Proposed) return failure(TeamError.WrongStatus)
        return updateScrim(
            scrim.copy(status = if (accept) ScrimStatus.Confirmed else ScrimStatus.Cancelled)
        )
    }

    @Synchronized
    override fun cancelScrim(scrimId: String): TeamResult {
        val mine = myTeam ?: return failure(TeamError.NoTeam)
        val scrim = state.value.scrim(scrimId) ?: return failure(TeamError.NotFound)
        if (!scrim.involves(mine.id)) return failure(TeamError.NotParticipant)
        if (scrim.status != ScrimStatus.Proposed && scrim.status != ScrimStatus.Confirmed) {
            return failure(TeamError.WrongStatus)
        }
        return updateScrim(scrim.copy(status = ScrimStatus.Cancelled))
    }

    @Synchronized
    override fun completeScrim(scrimId: String, homeScore: Int, awayScore: Int): TeamResult {
        val mine = myTeam ?: return failure(TeamError.NoTeam)
        val scrim = state.value.scrim(scrimId) ?: return failure(TeamError.NotFound)
        if (!scrim.involves(mine.id)) return failure(TeamError.NotParticipant)
        if (scrim.status != ScrimStatus.Confirmed) return failure(TeamError.WrongStatus)
        if (homeScore !in 0..13 || awayScore !in 0..13 || homeScore == awayScore) {
            return failure(TeamError.InvalidResult)
        }
        return updateScrim(scrim.copy(status = ScrimStatus.Completed, homeScore = homeScore, awayScore = awayScore))
    }

    @Synchronized
    override fun simulateIncomingScrim(): TeamResult {
        val mine = myTeam ?: return failure(TeamError.NoTeam)
        val rival = state.value.teams.firstOrNull { team ->
            team.id != mine.id && state.value.scrims.none {
                it.status == ScrimStatus.Proposed && it.involves(mine.id) && it.involves(team.id)
            }
        } ?: return failure(TeamError.DuplicateScrim)
        val scrim = Scrim(
            id = UUID.randomUUID().toString(),
            homeTeamId = rival.id,
            awayTeamId = mine.id,
            mode = GameMode.Competitive,
            schedule = DemoTeamData.incomingSchedules[incomingCount % DemoTeamData.incomingSchedules.size],
            notes = "Propuesta ficticia de un equipo rival.",
            status = ScrimStatus.Proposed
        )
        incomingCount++
        state.value = state.value.copy(scrims = listOf(scrim) + state.value.scrims)
        return TeamResult.Success(scrim.id)
    }

    private fun updateScrim(updated: Scrim): TeamResult {
        state.value = state.value.copy(scrims = state.value.scrims.map {
            if (it.id == updated.id) updated else it
        })
        return TeamResult.Success(updated.id)
    }

    private fun failure(error: TeamError) = TeamResult.Failure(error)
}
