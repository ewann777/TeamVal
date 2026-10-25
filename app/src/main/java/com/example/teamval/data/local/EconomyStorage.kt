package com.example.teamval.data.local

import androidx.room.withTransaction
import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.MissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Persistencia de la economía detrás de una interfaz pequeña: el repositorio
 * ejecuta sus reglas de negocio dentro de [transaction] y así saldo,
 * inventario e historial se actualizan de forma atómica e consistente
 * (en Room es una transacción real; en memoria, un bloqueo).
 */
interface EconomyTx {
    suspend fun transactionCount(): Int
    suspend fun balance(): Int
    suspend fun insertTransaction(tx: CoinTransaction)
    suspend fun isOwned(itemId: String): Boolean
    suspend fun insertOwned(itemId: String, acquiredAt: Long)
    suspend fun missionStatus(missionId: String): MissionStatus?
    suspend fun saveMissionStatus(status: MissionStatus)
}

interface EconomyStorage {
    val transactions: Flow<List<CoinTransaction>>
    val ownedItems: Flow<List<String>>
    val missionStatuses: Flow<List<MissionStatus>>

    /** Ejecuta [block] de forma atómica y devuelve su resultado. */
    suspend fun <R> transaction(block: suspend EconomyTx.() -> R): R
}

/** Implementación de producción basada en Room. */
class RoomEconomyStorage(private val db: EconomyDatabase) : EconomyStorage {

    private val dao: EconomyDao get() = db.economyDao()

    override val transactions: Flow<List<CoinTransaction>> =
        dao.observeTransactions().map { rows -> rows.map { it.toDomain() } }

    override val ownedItems: Flow<List<String>> = dao.observeOwnedItems()

    override val missionStatuses: Flow<List<MissionStatus>> =
        dao.observeMissionStatuses().map { rows -> rows.map { it.toDomain() } }

    override suspend fun <R> transaction(block: suspend EconomyTx.() -> R): R =
        db.withTransaction { RoomTx(dao).block() }

    private class RoomTx(private val dao: EconomyDao) : EconomyTx {
        override suspend fun transactionCount(): Int = dao.transactionCount()
        override suspend fun balance(): Int = dao.balance()
        override suspend fun insertTransaction(tx: CoinTransaction) {
            dao.insertTransaction(
                CoinTransactionEntity(
                    id = tx.id,
                    amount = tx.amount,
                    kind = tx.kind.name,
                    timestamp = tx.timestamp,
                    description = tx.description
                )
            )
        }
        override suspend fun isOwned(itemId: String): Boolean = dao.ownedCount(itemId) > 0
        override suspend fun insertOwned(itemId: String, acquiredAt: Long) {
            dao.insertOwned(OwnedItemEntity(itemId = itemId, acquiredAt = acquiredAt))
        }
        override suspend fun missionStatus(missionId: String): MissionStatus? =
            dao.missionStatus(missionId)?.toDomain()
        override suspend fun saveMissionStatus(status: MissionStatus) {
            dao.saveMissionStatus(
                MissionStatusEntity(
                    missionId = status.missionId,
                    progress = status.progress,
                    claimed = status.claimed
                )
            )
        }
    }
}
