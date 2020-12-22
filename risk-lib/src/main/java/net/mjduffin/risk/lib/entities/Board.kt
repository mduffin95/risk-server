package net.mjduffin.risk.lib.entities

class Board private constructor(builder: Board.Builder) {

    val territories: Map<TerritoryId, Territory> = builder.territories
    private val adjTerritories: Map<TerritoryId, List<TerritoryId>> = builder.adjTerritories

    data class Builder(
            val territories: MutableMap<TerritoryId, Territory> = mutableMapOf(),
            val adjTerritories: MutableMap<TerritoryId, List<TerritoryId>> = mutableMapOf()
    ) {

        fun addTerritories(territories: List<Territory>) = apply { this.territories.putAll(territories.map { it.getId() to it }.toMap()) }

        fun addEdge(from: TerritoryId, to: TerritoryId) = apply {
            val fromTerritory = territories[from] ?: throw IllegalArgumentException("TerritoryId does not exist: $from")
            val edges = this.adjTerritories.getOrDefault(fromTerritory.getId(), listOf())
            this.adjTerritories[from] = edges + to
        }

        fun build() = Board(this)
    }

    fun areAdjacent(a: TerritoryId, b: TerritoryId): Boolean {
        val adjacent = adjTerritories[a] ?: return false
        return adjacent.contains(b)
    }

    //TODO: Use BFS to check if two territories are connected
    fun areConnected(a: TerritoryId, b: TerritoryId): Boolean = true

//    fun getTerritory(name: String): Territory? = territories[TerritoryId(name)]

}