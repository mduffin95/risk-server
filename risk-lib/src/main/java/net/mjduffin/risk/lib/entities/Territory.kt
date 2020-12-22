package net.mjduffin.risk.lib.entities

data class TerritoryId(val name: String)

data class Territory(val name: String, val bonus: Int = 2) {
    fun getId(): TerritoryId {
        return TerritoryId(name)
    }
}