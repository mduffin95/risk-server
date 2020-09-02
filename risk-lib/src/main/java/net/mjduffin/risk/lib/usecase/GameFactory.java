package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.*;

import java.util.ArrayList;
import java.util.List;

public class GameFactory {
    private static String NORTH_AMERICA = "NorthAmerica";
    private static String SOUTH_AMERICA = "SouthAmerica";
    private static String EUROPE = "Europe";
    private static String AFRICA = "Africa";
    private static String ASIA = "Asia";
    private static String AUSTRALASIA = "Australasia";

    public static GameManager basicGame(String[] players) {
        Game.Builder gameBuilder = new Game.Builder();

        List<String> terr1 = new ArrayList<>();
        terr1.add(NORTH_AMERICA);
        terr1.add(SOUTH_AMERICA);
        terr1.add(EUROPE);
        List<String> terr2 = new ArrayList<>();
        terr2.add(AFRICA);
        terr2.add(ASIA);
        terr2.add(AUSTRALASIA);

        gameBuilder.addPlayerWithTerritories("Alice", terr1);
        gameBuilder.addPlayerWithTerritories("Bob", terr2);

        gameBuilder.addEdge(NORTH_AMERICA, SOUTH_AMERICA)
                .addEdge(NORTH_AMERICA, EUROPE)
                .addEdge(NORTH_AMERICA, ASIA)
                .addEdge(SOUTH_AMERICA, AFRICA)
                .addEdge(EUROPE, AFRICA)
                .addEdge(EUROPE, ASIA)
                .addEdge(ASIA, AUSTRALASIA);

        Game game = gameBuilder.build();

        DieThrow dieThrow = new RandomDieThrow();
        DiceManager diceManager = new DiceManager(dieThrow);
        GameManager gameManager = new GameManager(game, diceManager);
        return gameManager;

    }
}
