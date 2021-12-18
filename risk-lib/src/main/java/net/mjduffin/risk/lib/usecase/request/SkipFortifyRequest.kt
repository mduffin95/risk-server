package net.mjduffin.risk.lib.usecase.request

class SkipFortifyRequest : Request() {
    init {
        requestType = Type.SKIPFORTIFY
    }
}