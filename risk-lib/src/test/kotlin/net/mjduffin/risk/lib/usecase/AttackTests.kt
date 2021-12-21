package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.Board
import net.mjduffin.risk.lib.entities.DiceManager
import net.mjduffin.risk.lib.entities.DieThrow
import net.mjduffin.risk.lib.entities.Game
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class AttackTests {
    var PLAYER_A = "PlayerA"
    var PLAYER_B = "PlayerB"
    fun createTwoPlayerGame(dieThrow: DieThrow?): PlayerInput {
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories(PLAYER_A, listOf("England"))
        gameBuilder.addPlayerWithTerritories(PLAYER_B, listOf("Wales"))
        val board = Board.Builder().addEdge("England", "Wales").build()
        val game = gameBuilder.build()
        val diceManager = DiceManager(dieThrow!!)
        return GameManager(board, game, diceManager)
    }

    @Test
    fun attack() {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1)
        val playerInput = createTwoPlayerGame(dieThrow)
        playerInput.draft(PLAYER_A, mapOf(Pair("England", 3)))
        playerInput.draft(PLAYER_B, mapOf(Pair("Wales", 1)))
        val result = playerInput.attack(PLAYER_A, "England", "Wales")
        assertEquals(4, result.attackUnits)
        assertEquals(0, result.defendUnits)
    }

    @Test
    fun attackDefenderWins() {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(3).thenReturn(3)
            .thenReturn(1).thenReturn(4)
        val playerInput = createTwoPlayerGame(dieThrow)
        playerInput.draft(PLAYER_A, mapOf(Pair("England", 3)))
        playerInput.draft(PLAYER_B, mapOf(Pair("Wales", 1)))
        val result = playerInput.attack(PLAYER_A, "England", "Wales")

        //There has to be one unit left for the attacker
        assertEquals(1, result.attackUnits)
        assertEquals(2, result.defendUnits)
    }
}