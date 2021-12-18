package net.mjduffin.risk.lib.entities

import net.mjduffin.risk.lib.usecase.GameplayException
import java.util.*

class DiceManager(private val random: DieThrow) {
    class Result {
        var attackers = 0
        var defenders = 0
    }

//    @Throws(GameplayException::class)
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
        val result = Result()
        result.attackers = attackers
        result.defenders = defenders
        while (!attackResults.isEmpty() && !defendResults.isEmpty()) {
            val a = attackResults.poll()
            val d = defendResults.poll()
            if (a > d) {
                result.defenders--
            } else {
                result.attackers--
            }
        }
        return result
    }
}