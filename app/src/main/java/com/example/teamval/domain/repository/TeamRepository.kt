package com.example.teamval.domain.repository

import com.example.teamval.domain.model.Player
import com.example.teamval.domain.model.ScrimDraft
import com.example.teamval.domain.model.TeamDraft
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.TeamSnapshot
import kotlinx.coroutines.flow.StateFlow

enum class TeamError {
    InvalidTeam, InvalidScrim, InvalidResult, NotFound, AlreadyInTeam,
    NoTeam, OpponentIsOwn, NotParticipant, WrongStatus, DuplicateScrim
}

sealed interface TeamResult {
    data class Success(val id: String) : TeamResult
    data class Failure(val error: TeamError) : TeamResult
}

interface TeamRepository {
    val currentPlayer: Player
    val snapshot: StateFlow<TeamSnapshot>

    fun createTeam(draft: TeamDraft): TeamResult
    fun proposeScrim(draft: ScrimDraft): TeamResult

    /** The receiving team accepts (true) or declines (false) a proposed scrim. */
    fun respondToScrim(scrimId: String, accept: Boolean): TeamResult
    fun cancelScrim(scrimId: String): TeamResult
    fun completeScrim(scrimId: String, homeScore: Int, awayScore: Int): TeamResult

    fun updatePlayerName(name: String)
    fun updateTeamIdentity(teamId: String, identity: TeamIdentity)

    /** Demo-only input: a rival team proposes a scrim to the player's team. */
    fun simulateIncomingScrim(): TeamResult
}
