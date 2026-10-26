package com.example.teamval.domain.model

import org.junit.Assert.*
import org.junit.Test

class StackSearchTest {
    private val me = Player("me", "Nova#TEAM", ValorantRank.Gold)
    private val other = Player("host", "Vega#SUR", ValorantRank.Gold)

    private fun post(
        id: String = "post",
        capacity: Int = 5,
        owner: Player = other,
        members: Set<String> = setOf(owner.id),
        rank: ValorantRank = ValorantRank.Gold,
        region: Region = Region.LatamSouth,
        mode: GameMode = GameMode.Competitive
    ) = StackPost(id, owner, "Título", "Descripción", rank, region, mode, capacity, members)

    @Test
    fun capacityClassifiesGroupIntoDuoTrioOrStack() {
        assertEquals(StackCategory.Duo, StackCategory.ofCapacity(2))
        assertEquals(StackCategory.Trio, StackCategory.ofCapacity(3))
        // Caso ambiguo: un grupo de 4 no es dúo ni trío, se agrupa como stack.
        assertEquals(StackCategory.Stack, StackCategory.ofCapacity(4))
        assertEquals(StackCategory.Stack, StackCategory.ofCapacity(5))
        assertEquals(StackCategory.Duo, post(capacity = 2).category)
        assertEquals(StackCategory.Trio, post(capacity = 3).category)
        assertEquals(StackCategory.Stack, post(capacity = 4).category)
    }

    @Test
    fun groupSizeSlotsAndMissingPlayersForFiveNeverOverlap() {
        // Dúo a medio llenar: 1 integrante + 1 cupo + 3 fuera del grupo = 5.
        val duo = post(capacity = 2, members = setOf("host"))
        assertEquals(1, duo.currentMembers)
        assertEquals(1, duo.availableSlots)
        assertEquals(3, duo.playersMissingForFullTeam)
        assertEquals(StackCategory.FULL_TEAM, duo.currentMembers + duo.availableSlots + duo.playersMissingForFullTeam)

        // Trío completo: 3 + 0 + 2 = 5.
        val trio = post(capacity = 3, members = setOf("host", "a", "b"))
        assertEquals(3, trio.currentMembers)
        assertTrue(trio.isFull)
        assertEquals(2, trio.playersMissingForFullTeam)

        // Stack de 5 a medias: 3 + 2 + 0 = 5.
        val five = post(capacity = 5, members = setOf("host", "a", "b"))
        assertEquals(3, five.currentMembers)
        assertEquals(2, five.availableSlots)
        assertEquals(0, five.playersMissingForFullTeam)

        // Stack de 4 completo: sigue sin cerrar un equipo de 5.
        val four = post(capacity = 4, members = setOf("host", "a", "b", "c"))
        assertEquals(0, four.availableSlots)
        assertEquals(1, four.playersMissingForFullTeam)
        assertEquals(StackCategory.FULL_TEAM, four.currentMembers + four.availableSlots + four.playersMissingForFullTeam)
    }

    @Test
    fun searchCombinesCategoryFiltersAndOwnershipWithoutChangingExistingFilters() {
        val duo = post("duo", capacity = 2)
        val trio = post("trio", capacity = 3, rank = ValorantRank.Platinum)
        val stack = post("stack", capacity = 5, region = Region.Europe)
        val own = post("own", capacity = 5, owner = me)
        val posts = listOf(duo, trio, stack, own)

        // Sin categoría se ven todas las categorías y no las propias.
        assertEquals(posts - own, StackSearch(playerId = me.id).apply(posts))
        // Cada pestaña filtra por el tamaño del grupo.
        assertEquals(listOf(duo), StackSearch(category = StackCategory.Duo, playerId = me.id).apply(posts))
        assertEquals(listOf(trio), StackSearch(category = StackCategory.Trio, playerId = me.id).apply(posts))
        assertEquals(listOf(stack), StackSearch(category = StackCategory.Stack, playerId = me.id).apply(posts))
        // La categoría combina con los filtros existentes.
        assertEquals(
            emptyList<StackPost>(),
            StackSearch(
                category = StackCategory.Duo,
                filters = StackFilters(region = Region.Europe),
                playerId = me.id
            ).apply(posts)
        )
        assertEquals(
            listOf(duo),
            StackSearch(category = StackCategory.Duo, filters = StackFilters(rank = ValorantRank.Gold), playerId = me.id).apply(posts)
        )
        // «Mis publicaciones» sigue mostrando solo lo propio, en cualquier categoría.
        assertEquals(listOf(own), StackSearch(ownPostsOnly = true, playerId = me.id).apply(posts))
        assertEquals(
            listOf(own),
            StackSearch(category = StackCategory.Stack, ownPostsOnly = true, playerId = me.id).apply(posts)
        )
        assertTrue(StackSearch(category = StackCategory.Trio, ownPostsOnly = true, playerId = me.id).apply(posts).isEmpty())
    }
}
