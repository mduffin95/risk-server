package net.mjduffin.risk.lib.entities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class DiceManagerTest {

    @Test
    fun diceTest() {
        val dt = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dt.dieValue).thenReturn(1)
        val diceManager = DiceManager(dt)
        val result = diceManager.engage(3, 2)
        Assertions.assertEquals(1, result.attackers)
        Assertions.assertEquals(2, result.defenders)
    }

    @Test
    fun diceAttackerWins() {
        val dt = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dt.dieValue).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(4).thenReturn(5)
        val diceManager = DiceManager(dt)
        val result = diceManager.engage(3, 2)
        Assertions.assertEquals(3, result.attackers)
        Assertions.assertEquals(0, result.defenders)
    }
}