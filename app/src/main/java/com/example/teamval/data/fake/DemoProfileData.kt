package com.example.teamval.data.fake

import com.example.teamval.domain.model.PlayerReview
import com.example.teamval.domain.model.Reputation
import com.example.teamval.domain.model.ValorantRank
import com.example.teamval.domain.model.ValorantStats

object DemoProfileData {
    val stats = ValorantStats(matches = 120, wins = 72, kd = 1.24, headshotPercent = 27, rank = ValorantRank.Gold)
    val reputation = Reputation(
        reviews = listOf(
            PlayerReview("Luna#LAS", 5, "Muy mala comunicación. Siempre baitea al equipo y roba la calma."),
            PlayerReview("Echo#CL", 5, "Un gusto jugar juntos. Coordina las rondas y comparte buenas ideas."),
            PlayerReview("Kira#VAL", 4, "Buen compañero, con ganas de aprender y mejorar en cada partida.")
        ),
        communication = 96, teamwork = 94, attitude = 98
    )
}
