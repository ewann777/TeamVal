package com.example.teamval.data.repository

import com.example.teamval.data.local.EconomyStorage
import com.example.teamval.data.local.EconomyTx
import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.MissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Almacenamiento de la economía en memoria: misma semántica atómica que Room
 * (el bloqueo serializa las transacciones) pero sin Android, para poder probar
 * todas las reglas de compra y recompensas como unit tests.
 */
class InMemoryEconomyStorage : EconomyStorage {
    private val mutex = Mutex()
    private data class Snapshot(
        val transactions: List<CoinTransaction> = emptyList(),
        val owned: List<String> = emptyList(),
        val missions: List<MissionStatus> = emptyList()
    )
    private val state = MutableStateFlow(Snapshot())

    override val transactions = state.map { it.transactions }
    override val ownedItems = state.map { it.owned }
    override val missionStatuses = state.map { it.missions }

    override suspend fun <R> transaction(block: suspend EconomyTx.() -> R): R = mutex.withLock {
        val tx = MemoryTx(state.value)
        val result = tx.block()
        state.value = tx.snapshot
        result
    }

    private class MemoryTx(var snapshot: Snapshot) : EconomyTx {
        override suspend fun transactionCount(): Int = snapshot.transactions.size

        override suspend fun balance(): Int = snapshot.transactions.sumOf { it.amount }

        override suspend fun insertTransaction(tx: CoinTransaction) {
            check(snapshot.transactions.none { it.id == tx.id }) { "Transacción duplicada ${tx.id}" }
            snapshot = snapshot.copy(transactions = listOf(tx) + snapshot.transactions)
        }

        override suspend fun isOwned(itemId: String): Boolean = itemId in snapshot.owned

        override suspend fun insertOwned(itemId: String, acquiredAt: Long) {
            if (itemId in snapshot.owned) return
            snapshot = snapshot.copy(owned = snapshot.owned + itemId)
        }

        override suspend fun missionStatus(missionId: String): MissionStatus? =
            snapshot.missions.find { it.missionId == missionId }

        override suspend fun saveMissionStatus(status: MissionStatus) {
            snapshot = snapshot.copy(missions = snapshot.missions.filterNot { it.missionId == status.missionId } + status)
        }
    }
}
