package net.mjduffin.risk.lib.entities

/**
 * The game board which is completely immutable.
 */
class Board private constructor(
        private val adjTerritories: Map<TerritoryId, List<TerritoryId>>) {

    data class Builder(
            val adjTerritories: MutableMap<TerritoryId, List<TerritoryId>> = mutableMapOf()
    ) {

        fun addEdge(from: TerritoryId, to: TerritoryId) = apply {
            val edges = this.adjTerritories.getOrDefault(from, listOf())
            this.adjTerritories[from] = edges + to
        }

        fun build() = Board(adjTerritories.toMap())
    }

    fun allTerritories(): Set<TerritoryId> = adjTerritories.keys

    fun areAdjacent(a: TerritoryId, b: TerritoryId): Boolean {
        val adjacent = adjTerritories[a] ?: return false
        return adjacent.contains(b)
    }

    //TODO: Use BFS to check if two territories are connected
    fun areConnected(a: TerritoryId, b: TerritoryId): Boolean = true

//    fun getTerritory(name: String): Territory? = territories[TerritoryId(name)]

}