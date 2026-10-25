package com.example.teamval.domain.model

enum class TeamRole { Captain, Member }

data class TeamMember(
    val id: String,
    val name: String,
    val role: TeamRole,
    val rank: ValorantRank = ValorantRank.Unranked
)

data class Team(
    val id: String,
    val name: String,
    val tag: String,
    val region: Region,
    val motto: String,
    val description: String,
    val members: List<TeamMember>,
    /** Visual identity of the team: logo, colors, banner, badges, layout… (stage 4.1). */
    val identity: TeamIdentity = TeamIdentity(motto = motto, description = description)
) {
    val captain: TeamMember? get() = members.firstOrNull { it.role == TeamRole.Captain }
    val memberCount: Int get() = members.size
    fun isMember(playerId: String): Boolean = members.any { it.id == playerId }
}

enum class ScrimStatus { Proposed, Confirmed, Completed, Cancelled }

/**
 * Una scrim entre dos equipos. [homeTeamId] es quien propone y [awayTeamId] quien
 * recibe la propuesta; el marcador solo se registra al completarla.
 */
data class Scrim(
    val id: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val mode: GameMode,
    val schedule: String,
    val notes: String,
    val status: ScrimStatus,
    val homeScore: Int? = null,
    val awayScore: Int? = null
) {
    fun involves(teamId: String): Boolean = homeTeamId == teamId || awayTeamId == teamId

    fun opponentOf(teamId: String): String? = when (teamId) {
        homeTeamId -> awayTeamId
        awayTeamId -> homeTeamId
        else -> null
    }
}

enum class TeamDraftField { Name, Tag, Motto, Description }

data class TeamDraft(
    val name: String = "",
    val tag: String = "",
    val region: Region = Region.LatamSouth,
    val motto: String = "",
    val description: String = ""
) {
    fun invalidFields(): Set<TeamDraftField> = buildSet {
        if (name.trim().length !in 3..24) add(TeamDraftField.Name)
        val cleanTag = tag.trim()
        if (cleanTag.length !in 2..5 || !cleanTag.all { it.isLetterOrDigit() }) add(TeamDraftField.Tag)
        if (motto.trim().length > 80) add(TeamDraftField.Motto)
        if (description.trim().length !in 10..300) add(TeamDraftField.Description)
    }
}

enum class ScrimDraftField { Opponent, Schedule, Notes }

data class ScrimDraft(
    val opponentTeamId: String = "",
    val mode: GameMode = GameMode.Competitive,
    val schedule: String = "",
    val notes: String = ""
) {
    fun invalidFields(myTeamId: String?): Set<ScrimDraftField> = buildSet {
        if (opponentTeamId.isBlank() || opponentTeamId == myTeamId) add(ScrimDraftField.Opponent)
        if (schedule.trim().length !in 5..60) add(ScrimDraftField.Schedule)
        if (notes.trim().length > 300) add(ScrimDraftField.Notes)
    }
}

enum class ScrimResultField { HomeScore, AwayScore }

/** Marcador de una scrim finalizada: 0–13 por equipo y sin empates, como en VALORANT. */
data class ScrimResultDraft(val homeScore: String = "", val awayScore: String = "") {
    fun invalidFields(): Set<ScrimResultField> {
        val home = homeScore.toIntOrNull()?.takeIf { it in 0..13 }
        val away = awayScore.toIntOrNull()?.takeIf { it in 0..13 }
        return buildSet {
            if (home == null) add(ScrimResultField.HomeScore)
            if (away == null) add(ScrimResultField.AwayScore)
            if (home != null && away != null && home == away) {
                add(ScrimResultField.HomeScore)
                add(ScrimResultField.AwayScore)
            }
        }
    }
}

data class TeamSnapshot(
    val teams: List<Team> = emptyList(),
    val scrims: List<Scrim> = emptyList()
) {
    fun team(teamId: String): Team? = teams.find { it.id == teamId }
    fun scrim(scrimId: String): Scrim? = scrims.find { it.id == scrimId }
    fun scrimsOf(teamId: String): List<Scrim> = scrims.filter { it.involves(teamId) }
}
