package net.mjduffin.risk.lib.usecase.request

open class Request {

    enum class Type {
        DRAFT, ATTACK, ENDATTACK, MOVE, FORTIFY, SKIPFORTIFY
    }

    var requestType: Type? = null
        protected set
}


