package com.example.teamval.data.repository

import com.example.teamval.data.fake.DemoRewardData
import com.example.teamval.data.fake.DemoShopCatalog
import com.example.teamval.domain.model.EconomyError
import com.example.teamval.domain.model.EconomyResult
import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.domain.model.TransactionKind
import com.example.teamval.domain.model.state
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reglas de la economía de la etapa 5: bono inicial, compras atómicas,
 * sin saldos negativos, sin compras duplicadas y recompensas de un solo uso.
 */
class LocalEconomyRepositoryTest {

    private val clock = AtomicLong(1_000L)
    private val ids = AtomicInteger()

    private fun newRepository(
        storage: InMemoryEconomyStorage = InMemoryEconomyStorage(),
        catalog: List<ShopItem> = DemoShopCatalog.items,
        missions: List<Mission> = DemoRewardData.missions
    ) = LocalEconomyRepository(
        storage = storage,
        catalog = catalog,
        missions = missions,
        now = { clock.incrementAndGet() },
        newId = { "tx-${ids.incrementAndGet()}" }
    )

    private fun EconomyResult.error(): EconomyError = (this as EconomyResult.Failure).error

    private fun LocalEconomyRepository.txs() = runBlocking { transactions.first() }
    private fun LocalEconomyRepository.owned(): Set<String> = runBlocking { ownedItems.first().toSet() }
    private fun LocalEconomyRepository.balance() = txs().sumOf { it.amount }

    private fun item(id: String) = DemoShopCatalog.find(id) ?: error("Artículo desconocido $id")

    private fun failure(error: EconomyError) = EconomyResult.Failure(error)

    // --- Bono inicial ---------------------------------------------------------
    @Test
    fun initializeGrantsStartingBonusOnlyOnce() = runBlocking {
        val repo = newRepository()
        assertEquals(EconomyResult.Success, repo.initialize(DemoShopCatalog.StartingBonus))
        assertEquals(EconomyResult.Success, repo.initialize(DemoShopCatalog.StartingBonus))

        val transactions = repo.txs()
        assertEquals(1, transactions.size)
        assertEquals(500, transactions[0].amount)
        assertEquals(TransactionKind.StartingBonus, transactions[0].kind)
        assertEquals(500, repo.balance())
    }

    // --- Compras --------------------------------------------------------------
    @Test
    fun purchaseDeductsBalanceAndGrantsItem() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        val result = repo.purchase(item("frame_lavanda"))
        assertEquals(EconomyResult.Success, result)

        assertTrue("frame_lavanda debe quedar en el inventario", "frame_lavanda" in repo.owned())
        assertEquals(400, repo.balance())

