package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.Game
import net.mjduffin.risk.lib.entities.PlayerId

class UnitStore(private val numPlayers: Int, state: Game.State) {
    private val TOTAL_UNITS = 60
    var units = 0
        private set
    val draftRemaining: Map<PlayerId, Int> = mapOf()
    fun useUnits(units: Int) {
        this.units -= units
    }

    //Returns true if a particular player has finished drafting
    fun finishedDrafting(player: PlayerId): Boolean {
        return draftRemaining[player] == null || draftRemaining[player] == 0
    }

    init {
        //Initialise draft map
        units = if (state == Game.State.ALLDRAFT) {
            TOTAL_UNITS / numPlayers
        } else {
            10
        }
    }
}