package net.mjduffin.risk.lib.entities

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TerritoryTest {

    @Test
    fun territoryInitialisationTest() {

        var player = Player("Bob")
        var territory = Territory("Test")
        player.addTerritory(territory)

        assertEquals(1, player.totalUnits);

    }
}