package net.mjduffin.risk.lib.usecase.request

class FortifyRequest(val playerName: String, val fromTerritory: String, val toTerritory: String, val units: Int) :
    Request() {

    init {
        requestType = Type.FORTIFY
    }
}