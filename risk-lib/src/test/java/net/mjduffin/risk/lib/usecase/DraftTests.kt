package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
        val expected = GameState(BOB, "ATTACK", 3, listOf("England", "Wales"), listOf(BOB, BOB), listOf(2, 4), hasEnded = true)
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
        val draft: MutableMap<String, Int> = HashMap()
        draft["Wales"] = 4
        playerInput.draft(BOB, draft)
        draft.clear()
        draft["England"] = 1
        playerInput.draft(ALICE, draft)
        // then
        assertFailsWith<GameplayException> { playerInput.draft(BOB, draft) }
    }
}