package net.mjduffin.risk.lib.entities;

public interface StateChangeObserver {
    void notify(Game.State oldState, Game.State newState);
}
