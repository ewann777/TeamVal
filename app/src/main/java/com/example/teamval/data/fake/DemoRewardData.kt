package com.example.teamval.data.fake

import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.MissionKind

/**
 * Recompensas gratuitas de la etapa 5.
 *
 * - [MissionKind.ProfileCompletion] se evalúa con el perfil real del jugador.
 * - [MissionKind.VerificationPending] queda bloqueada hasta existir un sistema
 *   de verificación de sesiones y valoraciones (no se simula como completada).
 * - [MissionKind.DemoProgress] es una misión ficticia con botón
 *   "Simular progreso" para poder probar la economía.
 *
 * Ninguna recompensa premia puntuaciones positivas ni permite manipular la
 * reputación.
 */
object DemoRewardData {

    val missions: List<Mission> = listOf(
        Mission(
            id = "m_profile",
            title = "Completar el perfil",
            description = "Personaliza tu jugador: nombre, titular o biografía.",
            rewardCoins = 100,
            target = 1,
            kind = MissionKind.ProfileCompletion
        ),
        Mission(
            id = "m_sessions",
            title = "Tres sesiones verificadas",
            description = "Completa tres sesiones de juego verificadas. Requiere verificación.",
            rewardCoins = 50,
            target = 3,
            kind = MissionKind.VerificationPending
        ),
        Mission(
            id = "m_ratings",
            title = "Cinco valoraciones verificadas",
            description = "Recibe cinco valoraciones verificadas de la comunidad. Requiere verificación.",
            rewardCoins = 100,
            target = 5,
            kind = MissionKind.VerificationPending
        ),
        Mission(
            id = "m_scrims",
            title = "Actividad comunitaria: 3 scrims",
            description = "Misión ficticia de demostración. Avanza tu progreso con Simular progreso.",
            rewardCoins = 50,
            target = 3,
            kind = MissionKind.DemoProgress
        ),
        Mission(
            id = "m_share",
            title = "Comparte tu perfil 5 veces",
            description = "Misión ficticia de demostración. Avanza tu progreso con Simular progreso.",
            rewardCoins = 75,
            target = 5,
            kind = MissionKind.DemoProgress
        ),
        Mission(
            id = "m_event",
            title = "Semana de bienvenida",
            description = "Actividad especial ficticia de la etapa de demostración.",
            rewardCoins = 150,
            target = 1,
            kind = MissionKind.DemoProgress
        )
    )

    fun find(id: String): Mission? = missions.find { it.id == id }
}
