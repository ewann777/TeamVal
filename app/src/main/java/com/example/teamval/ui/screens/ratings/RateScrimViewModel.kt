package com.example.teamval.ui.screens.ratings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamval.R
import com.example.teamval.domain.model.CommentError
import com.example.teamval.domain.model.CommentPolicy
import com.example.teamval.domain.model.RatingBlock
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.RatingSnapshot
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.TeamLogo
import com.example.teamval.domain.repository.RatingError
import com.example.teamval.domain.repository.RatingRepository
import com.example.teamval.domain.repository.RatingResult
import com.example.teamval.domain.repository.TeamRepository
import com.example.teamval.ui.components.messageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class RateUiState(
    val scrimId: String = "",
    val exists: Boolean = false,
    val completed: Boolean = false,
    val rivalName: String = "",
    val rivalTag: String = "",
    val rivalLogo: TeamLogo = TeamLogo(),
    val block: RatingBlock? = null,
    val scores: Map<RatingCategory, Int> = RatingCategory.entries.associateWith { 3 },
    val comment: String = "",
    val commentError: CommentError? = null,
    val failureRes: Int? = null,
    val sent: Boolean = false,
    val reportedIds: Set<String> = emptySet(),
    val rivalTeamId: String? = null
)

/**
 * Rating form and anti-abuse state. All restrictions are validated again inside
 * the repository, so the UI only mirrors them.
 */
class RateScrimViewModel(
    private val ratings: RatingRepository,
    private val teams: TeamRepository
) : ViewModel() {

    private data class Form(
        val scrimId: String = "",
        val scores: Map<RatingCategory, Int> = RatingCategory.entries.associateWith { 3 },
        val comment: String = "",
        val commentError: CommentError? = null,
        val failureRes: Int? = null,
        val sent: Boolean = false
    )

    private val form = MutableStateFlow(Form())

    val uiState = combine(teams.snapshot, ratings.snapshot, form) { teamSnap, ratingSnap, current ->
        val scrim = current.scrimId.let { teamSnap.scrim(it) }
        val myTeamId = teamSnap.teams.find { it.isMember(teams.currentPlayer.id) }?.id
        val rivalId = scrim?.opponentOf(myTeamId.orEmpty())
        val rival = rivalId?.let { teamSnap.team(it) }
        RateUiState(
            scrimId = current.scrimId,
            exists = scrim != null,
            completed = scrim?.status == ScrimStatus.Completed,
            rivalName = rival?.name.orEmpty(),
            rivalTag = rival?.tag.orEmpty(),
            rivalLogo = rival?.identity?.logo ?: TeamLogo(),
            block = if (current.scrimId.isBlank()) RatingBlock.NoScrim
            else ratings.canRate(current.scrimId),
            scores = current.scores,
            comment = current.comment,
            commentError = current.commentError,
            failureRes = current.failureRes,
            sent = current.sent,
            reportedIds = ratingSnap.ratings.filter { it.reported }.map { it.id }.toSet(),
            rivalTeamId = rivalId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RateUiState()
    )

    fun startRating(scrimId: String) {
        form.value = Form(scrimId = scrimId)
    }

    fun setScore(category: RatingCategory, value: Int) = form.update { current ->
        current.copy(
            scores = current.scores + (category to value.coerceIn(1, 5)),
            failureRes = null
        )
    }

    fun setComment(text: String) = form.update {
        it.copy(comment = text, commentError = null, failureRes = null)
    }

    fun submit(): Boolean {
        val current = form.value
        val commentError = CommentPolicy.validate(current.comment)
        if (commentError != null) {
            form.update { it.copy(commentError = commentError, failureRes = null) }
            return false
        }
        val result = ratings.submitRating(current.scrimId, current.scores, current.comment)
        return when (result) {
            is RatingResult.Success -> {
                form.update { it.copy(sent = true, failureRes = null, commentError = null) }
                true
            }
            is RatingResult.Failure -> {
                when (result.error) {
                    RatingError.CommentRejected ->
                        form.update { it.copy(commentError = CommentPolicy.validate(current.comment)) }
                    else -> form.update { it.copy(failureRes = result.error.messageRes) }
                }
                false
            }
        }
    }

    fun reportComment(ratingId: String): RatingResult = ratings.reportComment(ratingId)

    fun blockFor(scrimId: String): RatingBlock? = ratings.canRate(scrimId)
    fun hasRated(scrimId: String): Boolean = ratings.hasRated(scrimId)
    fun reputationOf(teamId: String) = ratings.reputationOf(teamId)
    fun scrimStatusOf(scrimId: String): ScrimStatus? = teams.snapshot.value.scrim(scrimId)?.status
    fun myTeamName(): String? =
        teams.snapshot.value.teams.find { it.isMember(teams.currentPlayer.id) }?.name

    fun resetToScrim(scrimId: String) = form.update { it.copy(scrimId = scrimId) }

    companion object {
        /** Resource used when the repository rejects without a specific message. */
        val genericErrorRes = R.string.rating_error_generic
    }
}
