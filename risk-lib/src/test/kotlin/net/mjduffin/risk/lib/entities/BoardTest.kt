package net.mjduffin.risk.lib.entities

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BoardTest {

    @Test
    fun `adjacent territories should be detected`() {
        // given/when
        val england = TerritoryId("England")
        val wales = TerritoryId("Wales")
        val board = Board.Builder().addEdge(england, wales).build()

        // then
        assertTrue(board.areAdjacent(england, wales))
    }

    @Test
    fun `non-adjacent territories should be rejected`() {
        // given/when
        val england = TerritoryId("England")
        val wales = TerritoryId("Wales")
        val scotland = TerritoryId("Scotland")
        val board = Board.Builder()
            .addEdge(england, wales)
            .addEdge(england, scotland)
            .build()

        // then
        assertFalse(board.areAdjacent(wales, scotland))
    }


    @Test
    fun `connected territories should be detected`() {
        // given/when
        val england = TerritoryId("England")
        val wales = TerritoryId("Wales")
        val scotland = TerritoryId("Scotland")
        val board = Board.Builder()
            .addEdge(england, wales)
            .addEdge(england, scotland)
            .build()
        val a = PlayerId("PlayerA")
        val playerLookup = mapOf(Pair(england, a), Pair(wales, a), Pair(scotland, a))

        // then
        assertTrue(board.areConnected(wales, scotland, playerLookup))
    }


    @Test
    fun `unconnected territories should be rejected`() {
        // given/when
        val england = TerritoryId("England")
        val wales = TerritoryId("Wales")
        val scotland = TerritoryId("Scotland")
        val board = Board.Builder()
            .addEdge(england, wales)
            .addEdge(england, scotland)
            .build()
        val a = PlayerId("PlayerA")
        val b = PlayerId("PlayerB")
        val playerLookup = mapOf(Pair(england, a), Pair(wales, b), Pair(scotland, b))

        // then
        assertFalse(board.areConnected(wales, scotland, playerLookup))
    }

    @Test
    fun `full continents`() {
        // given/when
        val england = TerritoryId("England")
        val wales = TerritoryId("Wales")
        val scotland = TerritoryId("Scotland")
        val france = TerritoryId("France")
        val uk = Continent("UK", 3)
        val board = Board.Builder()
            .addEdge(england, wales)
            .addEdge(england, scotland)
            .addEdge(england, france)
            .addToContinent(uk, setOf(england, wales, scotland))
            .build()

        // then
        setOf(england, wales, scotland, )
        assertEquals(setOf(uk), board.fullContinents(setOf(england, wales, scotland, france)))
    }
}