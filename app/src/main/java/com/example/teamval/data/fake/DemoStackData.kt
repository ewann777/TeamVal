package com.example.teamval.data.fake

import com.example.teamval.domain.model.GameMode
import com.example.teamval.domain.model.JoinRequest
import com.example.teamval.domain.model.Player
import com.example.teamval.domain.model.Region
import com.example.teamval.domain.model.StackPost
import com.example.teamval.domain.model.StackSnapshot
import com.example.teamval.domain.model.ValorantRank

internal object DemoStackData {
    val currentPlayer = Player("demo-me", "Nova#TEAM", ValorantRank.Gold)
    val candidates = listOf(
        Player("candidate-1", "Luna#LAS", ValorantRank.Gold),
        Player("candidate-2", "Echo#CL", ValorantRank.Platinum),
        Player("candidate-3", "Kira#VAL", ValorantRank.Silver),
        Player("candidate-4", "Atlas#GG", ValorantRank.Diamond),
        Player("candidate-5", "Sora#PLAY", ValorantRank.Gold)
    )

    fun snapshot(): StackSnapshot {
        val posts = listOf(
            StackPost(
                "demo-duo", Player("host-1", "Vega#SUR", ValorantRank.Gold),
                "Un dúo con buena comunicación", "Busco compañía para ranked. Jugamos con calma, micrófono y ganas de mejorar.",
                ValorantRank.Gold, Region.LatamSouth, GameMode.Competitive, 2
            ),
            StackPost(
                "demo-night", Player("host-2", "Nox#NORTE", ValorantRank.Platinum),
                "Rankeds de noche, cero tilt", "Nos faltan dos para completar el equipo. Buen ambiente y comunicación en español.",
                ValorantRank.Platinum, Region.LatamNorth, GameMode.Competitive, 5,
                setOf("host-2", "friend-1", "friend-2")
            ),
            StackPost(
                "demo-casual", Player("host-3", "Mika#EU", ValorantRank.Silver),
                "Unas partidas sin presión", "Aprendemos agentes nuevos y compartimos buenas partidas. Todos los roles son bienvenidos.",
                ValorantRank.Silver, Region.Europe, GameMode.Unrated, 5
            ),
            StackPost(
                "demo-swift", Player("host-4", "Sol#BR", ValorantRank.Unranked),
                "Poco tiempo, buenas partidas", "Unas rápidas para desconectar. No importa el nivel, solo las ganas de jugar juntos.",
                ValorantRank.Unranked, Region.Brazil, GameMode.Swiftplay, 3
            ),
            StackPost(
                "demo-full", Player("host-5", "Raven#NA", ValorantRank.Diamond),
                "Equipo de práctica", "Sesión para practicar comunicación y coordinación. Este grupo ya está completo.",
                ValorantRank.Diamond, Region.NorthAmerica, GameMode.Competitive, 2,
                setOf("host-5", "friend-3")
            ),
            StackPost(
                "demo-owned", currentPlayer,
                "Mi equipo para esta noche", "Busco compañeros con buena actitud. Coordinamos roles y aprendemos en equipo.",
                ValorantRank.Gold, Region.LatamSouth, GameMode.Competitive, 3
            )
        )
        return StackSnapshot(
            posts = posts,
            requests = listOf(
                JoinRequest("demo-request-1", "demo-owned", candidates[0]),
                JoinRequest("demo-request-2", "demo-owned", candidates[1])
            )
        )
    }
}
