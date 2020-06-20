package net.mjduffin.risk.entities;

public interface PlayerChangeObserver {
    void notify(Player oldPlayer, Player newPlayer);
}
