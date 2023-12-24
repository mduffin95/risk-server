package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.entities.GameConstants.DRAFT_MAX
import net.mjduffin.risk.lib.usecase.PlayerNotFoundException
import net.mjduffin.risk.lib.usecase.TerritoryNotFoundException

object GameConstants {
    const val DRAFT_MAX = 10;
}

//Assume game is in draft mode as soon as it is created
// immutable
// all state that changes during a game
class Game private constructor(
    private val players: List<PlayerId>,
    private val playerTerritories: Map<PlayerId, List<TerritoryId>>,
    private val territoryUnits: Map<TerritoryId, Int>,
    val currentPlayer: PlayerId,
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

        private fun getUnitsMap(): Map<PlayerId, Int> {
            // TODO: Calculate draft properly
            return players.map { it to DRAFT_MAX }.toMap()
        }

        fun build(): Game {
            val units = playerTerritories.values.flatMap { it }.distinct().map { it to 1 }.toMap()
            return Game(players.toList(), playerTerritories.toMap(), units, players[0], state, getUnitsMap())
        }
    }

    enum class State {
        ALLDRAFT {
            override fun nextState(): State {
                return DRAFT
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
        val index = players.indexOf(currentPlayer)
        val newIndex = (index + 1) % players.size
        if (newIndex == index) {
            // if there is only one player left then we can't move to another player,
            // so it doesn't make sense to re-calculate the draft
            return this
        }
        return Game(players, playerTerritories, territoryUnits, players[newIndex], state, draftRemaining)
    }

    val isFirstPlayer: Boolean
        get() = players.indexOf(currentPlayer) == 0

    fun getPlayer(name: String): PlayerId {
        for (p in players) {
            if (p.name == name) {
                return p
            }
        }
        throw PlayerNotFoundException()
    }

    fun nextState(board: Board): Game {
        val hasEnded = playerTerritories.keys.size == 1
        val next = if (hasEnded) State.END else state.nextState()
        val draft = if (next === State.DRAFT) players.map { it to calculateDraft(it, board, next) }.toMap() else draftRemaining
        return goToState(next, draft)
    }

    fun goToState(newState: State): Game = goToState(newState, draftRemaining)

    private fun goToState(newState: State, draft: Map<PlayerId, Int>): Game {
        return Game(players, playerTerritories, territoryUnits, currentPlayer, newState, draft)
    }

    private fun totalTerritories(playerId: PlayerId): Int = playerTerritories[playerId]?.size ?: 0

    fun currentDraftableUnits(): Int = getDraftableUnits(currentPlayer)

    fun getDraftableUnits(playerId: PlayerId): Int {
        return draftRemaining[playerId] ?: throw PlayerNotFoundException()
    }

    private fun calculateDraft(playerId: PlayerId, board: Board, nextState: State): Int {
        return if (State.ALLDRAFT == nextState) {
            DRAFT_MAX
        } else {
            var territoryBonus = totalTerritories(playerId) / 3
            if (territoryBonus < 3) {
                territoryBonus = 3
            }
            val territories = playerTerritories[playerId]?.toSet() ?: throw PlayerNotFoundException()
            val continentBonus = board.fullContinents(territories).map { it.bonus }.sum()

            territoryBonus + continentBonus
        }
    }

    val territoryToPlayerMap
        get() = playerTerritories.entries
            .flatMap { entry -> entry.value.map { it to entry.key } }
            .toMap()

    fun getPlayerForTerritory(territoryId: TerritoryId): PlayerId {
        return territoryToPlayerMap[territoryId] ?: throw PlayerNotFoundException()
    }

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
        return Game(players, playerTerritories, newUnits.toMap(), currentPlayer, state, draftRemaining)
    }

    fun useUnits(player: PlayerId, units: Int): Game {
        val newDraft = draftRemaining.toMutableMap()
        val updated = newDraft[player]!! - units
        assert(updated >= 0)
        newDraft[player] = updated
        return Game(players, playerTerritories, territoryUnits, currentPlayer, state, newDraft.toMap())
    }

    // Player transition
    fun setOwner(oldOwner: PlayerId, newOwner: PlayerId, territoryId: TerritoryId): Game {
        val newPlayerTerritories = playerTerritories.toMutableMap()
        val losingPlayerTerritories = playerTerritories[oldOwner]!!.filter { it != territoryId } // remove from old
        val newPlayers = players.toMutableList()
        if (losingPlayerTerritories.isEmpty()) {
            newPlayerTerritories.remove(oldOwner)
            newPlayers.remove(oldOwner)
            // update playerIndex if necessary
        } else {
            newPlayerTerritories[oldOwner] = losingPlayerTerritories
        }
        newPlayerTerritories[newOwner] = playerTerritories[newOwner]!! + listOf(territoryId) // add to new player
        return Game(
            newPlayers.toList(),
            newPlayerTerritories.toMap(),
            territoryUnits,
            currentPlayer,
            state,
            draftRemaining
        )
    }
}