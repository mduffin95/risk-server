package net.mjduffin.risk.lib.entities;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private final String name;
    private final Set<Territory> territories = new HashSet<>();
    private int draftableUnits;

    public Player(String name) {
        this.name = name;
    }

    void addTerritory(Territory t) {
        territories.add(t);
    }

    void removeTerritory(Territory t) {
        territories.remove(t);
    }

    int getTotalUnits() {
        return territories.stream().map(Territory::getUnits).reduce(0, Integer::sum);
    }
    int getTotalTerritories() {
        return territories.size();
    }

    public String getName() {
        return name;
    }

    public void calulateAndSetDraftableUnits(Game.State state) {
        if (Game.State.ALLDRAFT.equals(state)) {
            draftableUnits = 10;
        } else {
            int territoryBonus = getTotalTerritories() / 3;
            if (territoryBonus < 3) {
                territoryBonus = 3;
            }

            draftableUnits = territoryBonus;
        }
    }

    public int getDraftableUnits() {
        return draftableUnits;
    }

    public void useUnits(int units) {
        this.draftableUnits -= units;

        //TODO: If draftableUnits reaches zero, trigger next player and next game state
    }

    public boolean finishedDrafting() {
        return this.draftableUnits == 0;
    }

    //Assume units is within our draft limit
    public boolean draft(Territory territory, int units) {
        if (territories.contains(territory)) {
            territory.addUnits(units);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Player)) {
            return false;
        }
        Player p = (Player) obj;
        if (this.name != null) {
            return this.name.equals(p.name);
        }
        return false;
    }
}
