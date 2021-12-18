package net.mjduffin.risk.lib.usecase.request

class DraftRequest(val player: String, val territory: String, val units: Int) : Request() {

    init {
        requestType = Type.DRAFT
    }
}