package net.mjduffin.risk.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryTest {

    @Test
    void territoryInitialisationTest() {
        Territory territory = new Territory("Test");
        Player player = new Player("Bob");

        territory.init(player);

        assertEquals(1, player.getTotalUnits());
    }

}