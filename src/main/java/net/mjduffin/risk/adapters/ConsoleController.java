package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.GameFactory;
import net.mjduffin.risk.usecase.GameState;
import net.mjduffin.risk.usecase.PlayerInput;
import net.mjduffin.risk.usecase.PlayerOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleController {
    private final Map<String, PlayerOutput> pcMap = new HashMap<>();

    public static void main(String[] args) {
        String[] players = {"Joe", "Sam"};
        List<PlayerOutput> playerControllers = new ArrayList<>();

        PlayerInput pi = GameFactory.basicGame(players);
        ConsoleInput input = new ConsoleInput();

        for (int i=0; i<players.length; i++) {

            PlayerOutput pc = new PlayerController(players[i], pi, input);
            playerControllers.add(pc);
        }

        new ConsoleController().start(pi, playerControllers);
    }


    public void start(PlayerInput pi, List<PlayerOutput> playerControllers) {
        for (PlayerOutput pc: playerControllers) {
            pcMap.put(pc.getPlayerName(), pc);
        }
        pi.start(this);
    }

    public void takeTurn(GameState gameState) {
        printGameState(gameState);
        PlayerOutput current = pcMap.get(gameState.getCurrentPlayer());
        current.turn(gameState);
    }

    void printGameState(GameState gameState) {
        System.out.println("Current player: " + gameState.getCurrentPlayer());
        System.out.println("Phase: " + gameState.getPhase());
        if (gameState.getPhase().equals("DRAFT") || gameState.getPhase().equals("ALLDRAFT")) {
            System.out.println("Units to place: " + gameState.unitsToPlace);
        }
        System.out.println("***********");
        for (int i = 0; i < gameState.territories.length; i++) {
            System.out.println(gameState.territories[i] + " -> " + gameState.occupyingPlayers[i] + " (" + gameState.units[i] +")");
        }
    }
}
