package net.mjduffin.risk.adapters;

import net.mjduffin.risk.entities.Board;
import net.mjduffin.risk.entities.Game;
import net.mjduffin.risk.usecase.BoardBuilder;
import net.mjduffin.risk.usecase.GameBuilder;

public class ConsoleController {
    private static String NORTH_AMERICA = "NorthAmerica";
    private static String SOUTH_AMERICA = "SouthAmerica";
    private static String EUROPE = "Europe";
    private static String AFRICA = "Africa";
    private static String ASIA = "Asia";
    private static String AUSTRALASIA = "Australasia";



    public static void main(String[] args) {
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

    }
}
