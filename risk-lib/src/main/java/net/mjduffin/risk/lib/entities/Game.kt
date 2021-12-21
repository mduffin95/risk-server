package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.PlayerNotFoundException
import net.mjduffin.risk.lib.usecase.TerritoryNotFoundException
import java.awt.datatransfer.ClipboardOwner
import java.util.function.Consumer
import kotlin.collections.HashSet

//Assume game is in draft mode as soon as it is created
// immutable
// all state that changes during a game
class Game private constructor(
    private val players: List<Player>,
    private val playerTerritories: Map<PlayerId, List<TerritoryId>>,
    private val territoryUnits: Map<TerritoryId, Int>,
    private val playerIndex: Int = 0,
    val state: State = State.ALLDRAFT
) {

    data class Builder(
        private var players: MutableList<Player> = mutableListOf(),
        private var playerTerritories: MutableMap<PlayerId, List<TerritoryId>> = mutableMapOf()
    ) {

        // add player and territories at the same time, therefore assigning a player to each territory
        fun addPlayerWithTerritories(playerName: String, territoryNames: List<String>) = apply {
            val player = Player(playerName)
            val territories = territoryNames.map { TerritoryId(it) }
            playerTerritories[player.getId()] = territories

            // add territories to board
            this.players.add(player)
        }

        fun build(): Game {
            val units = playerTerritories.values.flatMap { it }.distinct().map { it to 1 }.toMap()
            return Game(players.toList(), playerTerritories.toMap(), units)
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

    fun nextPlayer(): Game {
        val newIndex = (playerIndex + 1) % players.size
        return Game(players, playerTerritories, territoryUnits, newIndex, state)
    }

    val currentPlayer: PlayerId
        get() = players[playerIndex].getId()

    val isFirstPlayer: Boolean
        get() = playerIndex == 0

    fun getPlayer(name: String): Player {
        for (p in players) {
            if (p.name == name) {
                return p
            }
        }
        throw PlayerNotFoundException()
    }

    fun nextState(): Game {
        return Game(players, playerTerritories, territoryUnits, playerIndex, state.nextState())
    }

    fun getNumPlayers(): Int {
        return players.size
    }

    fun totalTerritories(playerId: PlayerId): Int = playerTerritories[playerId]?.size ?: 0

    fun currentDraftableUnits(): Int = calculateDraftableUnits(currentPlayer)

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

    // Unit operations
    fun getUnits(territoryId: TerritoryId): Int = territoryUnits[territoryId] ?: throw TerritoryNotFoundException()

    fun getAvailableUnits(territoryId: TerritoryId): Int = getUnits(territoryId) - 1

    fun moveUnits(from: TerritoryId, to: TerritoryId, units: Int): Game {
        return addUnits(from, -units).addUnits(to, units)
    }

    fun addUnits(territory: TerritoryId, units: Int): Game {
        val newUnits = territoryUnits.toMutableMap()
        newUnits[territory] = newUnits[territory]!! + units
        return Game(players, playerTerritories, newUnits.toMap(), playerIndex, state)
    }

    // Player transition
    fun setOwner(oldOwner: PlayerId, newOwner: PlayerId, territoryId: TerritoryId): Game {
        val newPlayerTerritories = playerTerritories.toMutableMap()
        newPlayerTerritories[oldOwner] = playerTerritories[oldOwner]!!.filter { it != territoryId } // remove from old
        newPlayerTerritories[newOwner] = playerTerritories[newOwner]!! + listOf(territoryId) // add to new player
        return Game(players, newPlayerTerritories.toMap(), territoryUnits, playerIndex, state)
    }
}