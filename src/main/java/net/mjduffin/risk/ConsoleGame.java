package net.mjduffin.risk;

import net.mjduffin.risk.adapters.ConsoleManager;
import net.mjduffin.risk.usecase.GameFactory;
import net.mjduffin.risk.usecase.GameState;
import net.mjduffin.risk.usecase.PlayerInput;
import net.mjduffin.risk.usecase.PlayerOutput;
import net.mjduffin.risk.view.ConsoleInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleGame {
    private final Map<String, PlayerOutput> pcMap = new HashMap<>();

    public static void main(String[] args) {
        String[] players = {"Joe", "Sam"};
        List<PlayerOutput> playerControllers = new ArrayList<>();

        PlayerInput pi = GameFactory.basicGame(players);
        ConsoleInput input = new ConsoleInput();

        for (int i=0; i<players.length; i++) {

            PlayerOutput pc = new ConsoleManager(players[i], pi, input);
            playerControllers.add(pc);
        }

        new ConsoleGame().start(pi, playerControllers);
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


}