        val purchase = repo.txs().first { it.kind == TransactionKind.Purchase }
        assertEquals(-100, purchase.amount)
        assertEquals("Compra: ${item("frame_lavanda").name}", purchase.description)
    }

    @Test
    fun purchaseRejectsInsufficientBalanceWithoutSideEffects() = runBlocking {
        val repo = newRepository()
        assertEquals(EconomyResult.Success, repo.initialize(500))
        // Agota el saldo con artículos caros (400 + 100 = 500).
        assertEquals(EconomyResult.Success, repo.purchase(item("pet_bruno")))
        assertEquals(EconomyResult.Success, repo.purchase(item("frame_lavanda")))
        assertEquals(0, repo.balance())

        val blocked = repo.purchase(item("sticker_gg"))
        assertEquals(EconomyError.InsufficientBalance, blocked.error())
        assertFalse("sticker_gg no debe concederse", "sticker_gg" in repo.owned())
        assertEquals(0, repo.balance())
        assertEquals(3, repo.txs().size)
    }

    @Test
    fun purchaseRejectsDuplicates() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        assertEquals(EconomyResult.Success, repo.purchase(item("frame_lavanda")))
        assertEquals(failure(EconomyError.AlreadyOwned), repo.purchase(item("frame_lavanda")))
        assertEquals(400, repo.balance())
        assertEquals(1, repo.txs().count { it.kind == TransactionKind.Purchase })
    }

    @Test
    fun bundleGrantsEveryContainedItemExactlyOnce() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        assertEquals(EconomyResult.Success, repo.purchase(item("bundle_inicio")))
        val owned = repo.owned()
        listOf("bundle_inicio", "frame_lavanda", "bg_pastel", "badge_estrella").forEach {
            assertTrue("$it debe estar en el inventario", it in owned)
        }
        assertEquals(0, repo.balance())
        assertEquals(failure(EconomyError.AlreadyOwned), repo.purchase(item("bundle_inicio")))
        assertEquals(0, repo.balance())
    }

    @Test
    fun bundleIsRejectedWhenContentsAlreadyOwned() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        repo.purchase(item("frame_lavanda"))
        repo.purchase(item("bg_pastel"))
        repo.purchase(item("badge_estrella"))

        val result = repo.purchase(item("bundle_inicio"))
        assertEquals(EconomyError.AlreadyOwned, result.error())
        assertEquals(170, repo.balance())
    }

    @Test
    fun balanceNeverGoesNegative() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        // Intenta comprar todo el catálogo en orden: nunca debe quedar en negativo.
        DemoShopCatalog.items.forEach { candidate ->
            if (!candidate.isPremium) repo.purchase(candidate)
        }
        assertTrue("El saldo no puede ser negativo", repo.balance() >= 0)
        assertTrue(repo.txs().all { it.amount > 0 || repo.balance() >= 0 })
    }

    @Test
    fun simultaneousPurchasesChargeOnlyOnce() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        val first = async(Dispatchers.Default) { repo.purchase(item("frame_lavanda")) }
        val second = async(Dispatchers.Default) { repo.purchase(item("frame_lavanda")) }
        val results = listOf(first.await(), second.await())

        assertEquals(1, results.count { it is EconomyResult.Success })
        assertEquals(1, results.count { it.errorOrNull() == EconomyError.AlreadyOwned })
        assertEquals(1, repo.txs().count { it.kind == TransactionKind.Purchase })
        assertEquals(400, repo.balance())
    }

    @Test
    fun everyTransactionHasUniqueIdAndDescription() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        repo.purchase(item("sticker_gg"))
        repo.purchase(item("badge_estrella"))

        val transactions = repo.txs()
        assertEquals(transactions.size, transactions.map { it.id }.toSet().size)
        assertTrue(transactions.all { it.description.isNotBlank() })
        assertTrue(transactions.all { it.timestamp > 0L })
    }

    @Test
    fun walletSumsTrackEarnedAndSpent() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        repo.purchase(item("frame_lavanda"))
        repo.claimReward("m_profile", eligible = true)

        val transactions = repo.txs()
        assertEquals(600, transactions.filter { it.amount > 0 }.sumOf { it.amount })
        assertEquals(100, transactions.filter { it.amount < 0 }.sumOf { -it.amount })
        assertEquals(500, repo.balance())
    }

    // --- Recompensas ----------------------------------------------------------
    @Test
    fun claimRewardPaysOnceAndRejectsSecondClaim() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        assertEquals(EconomyResult.Success, repo.claimReward("m_profile", eligible = true))
        assertEquals(600, repo.balance())
        assertEquals(failure(EconomyError.MissionAlreadyClaimed), repo.claimReward("m_profile", eligible = true))
        assertEquals(600, repo.balance())
        assertEquals(1, repo.txs().count { it.kind == TransactionKind.Reward })

        val statuses = repo.missionStatuses.first()
        assertTrue(statuses.first { it.missionId == "m_profile" }.claimed)
    }

    @Test
    fun claimRejectsIncompleteMission() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        val result = repo.claimReward("m_profile", eligible = false)
        assertEquals(EconomyError.MissionIncomplete, result.error())
        assertEquals(500, repo.balance())
        assertEquals(1, repo.txs().size)
    }

    @Test
    fun verificationMissionsAreNeverClaimable() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        listOf("m_sessions", "m_ratings").forEach { missionId ->
            val result = repo.claimReward(missionId, eligible = true)
            assertEquals(EconomyError.MissionPending, result.error())
        }
        assertEquals(500, repo.balance())
    }

    @Test
    fun unknownMissionReturnsNotFound() = runBlocking {
        val repo = newRepository()
        assertEquals(EconomyError.NotFound, repo.claimReward("no_existe", eligible = true).error())
        assertEquals(EconomyError.NotFound, repo.simulateProgress("no_existe", 1).error())
        assertEquals(EconomyError.NotFound, repo.purchase(item("frame_lavanda").copy(id = "no_existe")).error())
    }

    @Test
    fun simulateProgressAccumulatesCapsAndStopsAfterClaim() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)

        repeat(5) { assertEquals(EconomyResult.Success, repo.simulateProgress("m_scrims", 1)) }
        var status = repo.missionStatuses.first().first { it.missionId == "m_scrims" }
        assertEquals("El progreso debe quedar topeado en el objetivo", 3, status.progress)
        assertFalse(status.claimed)

        assertEquals(EconomyResult.Success, repo.claimReward("m_scrims", eligible = true))
        assertEquals(550, repo.balance())

        // Progresar tras reclamar no otorga más monedas.
        assertEquals(EconomyResult.Success, repo.simulateProgress("m_scrims", 1))
        assertEquals(550, repo.balance())
        status = repo.missionStatuses.first().first { it.missionId == "m_scrims" }
        assertEquals(3, status.progress)
        assertTrue(status.claimed)

        assertEquals(failure(EconomyError.MissionAlreadyClaimed), repo.claimReward("m_scrims", eligible = true))
        assertEquals(550, repo.balance())
    }

    @Test
    fun simulateRejectsNonDemoMissionsAndBadDelta() = runBlocking {
        val repo = newRepository()
        assertEquals(EconomyError.MissionPending, repo.simulateProgress("m_profile", 1).error())
        assertEquals(EconomyError.MissionPending, repo.simulateProgress("m_sessions", 1).error())
        assertEquals(EconomyError.InvalidOperation, repo.simulateProgress("m_scrims", 0).error())
        assertEquals(EconomyError.InvalidOperation, repo.simulateProgress("m_scrims", 99).error())
    }

    @Test
    fun premiumItemsCannotBePurchased() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        val premium = item("prem_frame_animado")
        assertEquals(EconomyError.InvalidOperation, repo.purchase(premium).error())
        assertEquals(500, repo.balance())
        assertFalse("prem_frame_animado" in repo.owned())
    }

    @Test
    fun itemStateReflectsOwnershipAndEquip() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        val frame = item("frame_lavanda")

        assertEquals(
            com.example.teamval.domain.model.ItemState.Available,
            frame.state(repo.owned(), equipped = false)
        )
        repo.purchase(frame)
        assertEquals(
            com.example.teamval.domain.model.ItemState.Owned,
            frame.state(repo.owned(), equipped = false)
        )
        assertEquals(
            com.example.teamval.domain.model.ItemState.Equipped,
            frame.state(repo.owned(), equipped = true)
        )
        assertEquals(
            com.example.teamval.domain.model.ItemState.Blocked,
            item("prem_frame_animado").state(repo.owned(), equipped = false)
        )
        assertEquals(
            com.example.teamval.domain.model.ItemState.Owned,
            item("bundle_inicio").state(setOf("frame_lavanda", "bg_pastel", "badge_estrella"), equipped = false)
        )
    }

    @Test
    fun profileCompletionMissionMatchesRealProfileState() {
        val mission = DemoRewardData.find("m_profile") ?: error("m_profile ausente")
        assertFalse(com.example.teamval.domain.model.MissionRules.isComplete(mission, PlayerProfile(), null))
        assertTrue(
            com.example.teamval.domain.model.MissionRules.isComplete(
                mission,
                PlayerProfile(headline = "Titular personalizado"),
                null
            )
        )
    }

    @Test
    fun purchaseUsesCatalogPriceInsteadOfCallerPrice() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        assertEquals(EconomyResult.Success, repo.purchase(item("frame_lavanda").copy(priceCoins = 1)))
        assertEquals(400, repo.balance())
    }

    @Test
    fun demoRewardRequiresPersistedProgressEvenWhenCallerSaysEligible() = runBlocking {
        val repo = newRepository()
        repo.initialize(500)
        assertEquals(failure(EconomyError.MissionIncomplete), repo.claimReward("m_scrims", true))
        repo.simulateProgress("m_scrims", 2)
        assertEquals(failure(EconomyError.MissionIncomplete), repo.claimReward("m_scrims", true))
        assertEquals(500, repo.balance())
        assertEquals(1, repo.txs().size)
    }

    @Test
    fun concurrentClaimsAcrossRepositoriesPayOnce() = runBlocking {
        val storage = InMemoryEconomyStorage()
        val firstRepo = newRepository(storage)
        val secondRepo = newRepository(storage)
        firstRepo.initialize(500)
        firstRepo.simulateProgress("m_event", 1)
        val first = async(Dispatchers.Default) { firstRepo.claimReward("m_event", true) }
        val second = async(Dispatchers.Default) { secondRepo.claimReward("m_event", true) }
        val results = listOf(first.await(), second.await())
        assertEquals(1, results.count { it == EconomyResult.Success })
        assertEquals(1, results.count { it == failure(EconomyError.MissionAlreadyClaimed) })
        assertEquals(650, firstRepo.balance())
    }

    @Test
    fun memoryTransactionRollsBackAllWritesOnFailure() = runBlocking {
        val storage = InMemoryEconomyStorage()
        val repo = newRepository(storage)
        repo.initialize(500)
        try {
            storage.transaction {
                insertTransaction(com.example.teamval.domain.model.CoinTransaction("failed", -100, TransactionKind.Purchase, 1, "Fallida"))
                insertOwned("frame_lavanda", 1)
                error("Fallo antes del commit")
            }
        } catch (_: IllegalStateException) {
            // El snapshot publicado sigue siendo el anterior.
        }
        assertEquals(500, repo.balance())
        assertTrue(repo.owned().isEmpty())
        assertEquals(1, repo.txs().size)
    }

    private fun EconomyResult.errorOrNull(): EconomyError? = (this as? EconomyResult.Failure)?.error
}
