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

}