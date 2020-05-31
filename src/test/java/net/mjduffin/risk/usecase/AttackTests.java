package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AttackTests {
    Game game;
    PlayerInput playerInput;
    String PLAYER_A = "PlayerA";
    String PLAYER_B = "PlayerB";

    @BeforeEach
    void setUp() {
        Board board = new Board();
        Random random = new Random(0);
        game = new Game(board, random);
        DieThrow dieThrow = new RandomDieThrow();
        DiceManager diceManager = new DiceManager(dieThrow);
        playerInput = new GameManager(game, diceManager);
        Player player_a = game.addPlayer(PLAYER_A);
        Player player_b = game.addPlayer(PLAYER_B);

        Territory england = game.getBoard().getOrCreateTerritory("England");
        Territory wales = game.getBoard().getOrCreateTerritory("Wales");

        game.getBoard().addEdge(england, wales);

        england.init(player_a);
        wales.init(player_b);

        game.init();
    }

    @Test
    void attack() throws TerritoryNotFoundException, PlayerNotFoundException, GameplayException {
        Map<String, Integer> draft = new HashMap<>();
        draft.put("England", 4);
//        draft.put("Wales", 3);

        playerInput.draft(PLAYER_A, draft);
        draft = new HashMap<>();
        draft.put("Wales", 1);
        playerInput.draft(PLAYER_B, draft);

        AttackResult result = playerInput.attack(PLAYER_A, "England", "Wales");
        System.out.println(result.attackUnits);
        System.out.println(result.defendUnits);
    }

//    @Test
//    void
}
