package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.DieThrow.dieValue
import net.mjduffin.risk.lib.entities.Game.Builder.addPlayerWithTerritories
import net.mjduffin.risk.lib.entities.Game.Builder.build
import net.mjduffin.risk.lib.usecase.PlayerInput.draft
import net.mjduffin.risk.lib.entities.Game.board
import net.mjduffin.risk.lib.usecase.PlayerInput.attack
import net.mjduffin.risk.lib.entities.Game
import net.mjduffin.risk.lib.usecase.PlayerInput
import net.mjduffin.risk.lib.entities.DieThrow
import org.mockito.Mockito
import net.mjduffin.risk.lib.entities.DiceManager
import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.lib.usecase.GameplayException
import net.mjduffin.risk.lib.usecase.TerritoryNotFoundException
import net.mjduffin.risk.lib.usecase.PlayerNotFoundException
import net.mjduffin.risk.lib.usecase.AttackResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

internal class DraftTests {
    private val BOB = "Bob"
    private val ALICE = "Alice"
    fun getPlayerInputFromGame(game: Game?): PlayerInput {
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(1)
        val diceManager = DiceManager(dieThrow)
        return GameManager(game!!, diceManager)
    }

    @Test
    @Throws(GameplayException::class)
    fun draft() {
        // given
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories(BOB, Arrays.asList("England", "Wales"))
        val game = gameBuilder.build()
        val playerInput = getPlayerInputFromGame(game)

        // when
        val draft: MutableMap<String, Int?> = HashMap()
        draft["England"] = 1
        draft["Wales"] = 3
        playerInput.draft(BOB, draft)
        assertEquals(2, game.board.getTerritory("England").getUnits())
        assertEquals(4, game.board.getTerritory("Wales").getUnits())
    }

    @Test
    @Throws(GameplayException::class)
    fun draftTwiceInRow() {
        // given
        val gameBuilder = Game.Builder()

        // TODO: This is currently dependent on the order
        gameBuilder.addPlayerWithTerritories(BOB, listOf("Wales"))
        gameBuilder.addPlayerWithTerritories(ALICE, listOf("England"))
        val game = gameBuilder.build()
        val playerInput = getPlayerInputFromGame(game)
        val draft: MutableMap<String, Int?> = HashMap()
        draft["Wales"] = 4
        playerInput.draft(BOB, draft)
        draft.clear()
        draft["England"] = 1
        playerInput.draft(ALICE, draft)
        Assertions.assertThrows(GameplayException::class.java) { playerInput.draft(BOB, draft) }
    }
}