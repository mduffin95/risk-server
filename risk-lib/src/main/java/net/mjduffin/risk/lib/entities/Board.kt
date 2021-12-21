package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.GameplayException
import java.util.*

/**
 * The game board which is completely immutable.
 */
class Board private constructor(
        private val adjTerritories: Map<TerritoryId, List<TerritoryId>>) {

    data class Builder(
            val adjTerritories: MutableMap<TerritoryId, List<TerritoryId>> = mutableMapOf()
    ) {

        fun addEdge(from: String, to: String) = apply {
            this.addEdge(TerritoryId(from), TerritoryId(to))
        }

        fun addEdge(from: TerritoryId, to: TerritoryId) = apply {
            val edgesFrom = this.adjTerritories.getOrDefault(from, listOf())
            val edgesTo = this.adjTerritories.getOrDefault(to, listOf())
            this.adjTerritories[from] = edgesFrom + to
            this.adjTerritories[to] = edgesTo + from
        }

        fun build() = Board(adjTerritories.toMap())
    }

    fun allTerritories(): Set<TerritoryId> = adjTerritories.keys

    fun areAdjacent(a: TerritoryId, b: TerritoryId): Boolean {
        val adjacent = adjTerritories[a] ?: return false
        return adjacent.contains(b)
    }

    //TODO: Use BFS to check if two territories are connected
    fun areConnected(a: TerritoryId, b: TerritoryId, playerLookup: (TerritoryId) -> PlayerId): Boolean {
        val player = playerLookup(a)
        require(player == playerLookup(b)) { "Players are not the same" }

        val visited = mutableSetOf<TerritoryId>()
        val getConnected =
            { x: TerritoryId -> adjTerritories[x] ?: throw GameplayException("No territories connected to ${a.name}") }
        val connected: List<TerritoryId> = getConnected(a).filter { player == playerLookup(it) }.toList()
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

//    fun getTerritory(name: String): Territory? = territories[TerritoryId(name)]

}