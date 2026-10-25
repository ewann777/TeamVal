package com.example.teamval.domain.model

enum class ValorantRank { Unranked, Iron, Bronze, Silver, Gold, Platinum, Diamond, Ascendant, Immortal, Radiant }
enum class Region { LatamNorth, LatamSouth, Brazil, NorthAmerica, Europe, AsiaPacific, Korea }
enum class GameMode { Competitive, Unrated, Swiftplay, SpikeRush, TeamDeathmatch }
enum class RequestStatus { Pending, Accepted, Rejected }

data class Player(val id: String, val name: String, val rank: ValorantRank)

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
    val availableSlots: Int get() = (capacity - memberIds.size).coerceAtLeast(0)
    val isFull: Boolean get() = availableSlots == 0
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
