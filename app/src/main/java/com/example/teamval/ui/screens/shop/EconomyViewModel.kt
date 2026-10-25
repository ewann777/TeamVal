package com.example.teamval.ui.screens.shop

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamval.R
import com.example.teamval.domain.model.AppAppearance
import com.example.teamval.domain.model.CosmeticContext
import com.example.teamval.domain.model.EconomyError
import com.example.teamval.domain.model.EconomyResult
import com.example.teamval.domain.model.EquipTarget
import com.example.teamval.domain.model.ItemState
import com.example.teamval.domain.model.Mission
import com.example.teamval.domain.model.MissionKind
import com.example.teamval.domain.model.MissionRules
import com.example.teamval.domain.model.MissionStatus
import com.example.teamval.domain.model.PlayerProfile
import com.example.teamval.domain.model.ShopItem
import com.example.teamval.domain.model.TeamIdentity
import com.example.teamval.domain.model.WalletSnapshot
import com.example.teamval.domain.model.applyToAppearance
import com.example.teamval.domain.model.applyToProfile
import com.example.teamval.domain.model.isEquipped
import com.example.teamval.domain.model.shopLockedVariants
import com.example.teamval.domain.model.state
import com.example.teamval.domain.repository.EconomyRepository
import com.example.teamval.domain.repository.PreferencesRepository
import com.example.teamval.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Aviso efímero mostrado en la snackbar global de la aplicación. */
data class EconomyNotice(val id: Long, @param:StringRes @get:StringRes val textRes: Int, val arg: String? = null)

data class EconomyUiState(
    val wallet: WalletSnapshot = WalletSnapshot(),
    val owned: Set<String> = emptySet(),
    val missionStatuses: Map<String, MissionStatus> = emptyMap(),
    val profile: PlayerProfile = PlayerProfile(),
    val appearance: AppAppearance = AppAppearance(),
    val teamIdentity: TeamIdentity? = null,
    val lockedProfileVariants: Set<String> = emptySet(),
    val lockedTeamVariants: Set<String> = emptySet(),
    val ownedTeamCosmetics: List<ShopItem> = emptyList(),
    val unlockedItemId: String? = null,
    val notice: EconomyNotice? = null
) {
    fun cosmeticContext(): CosmeticContext = CosmeticContext(profile, appearance, teamIdentity)

    /** Estado de un artículo del catálogo para el jugador actual. */
    fun stateOf(item: ShopItem): ItemState {
        val equipped = item.binding?.isEquipped(cosmeticContext()) == true
        return item.state(owned, equipped)
    }

    fun missionStatusOf(mission: Mission): MissionStatus? = missionStatuses[mission.id]
}

/**
 * ViewModel de la etapa 5: monedero, tienda, inventario y recompensas.
 * Toda la lógica de compra/recompensa vive en el repositorio y toda la lógica
 * de equipado vive aquí; las pantallas solo renderizan y delegan.
 */
