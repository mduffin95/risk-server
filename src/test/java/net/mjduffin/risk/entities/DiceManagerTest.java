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
        DieThrow dt = Mockito.mock(DieThrow.class);
        when(dt.getDieValue()).thenReturn(1);
        DiceManager diceManager = new DiceManager(dt);

        DiceManager.Result result = diceManager.engage(3, 2);
        assertEquals(1, result.attackers);
        assertEquals(2, result.defenders);
    }

    @Test
    public void diceAttackerWins() throws GameplayException {
        DieThrow dt = Mockito.mock(DieThrow.class);
        when(dt.getDieValue()).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(4).thenReturn(5);
        DiceManager diceManager = new DiceManager(dt);

        DiceManager.Result result = diceManager.engage(3, 2);
        assertEquals(3, result.attackers);
        assertEquals(0, result.defenders);
    }

}