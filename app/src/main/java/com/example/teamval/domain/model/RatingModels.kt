package com.example.teamval.domain.model

/**
 * Ratings between teams measure conduct, professionalism and respect — never the
 * competitive level of the rival. Every category uses 1–5 stars.
 */
enum class RatingCategory { Respect, Seriousness, Punctuality, Communication, Sportsmanship }

data class TeamRating(
    val id: String,
    val scrimId: String,
    val fromTeamId: String,
    val toTeamId: String,
    val captainName: String,
    val scores: Map<RatingCategory, Int>,
    val comment: String = "",
    val createdAt: Long = 0L,
    val reported: Boolean = false
) {
    val overall: Float
        get() = if (scores.isEmpty()) 0f else scores.values.sum() / scores.size.toFloat()
}

data class RatingSnapshot(val ratings: List<TeamRating> = emptyList()) {
    fun fromTeam(scrimId: String, teamId: String): TeamRating? =
        ratings.find { it.scrimId == scrimId && it.fromTeamId == teamId }
}

enum class ReputationBadge { Trusted, FairPlay, Punctual, Communicative, Active }

/** Aggregated reputation of a team, computed from the ratings it has received. */
data class TeamReputation(val teamId: String, val ratings: List<TeamRating>) {
    val count: Int get() = ratings.size

    val overall: Float
        get() {
            val all = ratings.flatMap { it.scores.values }
            return if (all.isEmpty()) 0f else all.sum() / all.size.toFloat()
        }

    fun averageOf(category: RatingCategory): Float {
        val values = ratings.mapNotNull { it.scores[category] }
        return if (values.isEmpty()) 0f else values.sum() / values.size.toFloat()
    }

    val categoryAverages: Map<RatingCategory, Float>
        get() = RatingCategory.entries.associateWith { averageOf(it) }

    fun recentComments(limit: Int = 3): List<TeamRating> =
        ratings.filter { it.comment.isNotBlank() }
            .sortedByDescending { it.createdAt }
            .take(limit)

    /** Decorative badges derived from the received ratings (demo logic). */
    val badges: List<ReputationBadge>
        get() = buildList {
            if (count >= 10 && overall >= 4.5f) add(ReputationBadge.Trusted)
            if (count >= 5) add(ReputationBadge.Active)
            if (count >= 3 && averageOf(RatingCategory.Respect) >= 4.7f) add(ReputationBadge.FairPlay)
            if (count >= 3 && averageOf(RatingCategory.Punctuality) >= 4.7f) add(ReputationBadge.Punctual)
            if (count >= 3 && averageOf(RatingCategory.Communication) >= 4.7f) add(ReputationBadge.Communicative)
        }

    companion object {
        fun of(teamId: String, ratings: List<TeamRating>): TeamReputation =
            TeamReputation(teamId, ratings.filter { it.toTeamId == teamId })
    }
}

/** Why a scrim cannot be rated right now. `null` from [RatingRules.check] means allowed. */
enum class RatingBlock { NoTeam, NoScrim, NotCompleted, NotParticipant, OwnTeam, AlreadyRated }

/**
 * Anti-abuse rules of the rating system (stage 4.1):
 *  1. only after a completed (and therefore confirmed) scrim;
 *  2. one single rating per team and matchup;
 *  3. the captain represents the team;
 *  4. never rate your own team;
 *  5. no duplicate ratings for the same matchup.
 */
object RatingRules {
    fun check(
        scrim: Scrim?,
        myTeamId: String?,
        existing: TeamRating?,
        opponentTeamId: String?
    ): RatingBlock? = when {
        myTeamId == null -> RatingBlock.NoTeam
        scrim == null -> RatingBlock.NoScrim
        !scrim.involves(myTeamId) -> RatingBlock.NotParticipant
        scrim.status != ScrimStatus.Completed -> RatingBlock.NotCompleted
        opponentTeamId == null || opponentTeamId == myTeamId -> RatingBlock.OwnTeam
        existing != null -> RatingBlock.AlreadyRated
        else -> null
    }

    fun validScores(scores: Map<RatingCategory, Int>): Boolean =
        scores.size == RatingCategory.entries.size && scores.values.all { it in 1..5 }
}

/** Demo moderation rules for comments. Real moderation requires the future backend. */
object CommentPolicy {
    const val MIN_LENGTH = 10
    const val MAX_LENGTH = 500

    /** Small demo blocklist; the real review flow arrives with the backend. */
    private val blockedTerms = listOf("idiota", "imbécil", "estafa", "scam", "hackeo", "tramposo")

    fun validate(comment: String): CommentError? {
        val text = comment.trim()
        if (text.isEmpty()) return null // the comment is optional
        if (text.length < MIN_LENGTH) return CommentError.TooShort
        if (text.length > MAX_LENGTH) return CommentError.TooLong
        if (text.contains("http://", true) || text.contains("https://", true) || text.contains("www.", true)) {
            return CommentError.Link
        }
        val lowered = text.lowercase()
        if (blockedTerms.any { lowered.contains(it) }) return CommentError.BlockedContent
        return null
    }
}

enum class CommentError { TooShort, TooLong, Link, BlockedContent }
