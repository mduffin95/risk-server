package net.mjduffin.risk.entities;

import java.util.Random;

public class RandomDieThrow implements DieThrow {
    Random random = new Random();

    @Override
    public int getDieValue() {
        return random.nextInt(6) + 1;
    }
}
