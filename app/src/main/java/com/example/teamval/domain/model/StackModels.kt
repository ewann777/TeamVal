package com.example.teamval.domain.model

enum class ValorantRank { Unranked, Iron, Bronze, Silver, Gold, Platinum, Diamond, Ascendant, Immortal, Radiant }
enum class Region { LatamNorth, LatamSouth, Brazil, NorthAmerica, Europe, AsiaPacific, Korea }
enum class GameMode { Competitive, Unrated, Swiftplay, SpikeRush, TeamDeathmatch }
enum class RequestStatus { Pending, Accepted, Rejected }

data class Player(val id: String, val name: String, val rank: ValorantRank)

/**
 * Tipo de grupo que forma una publicación según su tamaño objetivo (`capacity`):
 * dúo (2), trío (3) o stack (4–5). El tamaño del grupo no es lo mismo que los
 * cupos que se buscan ni que los jugadores que aún faltan para un equipo de 5.
 */
enum class StackCategory { Duo, Trio, Stack;

    companion object {
        /** Jugadores de un equipo completo de VALORANT. */
        const val FULL_TEAM = 5

        /**
         * Clasifica por el tamaño del grupo. Solo existen tamaños 2..5
         * (`StackDraft.invalidFields`), de modo que 4 se agrupa como Stack.
         */
        fun ofCapacity(capacity: Int): StackCategory = when {
            capacity <= 2 -> Duo
            capacity == 3 -> Trio
            else -> Stack
        }
    }
}

data class StackPost(
    val id: String,
    val owner: Player,
    val title: String,
    val description: String,
    val rank: ValorantRank,
    val region: Region,
    val mode: GameMode,
    val capacity: Int,
    val memberIds: Set<String> = setOf(owner.id)
) {
    /** Tamaño actual del grupo: integrantes que ya ocupan plaza. */
    val currentMembers: Int get() = memberIds.size
    /** Cupos que ofrece esta publicación: jugadores que se buscan. */
    val availableSlots: Int get() = (capacity - memberIds.size).coerceAtLeast(0)
    val isFull: Boolean get() = availableSlots == 0
    val category: StackCategory get() = StackCategory.ofCapacity(capacity)

    /**
     * Jugadores que este grupo necesita fuera de sus propios cupos para alcanzar
     * un equipo de 5. Siempre se cumple: integrantes + cupos + faltantes = 5.
     */
    val playersMissingForFullTeam: Int get() = (StackCategory.FULL_TEAM - capacity).coerceAtLeast(0)
}

/**
 * Búsqueda visible en el explorador: categoría (pestañas), filtros adicionales
 * y alcance propio/ajeno, combinados con «y». `category == null` muestra todos.
 */
data class StackSearch(
    val category: StackCategory? = null,
    val filters: StackFilters = StackFilters(),
    val ownPostsOnly: Boolean = false,
    val playerId: String = ""
) {
    fun matches(post: StackPost): Boolean =
        (if (ownPostsOnly) post.owner.id == playerId else post.owner.id != playerId) &&
            (category == null || post.category == category) &&
            filters.matches(post)

    fun apply(posts: List<StackPost>): List<StackPost> = posts.filter(::matches)
}

data class JoinRequest(
    val id: String,
    val postId: String,
    val applicant: Player,
    val status: RequestStatus = RequestStatus.Pending
)

data class StackSnapshot(
    val posts: List<StackPost> = emptyList(),
    val requests: List<JoinRequest> = emptyList()
)

data class StackFilters(
    val rank: ValorantRank? = null,
    val region: Region? = null,
    val mode: GameMode? = null
) {
    val isActive: Boolean get() = rank != null || region != null || mode != null

    fun matches(post: StackPost): Boolean =
        (rank == null || post.rank == rank) &&
            (region == null || post.region == region) &&
            (mode == null || post.mode == mode)
}

enum class DraftField { Title, Description, Capacity }

data class StackDraft(
    val title: String = "",
    val description: String = "",
    val rank: ValorantRank = ValorantRank.Gold,
    val region: Region = Region.LatamSouth,
    val mode: GameMode = GameMode.Competitive,
    val capacity: Int = 5
) {
    fun invalidFields(): Set<DraftField> = buildSet {
        if (title.trim().length !in 5..60) add(DraftField.Title)
        if (description.trim().length !in 10..300) add(DraftField.Description)
        if (capacity !in 2..5) add(DraftField.Capacity)
    }
}
