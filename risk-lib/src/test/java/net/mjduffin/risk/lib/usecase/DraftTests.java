package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DraftTests {
    private final String BOB = "Bob";
    private final String ALICE = "Alice";

    PlayerInput getPlayerInputFromGame(Game game) {
        DieThrow dieThrow = Mockito.mock(DieThrow.class);
        when(dieThrow.getDieValue()).thenReturn(1);
        DiceManager diceManager = new DiceManager(dieThrow);
        return new GameManager(game, diceManager);
    }

    @Test
    void draft() throws GameplayException {
        // given
        Game.Builder gameBuilder = new Game.Builder();

        gameBuilder.addPlayerWithTerritories(BOB, Arrays.asList("England", "Wales"));
        Game game = gameBuilder.build();

        PlayerInput playerInput = getPlayerInputFromGame(game);

        // when
        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 1);
        draft.put("Wales", 3);
        playerInput.draft(BOB, draft);

        assertEquals(2, game.getBoard().getTerritory("England").getUnits());
        assertEquals(4, game.getBoard().getTerritory("Wales").getUnits());
    }

    @Test
    void draftTwiceInRow() throws GameplayException {
        // given
        Game.Builder gameBuilder = new Game.Builder();

        // TODO: This is currently dependent on the order
        gameBuilder.addPlayerWithTerritories(BOB, Collections.singletonList("Wales"));
        gameBuilder.addPlayerWithTerritories(ALICE, Collections.singletonList("England"));
        Game game = gameBuilder.build();

        PlayerInput playerInput = getPlayerInputFromGame(game);

        Map<String, Integer> draft = new HashMap<>();
        draft.put("Wales", 4);
        playerInput.draft(BOB, draft);
        draft.clear();
        draft.put("England", 1);
        playerInput.draft(ALICE, draft);
        assertThrows(GameplayException.class, () -> playerInput.draft(BOB, draft));

    }

}