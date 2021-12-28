package net.mjduffin.risk.web.service

data class GameVM(val currentPlayer: String, val phase: String, val unitsToPlace: Int, val territories: List<TerritoryVM>, val error: String?)
data class LobbyVM(val players: List<String>, val error: String?)

data class TerritoryVM(val name: String, val top: Int, val left: Int, val player: String, val color: String, val units: Int)

data class Draft(val territory: String)
data class Attack(val from: String, val to: String)
data class Fortify(val from: String, val to: String, val units: Int)
data class Move(val units: Int)
data class Join(val player: String)
