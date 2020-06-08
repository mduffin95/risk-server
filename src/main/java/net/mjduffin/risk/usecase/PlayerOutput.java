package net.mjduffin.risk.usecase;

public interface PlayerOutput {
    void notifyTurn();
    String getPlayerName();
    void turn(GameState gameState);
}
