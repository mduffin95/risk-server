package net.mjduffin.risk.lib.usecase

interface PlayerInput {
    fun draft(playerName: String, draft: Map<String, Int>)

    fun draftSingle(playerName: String, territory: String, units: Int)

    fun attack(playerName: String, attackingTerritory: String, defendingTerritory: String): AttackResult

    fun endAttack(playerName: String)

    fun move(playerName: String, units: Int)

    fun fortify(playerName: String, fromTerritory: String, toTerritory: String, units: Int)
}