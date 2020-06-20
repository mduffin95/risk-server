package net.mjduffin.risk.entities;

public interface StateChangeObserver {
    void notify(Game.State oldState, Game.State newState);
}
