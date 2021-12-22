package net.mjduffin.risk.lib.usecase.request

interface Request

data class MoveRequest(val playerName: String, val units: Int) : Request

data class FortifyRequest(
    val playerName: String,
    val fromTerritory: String,
    val toTerritory: String,
    val units: Int) : Request

data class EndAttackRequest(val playerName: String) : Request

data class DraftRequest(val player: String, val territory: String, val units: Int) : Request

data class AttackRequest(val player: String, val attacker: String, val defender: String) : Request

class SkipFortifyRequest : Request
