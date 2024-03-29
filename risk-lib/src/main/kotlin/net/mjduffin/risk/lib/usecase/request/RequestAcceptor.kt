package net.mjduffin.risk.lib.usecase.request

import net.mjduffin.risk.lib.usecase.GameState

/**
 * Accepts a request.
 */
interface RequestAcceptor {
    fun receiveRequest(request: Request)
//    fun registerPlayerOutput(output: PlayerOutput)
    fun getGameState(): GameState
}