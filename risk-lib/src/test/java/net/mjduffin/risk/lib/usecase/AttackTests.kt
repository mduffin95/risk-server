package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.DiceManager
import net.mjduffin.risk.lib.entities.DieThrow
import net.mjduffin.risk.lib.entities.Game
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class AttackTests {
    var PLAYER_A = "PlayerA"
    var PLAYER_B = "PlayerB"
    fun createTwoPlayerGame(dieThrow: DieThrow?): PlayerInput {
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories(PLAYER_A, listOf("England"))
        gameBuilder.addPlayerWithTerritories(PLAYER_B, listOf("Wales"))
        val game = gameBuilder.build()
        val diceManager = DiceManager(dieThrow!!)
        return GameManager(game, diceManager)
    }

    @Test
    @Throws(TerritoryNotFoundException::class, PlayerNotFoundException::class, GameplayException::class)
    fun attack() {
        var draft: MutableMap<String, Int> = HashMap()
        draft["England"] = 3
        //        draft.put("Wales", 3);
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1)
        val playerInput = createTwoPlayerGame(dieThrow)
        playerInput.draft(PLAYER_A, draft)
        draft = HashMap()
        draft["Wales"] = 1
        playerInput.draft(PLAYER_B, draft)
        val result = playerInput.attack(PLAYER_A, "England", "Wales")
        Assertions.assertEquals(4, result.attackUnits)
        Assertions.assertEquals(0, result.defendUnits)
    }

    @Test
    @Throws(TerritoryNotFoundException::class, PlayerNotFoundException::class, GameplayException::class)
    fun attackDefenderWins() {
        var draft: MutableMap<String, Int> = HashMap()
        draft["England"] = 3
        val dieThrow = Mockito.mock(DieThrow::class.java)
        Mockito.`when`(dieThrow.dieValue).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(3).thenReturn(3)
            .thenReturn(1).thenReturn(4)
        val playerInput = createTwoPlayerGame(dieThrow)
        playerInput.draft(PLAYER_A, draft)
        draft = HashMap()
        draft["Wales"] = 1
        playerInput.draft(PLAYER_B, draft)
        val result = playerInput.attack(PLAYER_A, "England", "Wales")

        //There has to be one unit left for the attacker
        Assertions.assertEquals(1, result.attackUnits)
        Assertions.assertEquals(2, result.defendUnits)
    }
}