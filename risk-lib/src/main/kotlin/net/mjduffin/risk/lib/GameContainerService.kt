package net.mjduffin.risk.lib

interface GameContainerService {

    fun createGame() : String

    fun getContainer(gameId: String): GameContainer?
}