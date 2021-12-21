package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.PlayerNotFoundException
import net.mjduffin.risk.lib.usecase.TerritoryNotFoundException

//Assume game is in draft mode as soon as it is created
// immutable
// all state that changes during a game
class Game private constructor(
    private val players: List<PlayerId>,
    private val playerTerritories: Map<PlayerId, List<TerritoryId>>,
    private val territoryUnits: Map<TerritoryId, Int>,
    private val playerIndex: Int = 0,
    val state: State = State.ALLDRAFT,
    private val draftRemaining: Map<PlayerId, Int> = mapOf()
) {

    data class Builder(
        private var players: MutableList<PlayerId> = mutableListOf(),
        private var playerTerritories: MutableMap<PlayerId, List<TerritoryId>> = mutableMapOf(),
        private var state: State = State.ALLDRAFT
    ) {

        // add player and territories at the same time, therefore assigning a player to each territory
        fun addPlayerWithTerritories(playerName: String, territoryNames: List<String>) = apply {
            val player = PlayerId(playerName)
            val territories = territoryNames.map { TerritoryId(it) }
            playerTerritories[player] = territories

            // add territories to board
            this.players.add(player)
        }

//        private fun getUnits(playerId: PlayerId): Int {
//            return if (State.ALLDRAFT == state) {
//                10
//            } else {
//                var territoryBonus = totalTerritories(playerId) / 3
//                if (territoryBonus < 3) {
//                    territoryBonus = 3
//                }
//                territoryBonus
//            }
//        }

        private fun getUnitsMap(): Map<PlayerId, Int> {
            return players.map { it to 10 }.toMap()
        }

        fun build(): Game {
            val units = playerTerritories.values.flatMap { it }.distinct().map { it to 1 }.toMap()
            return Game(players.toList(), playerTerritories.toMap(), units, state = state, draftRemaining = getUnitsMap())
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
        // calculate new draft
        val draft = players.map { it to calculateDraft(it) }.toMap()

        return Game(players, playerTerritories, territoryUnits, playerIndex = newIndex, state = state, draft)
    }

    val currentPlayer: PlayerId
        get() = players[playerIndex]

    val isFirstPlayer: Boolean
        get() = playerIndex == 0

    fun getPlayer(name: String): PlayerId {
        for (p in players) {
            if (p.name == name) {
                return p
            }
        }
        throw PlayerNotFoundException()
    }

    fun nextState(): Game {
        return goToState(state.nextState())
    }

    fun goToState(newState: State): Game {
        return Game(players, playerTerritories, territoryUnits, playerIndex, newState, draftRemaining)
    }

    fun getNumPlayers(): Int {
        return players.size
    }

    fun totalTerritories(playerId: PlayerId): Int = playerTerritories[playerId]?.size ?: 0

    fun currentDraftableUnits(): Int = getDraftableUnits(currentPlayer)

    fun getDraftableUnits(playerId: PlayerId): Int {
        return draftRemaining[playerId] ?: throw PlayerNotFoundException()
    }

    private fun calculateDraft(playerId: PlayerId): Int {
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

    fun getPlayerForTerritory(territoryId: TerritoryId): PlayerId =
        territoryToPlayerMap()[territoryId] ?: throw PlayerNotFoundException()

    // Unit operations
    fun getUnits(territoryId: TerritoryId): Int = territoryUnits[territoryId] ?: throw TerritoryNotFoundException()

    fun getAvailableUnits(territoryId: TerritoryId): Int = getUnits(territoryId) - 1

    fun moveUnits(from: TerritoryId, to: TerritoryId, units: Int): Game {
        return addUnits(from, -units).addUnits(to, units)
    }

    fun addUnits(territory: TerritoryId, units: Int): Game {
        val newUnits = territoryUnits.toMutableMap()
        val updated = newUnits[territory]!! + units
        assert(updated >= 0) { "Updated units for territory ${territory.name} are less than zero" }
        newUnits[territory] = updated
        return Game(players, playerTerritories, newUnits.toMap(), playerIndex, state, draftRemaining)
    }

    fun useUnits(player: PlayerId, units: Int): Game {
        val newDraft = draftRemaining.toMutableMap()
        val updated = newDraft[player]!! - units
        assert(updated >= 0)
        newDraft[player] = updated
        return Game(players, playerTerritories, territoryUnits, playerIndex, state, newDraft.toMap())
    }

    // Player transition
    fun setOwner(oldOwner: PlayerId, newOwner: PlayerId, territoryId: TerritoryId): Game {
        val newPlayerTerritories = playerTerritories.toMutableMap()
        newPlayerTerritories[oldOwner] = playerTerritories[oldOwner]!!.filter { it != territoryId } // remove from old
        newPlayerTerritories[newOwner] = playerTerritories[newOwner]!! + listOf(territoryId) // add to new player
        return Game(players, newPlayerTerritories.toMap(), territoryUnits, playerIndex, state, draftRemaining)
    }
}