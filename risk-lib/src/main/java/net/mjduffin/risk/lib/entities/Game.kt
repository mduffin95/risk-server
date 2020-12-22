package net.mjduffin.risk.lib.entities

import java.util.function.Consumer
import kotlin.collections.HashSet

//Assume game is in draft mode as soon as it is created
class Game private constructor(
    val board: Board,
    private val players: List<Player>,
    private val playerTerritories: Map<PlayerId, List<TerritoryId>>
) {

    data class Builder(
        private var boardBuilder: Board.Builder = Board.Builder(),
        private var players: MutableList<Player> = mutableListOf(),
        private var playerTerritories: MutableMap<PlayerId, List<TerritoryId>> = mutableMapOf()
    ) {

        // add player and territories at the same time, therefore assigning a player to each territory
        fun addPlayerWithTerritories(playerName: String, territoryNames: List<String>) = apply {
            val player = Player(playerName)
            val territories = territoryNames.map { Territory(it) }
            playerTerritories[player.getId()] = territories.map { it.getId() }

            // add territories to board
            boardBuilder.addTerritories(territories)
            this.players.add(player)
        }

        fun addEdge(from: String, to: String) = apply {
            this.boardBuilder.addEdge(TerritoryId(from), TerritoryId(to))
        }

        fun build(): Game {
            val board = boardBuilder.build()
            return Game(board, players, playerTerritories)
        }
    }

    enum class State {
        ALLDRAFT {
            override fun nextState(): State {
                return ATTACK
            }
        },
        DRAFT {
            override fun nextState(): State {
                return ATTACK
            }
        },
        ATTACK {
            override fun nextState(): State {
                return MOVE
            }
        },
        MOVE {
            override fun nextState(): State {
                return ATTACK
            }
        },
        FORTIFY {
            override fun nextState(): State {
                return DRAFT
            }
        },
        END {
            override fun nextState(): State {
                return END
            }
        };

        abstract fun nextState(): State
    }

    var playerIndex = 0
    var state = State.ALLDRAFT
    var playerChangeObservers: MutableSet<PlayerChangeObserver> = HashSet()
    var stateChangeObservers: MutableSet<StateChangeObserver> = HashSet()

    //Return true if we have reached the end of a cycle
    fun nextPlayer() {
        val oldPlayer = currentPlayer
        playerIndex = (playerIndex + 1) % players.size
        val newPlayer = currentPlayer
        playerChangeObservers.forEach { observer -> observer.notify(oldPlayer, newPlayer) }
    }

    val currentPlayer: PlayerId
        get() = players[playerIndex].getId()

    val isFirstPlayer: Boolean
        get() = playerIndex == 0

    fun getPlayer(name: String): Player? {
        for (p in players) {
            if (p.name == name) {
                return p
            }
        }
        return null
    }

    fun nextState() {
        val oldState = state
        state = state.nextState()
        val newState = state
        stateChangeObservers.forEach(Consumer { x: StateChangeObserver -> x.notify(oldState, newState) })
    }

    fun getNumPlayers(): Int {
        return players.size
    }

    fun registerPlayerChangeObserver(observer: PlayerChangeObserver) {
        playerChangeObservers.add(observer)
    }

    fun registerStateChangeObserver(observer: StateChangeObserver) {
        stateChangeObservers.add(observer)
    }

    fun totalTerritories(playerId: PlayerId): Int = playerTerritories[playerId]?.size ?: 0

    fun calculateDraftableUnits(playerId: PlayerId): Int {
        currentPlayer

        return if (State.ALLDRAFT == state) {
            10
        } else {
            var territoryBonus = totalTerritories(playerId) / 3
            if (territoryBonus < 3) {
                territoryBonus = 3
            }
            territoryBonus
        }
    }

    private fun territoryToPlayerMap() =
        playerTerritories.entries.flatMap { entry -> entry.value.map { it to entry.key } }.toMap()

    fun getPlayerForTerritory(territoryId: TerritoryId): PlayerId? = territoryToPlayerMap()[territoryId]
}