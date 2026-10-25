package com.example.teamval.domain.repository

import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.EconomyResult
import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.MissionStatus
import com.example.teamval.domain.model.ShopItem
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de la economía virtual de la etapa 5.
 *
 * Las monedas son ficticias: sin valor real, no transferibles y sin
 * conversión a dinero. Todas las compras deben ser atómicas y consistentes.
 */
interface EconomyRepository {
    val catalog: List<ShopItem>
    val missions: List<Mission>

    val transactions: Flow<List<CoinTransaction>>
    val ownedItems: Flow<List<String>>
    val missionStatuses: Flow<List<MissionStatus>>

    /** Otorga el bono inicial una sola vez (idempotente). */
    suspend fun initialize(startingBonus: Int): EconomyResult

    /** Compra un artículo (o paquete) descontando saldo, sin duplicados. */
    suspend fun purchase(item: ShopItem): EconomyResult

    /** Reclama una recompensa ya completada; nunca dos veces. */
    suspend fun claimReward(missionId: String, eligible: Boolean): EconomyResult

    /** Avanza el progreso de una misión de demostración (botón "Simular progreso"). */
    suspend fun simulateProgress(missionId: String, delta: Int): EconomyResult
}
