package net.mjduffin.risk.usecase;

public class GameState {
    private String currentPlayer;
    public String[] territories;
    public String[] occupyingPlayers;
    public Integer[] units;

    GameState(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean hasEnded() {
        return false;
    }
}
