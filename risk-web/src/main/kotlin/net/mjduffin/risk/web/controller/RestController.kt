package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.*
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

val colors = listOf("red", "blue", "green", "violet", "orange", "magenta", "yellow")

@RestController
@RequestMapping("api")
class RestController(val territoryService: TerritoryService, val gameFactory: GameFactory) {

    private val containers: MutableMap<String, GameContainer> = mutableMapOf()

    fun getGameManager(id: String): GameManager? {
        val container = containers[id] ?: return null
        return container.getGameManager()
    }

    @GetMapping("/{id}/game")
    fun game(@PathVariable("id") id: String): GameVM {
        return containers[id]?.convert() ?: territoryService.error("No game found for $id")
    }

    @PostMapping("/{id}/draft")
    fun draft(@PathVariable("id") id: String, @RequestBody draft: Draft): GameVM {
        val container = containers[id] ?: return territoryService.error("No game found for $id")
        val gameManager = getGameManager(id) ?: return territoryService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.draftSingle(currentState.currentPlayer, draft.territory, 1)
        } catch (ex: Exception) {
        }
        return container.convert()
    }

    @PostMapping("/{id}/attack")
    fun attack(@PathVariable("id") id: String, @RequestBody attack: Attack): GameVM {
        val container = containers[id] ?: return territoryService.error("No game found for $id")
        val gameManager = getGameManager(id) ?: return territoryService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.attack(currentState.currentPlayer, attack.from, attack.to)
        } catch (ex: Exception) {
        }
        return container.convert()
    }

    @PostMapping("/{id}/move")
    fun move(@PathVariable("id") id: String, @RequestBody move: Move): GameVM {
        val container = containers[id] ?: return territoryService.error("No game found for $id")
        val gameManager = getGameManager(id) ?: return territoryService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.move(currentState.currentPlayer, move.units)
        } catch (ex: Exception) {
        }
        return container.convert()
    }

    @GetMapping("/{id}/end")
    fun end(@PathVariable("id") id: String): GameVM {
        val container = containers[id] ?: return territoryService.error("No game found for $id")
        val gameManager = container.getGameManager()
        val currentState = gameManager.getGameState()
        try {
            if (currentState.phase.equals("ATTACK")) {
                gameManager.endAttack(currentState.currentPlayer)
            } else {
                gameManager.endTurn()
            }
        } catch (ex: Exception) {
        }
        return container.convert()
    }

    @PostMapping("/{id}/fortify")
    fun attack(@PathVariable("id") id: String, @RequestBody fortify: Fortify): GameVM {
        val container = containers[id] ?: return territoryService.error("No game found for $id")
        val gameManager = getGameManager(id) ?: return territoryService.error("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.fortify(currentState.currentPlayer, fortify.from, fortify.to, fortify.units)
        } catch (ex: Exception) {
        }
        return container.convert()
    }

    @GetMapping("/newgame")
    fun newGame(): String {
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()

        containers[id] = GameContainer(gameFactory, territoryService)

        return id
    }

    @PostMapping("/{id}/join")
    fun join(@PathVariable("id") id: String, @RequestBody join: Join): LobbyVM {

        val gameContainer = containers[id]

        gameContainer?.addPlayer(join.player)

        return LobbyVM(gameContainer?.players ?: listOf(), false,null)
    }

    @PostMapping("/{id}/lobby")
    fun lobby(@PathVariable("id") id: String, @RequestBody lobby: LobbyVM): LobbyVM {

        val start = System.nanoTime()

        var newLobbyVM = lobby
        while (TimeUnit.SECONDS.convert(System.nanoTime() - start, TimeUnit.MILLISECONDS) < 3_000_000) {
            Thread.sleep(100)
            val gameContainer = containers[id]
            newLobbyVM = LobbyVM(gameContainer?.players ?: listOf(), gameContainer?.hasStarted() ?: false, null)
            if (newLobbyVM != lobby) {
                break
            }
        }
        System.out.println("Sending: $id")
        return newLobbyVM
    }

    @GetMapping("/{id}/start")
    fun start(@PathVariable("id") id: String): GameVM {
        val gameContainer = containers[id]!!
        gameContainer.startGame()
        return gameContainer.convert()
    }
}


class GameContainer(private val gameFactory: GameFactory, private val territoryService: TerritoryService) {

    private var manager: GameManager? = null
    val players: MutableList<Player> = mutableListOf()

    var count = 0

    fun getGameManager(): GameManager {
        if (!hasStarted()) {
            throw IllegalArgumentException("Game not started")
        } else {
            return manager!!
        }
    }

    fun addPlayer(name: String) = apply {
        players.add(Player(name, colors[count++]))
    }

    fun startGame() = apply {
        manager = gameFactory.mainGame(players.map { it.name }.toList())
    }

    fun hasStarted(): Boolean = manager != null


    fun convert(): GameVM {
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

        return GameVM(gameState.currentPlayer, gameState.phase, gameState.unitsToPlace, territories)
    }

    private fun error(errorMessage: String): GameVM {
        return GameVM("", "", 0, listOf(), errorMessage)
    }

    private fun getPlayer(playerName: String): Player {
        return players.find { it.name == playerName } ?: throw IllegalArgumentException("Missing color for $playerName")
    }

    private fun toTerritoryVM(territory: String, playerName: String, units: Int): TerritoryVM {
        val point = territoryService.getPosition(territory)
        val player = getPlayer(playerName)
        return TerritoryVM(territory, point.first, point.second, player, units)
    }
}
