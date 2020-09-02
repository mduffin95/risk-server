package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.Board;
import net.mjduffin.risk.lib.entities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBuilder {
    private int seed;
    private Board board;
    private List<Player> players = new ArrayList<>();

    public GameBuilder board(Board board) {
        this.board = board;
        return this;
    }

    public GameBuilder seed(int seed) {
        this.seed = seed;
        return this;
    }

    public GameBuilder addPlayer(String playerName) {
        Player player = new Player(playerName);
        players.add(player);
        return this;
    }

    public Game build(boolean shuffle) {
//        board.initialiseWithPlayers(players, shuffle); // Do this when adding territories
        return new Game(board, players, new Random(seed));
    }
}
