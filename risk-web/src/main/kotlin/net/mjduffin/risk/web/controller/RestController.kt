package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.*
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

val colors = listOf("red", "blue", "green", "violet", "orange", "magenta", "yellow")

@RestController
@RequestMapping("api")
class RestController(val territoryService: TerritoryService, val gameFactory: GameFactory) {

    val log: Logger = LoggerFactory.getLogger(RestController::class.java)

    private val containers: MutableMap<String, GameContainer> = mutableMapOf()

    fun getGameManager(id: String): GameManager? {
        val container = containers[id] ?: return null
        return container.getGameManager()
    }

    @PostMapping("/{id}/draft")
    fun draft(@PathVariable("id") id: String, @RequestBody draft: Draft): Response {
        log.info("Draft")
        val container = containers[id] ?: return Response("No game found for $id")
        val gameManager = getGameManager(id) ?: return Response("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.draftSingle(currentState.currentPlayer, draft.territory, 1)
            container.increment()
        } catch (ex: Exception) {
        }
        return Response(null)
    }

    @PostMapping("/{id}/attack")
    fun attack(@PathVariable("id") id: String, @RequestBody attack: Attack): Response {
        log.info("Attack")
        val container = containers[id] ?: return Response("No game found for $id")
        val gameManager = getGameManager(id) ?: return Response("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.attack(currentState.currentPlayer, attack.from, attack.to)
            container.increment()
        } catch (ex: Exception) {
        }
        return Response(null)
    }

    @PostMapping("/{id}/move")
    fun move(@PathVariable("id") id: String, @RequestBody move: Move): Response {
        log.info("Move")
        val container = containers[id] ?: return Response("No game found for $id")
        val gameManager = getGameManager(id) ?: return Response("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.move(currentState.currentPlayer, move.units)
            container.increment()
        } catch (ex: Exception) {
        }
        return Response(null)
    }

    @GetMapping("/{id}/end")
    fun end(@PathVariable("id") id: String): Response {
        log.info("End turn for {}", id)
        val container = containers[id] ?: return Response("No game found for $id")
        val gameManager = container.getGameManager()
        val currentState = gameManager.getGameState()
        try {
            if (currentState.phase.equals("ATTACK")) {
                gameManager.endAttack(currentState.currentPlayer)
            } else {
                gameManager.endTurn()
            }
            container.increment()
        } catch (ex: Exception) {
        }
        return Response(null)
    }

    @PostMapping("/{id}/fortify")
    fun fortify(@PathVariable("id") id: String, @RequestBody fortify: Fortify): Response {
        log.info("Fortfiy {}", id)
        val container = containers[id] ?: return Response("No game found for $id")
        val gameManager = getGameManager(id) ?: return Response("No game found for $id")
        val currentState = gameManager.getGameState()
        try {
            gameManager.fortify(currentState.currentPlayer, fortify.from, fortify.to, fortify.units)
            container.increment()
        } catch (ex: Exception) {
        }
        return Response(null)
    }

    @GetMapping("/newgame")
    fun newGame(): String {
        log.info("Start new game")
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()

        containers[id] = GameContainer(gameFactory, territoryService)

        return id
    }

    @PostMapping("/{id}/join")
    fun join(@PathVariable("id") id: String, @RequestBody join: Join): Response {
        log.info("New player {} joined game {}", join.player, id)
        val gameContainer = containers[id]

        gameContainer?.addPlayer(join.player)

        return Response(null)
    }

    @PostMapping("/{id}/game")
    fun game(@PathVariable("id") id: String, @RequestBody clientVm: ActionCount): ViewModel {
        val c = containers[id]
        return if (c != null) {
            val vm = c.toViewModel()
            if (vm.actionCount > clientVm.actionCount) {
                vm
            } else {
                c.waitThreeSeconds()
                c.toViewModel()
            }
        } else {
            ViewModel(Screen.ERROR, 0, "Container missing for $id")
        }
    }

    @GetMapping("/{id}/start")
    fun start(@PathVariable("id") id: String): Response {
        log.info("Start game {}", id)
        val gameContainer = containers[id]!!
        gameContainer.startGame()
        return Response(null)
    }

    fun getViewModel(id: String): ViewModel =
        containers[id]?.toViewModel() ?: ViewModel(Screen.ERROR, 0, "Container not found: $id")
}


class GameContainer(private val gameFactory: GameFactory, private val territoryService: TerritoryService) {

    private var manager: GameManager? = null
    private val players: MutableList<Player> = mutableListOf()

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    var actionCount = 0
    var playerCount = 0

    fun getGameManager(): GameManager {
        if (!hasStarted()) {
            throw IllegalArgumentException("Game not started")
        } else {
            return manager!!
        }
    }

    fun waitThreeSeconds() {
        lock.withLock { condition.await(3, TimeUnit.SECONDS) }
    }

    fun increment() {
        actionCount++
        lock.withLock { condition.signalAll() }
    }

    fun addPlayer(name: String) = apply {
        players.add(Player(name, colors[playerCount++]))
        increment()
    }

    fun startGame() = apply {
        manager = gameFactory.mainGame(players.map { it.name }.toList())
        increment()
    }

    private fun hasStarted(): Boolean = manager != null

    fun toViewModel(): ViewModel {
        return if (hasStarted()) {
            ViewModel(Screen.GAME, actionCount, convert())
        } else {
            val lobby = LobbyVM(players)
            ViewModel(Screen.LOBBY, actionCount, lobby)
        }
    }

    private fun convert(): GameVM {
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
