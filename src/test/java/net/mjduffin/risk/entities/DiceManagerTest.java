package net.mjduffin.risk.entities;

import net.mjduffin.risk.usecase.GameplayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DiceManagerTest {
    DiceManager diceManager;

    @BeforeEach
    public void setup() {


    }

    @Test
    public void diceTest() throws GameplayException {
        Random random = Mockito.mock(Random.class);
        when(random.nextInt()).thenReturn(1);
        DiceManager diceManager = new DiceManager(random);

        DiceManager.Result result = diceManager.engage(3, 2);
        assertEquals(1, result.attackers);
        assertEquals(2, result.defenders);
    }

}