package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DraftTests {
    private final String BOB = "Bob";
    private final String ALICE = "Alice";


    private Board simpleBoard() {
        BoardBuilder boardBuilder = new BoardBuilder();
        boardBuilder.addTerritory("England")
                .addTerritory("Wales");
        boardBuilder.addEdge("England", "Wales");

        return boardBuilder.build();
    }

    private GameBuilder simpleGameBuilderStarter() {
        Board board = simpleBoard();
        GameBuilder gameBuilder = new GameBuilder();

        gameBuilder.board(board);
        gameBuilder.addPlayer(BOB);
        return gameBuilder;
    }

    PlayerInput getPlayerInputFromGame(Game game) {
        DieThrow dieThrow = Mockito.mock(DieThrow.class);
        when(dieThrow.getDieValue()).thenReturn(1);
        DiceManager diceManager = new DiceManager(dieThrow);
        return new GameManager(game, diceManager);
    }

    @Test
    void draft() throws GameplayException {
        // given
        GameBuilder gameBuilder = simpleGameBuilderStarter();
        Game game = gameBuilder.build();
        PlayerInput playerInput = getPlayerInputFromGame(game);


        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 1);
        draft.put("Wales", 3);
        playerInput.draft(BOB, draft);

        assertEquals(2, game.getBoard().getTerritory("England").getUnits());
        assertEquals(4, game.getBoard().getTerritory("Wales").getUnits());
    }

    @Test
    void draftTwiceInRow() throws GameplayException {
        GameBuilder gameBuilder = simpleGameBuilderStarter();
        gameBuilder.addPlayer(ALICE);
        Game game = gameBuilder.build();
        PlayerInput playerInput = getPlayerInputFromGame(game);

        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 4);
        playerInput.draft(BOB, draft);
        draft.clear();
        draft.put("Wales", 1);
        playerInput.draft(ALICE, draft);
        assertThrows(GameplayException.class, () -> playerInput.draft(BOB, draft));

        //TODO: Check for error

    }

}