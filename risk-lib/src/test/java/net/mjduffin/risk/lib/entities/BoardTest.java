package net.mjduffin.risk.lib.entities;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void adjacentTerritoryTest() {
        Player player = new Player("TEST");
        Territory england = new Territory("England", player);
        Territory wales = new Territory("Wales", player);
        Board.Builder builder = new Board.Builder();
        List<Territory> territories = Arrays.asList(england, wales);
        builder.addTerritories(territories);
        builder.addEdge("England", "Wales");
        Board board = builder.build();

        assertTrue(board.areAdjacent(england, wales));
    }

    @Test
    void addPlayerTest() {
        //Test when adding a player to a territory that the player's unit count goes up by 1
        Player player = new Player("TEST");
        Territory england = new Territory("England", player);

        assertEquals(1, player.getTotalUnits());
        assertEquals(1, england.getUnits());
        assertEquals(0, england.getAvailableUnits());

    }

    @Test
    void increaseUnitsTest() {
        //Adding more units to a territory increases a player's units
        Player player = new Player("TEST");
        Territory england = new Territory("England", player);

        england.addUnits(5);

        assertEquals(6, player.getTotalUnits());
    }

}