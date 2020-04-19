package net.mjduffin.risk.entities;

import java.util.List;
import java.util.Map;

public class Game {
    List<Player> players;
    Board board;

    int playerIndex = 0;

    void nextPlayer() {
        playerIndex = (playerIndex + 1) % players.size();
    }

    Player getCurrentPlayer() {
        return players.get(playerIndex);
    }

}
