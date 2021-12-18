package net.mjduffin.risk.lib.usecase.request

import net.mjduffin.risk.lib.usecase.request.Request

class AttackRequest(player: String, attacker: String, defender: String) : Request() {
    val player: String
    val attacker: String
    val defender: String

    init {
        requestType = Type.ATTACK
        this.player = player
        this.attacker = attacker
        this.defender = defender
    }
}