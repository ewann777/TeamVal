package com.example.teamval.domain.repository

import com.example.teamval.domain.model.RatingBlock
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.RatingSnapshot
import com.example.teamval.domain.model.TeamReputation
import kotlinx.coroutines.flow.StateFlow

enum class RatingError {
    NoTeam, NoScrim, NotCompleted, NotParticipant, OwnTeam, AlreadyRated,
    InvalidScores, CommentRejected, NotFound, NoPermission
}

sealed interface RatingResult {
    data class Success(val id: String) : RatingResult
    data class Failure(val error: RatingError) : RatingResult
}

/**
 * Ratings between teams. The implementation validates every anti-abuse rule with
 * the live team/scrims state, so a rating can only be published after a completed
 * scrim and only once per team and matchup.
 */
interface RatingRepository {
    val snapshot: StateFlow<RatingSnapshot>

    /** `null` when the player's team may rate [scrimId]; otherwise the blocking rule. */
    fun canRate(scrimId: String): RatingBlock?

    fun submitRating(
        scrimId: String,
        scores: Map<RatingCategory, Int>,
        comment: String
    ): RatingResult

    fun reportComment(ratingId: String): RatingResult

    fun reputationOf(teamId: String): TeamReputation

    fun hasRated(scrimId: String): Boolean
}
