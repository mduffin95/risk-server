package net.mjduffin.risk.adapters;

import net.mjduffin.risk.adapters.PlayerController;
import net.mjduffin.risk.entities.Player;
import net.mjduffin.risk.usecase.GameFactory;
import net.mjduffin.risk.usecase.GameState;
import net.mjduffin.risk.usecase.PlayerInput;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleController {



    public static void main(String[] args) {
        String[] players = {"Joe", "Sam"};
        List<PlayerController> playerControllers = new ArrayList<>();

        PlayerInput pi = GameFactory.basicGame(players);
        ConsoleInput input = new ConsoleInput();

        for (int i=0; i<players.length; i++) {

            PlayerController pc = new PlayerController(players[i], pi, input);
            playerControllers.add(pc);
        }

        new ConsoleController().start(pi, playerControllers);
    }


    public void start(PlayerInput pi, List<PlayerController> playerControllers) {
        Map<String, PlayerController> pcMap = new HashMap<>();
        for (PlayerController pc: playerControllers) {
            pcMap.put(pc.name, pc);
        }

        GameState gameState = pi.getGameState();
        while (!gameState.hasEnded()) {
            printGameState(gameState);

            PlayerController current = pcMap.get(gameState.getCurrentPlayer());
            current.turn();
            gameState = pi.getGameState();
        }

    }


    void printGameState(GameState gameState) {
        System.out.println("Current player: " + gameState.getCurrentPlayer());
        for (int i = 0; i < gameState.territories.length; i++) {
            System.out.println(gameState.territories[i] + " -> " + gameState.occupyingPlayers[i] + " (" + gameState.units[i] +")");
        }
    }
}
