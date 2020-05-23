package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.Board;
import net.mjduffin.risk.entities.Game;
import net.mjduffin.risk.entities.Player;
import net.mjduffin.risk.entities.Territory;
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
        Board board = new Board();
        Random random = new Random(0);
        game = new Game(board, random);
        playerInput = new GameManager(game);
        Player bob = game.addPlayer(PLAYER_NAME);
        Territory england = game.getBoard().getOrCreateTerritory("England");
        Territory wales = game.getBoard().getOrCreateTerritory("Wales");
        england.init(bob);
        wales.init(bob);
        game.getBoard().addEdge(england, wales);
    }

    @Test
    void draft() {

        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 1);
        draft.put("Wales", 3);
        playerInput.draft(PLAYER_NAME, draft);

        assertEquals(2, game.getBoard().getTerritory("England").getUnits());
        assertEquals(4, game.getBoard().getTerritory("Wales").getUnits());
    }

    @Test
    void draftTwiceInRow() {
        Player alice = game.addPlayer("Alice");
        game.init();
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