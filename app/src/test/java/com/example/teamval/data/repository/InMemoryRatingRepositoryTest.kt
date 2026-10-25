package com.example.teamval.data.repository

import com.example.teamval.domain.model.RatingBlock
import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.ReputationBadge
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.TeamDraft
import com.example.teamval.domain.repository.RatingError
import com.example.teamval.domain.repository.RatingResult
import com.example.teamval.domain.repository.TeamResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Anti-abuse rating rules against the live teams/scrims state: only after a
 * completed scrim, one rating per team and matchup, captain only, no self-rating
 * and the documented Eclipse Esports reputation example (4,8/5).
 */
class InMemoryRatingRepositoryTest {

    private data class Fixture(
        val teams: InMemoryTeamRepository,
        val ratings: InMemoryRatingRepository,
        val myTeamId: String,
        val scrimId: String
    )

    private fun scores(value: Int = 5) = RatingCategory.entries.associateWith { value }

    private fun createTeam(teams: InMemoryTeamRepository): String {
        val result = teams.createTeam(
            TeamDraft(
                name = "Equipo Prisma",
                tag = "PRS",
                description = "Equipo de prueba para la verificación de la ampliación."
            )
        )
        assertTrue(result is TeamResult.Success)
        return (result as TeamResult.Success).id
    }

    /** Creates the team, simulates an incoming proposal and completes it. */
    private fun completedFixture(): Fixture {
        val teams = InMemoryTeamRepository()
        val ratings = InMemoryRatingRepository(teams)
        val teamId = createTeam(teams)
        teams.simulateIncomingScrim()
        val incoming = teams.snapshot.value.scrims.first {
            it.involves(teamId) && it.awayTeamId == teamId && it.status == ScrimStatus.Proposed
        }
        teams.respondToScrim(incoming.id, accept = true)
        teams.completeScrim(incoming.id, 13, 9)
        return Fixture(teams, ratings, teamId, incoming.id)
    }

    @Test
    fun ratingRequiresTeamAndKnownScrim() {
        val teams = InMemoryTeamRepository()
        val ratings = InMemoryRatingRepository(teams)
        assertEquals(RatingBlock.NoTeam, ratings.canRate("scrim-any"))

        val fixture = completedFixture()
        assertNull(fixture.ratings.canRate(fixture.scrimId))
        assertEquals(RatingBlock.NoScrim, fixture.ratings.canRate("missing-scrim"))
    }

    @Test
    fun onlyCompletedScrimsCanBeRated() {
        val teams = InMemoryTeamRepository()
        val ratings = InMemoryRatingRepository(teams)
        val teamId = createTeam(teams)
        teams.simulateIncomingScrim()
        val incoming = teams.snapshot.value.scrims.first {
            it.involves(teamId) && it.awayTeamId == teamId && it.status == ScrimStatus.Proposed
        }

        assertEquals(RatingBlock.NotCompleted, ratings.canRate(incoming.id))
        teams.respondToScrim(incoming.id, accept = true)
        assertEquals(RatingBlock.NotCompleted, ratings.canRate(incoming.id))
        teams.completeScrim(incoming.id, 13, 9)
        assertNull(ratings.canRate(incoming.id))
    }

    @Test
    fun captainSubmitsExactlyOneRatingPerMatchup() {
        val fixture = completedFixture()
        val result = fixture.ratings.submitRating(
            fixture.scrimId, scores(), "Trato excelente y puntualidad total."
        )
        assertTrue(result is RatingResult.Success)
        assertTrue(fixture.ratings.hasRated(fixture.scrimId))
        assertEquals(RatingBlock.AlreadyRated, fixture.ratings.canRate(fixture.scrimId))

        val duplicate = fixture.ratings.submitRating(
            fixture.scrimId, scores(), "Segunda valoración que no debería admitirse."
        )
        assertTrue(duplicate is RatingResult.Failure)
        assertEquals(RatingError.AlreadyRated, (duplicate as RatingResult.Failure).error)

        val mine = fixture.ratings.snapshot.value.ratings.filter { it.fromTeamId == fixture.myTeamId }
        assertEquals(1, mine.size)
        assertEquals(fixture.myTeamId, mine.first().fromTeamId)
        assertEquals(fixture.teams.currentPlayer.name, mine.first().captainName)
    }

