package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DraftTests {
    Game game;
    PlayerInput playerInput;
    final String PLAYER_NAME = "Bob";

    @BeforeEach
    void setUp() {
        BoardBuilder boardBuilder = new BoardBuilder();
        boardBuilder.addTerritory("England")
            .addTerritory("Wales");
        boardBuilder.addEdge("England", "Wales");

        Board board = boardBuilder.build();
        GameBuilder gameBuilder = new GameBuilder();

        gameBuilder.board(board);
        gameBuilder.addPlayer(PLAYER_NAME);

        game = gameBuilder.build();
        DiceManager diceManager = new DiceManager(new RandomDieThrow());
        playerInput = new GameManager(game, diceManager);
    }

    @Test
    void draft() throws GameplayException {

        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 1);
        draft.put("Wales", 3);
        playerInput.draft(PLAYER_NAME, draft);

        assertEquals(2, game.getBoard().getTerritory("England").getUnits());
        assertEquals(4, game.getBoard().getTerritory("Wales").getUnits());
    }

    @Test
    void draftTwiceInRow() throws GameplayException {
//        Player alice = game.addPlayer("Alice");
//        game.start();
        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 4);
        playerInput.draft(PLAYER_NAME, draft);
        draft.clear();
        draft.put("Wales", 1);
        playerInput.draft("Alice", draft);
        playerInput.draft(PLAYER_NAME, draft);

        //TODO: Check for error

    }

}