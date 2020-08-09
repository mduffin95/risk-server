package net.mjduffin.risk.lib.usecase;

public class GameState {
    private String currentPlayer;
    private String phase;
    public int unitsToPlace;
    public String[] territories;
    public String[] occupyingPlayers;
    public Integer[] units;

    GameState(String currentPlayer, String phase) {
        this.currentPlayer = currentPlayer;
        this.phase = phase;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean hasEnded() {
        return false;
    }

    public String getPhase() {
        return phase;
    }
}
