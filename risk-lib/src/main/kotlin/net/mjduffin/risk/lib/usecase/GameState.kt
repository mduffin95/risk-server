package net.mjduffin.risk.lib.usecase

data class GameState(
    val currentPlayer: String,
    val phase: String,
    val unitsToPlace: Int,
    val territories: List<String>,
    var occupyingPlayers: List<String>,
    val units: List<Int>,
    val hasEnded: Boolean = false)