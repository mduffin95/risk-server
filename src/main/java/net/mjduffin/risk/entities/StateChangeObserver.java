package net.mjduffin.risk.entities;

public interface StateChangeObserver {
    void notify(Game.State state);
}
