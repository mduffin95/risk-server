package net.mjduffin.risk.lib.entities;

import net.mjduffin.risk.lib.usecase.GameplayException;

import java.util.Collections;
import java.util.PriorityQueue;

public class DiceManager {
    private final DieThrow random;

    public static class Result {
        protected int attackers;
        protected int defenders;

        public int getAttackers() {
            return attackers;
        }

        public int getDefenders() {
            return defenders;
        }
    }

    public DiceManager(DieThrow random) {
        this.random = random;
    }

    public Result engage(int attackers, int defenders) throws GameplayException {
        if (attackers > 3 || defenders > 2) {
            throw new GameplayException("Incorrect number of dice");
        }
        PriorityQueue<Integer> attackResults = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> defendResults = new PriorityQueue<>(Collections.reverseOrder());

        for (int i = 0; i < attackers; i++) {
            int num = random.getDieValue();
            attackResults.add(num);
        }
        for (int i = 0; i < defenders; i++) {
            int num = random.getDieValue();
            defendResults.add(num);
        }

        Result result = new Result();
        result.attackers = attackers;
        result.defenders = defenders;
        while (!attackResults.isEmpty() && !defendResults.isEmpty()) {
            int a = attackResults.poll();
            int d = defendResults.poll();

            if (a > d) {
                result.defenders--;
            } else {
                result.attackers--;
            }
        }

        return result;
    }
}
