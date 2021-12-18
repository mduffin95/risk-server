package net.mjduffin.risk.lib.usecase.request

import net.mjduffin.risk.lib.usecase.GameState
import net.mjduffin.risk.lib.usecase.PlayerOutput

interface RequestAcceptor {
    fun receiveRequest(request: Request)
    fun registerPlayerOutput(output: PlayerOutput)
    val gameState: GameState
}