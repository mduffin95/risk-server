package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.*
import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.TerritoryService
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

val colors = listOf("red", "blue", "green", "violet", "orange", "magenta", "yellow")

@RestController
@RequestMapping("api")
class RestController(private val territoryService: TerritoryService, private val gameFactory: GameFactory) {

    private val log: Logger = LoggerFactory.getLogger(RestController::class.java)

    private val containers: MutableMap<String, GameContainer> = mutableMapOf()

    private fun getGameManager(id: String): GameManager? {
        val container = containers[id] ?: return null
        return container.getGameManager()
    }

    @PostMapping("/games/{gameId}/turn/draft")
    private fun draft(@PathVariable("gameId") gameId: String, @RequestBody draft: Draft): Response {
        log.info("Draft")
        val container = containers[gameId] ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        try {
            gameManager.draftSingle(draft.requestingPlayer, draft.territory, 1)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    @PostMapping("/games/{gameId}/turn/attack")
    private fun attack(@PathVariable("gameId") gameId: String, @RequestBody attack: Attack): Response {
        log.info("Attack")
        val container = containers[gameId] ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        try {
            gameManager.attack(attack.requestingPlayer, attack.from, attack.to)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    @PostMapping("/games/{gameId}/turn/move")
    fun move(@PathVariable("gameId") gameId: String, @RequestBody move: Move): Response {
        log.info("Move")
        val container = containers[gameId] ?: return Response("No game found for $gameId")
        val gameManager = getGameManager(gameId) ?: return Response("No game found for $gameId")
        try {
            gameManager.move(move.requestingPlayer, move.units)
            container.increment()
        } catch (ex: Exception) {
            return Response(ex.message)
        }
        return Response(null)
    }

    @PostMapping("/games/{gameId}/turn/end")
    private fun end(@PathVariable("gameId") gameId: String, @RequestBody endTurn: EndTurn): Response {
        log.info("End turn for {}", gameId)
        val container = containers[gameId] ?: return Response("No game found for $gameId")
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

    @PostMapping("/games/{gameId}/turn/fortify")
    private fun fortify(@PathVariable("gameId") gameId: String, @RequestBody fortify: Fortify): Response {
        log.info("Fortfiy {}", gameId)
        val container = containers[gameId] ?: return Response("No game found for $gameId")
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

    @PostMapping("/games")
    private fun newGame(): String {
        log.info("Start new game")
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()

        containers[id] = LocalGameContainer(gameFactory, territoryService)

        return id
    }

    @PutMapping("/games/{gameId}/players/{playerName}")
    fun join(@PathVariable("gameId") gameId: String, @PathVariable("playerName") playerName: String): Player {
        log.info("New player {} joined game {}", playerName, gameId)
        val gameContainer = containers[gameId]
        return gameContainer!!.addPlayer(playerName)
    }

    @GetMapping("/games/{gameId}/game/{count}")
    fun game(@PathVariable("gameId") gameId: String, @PathVariable("count") count: Int): ViewModel {
        val container = containers[gameId]
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

    @PostMapping("/games/{gameId}/start")
    fun start(@PathVariable("gameId") gameId: String): Response {
        log.info("Start game {}", gameId)
        val container = containers[gameId]!!
        container.startGame()
        return Response(null)
    }

    fun getViewModel(id: String): ViewModel =
        containers[id]?.toViewModel() ?: ViewModel(Screen.ERROR, 0, "Container not found: $id")
}
