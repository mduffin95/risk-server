package net.mjduffin.risk.lib.entities

interface StateChangeObserver {
    fun notify(oldState: Game.State, newState: Game.State)
}