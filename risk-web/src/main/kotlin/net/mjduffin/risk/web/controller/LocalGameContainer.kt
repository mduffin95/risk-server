package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.*
import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.lib.TerritoryService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Holds all information relevant to a single game.
 */
class LocalGameContainer(
    private val gameFactory: GameFactory,
    private val territoryService: TerritoryService
) : GameContainer {

    private var manager: GameManager? = null
    private val players: MutableList<Player> = mutableListOf()

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    // used by clients to determine if they have an up-to-date version of the game state
    private var actionCount = 0
    private var playerCount = 0

    override fun getGameManager(): GameManager {
        if (!hasStarted()) {
            throw IllegalArgumentException("Game not started")
        } else {
            return manager!!
        }
    }

    override fun waitThreeSeconds() {
        lock.withLock { condition.await(3, TimeUnit.SECONDS) }
    }

    /**
     * Increments the counter that tracks actions taken in the game.
     */
    override fun increment() {
        actionCount++
        lock.withLock { condition.signalAll() }
    }

    override fun addPlayer(name: String): Player {
        val existing = findPlayer(name)
        if (existing != null) {
            return existing
        }
        val newPlayer = Player(name, colors[playerCount++]);
        players.add(newPlayer)
        increment()
        return newPlayer
    }

    override fun startGame() {
        manager = gameFactory.mainGame(players.map { it.name }.toList())
        increment()
    }

    /**
     * Get the view-model for the game.
     */
    override fun toViewModel(): ViewModel {
        return if (hasStarted()) {
            ViewModel(Screen.GAME, actionCount, getGameViewModel())
        } else {
            val lobby = LobbyVM(players)
            ViewModel(Screen.LOBBY, actionCount, lobby)
        }
    }

    private fun hasStarted(): Boolean = manager != null

    /**
     * Get the view-model for the game screen.
     */
    private fun getGameViewModel(): GameVM {
        if (!hasStarted()) {
            return error("Game not started")
        }

        val gameState = manager?.getGameState()!!

        val territories = gameState.territories.indices.map {
            toTerritoryVM(
                gameState.territories[it],
                gameState.occupyingPlayers[it],
                gameState.units[it],
            )
        }
        var modal: ModalVM? = null
        if (gameState.phase.equals("MOVE")) {
            modal = ModalVM("Move from ${gameState.lastAttackingTerritory} to ${gameState.lastDefendingTerritory}", gameState.lastAttackingUnitCount, gameState.maxToMove!!)
        }

        return GameVM(gameState.currentPlayer, gameState.phase, gameState.unitsToPlace, territories, modal)
    }

    private fun error(errorMessage: String): GameVM {
        return GameVM("", "", 0, listOf(), null, errorMessage)
    }

    private fun findPlayer(playerName: String): Player? {
        return players.find { it.name == playerName }
    }

    private fun getPlayer(playerName: String): Player {
        return findPlayer(playerName) ?: throw IllegalArgumentException("Missing color for $playerName")
    }

    private fun toTerritoryVM(territory: String, playerName: String, units: Int): TerritoryVM {
        val point = territoryService.getPosition(territory)
        val player = getPlayer(playerName)
        return TerritoryVM(territory, point.first, point.second, player, units)
    }
}