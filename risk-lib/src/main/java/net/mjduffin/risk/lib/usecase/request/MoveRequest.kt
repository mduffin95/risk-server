package net.mjduffin.risk.lib.usecase.request

class MoveRequest(val playerName: String, val units: Int) : Request() {

    init {
        requestType = Type.MOVE
    }
}