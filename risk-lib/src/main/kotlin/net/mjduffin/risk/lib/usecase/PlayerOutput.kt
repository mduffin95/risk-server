package net.mjduffin.risk.lib.usecase

interface PlayerOutput {
    fun turn(gameState: GameState)
}