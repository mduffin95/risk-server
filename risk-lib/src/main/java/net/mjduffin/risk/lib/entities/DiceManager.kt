package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.GameplayException
import java.util.*

class DiceManager(private val random: DieThrow) {
    data class Result(val attackers: Int = 0, val defenders: Int = 0)

    fun engage(attackers: Int, defenders: Int): Result {
        if (attackers > 3 || defenders > 2) {
            throw GameplayException("Incorrect number of dice")
        }
        val attackResults = PriorityQueue(Collections.reverseOrder<Int>())
        val defendResults = PriorityQueue(Collections.reverseOrder<Int>())
        for (i in 0 until attackers) {
            val num = random.dieValue
            attackResults.add(num)
        }
        for (i in 0 until defenders) {
            val num = random.dieValue
            defendResults.add(num)
        }
        var remainingAttackers = attackers
        var remainingDefenders = defenders
        while (!attackResults.isEmpty() && !defendResults.isEmpty()) {
            val a = attackResults.poll()
            val d = defendResults.poll()
            if (a > d) {
                remainingDefenders--
            } else {
                remainingAttackers--
            }
        }
        return Result(remainingAttackers, remainingDefenders)
    }
}