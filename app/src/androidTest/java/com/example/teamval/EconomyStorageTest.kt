package com.example.teamval

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.teamval.data.fake.DemoRewardData
import com.example.teamval.data.fake.DemoShopCatalog
import com.example.teamval.data.local.EconomyDatabase
import com.example.teamval.data.local.RoomEconomyStorage
import com.example.teamval.data.repository.LocalEconomyRepository
import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.EconomyError
import com.example.teamval.domain.model.EconomyResult
import com.example.teamval.domain.model.TransactionKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verifica Room real: durabilidad, concurrencia y rollback, no solo la demo en memoria. */
class EconomyStorageTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun repository(db: EconomyDatabase) = LocalEconomyRepository(
        RoomEconomyStorage(db), DemoShopCatalog.items, DemoRewardData.missions
    )

    @Test
    fun purchasesAndRewardsSurviveDatabaseReopenWithoutAnotherBonus() = runBlocking {
        val name = "economy-persistence-test.db"
        context.deleteDatabase(name)
        var db = Room.databaseBuilder(context, EconomyDatabase::class.java, name).build()
        try {
            var repo = repository(db)
            repo.initialize(500)
            assertEquals(EconomyResult.Success, repo.purchase(DemoShopCatalog.find("sticker_gg")!!))
            repo.simulateProgress("m_event", 1)
            assertEquals(EconomyResult.Success, repo.claimReward("m_event", true))
            db.close()
            db = Room.databaseBuilder(context, EconomyDatabase::class.java, name).build()
            repo = repository(db)
            repo.initialize(500)
            assertEquals(590, repo.transactions.first().sumOf { it.amount })
            assertEquals(3, repo.transactions.first().size)
            assertEquals(listOf("sticker_gg"), repo.ownedItems.first())
            assertTrue(repo.missionStatuses.first().single().claimed)
            assertEquals(EconomyResult.Failure(EconomyError.MissionAlreadyClaimed), repo.claimReward("m_event", true))
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun roomSerializesPurchasesAndRollsBackPartialWrites() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, EconomyDatabase::class.java).build()
        try {
            val repo = repository(db)
            val secondRepo = repository(db)
            repo.initialize(500)
            val item = DemoShopCatalog.find("frame_lavanda")!!
            val first = async(Dispatchers.Default) { repo.purchase(item) }
            val second = async(Dispatchers.Default) { secondRepo.purchase(item) }
            val results = listOf(first.await(), second.await())
            assertEquals(1, results.count { it == EconomyResult.Success })
            assertEquals(1, results.count { it == EconomyResult.Failure(EconomyError.AlreadyOwned) })
            try {
                RoomEconomyStorage(db).transaction {
                    insertTransaction(CoinTransaction("rollback", -150, TransactionKind.Purchase, 1, "Fallida"))
                    insertOwned("bg_pastel", 1)
                    error("Fallo antes del commit")
                }
            } catch (_: IllegalStateException) {
                // Room debe deshacer ambas escrituras.
            }
            assertEquals(400, repo.transactions.first().sumOf { it.amount })
            assertEquals(2, repo.transactions.first().size)
            assertEquals(listOf("frame_lavanda"), repo.ownedItems.first())
        } finally {
            db.close()
        }
    }
}
