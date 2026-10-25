package com.example.teamval.data.fake

import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.BannerStyle
import com.example.teamval.domain.model.BuiltinLogo
import com.example.teamval.domain.model.GradientStyle
import com.example.teamval.domain.model.LogoFrame
import com.example.teamval.domain.model.LogoSymbol
import com.example.teamval.domain.model.Scrim
import com.example.teamval.domain.model.ScrimStatus
import com.example.teamval.domain.model.Team
import com.example.teamval.domain.model.TeamBadge
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.TeamLogo
import com.example.teamval.domain.model.TeamMember
import com.example.teamval.domain.model.TeamRole
import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.ValorantRank

internal object DemoTeamData {
    private fun squad(prefix: String, vararg members: Pair<String, ValorantRank>): List<TeamMember> =
        members.mapIndexed { index, (name, rank) ->
            TeamMember(
                id = "$prefix-${index + 1}",
                name = name,
                role = if (index == 0) TeamRole.Captain else TeamRole.Member,
                rank = rank
            )
        }

    /** Original demo branding so every rival team has its own logo and colors. */
    private fun customLogo(
        symbol: LogoSymbol,
        initials: String,
        primary: Long,
        secondary: Long,
        background: Long
    ) = TeamLogo(
        builtin = BuiltinLogo(
            symbol = symbol,
            initials = initials,
            primaryColor = primary,
            secondaryColor = secondary,
            gradient = GradientStyle.Linear,
            background = background,
            frame = LogoFrame.Glow
        )
    )

    private data class Branding(
        val logo: TeamLogo,
        val primary: Long,
        val secondary: Long,
        val banner: BannerStyle,
        val badges: Set<TeamBadge>
    )

    private val brands = mapOf(
        "team-eclipse" to Branding(
            customLogo(LogoSymbol.Hexagon, "ECL", 0xFF8B5CF6, 0xFF22D3EE, 0xFF171528),
            0xFF8B5CF6, 0xFF22D3EE, BannerStyle.Gradient,
            setOf(TeamBadge.FairPlay, TeamBadge.Organized)
        ),
        "team-aurora" to Branding(
            customLogo(LogoSymbol.Orbit, "AUR", 0xFFF472B6, 0xFFFBBF24, 0xFF1E1147),
            0xFFF472B6, 0xFFFBBF24, BannerStyle.Aurora,
            setOf(TeamBadge.Punctual, TeamBadge.Rising)
        ),
        "team-volt" to Branding(
            customLogo(LogoSymbol.Bolt, "VLT", 0xFFFACC15, 0xFF38BDF8, 0xFF201400),
            0xFFFACC15, 0xFF38BDF8, BannerStyle.Graphite,
            setOf(TeamBadge.Veteran)
        ),
        "team-neon" to Branding(
            customLogo(LogoSymbol.Diamond, "NEO", 0xFF34D399, 0xFF22D3EE, 0xFF052E2B),
            0xFF34D399, 0xFF22D3EE, BannerStyle.Grid,
            setOf(TeamBadge.FairPlay, TeamBadge.Communicative)
        ),
        "team-titan" to Branding(
            customLogo(LogoSymbol.Shield, "TTN", 0xFFF97316, 0xFFEF4444, 0xFF2A1005),
            0xFFF97316, 0xFFEF4444, BannerStyle.Gradient,
            setOf(TeamBadge.Veteran, TeamBadge.Communicative)
        )
    )

    /** Rival teams of the demo. The player starts without a team. */
    private val baseTeams = listOf(
        Team(
            id = "team-eclipse",
            name = "Eclipse Esports",
            tag = "ECL",
            region = Region.Europe,
            motto = "Compiten con respeto.",
            description = "Equipo europeo centrado en rutinas de scrims ordenadas y comunicación constante.",
            members = squad(
                "ecl",
                "Riven#EU" to ValorantRank.Immortal,
                "Nova#EU" to ValorantRank.Ascendant,
                "Kai#EU" to ValorantRank.Diamond,
                "Iris#EU" to ValorantRank.Diamond,
                "Zeus#EU" to ValorantRank.Platinum
            )
        ),
        Team(
            id = "team-aurora",
            name = "Aurora Clan",
            tag = "AUR",
            region = Region.LatamSouth,
            motto = "Subimos con calma.",
            description = "Clan chileno de práctica diaria, con foco en actitud y puntualidad.",
            members = squad(
                "aur",
                "Vega#CL" to ValorantRank.Ascendant,
                "Koko#CL" to ValorantRank.Diamond,
                "Lia#CL" to ValorantRank.Diamond,
                "Tito#CL" to ValorantRank.Platinum,
                "Mora#CL" to ValorantRank.Platinum
            )
        ),
        Team(
            id = "team-volt",
            name = "Volt Gaming",
            tag = "VLT",
            region = Region.Brazil,
            motto = "Energía en cada ronda.",
            description = "Organización brasileña con plantilla completa y horarios fijos de scrim.",
            members = squad(
                "vlt",
                "Bia#BR" to ValorantRank.Immortal,
                "Rafa#BR" to ValorantRank.Diamond,
                "Luna#BR" to ValorantRank.Diamond,
                "Theo#BR" to ValorantRank.Platinum,
                "Ana#BR" to ValorantRank.Platinum
            )
        ),
        Team(
            id = "team-neon",
            name = "Neon Spirits",
            tag = "NEO",
            region = Region.LatamNorth,
            motto = "Jugamos limpio.",
            description = "Equipo de México con fuerte enfoque en deportividad y buena comunicación.",
            members = squad(
                "neo",
                "Axel#MX" to ValorantRank.Diamond,
                "Paz#MX" to ValorantRank.Diamond,
                "Dian#MX" to ValorantRank.Platinum,
                "Sol#MX" to ValorantRank.Platinum,
                "Iker#MX" to ValorantRank.Silver
            )
        ),
        Team(
            id = "team-titan",
            name = "Titan Squad",
            tag = "TTN",
            region = Region.NorthAmerica,
            motto = "Rutina, enfoque, resultado.",
            description = "Escuadra norteamericana con experiencia en torneos comunitarios.",
            members = squad(
                "ttn",
                "Cole#NA" to ValorantRank.Immortal,
                "Jade#NA" to ValorantRank.Ascendant,
                "Will#NA" to ValorantRank.Diamond,
                "Emma#NA" to ValorantRank.Platinum,
                "Nick#NA" to ValorantRank.Platinum
            )
        )
    )

