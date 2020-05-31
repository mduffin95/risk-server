package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AttackTests {
//    Game game;
//    PlayerInput playerInput;
    String PLAYER_A = "PlayerA";
    String PLAYER_B = "PlayerB";

    PlayerInput createTwoPlayerGame(DieThrow dieThrow) {
        Board board = new Board();
        Random random = new Random(0);
        Game game = new Game(board, random);
//        DieThrow dieThrow = new RandomDieThrow();
        DiceManager diceManager = new DiceManager(dieThrow);
        PlayerInput playerInput = new GameManager(game, diceManager);
        Player player_a = game.addPlayer(PLAYER_A);
        Player player_b = game.addPlayer(PLAYER_B);

        Territory england = game.getBoard().getOrCreateTerritory("England");
        Territory wales = game.getBoard().getOrCreateTerritory("Wales");

        game.getBoard().addEdge(england, wales);

        england.init(player_a);
        wales.init(player_b);

        playerInput.startGame();
        return playerInput;
    }

//    @BeforeEach
//    void setUp() {
//
//    }

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
//        System.out.println(result.attackUnits);
//        System.out.println(result.defendUnits);

        assertEquals(4, result.attackUnits);
        assertEquals(0, result.defendUnits);
    }

//    @Test
//    void
}
