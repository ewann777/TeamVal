package com.example.teamval.data.fake

import com.example.teamval.domain.model.RatingCategory
import com.example.teamval.domain.model.TeamRating

/**
 * Fictitious ratings used during the demo. The Eclipse Esports seed reproduces the
 * documented example exactly: 4,8/5 overall with 4,9 / 4,7 / 4,8 / 4,6 / 5,0 per
 * category over 10 ratings.
 */
internal object DemoRatingData {
    private const val DAY_MS = 24L * 60 * 60 * 1000
    private const val BASE_TIME = 1_766_000_000_000L

    private fun rating(
        id: String,
        scrimId: String,
        from: String,
        to: String,
        captain: String,
        scores: Map<RatingCategory, Int>,
        comment: String,
        ageDays: Long
    ) = TeamRating(
        id = id,
        scrimId = scrimId,
        fromTeamId = from,
        toTeamId = to,
        captainName = captain,
        scores = scores,
        comment = comment,
        createdAt = BASE_TIME + ageDays * DAY_MS
    )

    /**
     * Builds a full set of scores for [count] ratings so every category ends with
     * the requested sum over 5-point ratings (9*s + 4*f == sum, f = 5*count - sum).
     */
    private fun seededScores(
        count: Int,
        sums: Map<RatingCategory, Int>
    ): List<Map<RatingCategory, Int>> {
        val base = List(count) { RatingCategory.entries.associateWith { 5 } }.map { it.toMutableMap() }
        RatingCategory.entries.forEachIndexed { categoryIndex, category ->
            val fours = count * 5 - (sums[category] ?: count * 5)
            repeat(fours) { k ->
                val target = (categoryIndex * 2 + k) % count
                base[target][category] = 4
            }
        }
        return base
    }

    private val eclipseComments = listOf(
        "Equipo muy respetuoso, llegaron puntuales y mantuvieron excelente comunicación. Recomendados para practicar.",
        "Rutina ordenada y trato impecable durante las tres mapas que jugamos.",
        "Cumplieron el horario al minuto y avisaron de cualquier cambio con tiempo.",
        "Poca charla previa, pero disciplinados y sin molestias durante la partida.",
        "Buen ambiente de juego: saludaron antes y después de cada mapa.",
        "Se tomaron en serio los acuerdos del scrim y cumplieron con el draft.",
        "Comunicación clara en el chat, resolvieron las dudas sin demoras.",
        "Nos avisaron con antelación de un retraso de diez minutos y ajustamos el horario.",
        "Actitud deportiva notable, aceptaron los resultados sin discusión.",
        "Preparados y puntuales, con propuestas claras para repetir la serie."
    )

    /** Ratings received by Eclipse Esports: sums 49/47/48/46/50 over 10 ratings. */
    private val eclipseRatings: List<TeamRating> = run {
        val sums = mapOf(
            RatingCategory.Respect to 49,
            RatingCategory.Seriousness to 47,
            RatingCategory.Punctuality to 48,
            RatingCategory.Communication to 46,
            RatingCategory.Sportsmanship to 50
        )
        val scrims = listOf(
            "scrim-ecl-1" to "team-aurora", "scrim-ecl-2" to "team-volt",
            "scrim-ecl-3" to "team-neon", "scrim-ecl-4" to "team-titan",
            "scrim-ecl-5" to "team-aurora", "scrim-ecl-6" to "team-volt",
            "scrim-ecl-7" to "team-neon", "scrim-ecl-8" to "team-titan",
            "scrim-ecl-9" to "team-aurora", "scrim-ecl-10" to "team-neon"
        )
        val captains = mapOf(
            "team-aurora" to "Vega#CL", "team-volt" to "Bia#BR",
            "team-neon" to "Axel#MX", "team-titan" to "Cole#NA"
        )
        seededScores(10, sums).mapIndexed { index, scores ->
            val (scrimId, from) = scrims[index]
            rating(
                id = "rating-ecl-${index + 1}",
                scrimId = scrimId,
                from = from,
                to = "team-eclipse",
                captain = captains.getValue(from),
                scores = scores,
                comment = eclipseComments[index],
                ageDays = (10 - index).toLong()
            )
        }
    }

    private val otherRatings = listOf(
        rating(
            "rating-aur-1", "scrim-demo-1", "team-eclipse", "team-aurora", "Riven#EU",
            mapOf(
                RatingCategory.Respect to 5, RatingCategory.Seriousness to 5,
                RatingCategory.Punctuality to 4, RatingCategory.Communication to 5,
                RatingCategory.Sportsmanship to 5
            ),
            "Muy buenos anfitriones, el acuerdo de horario se respetó al pie de la letra.",
            14
        ),
        rating(
            "rating-aur-2", "scrim-aur-1", "team-titan", "team-aurora", "Cole#NA",
            mapOf(
                RatingCategory.Respect to 5, RatingCategory.Seriousness to 4,
                RatingCategory.Punctuality to 5, RatingCategory.Communication to 4,
                RatingCategory.Sportsmanship to 5
            ),
            "Serie seria y ordenada; la charla antes del mapa fue breve pero clara.",
            9
        ),
        rating(
            "rating-aur-3", "scrim-aur-2", "team-neon", "team-aurora", "Axel#MX",
            mapOf(
                RatingCategory.Respect to 5, RatingCategory.Seriousness to 5,
                RatingCategory.Punctuality to 5, RatingCategory.Communication to 5,
                RatingCategory.Sportsmanship to 5
            ),
            "Referente de puntualidad: conectaron antes que nadie y avisaron de todo.",
            5
        ),
        rating(
            "rating-vlt-1", "scrim-vlt-1", "team-aurora", "team-volt", "Vega#CL",
            mapOf(
                RatingCategory.Respect to 5, RatingCategory.Seriousness to 5,
                RatingCategory.Punctuality to 5, RatingCategory.Communication to 4,
                RatingCategory.Sportsmanship to 5
            ),
            "Competitivos y respetuosos; la coordinación por chat fue rápida.",
            11
        ),
        rating(
            "rating-vlt-2", "scrim-vlt-2", "team-neon", "team-volt", "Axel#MX",
            mapOf(
                RatingCategory.Respect to 4, RatingCategory.Seriousness to 5,
                RatingCategory.Punctuality to 5, RatingCategory.Communication to 4,
                RatingCategory.Sportsmanship to 5
            ),
            "Buen trato general, aunque la charla previa se alargó más de lo previsto.",
            6
        ),
        rating(
            "rating-neo-1", "scrim-demo-4", "team-titan", "team-neon", "Cole#NA",
            mapOf(
                RatingCategory.Respect to 5, RatingCategory.Seriousness to 4,
                RatingCategory.Punctuality to 5, RatingCategory.Communication to 5,
                RatingCategory.Sportsmanship to 5
            ),
            "Actitud excelente durante toda la serie, sin discusiones ni excusas.",
            13
        ),
        rating(
            "rating-ttn-1", "scrim-ttn-1", "team-volt", "team-titan", "Bia#BR",
            mapOf(
                RatingCategory.Respect to 5, RatingCategory.Seriousness to 5,
                RatingCategory.Punctuality to 4, RatingCategory.Communication to 5,
                RatingCategory.Sportsmanship to 4
            ),
            "Equipo profesional; solo llegaron unos minutos tarde al primer mapa.",
            8
        )
    )

    val ratings: List<TeamRating> = eclipseRatings + otherRatings
}
