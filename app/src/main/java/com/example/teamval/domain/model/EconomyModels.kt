package com.example.teamval.domain.model

/**
 * Etapa 5: economía virtual de TeamVal (TeamVal Coins).
 *
 * Las monedas son ficticias: no tienen valor monetario real, no son
 * transferibles y no pueden convertirse en dinero. Los precios premium en CLP
 * son informativos y están bloqueados hasta que exista una pasarela de pago
 * real (fuera del alcance de esta etapa).
 */

/** Tipo de movimiento del monedero. */
enum class TransactionKind { StartingBonus, Reward, Purchase }

/**
 * Movimiento único del monedero. [amount] positivo suma y negativo descuenta;
 * el saldo nunca puede quedar en negativo.
 */
data class CoinTransaction(
    val id: String,
    val amount: Int,
    val kind: TransactionKind,
    val timestamp: Long,
    val description: String
)

/** Saldo y historial del monedero, derivados del registro de movimientos. */
data class WalletSnapshot(
    val balance: Int = 0,
    val earned: Int = 0,
    val spent: Int = 0,
    val transactions: List<CoinTransaction> = emptyList()
) {
    /** Recompensas y bonos recibidos (todo lo que no es una compra). */
    val received: List<CoinTransaction> get() = transactions.filter { it.kind != TransactionKind.Purchase }

    fun canAfford(price: Int): Boolean = price in 1..balance
}

enum class ItemCategory {
    AvatarFrame, ProfileBackground, Banner, Badge, Sticker, Pet,
    PlayerCard, TeamDecoration, TeamLogoFrame, VisualTheme, Bundle
}

/** Rareza exclusivamente decorativa: nunca otorga ventajas competitivas. */
enum class ItemRarity { Common, Rare, Epic, Legendary }

/** Dónde aplica un cosmético: perfil personal, apariencia global o equipo. */
enum class EquipTarget { Profile, Team, Appearance }

/** Campo concreto que un cosmético sobreescribe cuando se equipa. */
enum class EquipField {
    AvatarFrame, ProfileBackground, ProfileBanner, ProfileSticker, ProfilePet,
    PlayerCard, ProfileBadge, TeamFrame, TeamBackground, TeamBadge, VisualTheme
}

/**
 * Vínculo entre un artículo de la tienda y el personalizador existente
 * (etapas 3 y 4.1). [variant] es el nombre del valor de enum que se aplica.
 */
data class CosmeticBinding(val target: EquipTarget, val field: EquipField, val variant: String)

/** Artículo de la tienda (catálogo demo). */
data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val category: ItemCategory,
    val rarity: ItemRarity,
    val priceCoins: Int? = null,
    val premiumPriceClp: Int? = null,
    val binding: CosmeticBinding? = null,
    val contains: List<String> = emptyList(),
    val featured: Boolean = false
) {
    /** Artículo premium: solo se muestra su precio en CLP y estado "Próximamente". */
    val isPremium: Boolean get() = premiumPriceClp != null

    val isBundle: Boolean get() = contains.isNotEmpty()

    /** Un paquete está completo cuando todos sus artículos ya son tuyos. */
    fun bundleComplete(owned: Set<String>): Boolean = !isBundle || contains.all { it in owned }
}

/** Estado de un artículo para el jugador actual. */
enum class ItemState { Blocked, Available, Owned, Equipped }

/**
 * Deriva el estado de [this] para un jugador con [owned] artículos y
 * [equipped] indicando si su vínculo está aplicado ahora mismo.
 */
fun ShopItem.state(owned: Set<String>, equipped: Boolean): ItemState = when {
    isPremium -> ItemState.Blocked
    isBundle && bundleComplete(owned) -> ItemState.Owned
    isBundle -> ItemState.Available
    id in owned && equipped -> ItemState.Equipped
    id in owned -> ItemState.Owned
    else -> ItemState.Available
}

enum class MissionKind {
    /** Requiere un estado real del perfil (sin verificación externa). */
    ProfileCompletion,

    /** Basada en sesiones/valoraciones verificadas: pendiente de verificación. */
    VerificationPending,

    /** Misión ficticia de demostración con botón "Simular progreso". */
    DemoProgress
}

/** Misión o recompensa disponible en la pantalla de recompensas. */
data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val target: Int,
    val kind: MissionKind
)

/** Progreso persistido de una misión (0..target, con marca de reclamada). */
data class MissionStatus(
    val missionId: String,
    val progress: Int = 0,
    val claimed: Boolean = false
)

/** Errores posibles de las operaciones de la economía. */
enum class EconomyError {
    InsufficientBalance, AlreadyOwned, NotFound, MissionIncomplete,
    MissionAlreadyClaimed, MissionPending, InvalidOperation
}

sealed interface EconomyResult {
    data object Success : EconomyResult
    data class Failure(val error: EconomyError) : EconomyResult
}

/** Reglas puras de recompensas (unit tested). */
object MissionRules {
    /** ¿La misión está completada según el estado actual del jugador? */
    fun isComplete(mission: Mission, profile: PlayerProfile?, status: MissionStatus?): Boolean =
        when (mission.kind) {
            MissionKind.ProfileCompletion -> profile != null && profile != PlayerProfile()
            MissionKind.VerificationPending -> false
            MissionKind.DemoProgress -> (status?.progress ?: 0) >= mission.target
        }

    /** Solo se puede reclamar una vez, estando completada y sin estar bloqueada. */
    fun canClaim(mission: Mission, profile: PlayerProfile?, status: MissionStatus?): Boolean =
        mission.kind != MissionKind.VerificationPending &&
            status?.claimed != true &&
            isComplete(mission, profile, status)

    fun progressOf(mission: Mission, profile: PlayerProfile?, status: MissionStatus?): Int = when (mission.kind) {
        MissionKind.ProfileCompletion ->
            if (isComplete(mission, profile, status)) mission.target else 0
        MissionKind.VerificationPending -> status?.progress ?: 0
        MissionKind.DemoProgress -> (status?.progress ?: 0).coerceAtMost(mission.target)
    }
}
