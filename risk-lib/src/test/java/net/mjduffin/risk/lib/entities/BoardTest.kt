package net.mjduffin.risk.lib.entities

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class BoardTest {

    @Test
    fun adjacentTerritoryTest() {
        val england = TerritoryId("England")
        val wales = TerritoryId("Wales")
        val builder = Board.Builder()
        val territories = listOf(england, wales)
//        builder.addTerritories(territories)
        builder.addEdge(england, wales)
        val board = builder.build()
        assertTrue(board.areAdjacent(england, wales))
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