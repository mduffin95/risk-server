package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.Game;
import net.mjduffin.risk.entities.Player;

import java.util.HashMap;
import java.util.Map;

import static net.mjduffin.risk.entities.Game.State.ALLDRAFT;

public class UnitStore {
    private final int TOTAL_UNITS = 60;
    private final int numPlayers;
    private int units;
    Map<Player, Integer> draftRemaining = new HashMap<>();

    public UnitStore(int numPlayers, Game.State state) {
        this.numPlayers = numPlayers;
        //Initialise draft map
        if (state.equals(ALLDRAFT)) {
            units = TOTAL_UNITS / numPlayers;
        } else {
            units = 10;
        }
    }

    int getUnits() {
        return this.units;
    }

    public void useUnits(int units) {
        this.units -= units;
    }

    //Returns true if a particular player has finished drafting
    public boolean finishedDrafting(Player player) {
        return draftRemaining.get(player) == null || draftRemaining.get(player) == 0;
    }
}
