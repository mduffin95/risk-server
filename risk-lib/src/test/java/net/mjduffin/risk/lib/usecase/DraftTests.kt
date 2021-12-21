package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.Board
import net.mjduffin.risk.lib.entities.DiceManager
import net.mjduffin.risk.lib.entities.DieThrow
import net.mjduffin.risk.lib.entities.Game
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class DraftTests {
    private val BOB = "Bob"
    private val ALICE = "Alice"

    private fun getGameManagerFromGame(game: Game): GameManager {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(1)
        val diceManager = DiceManager(dieThrow)
        val board = Board.Builder().addEdge("England", "Wales").build()
        return GameManager(board, game, diceManager)
    }

    @Test
    fun draft() {
        // given
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories(BOB, listOf("England", "Wales"))
//        gameBuilder.addEdge("England", "Wales")
        val game = gameBuilder.build()
        val gameManager = getGameManagerFromGame(game)

        // when
        val draft: MutableMap<String, Int> = HashMap()
        draft["England"] = 1
        draft["Wales"] = 3
        gameManager.draft(BOB, draft)
        val gameState = gameManager.getGameState()

        // then
        val expected = GameState(BOB, "ATTACK", 6, listOf("England", "Wales"), listOf(BOB, BOB), listOf(2, 4), hasEnded = true)
        assertEquals(expected, gameState)
    }

    @Test
    fun draftTwiceInRow() {
        // given
        val gameBuilder = Game.Builder()

        // TODO: This is currently dependent on the order
        // when
        gameBuilder.addPlayerWithTerritories(BOB, listOf("Wales"))
        gameBuilder.addPlayerWithTerritories(ALICE, listOf("England"))
//        gameBuilder.addEdge("England", "Wales")
        val game = gameBuilder.build()
        val playerInput = getGameManagerFromGame(game)
        playerInput.draft(BOB, mapOf(Pair("Wales", 4)))
        playerInput.draft(ALICE, mapOf(Pair("England", 1)))
        // then
        assertFailsWith<GameplayException> { playerInput.draft(BOB, mapOf(Pair("England", 1))) }
    }

    @Test
    fun `can draft in two steps`() {
        // given
        val gameBuilder = Game.Builder()

        // TODO: This is currently dependent on the order
        // when
        gameBuilder.addPlayerWithTerritories(BOB, listOf("England"))
        gameBuilder.addPlayerWithTerritories(ALICE, listOf("Wales"))
//        gameBuilder.addEdge("England", "Wales")
        val game = gameBuilder.build()
        val playerInput = getGameManagerFromGame(game)
//        playerInput.draft(BOB, mapOf(Pair("Wales", 20)))
//        playerInput.draft(ALICE, mapOf(Pair("England", 1)))
        // then
        playerInput.draftSingle(BOB, "England", 3)
        playerInput.draftSingle(BOB, "England", 3)
        val gameState = playerInput.getGameState()

        // then
//        val expected = GameState(BOB, "ALLDRAFT", 4, listOf("England", "Wales"), listOf(BOB, ALICE), listOf(7, 1), hasEnded = false)
        assertEquals("ALLDRAFT", gameState.phase)
        assertEquals(listOf("England", "Wales"), gameState.territories)
        assertEquals(listOf(BOB, ALICE), gameState.occupyingPlayers)
        assertEquals(listOf(7, 1), gameState.units)
        assertTrue(!gameState.hasEnded)
    }
}