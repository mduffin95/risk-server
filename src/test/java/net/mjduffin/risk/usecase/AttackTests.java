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
        playerInput = new GameManager(game);
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
        draft.put("England", 1);
        draft.put("Wales", 3);


        AttackResult result = playerInput.attack(PLAYER_A, "England", "Wales");
    }
}