class EconomyViewModel(
    private val economy: EconomyRepository,
    private val preferences: PreferencesRepository,
    private val teams: TeamRepository,
    startingBonus: Int
) : ViewModel() {

    val catalog: List<ShopItem> get() = economy.catalog
    val missions: List<Mission> get() = economy.missions

    private var noticeCounter = 0L
    private val notice = MutableStateFlow<EconomyNotice?>(null)
    private val unlocked = MutableStateFlow<String?>(null)
    private val equipMutex = Mutex()

    private val core: Flow<EconomyUiState> = combine(
        combine(
            economy.transactions,
            economy.ownedItems,
            economy.missionStatuses
        ) { transactions, ownedRows, statusRows -> Triple(transactions, ownedRows, statusRows) },
        preferences.preferences,
        teams.snapshot
    ) { coreValues, prefs, teamSnapshot ->
        val (transactions, ownedRows, statusRows) = coreValues
        val owned = ownedRows.toSet()
        val myTeam = teamSnapshot.teams.find { it.captain?.id == teams.currentPlayer.id }
        EconomyUiState(
            wallet = WalletSnapshot(
                balance = transactions.sumOf { it.amount },
                earned = transactions.filter { it.amount > 0 }.sumOf { it.amount },
                spent = transactions.filter { it.amount < 0 }.sumOf { -it.amount },
                transactions = transactions
            ),
            owned = owned,
            missionStatuses = statusRows.associateBy { it.missionId },
            profile = prefs.profile,
            appearance = prefs.appearance,
            teamIdentity = myTeam?.identity,
            lockedProfileVariants = shopLockedVariants(economy.catalog, owned, EquipTarget.Profile),
            lockedTeamVariants = shopLockedVariants(economy.catalog, owned, EquipTarget.Team),
            ownedTeamCosmetics = economy.catalog.filter {
                it.binding?.target == EquipTarget.Team && it.id in owned
            }
        )
    }

    val uiState: StateFlow<EconomyUiState> = combine(core, notice, unlocked) { base, currentNotice, unlockedId ->
        base.copy(notice = currentNotice, unlockedItemId = unlockedId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EconomyUiState())

    init {
        launchOperation { economy.initialize(startingBonus) }
    }

    // --- Tienda ---------------------------------------------------------------

    fun purchase(item: ShopItem) {
        launchOperation {
            when (val result = economy.purchase(item)) {
                EconomyResult.Success -> {
                    unlocked.update { item.id }
                    notify(R.string.shop_buy_success, item.name)
                }
                is EconomyResult.Failure -> notifyError(result.error)
            }
        }
    }

    /** Equipa o desequipa un artículo sobre los editores existentes (3 y 4.1). */
    fun setEquipped(item: ShopItem, equip: Boolean) {
        launchOperation {
            equipMutex.withLock {
                val catalogItem = economy.catalog.find { it.id == item.id }
                val binding = catalogItem?.binding
                if (catalogItem == null || catalogItem.isPremium || binding == null ||
                    catalogItem.id !in economy.ownedItems.first()) {
                    notify(R.string.shop_error_not_owned)
                    return@withLock
                }
                val prefs = preferences.preferences.first()
                val team = teams.snapshot.value.teams.find { it.captain?.id == teams.currentPlayer.id }
                if (!equip && !binding.isEquipped(CosmeticContext(prefs.profile, prefs.appearance, team?.identity))) {
                    return@withLock
                }
                when (binding.target) {
                    EquipTarget.Profile -> {
                        preferences.saveProfile(binding.applyToProfile(prefs.profile, equip))
                        notify(if (equip) R.string.shop_equipped else R.string.shop_unequipped, catalogItem.name)
                    }
                    EquipTarget.Appearance -> {
                        preferences.saveAppearance(binding.applyToAppearance(prefs.appearance, equip))
                        notify(if (equip) R.string.shop_equipped else R.string.shop_unequipped, catalogItem.name)
                    }
                    EquipTarget.Team -> {
                        // Los cambios de equipo se confirman en el editor existente.
                        notify(if (team == null) R.string.shop_error_no_permission else R.string.shop_team_editor_required)
                    }
                }
            }
        }
    }

    fun equip(item: ShopItem) = setEquipped(item, equip = true)
    fun unequip(item: ShopItem) = setEquipped(item, equip = false)

    // --- Recompensas ----------------------------------------------------------

    fun claim(mission: Mission) {
        launchOperation {
            val current = economy.missions.find { it.id == mission.id } ?: return@launchOperation
            val profile = preferences.preferences.first().profile
            val status = economy.missionStatuses.first().find { it.missionId == current.id }
            val eligible = MissionRules.isComplete(current, profile, status)
            when (val result = economy.claimReward(current.id, eligible)) {
                EconomyResult.Success -> notify(R.string.shop_reward_claimed, current.rewardCoins.toString())
                is EconomyResult.Failure -> notifyError(result.error)
            }
        }
    }

    fun simulate(mission: Mission) {
        launchOperation {
            val before = uiState.value.missionStatusOf(mission)
            val willComplete = mission.kind == MissionKind.DemoProgress &&
                (before?.progress ?: 0) < mission.target &&
                (before?.progress ?: 0) + 1 >= mission.target &&
                before?.claimed != true
            when (val result = economy.simulateProgress(mission.id, delta = 1)) {
                EconomyResult.Success -> if (willComplete) notify(R.string.shop_mission_ready, mission.title)
                is EconomyResult.Failure -> notifyError(result.error)
            }
        }
    }

    // --- Noticias y animaciones ----------------------------------------------

    fun dismissNotice(id: Long) {
        notice.update { current -> if (current?.id == id) null else current }
    }

    fun clearUnlocked() {
        unlocked.update { null }
    }

    private fun notify(@StringRes textRes: Int, arg: String? = null) {
        val id = ++noticeCounter
        notice.update { EconomyNotice(id, textRes, arg) }
    }

    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                notify(R.string.shop_error_generic)
            }
        }
    }

    private fun notifyError(error: EconomyError) {
        val res = when (error) {
            EconomyError.InsufficientBalance -> R.string.shop_error_no_balance
            EconomyError.AlreadyOwned -> R.string.shop_error_owned
            EconomyError.MissionAlreadyClaimed -> R.string.shop_reward_already
            EconomyError.MissionIncomplete -> R.string.shop_reward_incomplete
            EconomyError.MissionPending -> R.string.shop_reward_pending
            EconomyError.NotFound, EconomyError.InvalidOperation -> R.string.shop_error_generic
        }
        notify(res)
    }
}
