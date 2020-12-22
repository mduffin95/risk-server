package net.mjduffin.risk.lib.entities

data class PlayerId(val name: String)

data class Player(val name: String) {

    var draftableUnits = 0
        private set

    fun getId(): PlayerId = PlayerId(name)

//    val totalUnits: Int
//        get() = territories.stream().map(Territory::units).reduce(0) { a: Int, b: Int -> Integer.sum(a, b) }
//    val totalTerritories: Int
//        get() = territories.size



    fun useUnits(units: Int) {
        draftableUnits -= units

        //TODO: If draftableUnits reaches zero, trigger next player and next game state
    }

    fun finishedDrafting(): Boolean = draftableUnits == 0

    //Assume units is within our draft limit
//    fun draft(territory: Territory, units: Int): Boolean {
//        if (territories.contains(territory)) {
//            territory.addUnits(units)
//            return true
//        }
//        return false
//    }

}