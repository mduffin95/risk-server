package net.mjduffin.risk.lib.entities;

public interface PlayerChangeObserver {
    void notify(Player oldPlayer, Player newPlayer);
}
