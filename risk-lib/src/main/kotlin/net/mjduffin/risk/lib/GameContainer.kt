package net.mjduffin.risk.lib

import net.mjduffin.risk.lib.usecase.GameManager

interface GameContainer {
    fun getGameManager() : GameManager

    fun waitThreeSeconds()

    fun increment()
    fun addPlayer(name: String): Player
    fun startGame()
    fun toViewModel(): ViewModel
}