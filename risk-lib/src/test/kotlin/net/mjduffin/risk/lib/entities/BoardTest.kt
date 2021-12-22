package net.mjduffin.risk.lib.entities

import org.junit.jupiter.api.Test
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

//    @Test
//    fun addPlayerTest() {
//        //Test when adding a player to a territory that the player's unit count goes up by 1
//        val player = Player("TEST")
//        val england = Territory("England", player)
//        assertEquals(1, player.getTotalUnits())
//        assertEquals(1, england.getUnits())
//        assertEquals(0, england.getAvailableUnits())
//    }

//    @Test
//    fun increaseUnitsTest() {
//        //Adding more units to a territory increases a player's units
//        val player = Player("TEST")
//        val england = Territory("England", player)
//        england.addUnits(5)
//        assertEquals(6, player.getTotalUnits())
//    }
}