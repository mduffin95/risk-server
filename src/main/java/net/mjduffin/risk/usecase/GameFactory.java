package net.mjduffin.risk.usecase;

import net.mjduffin.risk.adapters.PlayerController;
import net.mjduffin.risk.entities.*;

public class GameFactory {
    private static String NORTH_AMERICA = "NorthAmerica";
    private static String SOUTH_AMERICA = "SouthAmerica";
    private static String EUROPE = "Europe";
    private static String AFRICA = "Africa";
    private static String ASIA = "Asia";
    private static String AUSTRALASIA = "Australasia";

    public static void basicGame() {
        GameBuilder gameBuilder = new GameBuilder();
        BoardBuilder boardBuilder = new BoardBuilder();


        boardBuilder.addTerritory(NORTH_AMERICA)
                .addTerritory(SOUTH_AMERICA)
                .addTerritory(EUROPE)
                .addTerritory(AFRICA)
                .addTerritory(ASIA)
                .addTerritory(AUSTRALASIA);

        boardBuilder.addEdge(NORTH_AMERICA, SOUTH_AMERICA)
                .addEdge(NORTH_AMERICA, EUROPE)
                .addEdge(NORTH_AMERICA, ASIA)
                .addEdge(SOUTH_AMERICA, AFRICA)
                .addEdge(EUROPE, AFRICA)
                .addEdge(EUROPE, ASIA)
                .addEdge(ASIA, AUSTRALASIA);

        Board board = boardBuilder.build();

        gameBuilder.addPlayer("Joe");
        gameBuilder.addPlayer("Sam");

        gameBuilder.board(board);
        gameBuilder.seed(1234);

        Game game = gameBuilder.build();
        DieThrow dieThrow = new RandomDieThrow();
        DiceManager diceManager = new DiceManager(dieThrow);
        GameManager gameManager = new GameManager(game, diceManager);

        PlayerController pc1 = new PlayerController("Joe", gameManager);
        PlayerController pc2 = new PlayerController("Sam", gameManager);
    }
}