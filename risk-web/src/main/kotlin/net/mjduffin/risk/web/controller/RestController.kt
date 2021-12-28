package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.*
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class RestController(val messageService: TerritoryService, val gameFactory: GameFactory) {

    private val containers: MutableMap<String, GameContainer> = mutableMapOf()

    fun getGameManager(id: String): GameManager? {
        val container = containers[id] ?: return null
        return container.getGameManager()
    }

    @GetMapping("/{id}/game")
    fun game(@PathVariable("id") id: String): GameVM {
        val gameState = getGameManager(id)?.getGameState()
        return gameState?.let { messageService.convert(it) } ?: messageService.error("No game found for $id")
    }

    @PostMapping("/{id}/draft")
    fun draft(@PathVariable("id") id: String, @RequestBody draft: Draft): GameVM {
        val gameManager = getGameManager(id) ?: return messageService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.draftSingle(currentState.currentPlayer, draft.territory, 1)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @PostMapping("/{id}/attack")
    fun attack(@PathVariable("id") id: String, @RequestBody attack: Attack): GameVM {
        val gameManager = getGameManager(id) ?: return messageService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.attack(currentState.currentPlayer, attack.from, attack.to)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @PostMapping("/{id}/move")
    fun move(@PathVariable("id") id: String, @RequestBody move: Move): GameVM {
        val gameManager = getGameManager(id) ?: return messageService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.move(currentState.currentPlayer, move.units)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @GetMapping("/{id}/end")
    fun end(@PathVariable("id") id: String): GameVM {
        val gameManager = getGameManager(id) ?: return messageService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            if (currentState.phase.equals("ATTACK")) {
                gameManager.endAttack(currentState.currentPlayer)
            } else {
                gameManager.endTurn()
            }
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @PostMapping("/{id}/fortify")
    fun attack(@PathVariable("id") id: String, @RequestBody fortify: Fortify): GameVM {
        val gameManager = getGameManager(id) ?: return messageService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.fortify(currentState.currentPlayer, fortify.from, fortify.to, fortify.units)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @GetMapping("/newgame")
    fun newGame(): String {
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()

        containers[id] = GameContainer(mutableListOf(), gameFactory)

        return id
    }

    @PostMapping("/{id}/join")
    fun join(@PathVariable("id") id: String, @RequestBody join: Join): LobbyVM {

        val gameContainer = containers[id]

        gameContainer?.addPlayer(join.player)

        return LobbyVM(gameContainer?.players ?: listOf(), null)
    }

    @GetMapping("/{id}/start")
    fun start(@PathVariable("id") id: String): GameVM {

        val gameContainer = containers[id]

        val gameManager = gameContainer?.startGame()?.getGameManager()!!

        return messageService.convert(gameManager.getGameState())
    }

    class GameContainer(val players: MutableList<String>, val gameFactory: GameFactory) {

        private var manager: GameManager? = null

        fun getGameManager(): GameManager? {
            return manager
        }

        fun addPlayer(player: String) = apply {
            players.add(player)
        }

        fun startGame() = apply {
            manager = gameFactory.mainGame(players)
        }
    }
}