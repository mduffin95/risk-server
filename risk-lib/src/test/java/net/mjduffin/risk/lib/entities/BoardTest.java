package net.mjduffin.risk.lib.entities;

import net.mjduffin.risk.lib.usecase.BoardBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void adjacentTerritoryTest() {
        Board board = new BoardBuilder()
                .addTerritory("England")
                .addTerritory("Wales")
                .addEdge("England", "Wales")
                .build();

        Territory england = board.getTerritory("England");
        Territory wales = board.getTerritory("Wales");
        assertTrue(board.areAdjacent(england, wales));
    }

    @Test
    void addPlayerTest() {
        //Test when adding a player to a territory that the player's unit count goes up by 1
        Board board = new BoardBuilder().addTerritory("England").build();
        Player player = new Player("Bob");
        board.getTerritory("England").init(player);

        assertEquals(1, player.getTotalUnits());

    }

    @Test
    void increaseUnitsTest() {
        //Adding more units to a territory increases a player's units
        Board board = new BoardBuilder().addTerritory("England").build();
        Player player = new Player("Bob");

        board.getTerritory("England").init(player);
        board.getTerritory("England").addUnits(5);

        assertEquals(6, player.getTotalUnits());
    }

}