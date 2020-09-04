package net.mjduffin.risk.lib.entities

data class Player(val name: String) {

    private val territories: MutableSet<Territory> = HashSet()
    var draftableUnits = 0
        private set

    fun addTerritory(t: Territory) {
        territories.add(t)
    }

    fun removeTerritory(t: Territory) {
        territories.remove(t)
    }

    val totalUnits: Int
        get() = territories.stream().map(Territory::units).reduce(0) { a: Int, b: Int -> Integer.sum(a, b) }
    val totalTerritories: Int
        get() = territories.size

    fun calulateAndSetDraftableUnits(state: Game.State) {
        if (Game.State.ALLDRAFT == state) {
            draftableUnits = 10
        } else {
            var territoryBonus = totalTerritories / 3
            if (territoryBonus < 3) {
                territoryBonus = 3
            }
            draftableUnits = territoryBonus
        }
    }

    fun useUnits(units: Int) {
        draftableUnits -= units

        //TODO: If draftableUnits reaches zero, trigger next player and next game state
    }

    fun finishedDrafting(): Boolean = draftableUnits == 0

    //Assume units is within our draft limit
    fun draft(territory: Territory, units: Int): Boolean {
        if (territories.contains(territory)) {
            territory.addUnits(units)
            return true
        }
        return false
    }

}