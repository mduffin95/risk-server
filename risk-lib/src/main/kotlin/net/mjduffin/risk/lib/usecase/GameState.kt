package net.mjduffin.risk.lib.usecase

data class GameState(
    val currentPlayer: String,
    val phase: String,
    val unitsToPlace: Int,
    val territories: List<String>,
    var occupyingPlayers: List<String>,
    val units: List<Int>,
    val lastAttackingTerritory: String? = null,
    val lastDefendingTerritory: String? = null,
    val lastAttackingUnitCount: Int = 0,
    val maxToMove: Int? = null,
)