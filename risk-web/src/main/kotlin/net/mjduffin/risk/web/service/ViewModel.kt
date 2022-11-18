package net.mjduffin.risk.web.service


enum class Screen {
    GAME,
    LOBBY,
    ERROR
}

data class ViewModel(val screen: Screen, val actionCount: Int, val model: Any)

data class GameVM(val currentPlayer: String, val phase: String, val unitsToPlace: Int, val territories: List<TerritoryVM>, val error: String? = null)
data class LobbyVM(val players: List<Player>)
data class Player(val name: String, val color: String)
data class Response(val error: String?)

data class TerritoryVM(val name: String, val top: Int, val left: Int, val player: Player, val units: Int)

data class Draft(val territory: String)
data class Attack(val from: String, val to: String)
data class Fortify(val from: String, val to: String, val units: Int)
data class Move(val units: Int)
