package net.mjduffin.risk.lib.entities

interface PlayerChangeObserver {
    fun notify(oldPlayer: PlayerId, newPlayer: PlayerId)
}