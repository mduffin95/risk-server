package net.mjduffin.risk.lib.usecase.request

class EndAttackRequest(val playerName: String) : Request() {

    init {
        requestType = Type.ENDATTACK
    }
}