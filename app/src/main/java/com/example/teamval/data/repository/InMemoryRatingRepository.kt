package com.example.teamval.data.repository

import com.example.teamval.data.fake.DemoRatingData
import com.example.teamval.domain.model.CommentPolicy
import com.example.teamval.domain.model.RatingBlock
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.RatingRules
import com.example.teamval.domain.model.RatingSnapshot
import com.example.teamval.domain.model.TeamRating
import com.example.teamval.domain.model.TeamReputation
import com.example.teamval.domain.model.TeamRole
import com.example.teamval.domain.repository.RatingError
import com.example.teamval.domain.repository.RatingRepository
import com.example.teamval.domain.repository.RatingResult
import com.example.teamval.domain.repository.TeamRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Session-local ratings store. Every publishable rating is validated against the
 * live teams/scrims state (completed scrim, one rating per team and matchup, the
 * captain speaks for the team and never for itself).
 */
class InMemoryRatingRepository(
    private val teams: TeamRepository,
    initialRatings: List<TeamRating> = DemoRatingData.ratings
) : RatingRepository {
    private val state = MutableStateFlow(RatingSnapshot(initialRatings))
    override val snapshot = state.asStateFlow()

    private val myTeamId: String?
        get() = teams.snapshot.value.teams.find { team -> team.isMember(teams.currentPlayer.id) }?.id

    override fun canRate(scrimId: String): RatingBlock? {
        val myTeam = myTeamId
        val scrim = teams.snapshot.value.scrim(scrimId)
        val existing = myTeam?.let { state.value.fromTeam(scrimId, it) }
        val opponent = scrim?.opponentOf(myTeam.orEmpty())
        return RatingRules.check(scrim, myTeam, existing, opponent)
    }

    override fun hasRated(scrimId: String): Boolean {
        val myTeam = myTeamId ?: return false
        return state.value.fromTeam(scrimId, myTeam) != null
    }

    override fun submitRating(
        scrimId: String,
        scores: Map<RatingCategory, Int>,
        comment: String
    ): RatingResult {
        val myTeam = myTeamId ?: return failure(RatingError.NoTeam)
        // Rule 3: the captain represents the team.
        val captain = teams.snapshot.value.team(myTeam)?.members
            ?.find { it.id == teams.currentPlayer.id }
        if (captain == null || captain.role != TeamRole.Captain) return failure(RatingError.NoPermission)

        canRate(scrimId)?.let { return failure(it.toRatingError()) }
        if (!RatingRules.validScores(scores)) return failure(RatingError.InvalidScores)
        if (CommentPolicy.validate(comment) != null) return failure(RatingError.CommentRejected)

        val scrim = teams.snapshot.value.scrim(scrimId) ?: return failure(RatingError.NoScrim)
        val rating = TeamRating(
            id = UUID.randomUUID().toString(),
            scrimId = scrimId,
            fromTeamId = myTeam,
            toTeamId = scrim.opponentOf(myTeam) ?: return failure(RatingError.OwnTeam),
            captainName = teams.currentPlayer.name,
            scores = scores,
            comment = comment.trim(),
            createdAt = System.currentTimeMillis()
        )
        state.update { it.copy(ratings = it.ratings + rating) }
        return RatingResult.Success(rating.id)
    }

    override fun reportComment(ratingId: String): RatingResult {
        val current = state.value.ratings.find { it.id == ratingId }
            ?: return failure(RatingError.NotFound)
        if (current.reported) return failure(RatingError.NoPermission)
        state.update { snapshot ->
            snapshot.copy(ratings = snapshot.ratings.map {
                if (it.id == ratingId) it.copy(reported = true) else it
            })
        }
        return RatingResult.Success(ratingId)
    }

    override fun reputationOf(teamId: String): TeamReputation =
        TeamReputation.of(teamId, state.value.ratings)

    private fun failure(error: RatingError) = RatingResult.Failure(error)

    private fun RatingBlock.toRatingError(): RatingError = when (this) {
        RatingBlock.NoTeam -> RatingError.NoTeam
        RatingBlock.NoScrim -> RatingError.NoScrim
        RatingBlock.NotCompleted -> RatingError.NotCompleted
        RatingBlock.NotParticipant -> RatingError.NotParticipant
        RatingBlock.OwnTeam -> RatingError.OwnTeam
        RatingBlock.AlreadyRated -> RatingError.AlreadyRated
    }
}
