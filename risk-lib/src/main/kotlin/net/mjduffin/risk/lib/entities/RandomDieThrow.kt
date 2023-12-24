package net.mjduffin.risk.lib.entities

import java.util.*

class RandomDieThrow : DieThrow {
    var random = Random()
    override val dieValue: Int
        get() = random.nextInt(6) + 1
}