    /** Rival teams with their demo identity applied (logo, colors, banner, badges). */
    val teams = baseTeams.map { team ->
        val brand = brands[team.id] ?: return@map team
        team.copy(
            identity = team.identity.copy(
                logo = brand.logo,
                primaryColor = brand.primary,
                secondaryColor = brand.secondary,
                banner = brand.banner,
                badges = brand.badges
            )
        )
    }

    private fun completed(
        id: String, home: String, away: String, homeScore: Int, awayScore: Int, schedule: String
    ) = Scrim(
        id = id, homeTeamId = home, awayTeamId = away, mode = GameMode.Competitive,
        schedule = schedule, notes = "", status = ScrimStatus.Completed,
        homeScore = homeScore, awayScore = awayScore
    )

    /** Historic scrims between rival teams, used to fill team histories. */
    val scrims = listOf(
        Scrim("scrim-demo-1", "team-eclipse", "team-aurora", GameMode.Competitive,
            "Sábado 18:00", "Mejor de 3 mapas.", ScrimStatus.Completed, 13, 9),
        Scrim("scrim-demo-2", "team-volt", "team-eclipse", GameMode.Competitive,
            "Domingo 20:00", "Práctica de mapa corto.", ScrimStatus.Completed, 11, 13),
        Scrim("scrim-demo-3", "team-neon", "team-aurora", GameMode.Swiftplay,
            "Viernes 21:00", "Ronda de calentamiento.", ScrimStatus.Confirmed),
        Scrim("scrim-demo-4", "team-titan", "team-neon", GameMode.Competitive,
            "Miércoles 19:00", "Control de horarios.", ScrimStatus.Completed, 13, 11),
        Scrim("scrim-demo-5", "team-eclipse", "team-titan", GameMode.Unrated,
            "Lunes 22:00", "Cancelada por horario.", ScrimStatus.Cancelled),
        // Historic series that feed the demo reputation of Eclipse Esports.
        completed("scrim-ecl-1", "team-eclipse", "team-aurora", 13, 9, "Sábado 18:00"),
        completed("scrim-ecl-2", "team-volt", "team-eclipse", 11, 13, "Domingo 20:00"),
        completed("scrim-ecl-3", "team-eclipse", "team-neon", 13, 7, "Martes 19:30"),
        completed("scrim-ecl-4", "team-titan", "team-eclipse", 10, 13, "Jueves 21:00"),
        completed("scrim-ecl-5", "team-eclipse", "team-aurora", 13, 11, "Sábado 17:00"),
        completed("scrim-ecl-6", "team-eclipse", "team-volt", 9, 13, "Lunes 20:30"),
        completed("scrim-ecl-7", "team-neon", "team-eclipse", 13, 5, "Miércoles 18:00"),
        completed("scrim-ecl-8", "team-eclipse", "team-titan", 13, 8, "Viernes 22:00"),
        completed("scrim-ecl-9", "team-aurora", "team-eclipse", 11, 13, "Domingo 19:00"),
        completed("scrim-ecl-10", "team-eclipse", "team-neon", 13, 10, "Sábado 21:30"),
        Scrim("scrim-ecl-11", "team-titan", "team-eclipse", GameMode.Competitive,
            "Próximo sábado 20:00", "Serie por confirmar.", ScrimStatus.Confirmed),
        // Extra completed scrims that back the ratings of the other demo teams.
        completed("scrim-aur-1", "team-titan", "team-aurora", 13, 11, "Jueves 20:00"),
        completed("scrim-aur-2", "team-neon", "team-aurora", 9, 13, "Sábado 16:00"),
        completed("scrim-vlt-1", "team-aurora", "team-volt", 13, 10, "Martes 21:00"),
        completed("scrim-vlt-2", "team-volt", "team-neon", 13, 8, "Viernes 20:30"),
        completed("scrim-ttn-1", "team-volt", "team-titan", 11, 13, "Miércoles 22:00")
    )

    val incomingSchedules = listOf(
        "Mañana 20:00",
        "Sábado 19:00",
        "Domingo 18:30",
        "Viernes 21:00"
    )
}
