package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.GameplayException
import java.util.*

/**
 * The game board which is completely immutable.
 */
class Board private constructor(
    private val adjTerritories: Map<TerritoryId, Set<TerritoryId>>,
    private val continents: Map<Continent, Set<TerritoryId>>) {

    fun allTerritories(): Set<TerritoryId> = adjTerritories.keys

    fun areAdjacent(a: TerritoryId, b: TerritoryId): Boolean {
        val adjacent = getConnected(a)
        return adjacent.contains(b)
    }

    fun areConnected(a: TerritoryId, b: TerritoryId, playerLookup: (TerritoryId) -> PlayerId): Boolean {
        val player = playerLookup(a)
        require(player == playerLookup(b)) { "Players are not the same" }

        val visited = mutableSetOf<TerritoryId>()

        val connected: Set<TerritoryId> = getConnected(a).filter { player == playerLookup(it) }.toSet()
        val queue: Queue<TerritoryId> = LinkedList()
        queue.addAll(connected)

        while (queue.isNotEmpty()) {
            val id = queue.poll()
            if (id == b) {
                return true
            }
            visited.add(id)
            val unvisited: List<TerritoryId> = getConnected(id).stream().filter { !visited.contains(it) }.toList()
            queue.addAll(unvisited)
        }
        return false
    }

    private fun getConnected(territory: TerritoryId): Set<TerritoryId> {
        return adjTerritories[territory] ?: throw GameplayException("No territories connected to ${territory.name}")
    }

    data class Builder(
        val adjTerritories: MutableMap<TerritoryId, Set<TerritoryId>> = mutableMapOf(),
        val continents: MutableMap<Continent, Set<TerritoryId>> = mutableMapOf()
    ) {

        fun addEdge(from: String, to: String) = apply {
            this.addEdge(TerritoryId(from), TerritoryId(to))
        }

        fun addEdge(from: TerritoryId, to: TerritoryId) = apply {
            val edgesFrom = this.adjTerritories.getOrDefault(from, setOf())
            val edgesTo = this.adjTerritories.getOrDefault(to, setOf())
            this.adjTerritories[from] = edgesFrom + to
            this.adjTerritories[to] = edgesTo + from
        }

        fun addToContinent(continent: Continent, territory: TerritoryId) = apply {
            this.continents[continent] = this.continents.getOrDefault(continent, setOf()) + territory
        }

        fun build() = Board(adjTerritories.toMap(), continents.toMap())
    }
}