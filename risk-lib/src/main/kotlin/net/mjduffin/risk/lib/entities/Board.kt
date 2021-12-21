package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.GameplayException
import java.util.*

/**
 * The game board which is completely immutable.
 */
class Board private constructor(private val adjTerritories: Map<TerritoryId, List<TerritoryId>>) {

    fun allTerritories(): Set<TerritoryId> = adjTerritories.keys

    fun areAdjacent(a: TerritoryId, b: TerritoryId): Boolean {
        val adjacent = getConnected(a)
        return adjacent.contains(b)
    }

    fun areConnected(a: TerritoryId, b: TerritoryId, playerLookup: (TerritoryId) -> PlayerId): Boolean {
        val player = playerLookup(a)
        require(player == playerLookup(b)) { "Players are not the same" }

        val visited = mutableSetOf<TerritoryId>()

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

    private fun getConnected(territory: TerritoryId): List<TerritoryId> {
        return adjTerritories[territory] ?: throw GameplayException("No territories connected to ${territory.name}")
    }

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
}