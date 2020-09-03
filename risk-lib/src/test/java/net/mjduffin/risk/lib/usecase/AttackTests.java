package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.Board;
import net.mjduffin.risk.lib.entities.DiceManager;
import net.mjduffin.risk.lib.entities.DieThrow;
import net.mjduffin.risk.lib.entities.Game;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AttackTests {
    String PLAYER_A = "PlayerA";
    String PLAYER_B = "PlayerB";

    PlayerInput createTwoPlayerGame(DieThrow dieThrow) {

        Game.Builder gameBuilder = new Game.Builder();
        gameBuilder.addPlayerWithTerritories(PLAYER_A, Collections.singletonList("England"));
        gameBuilder.addPlayerWithTerritories(PLAYER_B, Collections.singletonList("Wales"));

        Game game = gameBuilder.build();

        DiceManager diceManager = new DiceManager(dieThrow);

        return new GameManager(game, diceManager);
    }

    @Test
    void attack() throws TerritoryNotFoundException, PlayerNotFoundException, GameplayException {
        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 3);
//        draft.put("Wales", 3);

        DieThrow dieThrow = Mockito.mock(DieThrow.class);
        when(dieThrow.getDieValue()).thenReturn(6).thenReturn(6).thenReturn(6).thenReturn(1).thenReturn(1);
        PlayerInput playerInput = createTwoPlayerGame(dieThrow);
        playerInput.draft(PLAYER_A, draft);
        draft = new HashMap<>();
        draft.put("Wales", 1);
        playerInput.draft(PLAYER_B, draft);

        AttackResult result = playerInput.attack(PLAYER_A, "England", "Wales");

        assertEquals(4, result.attackUnits);
        assertEquals(0, result.defendUnits);
    }

    @Test
    void attackDefenderWins() throws TerritoryNotFoundException, PlayerNotFoundException, GameplayException {
        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 3);

        DieThrow dieThrow = Mockito.mock(DieThrow.class);
        when(dieThrow.getDieValue()).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(3).thenReturn(3).thenReturn(1).thenReturn(4);
        PlayerInput playerInput = createTwoPlayerGame(dieThrow);
        playerInput.draft(PLAYER_A, draft);
        draft = new HashMap<>();
        draft.put("Wales", 1);
        playerInput.draft(PLAYER_B, draft);

        AttackResult result = playerInput.attack(PLAYER_A, "England", "Wales");

        //There has to be one unit left for the attacker
        assertEquals(1, result.attackUnits);
        assertEquals(2, result.defendUnits);
    }

}
