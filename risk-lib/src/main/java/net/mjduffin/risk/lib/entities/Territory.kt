package net.mjduffin.risk.lib.entities

data class Territory(val name: String, @JvmField var player: Player) {

    init {
        player.addTerritory(this)
    }

    var units = 1
        private set

    val availableUnits: Int
        get() = units - 1

    fun addUnits(num: Int) {
        units += Math.abs(num)
    }

    fun subtractUnits(num: Int) {
        if (num <= units) {
            units -= Math.abs(num)
        }
    }
}