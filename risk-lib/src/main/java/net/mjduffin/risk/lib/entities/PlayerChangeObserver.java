package net.mjduffin.risk.lib.entities;

public interface PlayerChangeObserver {
    void notify(PlayerId oldPlayer, PlayerId newPlayer);
}
