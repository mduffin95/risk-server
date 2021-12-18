package net.mjduffin.risk.lib.usecase

class AttackResult {
    var attackTerritory: String? = null
    var defendTerritory: String? = null
    @JvmField
    var attackUnits = 0
    @JvmField
    var defendUnits = 0
}