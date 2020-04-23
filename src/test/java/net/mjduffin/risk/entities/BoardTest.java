package net.mjduffin.risk.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void adjacentTerritoryTest() {
        Board board = new Board();
        Territory england = board.getOrCreateTerritory("England");
        Territory wales = board.getOrCreateTerritory("Wales");

        board.addEdge(england, wales);
        assertTrue(board.areAdjacent(england, wales));
    }

    @Test
    void addPlayerTest() {
        //Test when adding a player to a territory that the player's unit count goes up by 1
        Board board = new Board();
        Territory england = board.getOrCreateTerritory("England");
        Player player = new Player("Bob");
        england.init(player);

        assertEquals(1, player.getTotalUnits());

    }

    @Test
    void increaseUnitsTest() {
        //Adding more units to a territory increases a player's units
        Board board = new Board();
        Territory england = board.getOrCreateTerritory("England");
        Player player = new Player("Bob");
        england.init(player);
        england.addUnits(5);

        assertEquals(6, player.getTotalUnits());
    }

}