    @Test
    fun invalidScoresAndCommentsAreRejected() {
        val fixture = completedFixture()

        val badScores = fixture.ratings.submitRating(
            fixture.scrimId, scores(6), "Comentario válido para la prueba del formulario."
        )
        assertTrue(badScores is RatingResult.Failure)
        assertEquals(RatingError.InvalidScores, (badScores as RatingResult.Failure).error)

        val badComment = fixture.ratings.submitRating(
            fixture.scrimId, scores(), "Se comportaron como unos idiotas durante la serie."
        )
        assertTrue(badComment is RatingResult.Failure)
        assertEquals(RatingError.CommentRejected, (badComment as RatingResult.Failure).error)

        // Failed attempts never consume the single allowed rating.
        assertFalse(fixture.ratings.hasRated(fixture.scrimId))
        assertNull(fixture.ratings.canRate(fixture.scrimId))
    }

    @Test
    fun commentsAreReportedOnlyOnce() {
        val fixture = completedFixture()
        val target = fixture.ratings.snapshot.value.ratings.first { it.toTeamId == "team-eclipse" }

        assertTrue(fixture.ratings.reportComment(target.id) is RatingResult.Success)
        assertTrue(
            fixture.ratings.snapshot.value.ratings.first { it.id == target.id }.reported
        )
        val second = fixture.ratings.reportComment(target.id)
        assertTrue(second is RatingResult.Failure)
        assertEquals(RatingError.NoPermission, (second as RatingResult.Failure).error)

        val missing = fixture.ratings.reportComment("rating-unknown")
        assertEquals(RatingError.NotFound, (missing as RatingResult.Failure).error)
    }

    @Test
    fun eclipseDemoReputationMatchesTheDocumentedExample() {
        val fixture = completedFixture()
        val reputation = fixture.ratings.reputationOf("team-eclipse")

        assertEquals(10, reputation.count)
        assertEquals(4.8f, reputation.overall, 0.001f)
        assertEquals(4.9f, reputation.averageOf(RatingCategory.Respect), 0.001f)
        assertEquals(4.7f, reputation.averageOf(RatingCategory.Seriousness), 0.001f)
        assertEquals(4.8f, reputation.averageOf(RatingCategory.Punctuality), 0.001f)
        assertEquals(4.6f, reputation.averageOf(RatingCategory.Communication), 0.001f)
        assertEquals(5.0f, reputation.averageOf(RatingCategory.Sportsmanship), 0.001f)

        assertTrue(ReputationBadge.Trusted in reputation.badges)
        assertTrue(ReputationBadge.Active in reputation.badges)
        assertTrue(ReputationBadge.FairPlay in reputation.badges)
        assertTrue(ReputationBadge.Punctual in reputation.badges)
        assertFalse(ReputationBadge.Communicative in reputation.badges)

        val recent = reputation.recentComments()
        assertEquals(3, recent.size)
        assertEquals(recent, recent.sortedByDescending { it.createdAt })
        recent.forEach { assertTrue(it.comment.isNotBlank()) }
    }

    @Test
    fun everyDemoTeamHasFictitiousReputationData() {
        val fixture = completedFixture()
        listOf("team-aurora", "team-volt", "team-neon", "team-titan").forEach { teamId ->
            assertTrue(
                "Expected ratings for $teamId",
                fixture.ratings.reputationOf(teamId).count > 0
            )
        }
        assertEquals(0, fixture.ratings.reputationOf("team-unknown").count)
    }

    @Test
    fun ratingsNeverTouchTeamStateOrCurrency() {
        val fixture = completedFixture()
        // The rating flow only writes ratings: no coins, no rewards, no penalties.
        val teamsBefore = fixture.teams.snapshot.value.teams
        val ratingsBefore = fixture.ratings.snapshot.value.ratings.size

        val result = fixture.ratings.submitRating(
            fixture.scrimId, scores(), "Rutina seria y muy buen trato."
        )

        assertTrue(result is RatingResult.Success)
        assertEquals(ratingsBefore + 1, fixture.ratings.snapshot.value.ratings.size)
        // Losing, drawing or winning never modifies teams either.
        assertEquals(teamsBefore, fixture.teams.snapshot.value.teams)
    }
}
