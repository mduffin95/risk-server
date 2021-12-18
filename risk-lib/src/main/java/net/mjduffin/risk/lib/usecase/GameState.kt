package net.mjduffin.risk.lib.usecase

data class GameState(val currentPlayer: String, val phase: String) {
    var unitsToPlace = 0
    var territories: List<String> = listOf()
    var occupyingPlayers: List<String> = listOf()
    var units: List<Int> = listOf()
    var hasEnded = false
}