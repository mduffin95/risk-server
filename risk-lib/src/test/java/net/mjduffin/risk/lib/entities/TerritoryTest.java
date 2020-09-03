package net.mjduffin.risk.lib.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerritoryTest {

    @Test
    void territoryInitialisationTest() {
        Player player = new Player("Bob");
        new Territory("Test", player);

        assertEquals(1, player.getTotalUnits());
    }



}