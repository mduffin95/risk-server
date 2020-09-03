package net.mjduffin.risk.lib.entities

class Board private constructor(builder: Board.Builder) {

    val territories: List<Territory>
    val adjTerritories: Map<Territory, List<Territory>>

    init {
        territories = builder.territories
        adjTerritories = builder.adjTerritories
    }

    data class Builder(
            val territories: MutableList<Territory> = mutableListOf(),
            val adjTerritories: MutableMap<Territory, List<Territory>> = mutableMapOf()
    ) {
        private fun findTerritory(name: String): Territory {
            return territories.first { it.name == name }
        }

        fun addTerritories(territories: List<Territory>) = apply { this.territories.addAll(territories) }

        fun addEdge(from: String, to: String) = apply {
            val fromTerritory = findTerritory(from)
            val edges = this.adjTerritories.getOrDefault(fromTerritory, listOf())
            this.adjTerritories[fromTerritory] = edges + findTerritory(to)
        }

        fun build() = Board(this)
    }

    fun areAdjacent(a: Territory, b: Territory): Boolean {
        val adjacent = adjTerritories[a] ?: return false
        return adjacent.contains(b)
    }

    //TODO: Use BFS to check if two territories are connected
    fun areConnected(a: Territory, b: Territory): Boolean = true

    fun getTerritory(name: String): Territory? = territories.first { it.name == name }

}