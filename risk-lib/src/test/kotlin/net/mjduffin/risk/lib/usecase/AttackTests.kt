package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.Board
import net.mjduffin.risk.lib.entities.DiceManager
import net.mjduffin.risk.lib.entities.DieThrow
import net.mjduffin.risk.lib.entities.Game
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class AttackTests {
    private val PLAYER_A = "PlayerA"
    private val PLAYER_B = "PlayerB"
    private val PLAYER_C = "PlayerC"

    private fun createTwoPlayerGame(dieThrow: DieThrow): GameManager {
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories(PLAYER_A, listOf("England"))
        gameBuilder.addPlayerWithTerritories(PLAYER_B, listOf("Wales"))
        val board = Board.Builder().addEdge("England", "Wales").build()
        val game = gameBuilder.build()
        val diceManager = DiceManager(dieThrow)
        return GameManager(board, game, diceManager)
    }

    private fun createThreePlayerGame(dieThrow: DieThrow): GameManager {
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories(PLAYER_A, listOf("England"))
        gameBuilder.addPlayerWithTerritories(PLAYER_B, listOf("Wales"))
        gameBuilder.addPlayerWithTerritories(PLAYER_C, listOf("Scotland"))
        val board = Board.Builder()
            .addEdge("England", "Wales")
            .addEdge("England", "Scotland")
            .build()
        val game = gameBuilder.build()
        val diceManager = DiceManager(dieThrow)
        return GameManager(board, game, diceManager)
    }

    @Test
    fun `attack and attacker wins`() {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1)
        val playerInput = createTwoPlayerGame(dieThrow)
        // all draft
        playerInput.draft(PLAYER_A, mapOf(Pair("England", 3)))
        playerInput.draft(PLAYER_B, mapOf(Pair("Wales", 1)))
        // draft
        playerInput.draftSingle(PLAYER_A, "England", 3)

        // attack
        val result = playerInput.attack(PLAYER_A, "England", "Wales")
        assertEquals(7, result.attackUnits)
        assertEquals(0, result.defendUnits)
    }

    @Test
    fun `attack and defender wins`() {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(3).thenReturn(3)
            .thenReturn(1).thenReturn(4)
        val playerInput = createTwoPlayerGame(dieThrow)
        // all-draft
        playerInput.draft(PLAYER_A, mapOf(Pair("England", 3)))
        playerInput.draft(PLAYER_B, mapOf(Pair("Wales", 1)))
        // draft
        playerInput.draft(PLAYER_A, mapOf(Pair("England", 3)))
        // attack
        val result = playerInput.attack(PLAYER_A, "England", "Wales")

        //There has to be one unit left for the attacker
        assertEquals(1, result.attackUnits)
        assertEquals(2, result.defendUnits)
    }

    @Test
    fun `attack and player is eliminated`() {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1)
        val gameManager = createThreePlayerGame(dieThrow)
        gameManager.draft(PLAYER_A, mapOf(Pair("England", 3)))
        gameManager.draft(PLAYER_B, mapOf(Pair("Wales", 1)))
        gameManager.draft(PLAYER_C, mapOf(Pair("Scotland", 1)))

        var gameState = gameManager.getGameState()
        assertEquals("DRAFT", gameState.phase)
        assertEquals(PLAYER_A, gameState.currentPlayer)

        // place draft for player A
        gameManager.draftSingle(PLAYER_A, "England", 3)

        assertEquals("ATTACK", gameManager.getGameState().phase)
        // player A knocks out player B
        gameManager.attack(PLAYER_A, "England", "Wales")
        gameManager.move(PLAYER_A, 3)
        gameManager.endAttack(PLAYER_A)
        gameManager.endTurn()

        gameState = gameManager.getGameState()
        // player B doesn't have any territories left and has been eliminated, so play passes to player C
        assertEquals(PLAYER_C, gameState.currentPlayer)
    }


    @Test
    fun `player wins game`() {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1)
            .thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1)
        val gameManager = createThreePlayerGame(dieThrow)
        gameManager.draft(PLAYER_A, mapOf(Pair("England", 10)))
        gameManager.draft(PLAYER_B, mapOf(Pair("Wales", 1)))
        gameManager.draft(PLAYER_C, mapOf(Pair("Scotland", 1)))

        var gameState = gameManager.getGameState()
        assertEquals("ATTACK", gameState.phase)
        assertEquals(PLAYER_A, gameState.currentPlayer)

        // player A knocks out player B
        gameManager.attack(PLAYER_A, "England", "Wales")
        gameManager.move(PLAYER_A, 3)
        gameManager.attack(PLAYER_A, "England", "Scotland")
        gameManager.move(PLAYER_A, 3)
        gameManager.endAttack(PLAYER_A)
        gameManager.endTurn()

        gameState = gameManager.getGameState()
        // player B doesn't have any territories left and has been eliminated, so play passes to player C
        assertEquals("END", gameState.phase)
    }
}