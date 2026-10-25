package com.example.teamval.data.repository

import com.example.teamval.data.local.EconomyStorage
import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.EconomyError
import com.example.teamval.domain.model.EconomyResult
import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.MissionKind
import com.example.teamval.domain.model.MissionStatus
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.domain.model.TransactionKind
import com.example.teamval.domain.repository.EconomyRepository
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Repositorio de la economía sobre [EconomyStorage]. Toda operación corre
 * serializada bajo un mutex y dentro de una transacción única del
 * almacenamiento: así se evitan saldos negativos, compras duplicadas y
 * recompensas reclamadas más de una vez aunque se pulse dos veces seguidas.
 *
 * El almacenamiento de producción es Room; los tests unitarios usan la
 * variante en memoria con la misma semántica.
 */
class LocalEconomyRepository(
    private val storage: EconomyStorage,
    override val catalog: List<ShopItem>,
    override val missions: List<Mission>,
    private val now: () -> Long = System::currentTimeMillis,
    private val newId: () -> String = { UUID.randomUUID().toString() }
) : EconomyRepository {
    private val mutex = Mutex()

    override val transactions: Flow<List<CoinTransaction>> = storage.transactions
    override val ownedItems: Flow<List<String>> = storage.ownedItems
    override val missionStatuses: Flow<List<MissionStatus>> = storage.missionStatuses

    override suspend fun initialize(startingBonus: Int): EconomyResult = mutex.withLock {
        if (startingBonus <= 0) return EconomyResult.Success
        storage.transaction {
            if (transactionCount() == 0) {
                insertTransaction(
                    CoinTransaction(
                        id = newId(),
                        amount = startingBonus,
                        kind = TransactionKind.StartingBonus,
                        timestamp = now(),
                        description = "Bono inicial de bienvenida"
                    )
                )
            }
            EconomyResult.Success
        }
    }

    override suspend fun purchase(item: ShopItem): EconomyResult = mutex.withLock {
        // Siempre se usa el artículo del catálogo: la interfaz nunca debe poder
        // inyectar precios o identificadores ajenos a la tienda.
        val catalogItem = catalog.find { it.id == item.id }
            ?: return EconomyResult.Failure(EconomyError.NotFound)
        val price = catalogItem.priceCoins
        if (catalogItem.isPremium || price == null || price <= 0) {
            return EconomyResult.Failure(EconomyError.InvalidOperation)
        }
        val contents = catalogItem.contains.map { id -> catalog.find { it.id == id } }
        if (contents.any { it == null }) return EconomyResult.Failure(EconomyError.NotFound)
        val pendingContents = contents.filterNotNull()

        storage.transaction {
            val alreadyOwned = isOwned(catalogItem.id)
            val missingContents = pendingContents.filterNot { isOwned(it.id) }
            // Un paquete ya completo no se puede volver a comprar.
            if (catalogItem.isBundle && alreadyOwned) return@transaction EconomyResult.Failure(EconomyError.AlreadyOwned)
            if (catalogItem.isBundle && missingContents.isEmpty()) {
                return@transaction EconomyResult.Failure(EconomyError.AlreadyOwned)
            }
            if (!catalogItem.isBundle && alreadyOwned) return@transaction EconomyResult.Failure(EconomyError.AlreadyOwned)
            if (balance() < price) return@transaction EconomyResult.Failure(EconomyError.InsufficientBalance)

            insertTransaction(
                CoinTransaction(
                    id = newId(),
                    amount = -price,
                    kind = TransactionKind.Purchase,
                    timestamp = now(),
                    description = "Compra: ${catalogItem.name}"
                )
            )
            insertOwned(catalogItem.id, now())
            missingContents.forEach { insertOwned(it.id, now()) }
            EconomyResult.Success
        }
    }

    override suspend fun claimReward(missionId: String, eligible: Boolean): EconomyResult = mutex.withLock {
        val mission = missions.find { it.id == missionId }
            ?: return EconomyResult.Failure(EconomyError.NotFound)
        if (mission.kind == MissionKind.VerificationPending) {
            return EconomyResult.Failure(EconomyError.MissionPending)
        }
        if (!eligible) return EconomyResult.Failure(EconomyError.MissionIncomplete)

        storage.transaction {
            val status = missionStatus(missionId)
            if (status?.claimed == true) return@transaction EconomyResult.Failure(EconomyError.MissionAlreadyClaimed)
            // El progreso de la demo se valida en la misma transacción que el
            // abono; un estado de UI desactualizado no puede conceder monedas.
            if (mission.kind == MissionKind.DemoProgress && (status?.progress ?: 0) < mission.target) {
                return@transaction EconomyResult.Failure(EconomyError.MissionIncomplete)
            }
            insertTransaction(
                CoinTransaction(
                    id = newId(),
                    amount = mission.rewardCoins,
                    kind = TransactionKind.Reward,
                    timestamp = now(),
                    description = "Recompensa: ${mission.title}"
                )
            )
            saveMissionStatus(MissionStatus(missionId = missionId, progress = mission.target, claimed = true))
            EconomyResult.Success
        }
    }

    override suspend fun simulateProgress(missionId: String, delta: Int): EconomyResult = mutex.withLock {
        val mission = missions.find { it.id == missionId }
            ?: return EconomyResult.Failure(EconomyError.NotFound)
        if (mission.kind != MissionKind.DemoProgress) {
            return EconomyResult.Failure(EconomyError.MissionPending)
        }
        if (delta !in 1..5) return EconomyResult.Failure(EconomyError.InvalidOperation)

        storage.transaction {
            val status = missionStatus(missionId)
            // Reclamada: progresar de nuevo no otorga nada más (idempotente).
            if (status?.claimed == true) return@transaction EconomyResult.Success
            val progress = ((status?.progress ?: 0) + delta).coerceAtMost(mission.target)
            saveMissionStatus(MissionStatus(missionId = missionId, progress = progress, claimed = false))
            EconomyResult.Success
        }
    }
}
