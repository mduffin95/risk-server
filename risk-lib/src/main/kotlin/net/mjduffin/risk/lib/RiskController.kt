package net.mjduffin.risk.lib

import net.mjduffin.risk.lib.*
import net.mjduffin.risk.lib.usecase.GameManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val colors = listOf("red", "blue", "green", "violet", "orange", "magenta", "yellow")

class RiskController(private val containers: GameContainerService) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RiskController::class.java)
    }

    private fun getGameManager(id: String): GameManager? {
        val container = containers.getContainer(id) ?: return null
        return container.getGameManager()
    }

    public fun draft(gameId: String, draft: Draft): Response {
        LOG.info("Draft")
        val container = containers.getContainer(gameId) ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        try {
            gameManager.draftSingle(draft.requestingPlayer, draft.territory, 1)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    fun attack(gameId: String, attack: Attack): Response {
        LOG.info("Attack")
        val container = containers.getContainer(gameId) ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        try {
            gameManager.attack(attack.requestingPlayer, attack.from, attack.to)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    fun move(gameId: String, move: Move): Response {
        LOG.info("Move")
        val container = containers.getContainer(gameId) ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        try {
            gameManager.move(move.requestingPlayer, move.units)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    fun end(gameId: String, endTurn: EndTurn): Response {
        LOG.info("End turn for {}", gameId)
        val container = containers.getContainer(gameId) ?: return Response("No game found for $gameId")
        val gameManager = container.getGameManager()
        val currentState = gameManager.getGameState()
        if (currentState.currentPlayer != endTurn.requestingPlayer) {
            return Response("Not able to end turn for ${endTurn.requestingPlayer}")
        }
        try {
            if (currentState.phase.equals("ATTACK")) {
                gameManager.endAttack(endTurn.requestingPlayer)
            } else {
                gameManager.endTurn(endTurn.requestingPlayer)
            }
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    fun fortify(gameId: String, fortify: Fortify): Response {
        LOG.info("Fortfiy {}", gameId)
        val container = containers.getContainer(gameId) ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        val currentState = gameManager.getGameState()
        try {
            gameManager.fortify(currentState.currentPlayer, fortify.from, fortify.to, fortify.units)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    fun newGame(): String {
        LOG.info("Start new game")
        return containers.createGame()
    }

    fun join(gameId: String, playerName: String): Player {
        LOG.info("New player {} joined game {}", playerName, gameId)
        val gameContainer = containers.getContainer(gameId)
        return gameContainer!!.addPlayer(playerName)
    }

    fun game(gameId: String, count: Int): ViewModel {
        val container = containers.getContainer(gameId)
        return if (container != null) {
            val viewModel = container.toViewModel()
            if (viewModel.actionCount > count) {
                viewModel
            } else {
                container.waitThreeSeconds()
                container.toViewModel()
            }
        } else {
            ViewModel(Screen.ERROR, 0, "Container missing for $gameId")
        }
    }

    fun start(gameId: String): Response {
        LOG.info("Start game {}", gameId)
        val container = containers.getContainer(gameId)!!
        container.startGame()
        return Response(null)
    }

    fun getViewModel(id: String): ViewModel =
        containers.getContainer(id)?.toViewModel() ?: ViewModel(Screen.ERROR, 0, "Container not found: $id")
}
