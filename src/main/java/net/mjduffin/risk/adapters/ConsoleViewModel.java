package net.mjduffin.risk.adapters;

public class ConsoleViewModel {
    //Game state formatted for console output
    private String currentPlayer;
    private String phase;
    public int unitsToPlace;
    public String[] territories;
    public String[] occupyingPlayers;
    public Integer[] units;

    public String getCurrentPlayer() {
        return this.currentPlayer;
    }

    public String getPhase() {
        return this.phase;
    }
}
