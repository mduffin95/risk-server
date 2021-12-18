package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.entities.DieThrow
import java.util.*

class RandomDieThrow : DieThrow {
    var random = Random()
    override val dieValue: Int
        get() = random.nextInt(6) + 1
}